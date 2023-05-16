package com.digitalstack.logistics.service;

import com.digitalstack.logistics.helpers.InvalidDestinationDtoException;
import com.digitalstack.logistics.helpers.OrderStatus;
import com.digitalstack.logistics.model.converter.DestinationConverter;
import com.digitalstack.logistics.model.dto.DestinationDto;
import com.digitalstack.logistics.model.entity.Destination;
import com.digitalstack.logistics.model.entity.Order;
import com.digitalstack.logistics.repository.DestinationRepository;
import com.digitalstack.logistics.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DestinationService
{
    private final OrdersRepository ordersRepository;
    private final DestinationRepository destinationRepository;

    public DestinationService(OrdersRepository ordersRepository, DestinationRepository destinationRepository)
    {
        this.ordersRepository = ordersRepository;
        this.destinationRepository = destinationRepository;
    }

    public List<DestinationDto> getAllDestinations() {

//        Iterable<Destination> destinations = destinationRepository.findAll();
//
//        List<DestinationDto> destinationList = new ArrayList<>();
//        destinations.forEach(destination -> destinationList.add(DestinationConverter.fromModelToDto(destination)));
//
//        return destinationList;

        return StreamSupport.stream(destinationRepository.findAll().spliterator(), false)
                .map(DestinationConverter::fromModelToDto)
                .collect(Collectors.toList());
    }

    public DestinationDto getDestinationById(Long destinationId)
    {
        return destinationRepository.findById(destinationId)
                .map(DestinationConverter::fromModelToDto)
                .orElse(null);
    }

    @Transactional
    public void deleteDestinationById(Long id)
    {
        Optional<Destination> optionalDestination = destinationRepository.findById(id);

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
        }

    }

    public DestinationDto addDestination(DestinationDto requestBody) throws InvalidDestinationDtoException
    {
        validateInputForAdd(requestBody);

        Destination destination = DestinationConverter.fromDtoToModel(requestBody);
        destinationRepository.save(destination);

        return DestinationConverter.fromModelToDto(destination);
    }

    public DestinationDto updateDestination(DestinationDto requestBody) throws InvalidDestinationDtoException
    {
        validateInputForUpdate(requestBody);

        Destination destination = destinationRepository.findById(requestBody.getId())
                .orElseThrow(() -> new InvalidDestinationDtoException("Destination id=" + requestBody.getId() + " not found!!!"));
        destination.setName(requestBody.getName());
        destination.setDistance(requestBody.getDistance());
        destinationRepository.save(destination);

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
        Optional<Destination> optionalDestination = destinationRepository.findByName(requestBody.getName());
        if (optionalDestination.isPresent()) {
            errorMessage += String.format("Destination name=%s already exists!", requestBody.getName());
        }
        if (!errorMessage.isEmpty()){
            throw new InvalidDestinationDtoException(errorMessage);
        }
    }
}
