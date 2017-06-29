package com.mryu.richeditor.model;

/**
 * Created by MryU93 on 2017/6/15.
 * Desc:
 */

public class InsertModel {
    private String insertRule;
    private String insertContent;
    private String insertColor;

    public InsertModel(String insertRule, String insertContent, String insertColor) {
        this.insertRule = insertRule;
        this.insertContent = insertContent;
        this.insertColor = insertColor;
    }

    public String getInsertRule() {
        return insertRule;
    }

    public void setInsertRule(String insertRule) {
        this.insertRule = insertRule;
    }

    public String getInsertContent() {
        return insertContent;
    }

    public void setInsertContent(String insertContent) {
        this.insertContent = insertContent;
    }

    public String getInsertColor() {
        return insertColor;
    }

    public void setInsertColor(String insertColor) {
        this.insertColor = insertColor;
    }

    @Override
    public String toString() {
        return "InsertModel{" +
                "insertRule='" + insertRule + '\'' +
                ", insertContent='" + insertContent + '\'' +
                ", insertColor='" + insertColor + '\'' +
                '}';
    }
}
