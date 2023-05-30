package com.digitalstack.logistics.service;

import com.digitalstack.logistics.company_manager.CompanyManager;
import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.OrdersRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AsyncShippingService
{
    private final OrdersRepository ordersRepository;
    private final CompanyManager companyManager;

    public AsyncShippingService(OrdersRepository ordersRepository, CompanyManager companyManager)
    {
        this.ordersRepository = ordersRepository;
        this.companyManager = companyManager;
    }

    @SneakyThrows
    @Async("${async.shipping.executor}")
    public void shipAsync(Map.Entry<Destination, List<Order>> destinationListEntry)
    {
        Destination destination = destinationListEntry.getKey();
        List<Long> orderIds = destinationListEntry.getValue().stream()
                .mapToLong(Order::getId)
                .boxed()
                .toList();

        int noOfDeliveringOrders = updateOrders(OrderStatus.NEW, OrderStatus.DELIVERING, orderIds);
        log.info("Starting deliveries for {}. We are delivering {} orders for {} km.", destination.getName(), noOfDeliveringOrders, destination.getDistance());

        Thread.sleep(destination.getDistance() * 1000);

        int deliveredOrders = updateOrders(OrderStatus.DELIVERING, OrderStatus.DELIVERED, orderIds);
        companyManager.updateProfit((long) deliveredOrders * destination.getDistance());
        log.info("Delivered {} orders to {}", deliveredOrders, destination.getName());
    }

    @SneakyThrows
    public int shipAndReturnProfit(Map.Entry<Destination, List<Order>> destinationListEntry)
    {
        Destination destination = destinationListEntry.getKey();
        List<Long> orderIds = destinationListEntry.getValue().stream()
                .mapToLong(Order::getId)
                .boxed()
                .toList();

        int noOfDeliveringOrders = updateOrders(OrderStatus.NEW, OrderStatus.DELIVERING, orderIds);
        log.info("Starting deliveries for {}. We are delivering {} orders for {} km.", destination.getName(), noOfDeliveringOrders, destination.getDistance());

        Thread.sleep(destination.getDistance() * 1000);

        int deliveredOrders = updateOrders(OrderStatus.DELIVERING, OrderStatus.DELIVERED, orderIds);
        log.info("Delivered {} orders to {}", deliveredOrders, destination.getName());

        return deliveredOrders * destination.getDistance();
    }


    private int updateOrders(OrderStatus oldStatus, OrderStatus newStatus, List<Long> orderIds) {

        Iterable<Order> ordersFromDb = getOrdersWithStatus(oldStatus, orderIds);

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

    private Iterable<Order> getOrdersWithStatus(OrderStatus oldStatus, List<Long> orderIds)
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
