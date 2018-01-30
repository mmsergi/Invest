package com.sergi.investmentadvisor;

/**
 * Created by Sergi on 18/01/2018.
 */

public class Money {

    private String name;
    private float amount;
    private String type;

    public Money() {
    }

    public Money(String name, float amount, String type) {
        this.name = name;
        this.amount = amount;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
