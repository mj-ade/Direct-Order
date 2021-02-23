package com.example.direct_order.ordersheet;

public class Option {
    int number;
    int parentNumber;
    String title;
    String desc;
    int type;
    int numOfOption;
    String content;
    String func;
    String preview;
    String previewDesc;

    public Option() {

    }

    public Option(int number, int parentNumber, String title, String desc, int type, int numOfOption, String content, String func, String preview, String previewDesc) {
        this.number = number;
        this.parentNumber = parentNumber;
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.numOfOption = numOfOption;
        this.content = content;
        this.func = func;
        this.preview = preview;
        this.previewDesc = previewDesc;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getParentNumber() {
        return parentNumber;
    }

    public void setParentNumber(int parentNumber) {
        this.parentNumber = parentNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNumOfOption() {
        return numOfOption;
    }

    public void setNumOfOption(int numOfOption) {
        this.numOfOption = numOfOption;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getPreviewDesc() {
        return previewDesc;
    }

    public void setPreviewDesc(String previewDesc) {
        this.previewDesc = previewDesc;
    }
}
