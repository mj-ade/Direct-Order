package com.example.direct_order.customermain;

public class CustomerMainData {
    private String market_name;
    private int imgId;
    private  boolean like;
    //해당 가게 주문서, 후기페이지, 메시지

    public String getMarket_name() { return market_name; }

    public void setMarket_name(String market_name) { this.market_name = market_name; }

    public int getImgId() { return imgId; }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public boolean isLike() { return like; }

    public void setLike(boolean like) { this.like = like; }
}
