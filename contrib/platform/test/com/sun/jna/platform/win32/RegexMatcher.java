/*
 * Copyright (c) 2013 Markus Karg, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */

package com.sun.jna.platform.win32;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * This Hamcrest matcher asserts that a {@link String} matches a provided regex pattern.
 *
 * @author Markus KARG (markus[at]headcrashing[dot]eu)
 */
final class RegexMatcher extends TypeSafeMatcher<String> {
    private final String regex;

    public RegexMatcher(final String regex) {
        this.regex = regex;
    }

    @Override
    public final boolean matchesSafely(final String string) {
        return string.matches(this.regex);
    }

    @Override
    public final void describeTo(final Description description) {
        description.appendText("matches regex ").appendValue(this.regex);
    }

    public final static RegexMatcher matches(final String regex) {
        return new RegexMatcher(regex);
    }
}
