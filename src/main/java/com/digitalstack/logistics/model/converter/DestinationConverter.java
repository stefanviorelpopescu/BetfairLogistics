package com.digitalstack.logistics.model.converter;

import com.digitalstack.logistics.model.dto.DestinationDto;
import com.digitalstack.logistics.model.entity.Destination;

public class DestinationConverter
{
    public static DestinationDto fromModelToDto(Destination destination) {
        DestinationDto destinationDto = new DestinationDto();
        destinationDto.setId(destination.getId());
        destinationDto.setName(destination.getName());
        destinationDto.setDistance(destination.getDistance());
        return destinationDto;
    }

    public static Destination fromDtoToModel(DestinationDto destinationDto) {
        Destination destination = new Destination();
        destination.setName(destinationDto.getName());
        destination.setDistance(destinationDto.getDistance());
        return destination;
    }
}
