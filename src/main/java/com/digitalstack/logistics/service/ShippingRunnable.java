package com.digitalstack.logistics.service;

import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShippingRunnable implements Runnable
{
    private Destination destination;
    private List<Order> orders;

    public ShippingRunnable(Map.Entry<Destination, List<Order>> destinationListEntry)
    {
        this.destination = destinationListEntry.getKey();
        this.orders = new ArrayList<>(destinationListEntry.getValue());
    }

    @Override
    public void run()
    {

    }
}
