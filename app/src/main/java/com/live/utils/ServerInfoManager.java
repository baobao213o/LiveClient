package com.live.utils;

import android.os.Bundle;

import com.live.entity.ServerInfo;

public class ServerInfoManager {

    private final static String KEY = "ServerInfo";

    private static volatile ServerInfoManager instance;

    private ServerInfo serverInfo;

    public static ServerInfoManager getInstance() {
        if (instance == null) {
            synchronized (ServerInfoManager.class) {
                if (instance == null) {
                    instance = new ServerInfoManager();
                }
            }
        }
        return instance;
    }

    public ServerInfo getServerInfo(Bundle saveInstanceStatus) {
        if (serverInfo != null) {
            return serverInfo;
        } else {
            if (saveInstanceStatus == null) {
                return null;
            }
            return (ServerInfo) saveInstanceStatus.getSerializable(KEY);
        }
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public void saveServerInfo(Bundle saveInstanceStatus) {
        if (serverInfo != null) {
            saveInstanceStatus.putSerializable(KEY, serverInfo);
        }
    }

}
