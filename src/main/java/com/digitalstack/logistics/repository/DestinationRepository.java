package com.digitalstack.logistics.repository;

import com.digitalstack.logistics.entity.Destination;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DestinationRepository extends CrudRepository<Destination, Long>
{
    Optional<Destination> findByName(String destinationName);
}
