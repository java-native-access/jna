package com.sun.jna.platform.win32;

import static org.junit.Assert.assertNotEquals;
import static com.sun.jna.platform.WindowUtilsTest.assertPixelColor;
import static com.sun.jna.platform.WindowUtilsTest.getPixelColor;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Dimension;

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

public class WindowUtilsTest extends TestCase {

    public void testGetAllWindows() {

        final List<DesktopWindow> allWindows = WindowUtils.getAllWindows(false);
        final List<DesktopWindow> allVisibleWindows = WindowUtils
            .getAllWindows(true);

        assertTrue(allWindows.size() > 0);
        assertTrue(allVisibleWindows.size() > 0);
        assertTrue(allWindows.size() > allVisibleWindows.size());

        DesktopWindow explorerProc = null;
        for (final DesktopWindow dw : allWindows) {
            if (dw.getFilePath().toLowerCase().endsWith("explorer.exe")) {
                explorerProc = dw;
                break;
            }
        }

        assertNotNull(explorerProc);

        explorerProc = null;
        for (final DesktopWindow dw : allVisibleWindows) {
            if (dw.getFilePath().toLowerCase().endsWith("explorer.exe")) {
                explorerProc = dw;
                break;
            }
        }

        assertNotNull(explorerProc);
    }

    public void testGetWindowIcon() throws Exception {

        final JFrame w = new JFrame();
        try {
            final BufferedImage expectedIcon = ImageIO
                .read(new FileInputStream(new File(getClass().getResource(
                                                                          "/res/test_icon.png").getPath())));
            w.setIconImage(expectedIcon);
            w.setVisible(true);
            HWND hwnd = new HWND();
            hwnd.setPointer(Native.getComponentPointer(w));

            final BufferedImage obtainedIcon = WindowUtils.getWindowIcon(hwnd);

            assertTrue(obtainedIcon.getWidth() > 0);
            assertTrue(obtainedIcon.getHeight() > 0);

            int[] expectedColors = getPixelColor(expectedIcon, 10, 10);
            assertPixelColor(obtainedIcon, 10, 10, expectedColors[0],
                             expectedColors[1], expectedColors[2]);

            expectedColors = getPixelColor(expectedIcon,
                                           expectedIcon.getWidth() - 10, 10);
            assertPixelColor(obtainedIcon, obtainedIcon.getWidth() - 10, 10,
                             expectedColors[0], expectedColors[1], expectedColors[2]);

            expectedColors = getPixelColor(expectedIcon,
                                           expectedIcon.getWidth() - 10, expectedIcon.getHeight() - 10);
            assertPixelColor(obtainedIcon, obtainedIcon.getWidth() - 10,
                             obtainedIcon.getHeight() - 10, expectedColors[0],
                             expectedColors[1], expectedColors[2]);

            expectedColors = getPixelColor(expectedIcon, 10,
                                           expectedIcon.getHeight() - 10);
            assertPixelColor(obtainedIcon, 10, obtainedIcon.getHeight() - 10,
                             expectedColors[0], expectedColors[1], expectedColors[2]);
        } finally {
            w.dispose();
        }
    }
	
    public void testGetWindowLocationAndSize() {

        final JFrame w = new JFrame();
        try {
            w.setLocation(23, 23);
            w.setPreferredSize(new Dimension(100, 100));
            w.pack();
            w.setVisible(true);

            HWND hwnd = new HWND();
            hwnd.setPointer(Native.getComponentPointer(w));
            final Rectangle locAndSize = WindowUtils
                .getWindowLocationAndSize(hwnd);

            assertEquals(w.getLocation().x, locAndSize.x);
            assertEquals(w.getLocation().y, locAndSize.y);
            assertEquals(w.getSize().width, locAndSize.width);
            assertEquals(w.getSize().height, locAndSize.height);
        } finally {
            w.dispose();
        }
    }
	
    public void testGetWindowTitle() {

        final JFrame w = new JFrame("A super unique title by PAX! "
                                    + System.currentTimeMillis());
        try {
            w.setVisible(true);

            HWND hwnd = new HWND();
            hwnd.setPointer(Native.getComponentPointer(w));

            assertEquals(w.getTitle(), WindowUtils.getWindowTitle(hwnd));
        } finally {
            w.dispose();
        }
    }
	
    public void testGetIconSize() throws Exception {
        
        final JFrame w = new JFrame();
        try {
            final BufferedImage expectedIcon = ImageIO
                .read(new FileInputStream(new File(getClass().getResource("/res/test_icon.png").getPath())));
            w.setIconImage(expectedIcon);
            w.setVisible(true);
            HWND hwnd = new HWND();
            hwnd.setPointer(Native.getComponentPointer(w));
            
            final DWORDByReference hIconNumber = new DWORDByReference();
            long result = User32.INSTANCE.SendMessageTimeout(hwnd,
                                                             WinUser.WM_GETICON, WinUser.ICON_BIG, 0,
                                                             WinUser.SMTO_ABORTIFHUNG, 500, hIconNumber);
            
            assertNotEquals(0, result);
            
            final HICON hIcon = new HICON(new Pointer(hIconNumber.getValue()
                                                      .longValue()));
            assertTrue(WindowUtils.getIconSize(hIcon).width >= 32);
            assertTrue(WindowUtils.getIconSize(hIcon).height >= 32);
            assertEquals(WindowUtils.getIconSize(hIcon).width,
                         WindowUtils.getIconSize(hIcon).height);
        } finally {
            w.dispose();
        }
    }
	
    public void testGetProcessFilePath() {
        if (!Platform.isWindows()) {
            return;
        }
        
        final JFrame w = new JFrame();
        try {
            w.setVisible(true);
            
            final String searchSubStr = "\\bin\\java";
            final HWND hwnd = new HWND(Native.getComponentPointer(w));
            
            assertTrue("Path didn't contain '" + searchSubStr + "': "
                       + WindowUtils.getProcessFilePath(hwnd),
                       WindowUtils.getProcessFilePath(hwnd).toLowerCase()
                       .contains(searchSubStr));
        } finally {
            w.dispose();
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(WindowUtilsTest.class);
    }
}


