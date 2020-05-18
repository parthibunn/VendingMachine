package com.stee.constants;

public enum Item{
    COKE("Coke", 25), PEPSI("Pepsi", 37), SODA("Soda", 47);
   
    private String name;
    private int price;
   
    private Item(String name, int price){
        this.name = name;
        this.price = price;
    }
   
    public String getName(){
        return name;
    }
   
    public long getPrice(){
        return price;
    }
}

