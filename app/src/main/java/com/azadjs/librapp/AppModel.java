package com.azadjs.librapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class AppModel implements Serializable {
    private String appId, image, appText, appCategory, appDesc, appUrl;

    public AppModel() {
    }

    public AppModel(String appId, String image, String appText, String appCategory, String appDesc, String appUrl) {
        this.appId = appId;
        this.image = image;
        this.appText = appText;
        this.appCategory = appCategory;
        this.appDesc = appDesc;
        this.appUrl = appUrl;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getAppText() {
        return appText;
    }

    public void setAppText(String appText) {
        this.appText = appText;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
