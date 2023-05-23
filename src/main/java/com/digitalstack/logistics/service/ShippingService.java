package com.digitalstack.logistics.service;

import com.digitalstack.logistics.company_manager.CompanyManager;
import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.converter.OrderConverter;
import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.OrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Class used for starting a new day. <br>
 * The entrypoint is the {@link #newDay()} method <br>
 * The following processing steps are executed <br>
 * 1.  Advance the current application date <br>
 * 2.  Select orders from the current date and group them by destination <br>
 * 3.  Async process the orders for each destination
 */
@Service
@Slf4j
public class ShippingService
{
    private final CompanyManager companyManager;
    private final OrdersRepository ordersRepository;
    private final ExecutorService executor;

    public ShippingService(CompanyManager companyManager, OrdersRepository ordersRepository)
    {
        this.companyManager = companyManager;
        this.ordersRepository = ordersRepository;

        int corePoolSize = 4;
        int maximumPoolSize = 4;
        long keepAliveTime = 0;
        TimeUnit unit = TimeUnit.SECONDS; //the time unit for the {@code keepAliveTime} argument
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        this.executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }

    public String newDay()
    {
        companyManager.advanceDate();
        log.info("New Day Starting: {}", companyManager.getCurrentDate().format(OrderConverter.formatter));

        Map<Destination, List<Order>> ordersByDestination = getOrdersByDestination();
        String destinationsString = ordersByDestination.keySet().stream()
                .map(Destination::getName)
                .collect(Collectors.joining(","));
        log.info("Today we will be delivering to {}", destinationsString);

        ordersByDestination.entrySet().forEach(destinationListEntry ->
                executor.execute(new ShippingRunnable(ordersRepository, companyManager, destinationListEntry)));

        int numberOfOrders = ordersByDestination.values().stream()
                .map(List::size)
                .mapToInt(value -> value)
                .sum();
        return String.format("Starting delivery of %d orders!", numberOfOrders);
    }

    private Map<Destination, List<Order>> getOrdersByDestination()
    {
        LocalDate currentDate = companyManager.getCurrentDate();
        long currentDateAsLong = currentDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        List<Order> orders = ordersRepository.findAllByDeliveryDate(currentDateAsLong);
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.NEW)
                .collect(Collectors.groupingBy(Order::getDestination, Collectors.toList()));
    }
}
