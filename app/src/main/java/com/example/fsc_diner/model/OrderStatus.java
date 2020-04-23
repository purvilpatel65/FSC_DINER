package com.example.fsc_diner.model;

import java.util.Dictionary;
import java.util.Enumeration;

public class OrderStatus {

    private Dictionary<Integer, String> status;

    public OrderStatus(){
        status.put(1, "Order being placed");
        status.put(2, "Order being handled");
        status.put(3, "Order in process");
        status.put(4, "Order is ready for pickup");
    }

    public Dictionary<Integer, String> getStatus() {
        return status;
    }
}
