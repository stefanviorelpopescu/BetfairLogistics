package com.digitalstack.logistics.service;

import com.digitalstack.logistics.company_manager.CompanyManager;
import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.OrdersRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class ShippingRunnable implements Runnable
{
    private final OrdersRepository ordersRepository;
    private final CompanyManager companyManager;
    private final Destination destination;
    private final List<Long> orderIds;

    public ShippingRunnable(OrdersRepository ordersRepository, CompanyManager companyManager, Map.Entry<Destination, List<Order>> destinationListEntry)
    {
        this.ordersRepository = ordersRepository;
        this.companyManager = companyManager;
        this.destination = destinationListEntry.getKey();
        this.orderIds = destinationListEntry.getValue().stream()
                .mapToLong(Order::getId)
                .boxed()
                .toList();
    }

    @SneakyThrows
    @Override
    public void run()
    {
        int noOfDeliveringOrders = updateOrders(OrderStatus.NEW, OrderStatus.DELIVERING);
        log.info("Starting deliveries for {}. We are delivering {} orders for {} km.", destination.getName(), noOfDeliveringOrders, destination.getDistance());

        Thread.sleep(destination.getDistance() * 1000);

        int deliveredOrders = updateOrders(OrderStatus.DELIVERING, OrderStatus.DELIVERED);
        companyManager.updateProfit((long) deliveredOrders * destination.getDistance());
        log.info("Delivered {} orders to {}", deliveredOrders, destination.getName());
    }

    private int updateOrders(OrderStatus oldStatus, OrderStatus newStatus) {

        Iterable<Order> ordersFromDb = getOrdersWithStatus(oldStatus);

        int count = 0;
        for (Order order : ordersFromDb)
        {
            if (order.getStatus() == oldStatus) {
                order.setStatus(newStatus);
                count++;
            }
        }
        ordersRepository.saveAll(ordersFromDb);
        return count;
    }

    private Iterable<Order> getOrdersWithStatus(OrderStatus oldStatus)
    {
        Iterable<Order> ordersFromDb = ordersRepository.findAllById(orderIds);
        Iterator<Order> orderIterator = ordersFromDb.iterator();
        while (orderIterator.hasNext()) {
            Order order = orderIterator.next();
            if (order.getStatus() != oldStatus) {
                orderIterator.remove();
            }
        }
        return ordersFromDb;
    }
}
