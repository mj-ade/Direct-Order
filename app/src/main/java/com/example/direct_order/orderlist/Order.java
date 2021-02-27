package com.example.direct_order.orderlist;

public class Order {
    private String name;
    private String date;
    private String time;
    private String pickup;
    private String pickupTime;
    private int price;
    private int process;
    private String screenshot;

    public Order() {

    }

    public Order(String name, String date, String time, String pickup, String pickupTime, int price, int process, String screenshot) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.pickup = pickup;
        this.pickupTime = pickupTime;
        this.price = price;
        this.process = process;
        this.screenshot = screenshot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }
}
