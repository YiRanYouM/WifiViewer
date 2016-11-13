package org.wuwz.wifiviewer.model;

/**
 * @Author: wuwz.
 * @EMail: wuwz@live.com.
 * @Date: 2016/11/14.
 * @Url: http://www.github.com/wuwz
 */
public class WifiInfo {
    private String wifiName;
    private String wifiPwd;
    public String getWifiName() {
        return wifiName;
    }
    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }
    public String getWifiPwd() {
        return wifiPwd;
    }
    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }
    @Override
    public String toString() {
        return "WifiInfo{" +
                "wifiName='" + wifiName + '\'' +
                ", wifiPwd='" + wifiPwd + '\'' +
                '}';
    }
}
