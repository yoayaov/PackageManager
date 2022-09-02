package com.example.myapplication;

import android.graphics.drawable.Drawable;

public class SimpleAppInfo {
    private String appName;
    private String appPackage;
    private Drawable appIcon;
    private String valuesAppName;
    private Drawable drawablePreiewImg;
    private boolean system;
    private String packagePath;
    private String versionName;
    private int versionCode;

    public SimpleAppInfo(String appName, String appPackage, Drawable appIcon, boolean system, String packagePath, String versionName, int versionCode) {
        this.appName = appName;
        this.appPackage = appPackage;
        this.appIcon = appIcon;
        this.system = system;
        this.packagePath = packagePath;
        this.versionName = versionName;
        this.versionCode = versionCode;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getValuesAppName() {
        return valuesAppName;
    }

    public void setValuesAppName(String valuesAppName) {
        this.valuesAppName = valuesAppName;
    }

    public Drawable getDrawablePreiewImg() {
        return drawablePreiewImg;
    }

    public void setDrawablePreiewImg(Drawable drawablePreiewImg) {
        this.drawablePreiewImg = drawablePreiewImg;
    }
}