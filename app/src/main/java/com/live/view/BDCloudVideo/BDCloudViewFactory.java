package com.live.view.BDCloudVideo;

import com.live.LiveApp;

public class BDCloudViewFactory {

    private static BDCloudVideoView bdCloudVideoView;

    public static BDCloudVideoView generatorBDView() {
        if (bdCloudVideoView == null) {
            bdCloudVideoView = new BDCloudVideoView(LiveApp.getInstance());
        }
        return bdCloudVideoView;
    }


}
