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

package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.office.word.*;
import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum XlChartType implements IComEnum {
    
    xlColumnClustered(51),
    xlColumnStacked(52),
    xlColumnStacked100(53),
    xl3DColumnClustered(54),
    xl3DColumnStacked(55),
    xl3DColumnStacked100(56),
    xlBarClustered(57),
    xlBarStacked(58),
    xlBarStacked100(59),
    xl3DBarClustered(60),
    xl3DBarStacked(61),
    xl3DBarStacked100(62),
    xlLineStacked(63),
    xlLineStacked100(64),
    xlLineMarkers(65),
    xlLineMarkersStacked(66),
    xlLineMarkersStacked100(67),
    xlPieOfPie(68),
    xlPieExploded(69),
    xl3DPieExploded(70),
    xlBarOfPie(71),
    xlXYScatterSmooth(72),
    xlXYScatterSmoothNoMarkers(73),
    xlXYScatterLines(74),
    xlXYScatterLinesNoMarkers(75),
    xlAreaStacked(76),
    xlAreaStacked100(77),
    xl3DAreaStacked(78),
    xl3DAreaStacked100(79),
    xlDoughnutExploded(80),
    xlRadarMarkers(81),
    xlRadarFilled(82),
    xlSurface(83),
    xlSurfaceWireframe(84),
    xlSurfaceTopView(85),
    xlSurfaceTopViewWireframe(86),
    xlBubble(15),
    xlBubble3DEffect(87),
    xlStockHLC(88),
    xlStockOHLC(89),
    xlStockVHLC(90),
    xlStockVOHLC(91),
    xlCylinderColClustered(92),
    xlCylinderColStacked(93),
    xlCylinderColStacked100(94),
    xlCylinderBarClustered(95),
    xlCylinderBarStacked(96),
    xlCylinderBarStacked100(97),
    xlCylinderCol(98),
    xlConeColClustered(99),
    xlConeColStacked(100),
    xlConeColStacked100(101),
    xlConeBarClustered(102),
    xlConeBarStacked(103),
    xlConeBarStacked100(104),
    xlConeCol(105),
    xlPyramidColClustered(106),
    xlPyramidColStacked(107),
    xlPyramidColStacked100(108),
    xlPyramidBarClustered(109),
    xlPyramidBarStacked(110),
    xlPyramidBarStacked100(111),
    xlPyramidCol(112),
    xl3DColumn(-4100),
    xlLine(4),
    xl3DLine(-4101),
    xl3DPie(-4102),
    xlPie(5),
    xlXYScatter(-4169),
    xl3DArea(-4098),
    xlArea(1),
    xlDoughnut(-4120),
    xlRadar(-4151);

    private XlChartType(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}