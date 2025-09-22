/*
 * Copyright (c) 2015, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
"use strict";
const messages = {
    enterTerm: "Enter a search term",
    noResult: "No results found",
    oneResult: "Found one result",
    manyResults: "Found {0} results",
    loading: "Loading search index...",
    searching: "Searching...",
    redirecting: "Redirecting to first result...",
    linkIcon: "Link icon",
    linkToSection: "Link to this section"
}
const categories = {
    modules: "Modules",
    packages: "Packages",
    types: "Classes and Interfaces",
    members: "Members",
    searchTags: "Search Tags"
};
const highlight = "<span class='result-highlight'>$&</span>";
const NO_MATCH = {};
const MAX_RESULTS = 300;
function checkUnnamed(name, separator) {
    return name === "<Unnamed>" || !name ? "" : name + separator;
}
function escapeHtml(str) {
    return str.replace(/</g, "&lt;").replace(/>/g, "&gt;");
}
function getHighlightedText(str, boundaries, from, to) {
    var start = from;
    var text = "";
    for (var i = 0; i < boundaries.length; i += 2) {
        var b0 = boundaries[i];
        var b1 = boundaries[i + 1];
        if (b0 >= to || b1 <= from) {
            continue;
        }
        text += escapeHtml(str.slice(start, Math.max(start, b0)));
        text += "<span class='result-highlight'>";
        text += escapeHtml(str.slice(Math.max(start, b0), Math.min(to, b1)));
        text += "</span>";
        start = Math.min(to, b1);
    }
    text += escapeHtml(str.slice(start, to));
    return text;
}
function getURLPrefix(item, category) {
    var urlPrefix = "";
    var slash = "/";
    if (category === "modules") {
        return item.l + slash;
    } else if (category === "packages" && item.m) {
        return item.m + slash;
    } else if (category === "types" || category === "members") {
        if (item.m) {
            urlPrefix = item.m + slash;
        } else {
            $.each(packageSearchIndex, function(index, it) {
                if (it.m && item.p === it.l) {
                    urlPrefix = it.m + slash;
                }
            });
        }
    }
    return urlPrefix;
}
function getURL(item, category) {
    if (item.url) {
        return item.url;
    }
    var url = getURLPrefix(item, category);
    if (category === "modules") {
        url += "module-summary.html";
    } else if (category === "packages") {
        if (item.u) {
            url = item.u;
        } else {
            url += item.l.replace(/\./g, '/') + "/package-summary.html";
        }
    } else if (category === "types") {
        if (item.u) {
            url = item.u;
        } else {
            url += checkUnnamed(item.p, "/").replace(/\./g, '/') + item.l + ".html";
        }
    } else if (category === "members") {
        url += checkUnnamed(item.p, "/").replace(/\./g, '/') + item.c + ".html" + "#";
        if (item.u) {
            url += item.u;
        } else {
            url += item.l;
        }
    } else if (category === "searchTags") {
        url += item.u;
    }
    item.url = url;
    return url;
}
function createMatcher(term, camelCase) {
    if (camelCase && !isUpperCase(term)) {
        return null;  // no need for camel-case matcher for lower case query
    }
    var pattern = "";
    var upperCase = [];
    term.trim().split(/\s+/).forEach(function(w, index, array) {
        var tokens = w.split(/(?=[A-Z,.()<>?[\/])/);
        for (var i = 0; i < tokens.length; i++) {
            var s = tokens[i];
            // ',' and '?' are the only delimiters commonly followed by space in java signatures
            pattern += "(" + $.ui.autocomplete.escapeRegex(s).replace(/[,?]/g, "$&\\s*?") + ")";
            upperCase.push(false);
            var isWordToken =  /\w$/.test(s);
            if (isWordToken) {
                if (i === tokens.length - 1 && index < array.length - 1) {
                    // space in query string matches all delimiters
                    pattern += "(.*?)";
                    upperCase.push(isUpperCase(s[0]));
                } else {
                    if (!camelCase && isUpperCase(s) && s.length === 1) {
                        pattern += "()";
                    } else {
                        pattern += "([a-z0-9$<>?[\\]]*?)";
                    }
                    upperCase.push(isUpperCase(s[0]));
                }
            } else {
                pattern += "()";
                upperCase.push(false);
            }
        }
    });
    var re = new RegExp(pattern, "gi");
    re.upperCase = upperCase;
    return re;
}
function findMatch(matcher, input, startOfName, endOfName) {
    var from = startOfName;
    matcher.lastIndex = from;
    var match = matcher.exec(input);
    // Expand search area until we get a valid result or reach the beginning of the string
    while (!match || match.index + match[0].length < startOfName || endOfName < match.index) {
        if (from === 0) {
            return NO_MATCH;
        }
        from = input.lastIndexOf(".", from - 2) + 1;
        matcher.lastIndex = from;
        match = matcher.exec(input);
    }
    var boundaries = [];
    var matchEnd = match.index + match[0].length;
    var score = 5;
    var start = match.index;
    var prevEnd = -1;
    for (var i = 1; i < match.length; i += 2) {
        var isUpper = isUpperCase(input[start]);
        var isMatcherUpper = matcher.upperCase[i];
        // capturing groups come in pairs, match and non-match
        boundaries.push(start, start + match[i].length);
        // make sure groups are anchored on a left word boundary
        var prevChar = input[start - 1] || "";
        var nextChar = input[start + 1] || "";
        if (start !== 0 && !/[\W_]/.test(prevChar) && !/[\W_]/.test(input[start])) {
            if (isUpper && (isLowerCase(prevChar) || isLowerCase(nextChar))) {
                score -= 0.1;
            } else if (isMatcherUpper && start === prevEnd) {
                score -= isUpper ? 0.1 : 1.0;
            } else {
                return NO_MATCH;
            }
        }
        prevEnd = start + match[i].length;
        start += match[i].length + match[i + 1].length;

        // lower score for parts of the name that are missing
        if (match[i + 1] && prevEnd < endOfName) {
            score -= rateNoise(match[i + 1]);
        }
    }
    // lower score if a type name contains unmatched camel-case parts
    if (input[matchEnd - 1] !== "." && endOfName > matchEnd)
        score -= rateNoise(input.slice(matchEnd, endOfName));
    score -= rateNoise(input.slice(0, Math.max(startOfName, match.index)));

    if (score <= 0) {
        return NO_MATCH;
    }
    return {
        input: input,
        score: score,
        boundaries: boundaries
    };
}
function isUpperCase(s) {
    return s !== s.toLowerCase();
}
function isLowerCase(s) {
    return s !== s.toUpperCase();
}
function rateNoise(str) {
    return (str.match(/([.(])/g) || []).length / 5
         + (str.match(/([A-Z]+)/g) || []).length / 10
         +  str.length / 20;
}
function doSearch(request, response) {
    var term = request.term.trim();
    var maxResults = request.maxResults || MAX_RESULTS;
    if (term.length === 0) {
        return this.close();
    }
    var matcher = {
        plainMatcher: createMatcher(term, false),
        camelCaseMatcher: createMatcher(term, true)
    }
    var indexLoaded = indexFilesLoaded();

    function getPrefix(item, category) {
        switch (category) {
            case "packages":
                return checkUnnamed(item.m, "/");
            case "types":
                return checkUnnamed(item.p, ".");
            case "members":
                return checkUnnamed(item.p, ".") + item.c + ".";
            default:
                return "";
        }
    }
    function useQualifiedName(category) {
        switch (category) {
            case "packages":
                return /[\s/]/.test(term);
            case "types":
            case "members":
                return /[\s.]/.test(term);
            default:
                return false;
        }
    }
    function searchIndex(indexArray, category) {
        var matches = [];
        if (!indexArray) {
            if (!indexLoaded) {
                matches.push({ l: messages.loading, category: category });
            }
            return matches;
        }
        $.each(indexArray, function (i, item) {
            var prefix = getPrefix(item, category);
            var simpleName = item.l;
            var qualifiedName = prefix + simpleName;
            var useQualified = useQualifiedName(category);
            var input = useQualified ? qualifiedName : simpleName;
            var startOfName = useQualified ? prefix.length : 0;
            var endOfName = category === "members" && input.indexOf("(", startOfName) > -1
                ? input.indexOf("(", startOfName) : input.length;
            var m = findMatch(matcher.plainMatcher, input, startOfName, endOfName);
            if (m === NO_MATCH && matcher.camelCaseMatcher) {
                m = findMatch(matcher.camelCaseMatcher, input, startOfName, endOfName);
            }
            if (m !== NO_MATCH) {
                m.indexItem = item;
                m.prefix = prefix;
                m.category = category;
                if (!useQualified) {
                    m.input = qualifiedName;
                    m.boundaries = m.boundaries.map(function(b) {
                        return b + prefix.length;
                    });
                }
                matches.push(m);
            }
            return true;
        });
        return matches.sort(function(e1, e2) {
            return e2.score - e1.score;
        }).slice(0, maxResults);
    }

    var result = searchIndex(moduleSearchIndex, "modules")
         .concat(searchIndex(packageSearchIndex, "packages"))
         .concat(searchIndex(typeSearchIndex, "types"))
         .concat(searchIndex(memberSearchIndex, "members"))
         .concat(searchIndex(tagSearchIndex, "searchTags"));

    if (!indexLoaded) {
        updateSearchResults = function() {
            doSearch(request, response);
        }
    } else {
        updateSearchResults = function() {};
    }
    response(result);
}
// JQuery search menu implementation
$.widget("custom.catcomplete", $.ui.autocomplete, {
    _create: function() {
        this._super();
        this.widget().menu("option", "items", "> .result-item");
        // workaround for search result scrolling
        this.menu._scrollIntoView = function _scrollIntoView( item ) {
            var borderTop, paddingTop, offset, scroll, elementHeight, itemHeight;
            if ( this._hasScroll() ) {
                borderTop = parseFloat( $.css( this.activeMenu[ 0 ], "borderTopWidth" ) ) || 0;
                paddingTop = parseFloat( $.css( this.activeMenu[ 0 ], "paddingTop" ) ) || 0;
                offset = item.offset().top - this.activeMenu.offset().top - borderTop - paddingTop;
                scroll = this.activeMenu.scrollTop();
                elementHeight = this.activeMenu.height() - 26;
                itemHeight = item.outerHeight();

                if ( offset < 0 ) {
                    this.activeMenu.scrollTop( scroll + offset );
                } else if ( offset + itemHeight > elementHeight ) {
                    this.activeMenu.scrollTop( scroll + offset - elementHeight + itemHeight );
                }
            }
        };
    },
    _renderMenu: function(ul, items) {
        var currentCategory = "";
        var widget = this;
        widget.menu.bindings = $();
        $.each(items, function(index, item) {
            if (item.category && item.category !== currentCategory) {
                ul.append("<li class='ui-autocomplete-category'>" + categories[item.category] + "</li>");
                currentCategory = item.category;
            }
            var li = widget._renderItemData(ul, item);
            if (item.category) {
                li.attr("aria-label", categories[item.category] + " : " + item.l);
            } else {
                li.attr("aria-label", item.l);
            }
            li.attr("class", "result-item");
        });
        ul.append("<li class='ui-static-link'><a href='" + pathtoroot + "search.html?q="
            + encodeURI(widget.term) + "'>Go to search page</a></li>");
    },
    _renderItem: function(ul, item) {
        var li = $("<li/>").appendTo(ul);
        var div = $("<div/>").appendTo(li);
        var label = item.l
            ? item.l
            : getHighlightedText(item.input, item.boundaries, 0, item.input.length);
        var idx = item.indexItem;
        if (item.category === "searchTags" && idx && idx.h) {
            if (idx.d) {
                div.html(label + "<span class='search-tag-holder-result'> (" + idx.h + ")</span><br><span class='search-tag-desc-result'>"
                    + idx.d + "</span><br>");
            } else {
                div.html(label + "<span class='search-tag-holder-result'> (" + idx.h + ")</span>");
            }
        } else {
            div.html(label);
        }
        return li;
    }
});
$(function() {
    var expanded = false;
    var windowWidth;
    function collapse() {
        if (expanded) {
            $("div#navbar-top").removeAttr("style");
            $("button#navbar-toggle-button")
                .removeClass("expanded")
                .attr("aria-expanded", "false");
            expanded = false;
        }
    }
    $("button#navbar-toggle-button").click(function (e) {
        if (expanded) {
            collapse();
        } else {
            var navbar = $("div#navbar-top");
            navbar.height(navbar.prop("scrollHeight"));
            $("button#navbar-toggle-button")
                .addClass("expanded")
                .attr("aria-expanded", "true");
            expanded = true;
            windowWidth = window.innerWidth;
        }
    });
    $("ul.sub-nav-list-small li a").click(collapse);
    $("input#search-input").focus(collapse);
    $("main").click(collapse);
    $("section[id] > :header, :header[id], :header:has(a[id])").each(function(idx, el) {
        // Create anchor links for headers with an associated id attribute
        var hdr = $(el);
        var id = hdr.attr("id") || hdr.parent("section").attr("id") || hdr.children("a").attr("id");
        if (id) {
            hdr.append(" <a href='#" + id + "' class='anchor-link' aria-label='" + messages.linkToSection
                + "'><img src='" + pathtoroot + "link.svg' alt='" + messages.linkIcon +"' tabindex='0'"
                + " width='16' height='16'></a>");
        }
    });
    $(window).on("orientationchange", collapse).on("resize", function(e) {
        if (expanded && windowWidth !== window.innerWidth) collapse();
    });
    var search = $("#search-input");
    var reset = $("#reset-button");
    search.catcomplete({
        minLength: 1,
        delay: 200,
        source: doSearch,
        response: function(event, ui) {
            if (!ui.content.length) {
                ui.content.push({ l: messages.noResult });
            } else {
                $("#search-input").empty();
            }
        },
        autoFocus: true,
        focus: function(event, ui) {
            return false;
        },
        position: {
            collision: "flip"
        },
        select: function(event, ui) {
            if (ui.item.indexItem) {
                var url = getURL(ui.item.indexItem, ui.item.category);
                window.location.href = pathtoroot + url;
                $("#search-input").focus();
            }
        }
    });
    search.val('');
    search.prop("disabled", false);
    reset.prop("disabled", false);
    reset.click(function() {
        search.val('').focus();
    });
    search.focus();
});
