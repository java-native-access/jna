package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;

@ComInterface
public interface Chart {
    @ComMethod
    void ChartWizard(Object Source,Object Gallery,Object Format,Object PlotBy,
            Object CategoryLabels,Object SeriesLabels,Object HasLegend,
            Object Title,Object CategoryTitle,Object ValueTitle,Object ExtraTitle);
    
    @ComMethod
    Series SeriesCollection(Object index);
    
    @ComMethod
    IDispatch Location(XlChartLocation location, String name);
}
