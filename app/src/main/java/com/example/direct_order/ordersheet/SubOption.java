package com.example.direct_order.ordersheet;

public class SubOption {
    int number;
    String title;
    SubOptionForm subOptionForm;

    public SubOption(int number, String title, SubOptionForm subOptionForm) {
        this.number = number;
        this.title = title;
        this.subOptionForm = subOptionForm;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SubOptionForm getLayout() {
        return subOptionForm;
    }

    public void setLayout(SubOptionForm layout) {
        this.subOptionForm = subOptionForm;
    }
}
