package com.example.direct_order.customermain;

public class CustomerMainData {
    private String shopid;
    private String market_name;
    private  boolean like;
    //해당 가게 주문서, 후기페이지, 메시지

    /*public CustomerMainData() {

    }

    public CustomerMainData(String shopid, String market_name, String imgId) {
        this.shopid = shopid;
        this.market_name = market_name;
        this.imgId = imgId;
    }*/

    public String getShopid() { return shopid; }

    public  void setShopid(String shopid) { this.shopid = shopid; }

    public String getMarket_name() { return market_name; }

    public void setMarket_name(String market_name) { this.market_name = market_name; }

    public boolean isLike() { return like; }

    public void setLike(boolean like) { this.like = like; }
}
