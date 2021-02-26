package com.example.direct_order.customermain;

import android.util.Log;

public class Market {
    private String shopuid;
    private String shopname;
    //private boolean like;
    private String shopnum;
    private String shopaddress;
    private String instagram;
    private boolean orderedit;

    public Market() {

    }

    public Market(String shopuid, String shopname, String shopnum, String shopaddress, String instagram, boolean orderedit) {
        this.shopuid = shopuid;
        this.shopname = shopname;
        this.shopnum = shopnum;
        this.shopaddress = shopaddress;
        this.instagram = instagram;
        this.orderedit = orderedit;
    }

    public String getShopuid() {
        return shopuid;
    }

    public void setShopuid(String shopuid) {
        this.shopuid = shopuid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShopnum() {
        return shopnum;
    }

    public void setShopnum(String shopnum) {
        this.shopnum = shopnum;
    }

    public String getShopaddress() {
        return shopaddress;
    }

    public void setShopaddress(String shopaddress) {
        this.shopaddress = shopaddress;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public boolean isOrderedit() {
        return orderedit;
    }

    public void setOrderedit(boolean orderedit) {
        this.orderedit = orderedit;
    }
}
