/* Copyright (c) 2008 Timothy Wall, All Rights Reserved
 *
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
package com.sun.jna.platform;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashSet;
import java.util.Set;

import com.sun.jna.platform.RasterRangesUtils.RangesOutput;

import junit.framework.TestCase;

public class RasterRangesUtilsTest extends TestCase {

    Set<Rectangle> rects = new HashSet<>();

    RangesOutput out = new RangesOutput() {
        @Override
        public boolean outputRange(int x, int y, int w, int h) {
            rects.add(new Rectangle(x, y, w, h));
            return true;
        }
    };

    private Raster createRaster(Shape mask) {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.fill(mask);
        g.dispose();
        return image.getRaster();
    }

    public void testDecomposeRectangles() {
        Set<Rectangle> EXPECTED = new HashSet<Rectangle>() {
            {
                add(new Rectangle(0, 0, 100, 50));
                add(new Rectangle(0, 50, 50, 50));
            }
            private static final long serialVersionUID = 1L;
        };

        Area mask = new Area(new Rectangle(0, 0, 100, 100));
        mask.subtract(new Area(new Rectangle(50, 50, 50, 50)));
        RasterRangesUtils.outputOccupiedRanges(createRaster(mask), out);

        assertEquals("Wrong number of rectangles", EXPECTED.size(), rects.size());
        assertEquals("Wrong rectangles", EXPECTED, rects);
    }

    public void testDecomposeRectanglesWithHole() {
        Set<Rectangle> EXPECTED = new HashSet<Rectangle>() {
            {
                add(new Rectangle(0, 0, 100, 25));
                add(new Rectangle(0, 25, 25, 50));
                add(new Rectangle(75, 25, 25, 50));
                add(new Rectangle(0, 75, 100, 25));
            }
            private static final long serialVersionUID = 1L;
        };

        Area mask = new Area(new Rectangle(0, 0, 100, 100));
        mask.subtract(new Area(new Rectangle(25, 25, 50, 50)));
        RasterRangesUtils.outputOccupiedRanges(createRaster(mask), out);

        assertEquals("Wrong number of rectangles", EXPECTED.size(), rects.size());
        assertEquals("Wrong rectangles", EXPECTED, rects);

        /*
        long start = System.currentTimeMillis();
        for (int i=0;i < 100;i++) {
            RasterRangesUtils.outputOccupiedRanges(createRaster(mask), out);
        }
        System.out.println("raster: " + (System.currentTimeMillis()-start));
        */
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(RasterRangesUtilsTest.class);
    }
}
