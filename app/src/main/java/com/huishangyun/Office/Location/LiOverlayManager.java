package com.huishangyun.Office.Location;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.overlayutil.OverlayManager;

public class LiOverlayManager extends OverlayManager {
    private List<OverlayOptions> optionsList = new ArrayList<OverlayOptions>();

    public LiOverlayManager(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public List<OverlayOptions> getOverlayOptions() {
        return optionsList;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void setData(List<OverlayOptions> optionsList) {
        this.optionsList = optionsList;
    }
}

