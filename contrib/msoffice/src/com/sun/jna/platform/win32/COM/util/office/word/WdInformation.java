/*
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({26E3C1D3-6937-3EFA-8859-7FFC81869CE5})</p>
 */
public enum WdInformation implements IComEnum {
    
    /**
     * (1)
     */
    wdActiveEndAdjustedPageNumber(1),
    
    /**
     * (2)
     */
    wdActiveEndSectionNumber(2),
    
    /**
     * (3)
     */
    wdActiveEndPageNumber(3),
    
    /**
     * (4)
     */
    wdNumberOfPagesInDocument(4),
    
    /**
     * (5)
     */
    wdHorizontalPositionRelativeToPage(5),
    
    /**
     * (6)
     */
    wdVerticalPositionRelativeToPage(6),
    
    /**
     * (7)
     */
    wdHorizontalPositionRelativeToTextBoundary(7),
    
    /**
     * (8)
     */
    wdVerticalPositionRelativeToTextBoundary(8),
    
    /**
     * (9)
     */
    wdFirstCharacterColumnNumber(9),
    
    /**
     * (10)
     */
    wdFirstCharacterLineNumber(10),
    
    /**
     * (11)
     */
    wdFrameIsSelected(11),
    
    /**
     * (12)
     */
    wdWithInTable(12),
    
    /**
     * (13)
     */
    wdStartOfRangeRowNumber(13),
    
    /**
     * (14)
     */
    wdEndOfRangeRowNumber(14),
    
    /**
     * (15)
     */
    wdMaximumNumberOfRows(15),
    
    /**
     * (16)
     */
    wdStartOfRangeColumnNumber(16),
    
    /**
     * (17)
     */
    wdEndOfRangeColumnNumber(17),
    
    /**
     * (18)
     */
    wdMaximumNumberOfColumns(18),
    
    /**
     * (19)
     */
    wdZoomPercentage(19),
    
    /**
     * (20)
     */
    wdSelectionMode(20),
    
    /**
     * (21)
     */
    wdCapsLock(21),
    
    /**
     * (22)
     */
    wdNumLock(22),
    
    /**
     * (23)
     */
    wdOverType(23),
    
    /**
     * (24)
     */
    wdRevisionMarking(24),
    
    /**
     * (25)
     */
    wdInFootnoteEndnotePane(25),
    
    /**
     * (26)
     */
    wdInCommentPane(26),
    
    /**
     * (28)
     */
    wdInHeaderFooter(28),
    
    /**
     * (31)
     */
    wdAtEndOfRowMarker(31),
    
    /**
     * (32)
     */
    wdReferenceOfType(32),
    
    /**
     * (33)
     */
    wdHeaderFooterType(33),
    
    /**
     * (34)
     */
    wdInMasterDocument(34),
    
    /**
     * (35)
     */
    wdInFootnote(35),
    
    /**
     * (36)
     */
    wdInEndnote(36),
    
    /**
     * (37)
     */
    wdInWordMail(37),
    
    /**
     * (38)
     */
    wdInClipboard(38),
    
    /**
     * (41)
     */
    wdInCoverPage(41),
    
    /**
     * (42)
     */
    wdInBibliography(42),
    
    /**
     * (43)
     */
    wdInCitation(43),
    
    /**
     * (44)
     */
    wdInFieldCode(44),
    
    /**
     * (45)
     */
    wdInFieldResult(45),
    
    /**
     * (46)
     */
    wdInContentControl(46),
    ;

    private WdInformation(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}