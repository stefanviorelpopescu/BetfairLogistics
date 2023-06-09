package com.digitalstack.logistics.service;

import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.repository.DestinationCache;
import com.digitalstack.logistics.repository.DestinationRepository;
import com.digitalstack.logistics.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class DataLoadingService
{
    @Value("${data.csv.destinations.path}")
    public String DESTINATIONS_FILE_NAME;
    @Value("${data.csv.orders.path}")
    public String ORDERS_FILE_NAME;

    private final DestinationRepository destinationRepository;
    private final OrdersRepository ordersRepository;
    private final DestinationCache destinationCache;

    public DataLoadingService(DestinationRepository destinationRepository,
                              OrdersRepository ordersRepository, DestinationCache destinationCache)
    {
        this.destinationRepository = destinationRepository;
        this.ordersRepository = ordersRepository;
        this.destinationCache = destinationCache;
    }

    public String loadCsvData() throws IOException
    {
        String response = "Loading complete.\n%d destinations loaded.\n%d orders loaded.";

        int loadedDestinations = loadEntities(DESTINATIONS_FILE_NAME, Destination.class);
        int loadedOrders = loadEntities(ORDERS_FILE_NAME, Order.class);

        return String.format(response, loadedDestinations, loadedOrders);
    }

    private int loadEntities(String fileName, Class<?> className) throws IOException
    {
        int count = 0;
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            String[] tokens = line.split(",");

            saveEntity(className, tokens);

            count++;
            line = reader.readLine();
        }
        return count;
    }

    private void saveEntity(Class<?> className, String[] tokens)
    {
        if (className == Destination.class) {
            saveDestination(tokens);
        }
        if (className == Order.class) {
            saveOrder(tokens);
        }
    }

    private void saveDestination(String[] tokens)
    {
        Destination destination = new Destination(tokens[0], Integer.parseInt(tokens[1]));
        destinationRepository.save(destination);
        destinationCache.updateDestinationInCache(destination);
    }

    private void saveOrder(String[] tokens)
    {
        Order order = createOrder(tokens);
        ordersRepository.save(order);
    }

    private Order createOrder(String[] tokens)
    {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setLastUpdated(System.currentTimeMillis());

        String destinationName = tokens[0];
//        Destination destination = destinationRepository.findByName(destinationName)
        Destination destination = destinationCache.getByName(destinationName)
                // throw new NoSuchElementException("No value present");
                .orElseThrow();
        order.setDestination(destination);

        String deliveryDate = tokens[1];
        String [] dateTokens = deliveryDate.split("-");
        LocalDate localDate = LocalDate.of(Integer.parseInt(dateTokens[2]), Integer.parseInt(dateTokens[1]), Integer.parseInt(dateTokens[0]));
        order.setDeliveryDate(localDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
        return order;
    }
}
