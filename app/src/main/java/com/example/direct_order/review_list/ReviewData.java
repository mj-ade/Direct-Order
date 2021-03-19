package com.example.direct_order.review_list;

public class ReviewData {
    private int star;
    private String image;
    private String content;

    public ReviewData() {

    }

    public ReviewData(int star, String image, String content) {
        this.star = star;
        this.image = image;
        this.content = content;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}