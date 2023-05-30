package com.digitalstack.logistics.service;

import com.digitalstack.logistics.helpers.InvalidDestinationDtoException;
import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.converter.DestinationConverter;
import com.digitalstack.logistics.model.dto.DestinationDto;
import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.DestinationCache;
import com.digitalstack.logistics.repository.DestinationRepository;
import com.digitalstack.logistics.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DestinationService
{
    private final OrdersRepository ordersRepository;
    private final DestinationRepository destinationRepository;
    private final DestinationCache destinationCache;

    public DestinationService(OrdersRepository ordersRepository, DestinationRepository destinationRepository, DestinationCache destinationCache)
    {
        this.ordersRepository = ordersRepository;
        this.destinationRepository = destinationRepository;
        this.destinationCache = destinationCache;
    }

    public List<DestinationDto> getAllDestinations() {

//        Iterable<Destination> destinations = destinationRepository.findAll();
//
//        List<DestinationDto> destinationList = new ArrayList<>();
//        destinations.forEach(destination -> destinationList.add(DestinationConverter.fromModelToDto(destination)));
//
//        return destinationList;

        return destinationCache.getAllDestinations().stream()
                .map(DestinationConverter::fromModelToDto)
                .collect(Collectors.toList());
    }

    public DestinationDto getDestinationById(Long destinationId)
    {
        return destinationCache.getById(destinationId)
                .map(DestinationConverter::fromModelToDto)
                .orElse(null);
    }

    @Transactional
    public void deleteDestinationById(Long id)
    {
        Optional<Destination> optionalDestination = destinationCache.getById(id);

        if (optionalDestination.isPresent()) {
            Destination destination = optionalDestination.get();

            List<Order> orders = ordersRepository.findAllByDestination(destination);
            for (Order order : orders)
            {
                order.setDestination(null);
                order.setStatus(OrderStatus.INVALID);
            }
            ordersRepository.saveAll(orders);

            destinationRepository.delete(destination);
            destinationCache.removeDestinationFromCache(destination);
        }

    }

    public DestinationDto addDestination(DestinationDto requestBody) throws InvalidDestinationDtoException
    {
        validateInputForAdd(requestBody);

        Destination destination = DestinationConverter.fromDtoToModel(requestBody);
        destinationRepository.save(destination);
        destinationCache.updateDestinationInCache(destination);

        return DestinationConverter.fromModelToDto(destination);
    }

    public DestinationDto updateDestination(DestinationDto requestBody) throws InvalidDestinationDtoException
    {
        validateInputForUpdate(requestBody);

        Destination destination = destinationCache.getById(requestBody.getId())
                .orElseThrow(() -> new InvalidDestinationDtoException("Destination id=" + requestBody.getId() + " not found!!!"));
        destination.setName(requestBody.getName());
        destination.setDistance(requestBody.getDistance());
        destinationRepository.save(destination);
        destinationCache.updateDestinationInCache(destination);

        return DestinationConverter.fromModelToDto(destination);
    }

    private void validateInputForUpdate(DestinationDto requestBody) throws InvalidDestinationDtoException
    {
        String errorMessage = "";
        if (requestBody.getId() == null) {
            errorMessage += "Destination ID should be present when updating destinations!\n";
        }
        if (!errorMessage.isEmpty()){
            throw new InvalidDestinationDtoException(errorMessage);
        }
    }

    private void validateInputForAdd(DestinationDto requestBody) throws InvalidDestinationDtoException
    {
        String errorMessage = "";
        if (requestBody.getId() != null) {
            errorMessage += "Destination ID should not be present when creating new destinations!\n";
        }
//        Optional<Destination> optionalDestination = destinationRepository.findByName(requestBody.getName());
        Optional<Destination> optionalDestination = destinationCache.getByName(requestBody.getName());
        if (optionalDestination.isPresent()) {
            errorMessage += String.format("Destination name=%s already exists!", requestBody.getName());
        }
        if (!errorMessage.isEmpty()){
            throw new InvalidDestinationDtoException(errorMessage);
        }
    }

    public void reloadDestinationsCache()
    {
        destinationCache.reloadData();
    }
}
