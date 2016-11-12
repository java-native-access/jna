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

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

/**
 * <p>uuid({0002095E-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{0002095E-0000-0000-C000-000000000046}")
public interface Range {
    /**
     * <p>id(0x0)</p>
     */
    @ComProperty(name = "Text", dispId = 0x0)
    String getText();
            
    /**
     * <p>id(0x0)</p>
     */
    @ComProperty(name = "Text", dispId = 0x0)
    void setText(String param0);
        
    /**
     * <p>id(0x5)</p>
     */
    @ComProperty(name = "Font", dispId = 0x5)
    Font getFont();
            
    /**
     * <p>id(0x5)</p>
     */
    @ComProperty(name = "Font", dispId = 0x5)
    void setFont(Font param0);
       
    /**
     * <p>id(0x3b)</p>
     */
    @ComProperty(name = "Paragraphs", dispId = 0x3b)
    Paragraphs getParagraphs();
            
            
    /**
     * <p>id(0x44e)</p>
     */
    @ComProperty(name = "ParagraphFormat", dispId = 0x44e)
    ParagraphFormat getParagraphFormat();
            
    /**
     * <p>id(0x44e)</p>
     */
    @ComProperty(name = "ParagraphFormat", dispId = 0x44e)
    void setParagraphFormat(ParagraphFormat param0);
            
    /**
     * <p>id(0x4b)</p>
     */
    @ComProperty(name = "Bookmarks", dispId = 0x4b)
    Bookmarks getBookmarks();
            
    /**
     * <p>id(0xd4)</p>
     */
    @ComMethod(name = "InsertParagraphBefore", dispId = 0xd4)
    void InsertParagraphBefore();
    

    /**
     * <p>id(0x77)</p>
     */
    @ComMethod(name = "Cut", dispId = 0x77)
    void Cut();
            
    /**
     * <p>id(0x78)</p>
     */
    @ComMethod(name = "Copy", dispId = 0x78)
    void Copy();
            
    /**
     * <p>id(0x79)</p>
     */
    @ComMethod(name = "Paste", dispId = 0x79)
    void Paste();
            
    /**
     * <p>id(0x7a)</p>
     */
    @ComMethod(name = "InsertBreak", dispId = 0x7a)
    void InsertBreak(Object Type);
    
    /**
     * <p>id(0xa0)</p>
     */
    @ComMethod(name = "InsertParagraph", dispId = 0xa0)
    void InsertParagraph();
            
    /**
     * <p>id(0xa1)</p>
     */
    @ComMethod(name = "InsertParagraphAfter", dispId = 0xa1)
    void InsertParagraphAfter();
            
    /**
     * <p>id(0x65)</p>
     */
    @ComMethod(name = "Collapse", dispId = 0x65)
    void Collapse(Object Direction);
            
    /**
     * <p>id(0x66)</p>
     */
    @ComMethod(name = "InsertBefore", dispId = 0x66)
    void InsertBefore(String Text);
            
    /**
     * <p>id(0x68)</p>
     */
    @ComMethod(name = "InsertAfter", dispId = 0x68)
    void InsertAfter(String Text);
            
    /**
     * <p>id(0x139)</p>
     */
    @ComProperty(name = "Information", dispId = 0x139)
    Object getInformation(WdInformation Type);
    
    /**
     * <p>id(0x13f)</p>
     */
    @ComProperty(name = "InlineShapes", dispId = 0x13f)
    InlineShapes getInlineShapes();

}