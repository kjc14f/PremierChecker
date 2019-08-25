package com.model;

public class Pick {

    private int element;
    private int position;
    private int selling_price ;
    private int multiplier;
    private int purchase_price;
    private boolean is_captain;
    private boolean is_vice_captain;
    private Player player;

    public Pick(int element, int position, int selling_price, int multiplier, int purchase_price, boolean is_captain, boolean is_vice_captain) {
        this.element = element;
        this.position = position;
        this.selling_price = selling_price;
        this.multiplier = multiplier;
        this.purchase_price = purchase_price;
        this.is_captain = is_captain;
        this.is_vice_captain = is_vice_captain;
    }

    public int getElement() {
        return element;
    }

    public void setElement(int element) {
        this.element = element;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(int selling_price) {
        this.selling_price = selling_price;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(int purchase_price) {
        this.purchase_price = purchase_price;
    }

    public boolean isIs_captain() {
        return is_captain;
    }

    public void setIs_captain(boolean is_captain) {
        this.is_captain = is_captain;
    }

    public boolean isIs_vice_captain() {
        return is_vice_captain;
    }

    public void setIs_vice_captain(boolean is_vice_captain) {
        this.is_vice_captain = is_vice_captain;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
