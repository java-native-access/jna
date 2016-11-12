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

package com.sun.jna.platform.win32.COM.util.office.office;

import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum XlChartType implements IComEnum {
    
    /**
     * (51)
     */
    xlColumnClustered(51),
    
    /**
     * (52)
     */
    xlColumnStacked(52),
    
    /**
     * (53)
     */
    xlColumnStacked100(53),
    
    /**
     * (54)
     */
    xl3DColumnClustered(54),
    
    /**
     * (55)
     */
    xl3DColumnStacked(55),
    
    /**
     * (56)
     */
    xl3DColumnStacked100(56),
    
    /**
     * (57)
     */
    xlBarClustered(57),
    
    /**
     * (58)
     */
    xlBarStacked(58),
    
    /**
     * (59)
     */
    xlBarStacked100(59),
    
    /**
     * (60)
     */
    xl3DBarClustered(60),
    
    /**
     * (61)
     */
    xl3DBarStacked(61),
    
    /**
     * (62)
     */
    xl3DBarStacked100(62),
    
    /**
     * (63)
     */
    xlLineStacked(63),
    
    /**
     * (64)
     */
    xlLineStacked100(64),
    
    /**
     * (65)
     */
    xlLineMarkers(65),
    
    /**
     * (66)
     */
    xlLineMarkersStacked(66),
    
    /**
     * (67)
     */
    xlLineMarkersStacked100(67),
    
    /**
     * (68)
     */
    xlPieOfPie(68),
    
    /**
     * (69)
     */
    xlPieExploded(69),
    
    /**
     * (70)
     */
    xl3DPieExploded(70),
    
    /**
     * (71)
     */
    xlBarOfPie(71),
    
    /**
     * (72)
     */
    xlXYScatterSmooth(72),
    
    /**
     * (73)
     */
    xlXYScatterSmoothNoMarkers(73),
    
    /**
     * (74)
     */
    xlXYScatterLines(74),
    
    /**
     * (75)
     */
    xlXYScatterLinesNoMarkers(75),
    
    /**
     * (76)
     */
    xlAreaStacked(76),
    
    /**
     * (77)
     */
    xlAreaStacked100(77),
    
    /**
     * (78)
     */
    xl3DAreaStacked(78),
    
    /**
     * (79)
     */
    xl3DAreaStacked100(79),
    
    /**
     * (80)
     */
    xlDoughnutExploded(80),
    
    /**
     * (81)
     */
    xlRadarMarkers(81),
    
    /**
     * (82)
     */
    xlRadarFilled(82),
    
    /**
     * (83)
     */
    xlSurface(83),
    
    /**
     * (84)
     */
    xlSurfaceWireframe(84),
    
    /**
     * (85)
     */
    xlSurfaceTopView(85),
    
    /**
     * (86)
     */
    xlSurfaceTopViewWireframe(86),
    
    /**
     * (15)
     */
    xlBubble(15),
    
    /**
     * (87)
     */
    xlBubble3DEffect(87),
    
    /**
     * (88)
     */
    xlStockHLC(88),
    
    /**
     * (89)
     */
    xlStockOHLC(89),
    
    /**
     * (90)
     */
    xlStockVHLC(90),
    
    /**
     * (91)
     */
    xlStockVOHLC(91),
    
    /**
     * (92)
     */
    xlCylinderColClustered(92),
    
    /**
     * (93)
     */
    xlCylinderColStacked(93),
    
    /**
     * (94)
     */
    xlCylinderColStacked100(94),
    
    /**
     * (95)
     */
    xlCylinderBarClustered(95),
    
    /**
     * (96)
     */
    xlCylinderBarStacked(96),
    
    /**
     * (97)
     */
    xlCylinderBarStacked100(97),
    
    /**
     * (98)
     */
    xlCylinderCol(98),
    
    /**
     * (99)
     */
    xlConeColClustered(99),
    
    /**
     * (100)
     */
    xlConeColStacked(100),
    
    /**
     * (101)
     */
    xlConeColStacked100(101),
    
    /**
     * (102)
     */
    xlConeBarClustered(102),
    
    /**
     * (103)
     */
    xlConeBarStacked(103),
    
    /**
     * (104)
     */
    xlConeBarStacked100(104),
    
    /**
     * (105)
     */
    xlConeCol(105),
    
    /**
     * (106)
     */
    xlPyramidColClustered(106),
    
    /**
     * (107)
     */
    xlPyramidColStacked(107),
    
    /**
     * (108)
     */
    xlPyramidColStacked100(108),
    
    /**
     * (109)
     */
    xlPyramidBarClustered(109),
    
    /**
     * (110)
     */
    xlPyramidBarStacked(110),
    
    /**
     * (111)
     */
    xlPyramidBarStacked100(111),
    
    /**
     * (112)
     */
    xlPyramidCol(112),
    
    /**
     * (-4100)
     */
    xl3DColumn(-4100),
    
    /**
     * (4)
     */
    xlLine(4),
    
    /**
     * (-4101)
     */
    xl3DLine(-4101),
    
    /**
     * (-4102)
     */
    xl3DPie(-4102),
    
    /**
     * (5)
     */
    xlPie(5),
    
    /**
     * (-4169)
     */
    xlXYScatter(-4169),
    
    /**
     * (-4098)
     */
    xl3DArea(-4098),
    
    /**
     * (1)
     */
    xlArea(1),
    
    /**
     * (-4120)
     */
    xlDoughnut(-4120),
    
    /**
     * (-4151)
     */
    xlRadar(-4151),
    
    /**
     * (-4152)
     */
    xlCombo(-4152),
    
    /**
     * (113)
     */
    xlComboColumnClusteredLine(113),
    
    /**
     * (114)
     */
    xlComboColumnClusteredLineSecondaryAxis(114),
    
    /**
     * (115)
     */
    xlComboAreaStackedColumnClustered(115),
    
    /**
     * (116)
     */
    xlOtherCombinations(116),
    
    /**
     * (-2)
     */
    xlSuggestedChart(-2),
    ;

    private XlChartType(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}