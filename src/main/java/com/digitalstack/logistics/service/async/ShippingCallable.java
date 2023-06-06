package com.digitalstack.logistics.service.async;

import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.OrdersRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class ShippingCallable implements Callable<Integer>
{
    private final OrdersRepository ordersRepository;
    private final Destination destination;
    private final List<Long> orderIds;

    public ShippingCallable(OrdersRepository ordersRepository, Map.Entry<Destination, List<Order>> destinationListEntry)
    {
        this.ordersRepository = ordersRepository;
        this.destination = destinationListEntry.getKey();
        this.orderIds = destinationListEntry.getValue().stream()
                .mapToLong(Order::getId)
                .boxed()
                .toList();
    }

    @Override
    public Integer call() throws Exception
    {
        int noOfDeliveringOrders = updateOrders(OrderStatus.NEW, OrderStatus.DELIVERING);
        log.info("Starting deliveries for {}. We are delivering {} orders for {} km.", destination.getName(), noOfDeliveringOrders, destination.getDistance());

        Thread.sleep(destination.getDistance() * 1000);

        int deliveredOrders = updateOrders(OrderStatus.DELIVERING, OrderStatus.DELIVERED);
        log.info("Delivered {} orders to {}", deliveredOrders, destination.getName());

        return deliveredOrders * destination.getDistance();
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
