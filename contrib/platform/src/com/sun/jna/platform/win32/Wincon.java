package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import java.util.Arrays;
import java.util.List;

/**
 * Interface for the Wincon.h header file.
 */
public interface Wincon extends StdCallLibrary, WinDef {

    /**
     * Contains information for a console font.
     */
    public static class CONSOLE_FONT_INFO extends Structure {

        public static class ByReference extends CONSOLE_FONT_INFO implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public CONSOLE_FONT_INFO() {
        }

        public CONSOLE_FONT_INFO(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * The index of the font in the system's console font table.
         */
        public DWORD nFont;

        /**
         * A COORD structure that contains the width and height of each character in the font, in logical units. The X
         * member contains the width, while the Y member contains the height.
         */
        public COORD dwFontSize;

        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"nFont", "dwFontSize"});
        }
    }

    /**
     * Defines the coordinates of a character cell in a console screen buffer. The origin of the coordinate system (0,0)
     * is at the top, left cell of the buffer.
     */
    public static class COORD extends Structure {

        public COORD() {
        }

        public COORD(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * The horizontal coordinate or column value. The units depend on the function call.
         */
        public SHORT x;

        /**
         * The vertical coordinate or row value. The units depend on the function call.
         */
        public SHORT y;

        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"x", "y"});
        }
    }

}
