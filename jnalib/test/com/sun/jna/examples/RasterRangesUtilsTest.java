/* Copyright (c) 2008 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.examples;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.sun.jna.examples.RasterRangesUtils.RangesOutput;

public class RasterRangesUtilsTest extends TestCase {

    Set rects = new HashSet();

    RangesOutput out = new RangesOutput() {
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
        Set EXPECTED = new HashSet() {
            {
                add(new Rectangle(0, 0, 100, 50));
                add(new Rectangle(0, 50, 50, 50));
            }
        };

        Area mask = new Area(new Rectangle(0, 0, 100, 100));
        mask.subtract(new Area(new Rectangle(50, 50, 50, 50)));
        RasterRangesUtils.outputOccupiedRanges(createRaster(mask), out);

        assertEquals("Wrong number of rectangles", EXPECTED.size(), rects.size());
        assertEquals("Wrong rectangles", EXPECTED, rects);
    }

    public void testDecomposeRectanglesWithHole() {
        Set EXPECTED = new HashSet() {
            {
                add(new Rectangle(0, 0, 100, 25));
                add(new Rectangle(0, 25, 25, 50));
                add(new Rectangle(75, 25, 25, 50));
                add(new Rectangle(0, 75, 100, 25));
            }
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
