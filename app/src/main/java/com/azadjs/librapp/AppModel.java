package com.azadjs.librapp;

public class AppModel {
    private String image,appText,appCategory,appDesc;

    public AppModel() {
    }

    public AppModel(String image, String appText, String appCategory, String appDesc) {
        this.image = image;
        this.appText = appText;
        this.appCategory = appCategory;
        this.appDesc = appDesc;
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
}
