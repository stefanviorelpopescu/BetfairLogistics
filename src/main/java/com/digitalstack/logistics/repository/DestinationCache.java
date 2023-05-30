package com.digitalstack.logistics.repository;

import com.digitalstack.logistics.model.entity.Destination;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DestinationCache
{
    private final DestinationRepository destinationRepository;
    private final Map<Long, Destination> destinations = new HashMap<>();

    public DestinationCache(DestinationRepository destinationRepository)
    {
        this.destinationRepository = destinationRepository;
    }

    private Map<Long, Destination> getCacheData() {
        if (this.destinations.isEmpty()) {
            reloadData();
        }
        return this.destinations;
    }

    public void reloadData()
    {
        destinations.clear();
        destinationRepository.findAll().forEach(destination -> this.destinations.put(destination.getId(), destination));
    }

    public Optional<Destination> getById(Long id) {
        return Optional.ofNullable(getCacheData().get(id));
    }

    public Optional<Destination> getByName(String name) {
        return getCacheData().values().stream()
                .filter(destination -> destination.getName().equals(name))
                .findFirst();
    }

    public Optional<Destination> getByNameContainingCaseInsensitive(String name) {
        return getCacheData().values().stream()
                .filter(destination -> destination.getName().toLowerCase().contains(name.toLowerCase()))
                .findFirst();
    }

    public void updateDestinationInCache(Destination destination) {
        getCacheData().put(destination.getId(), destination);
    }

    public void removeDestinationFromCache(Destination destination) {
        getCacheData().remove(destination.getId());
    }

    public Collection<Destination> getAllDestinations() {
        return getCacheData().values();
    }
}
