package br.com.olympicgames.service.api;

import java.util.List;

import br.com.olympicgames.repository.model.Location;
import br.com.olympicgames.service.exception.ApiException;

public interface ILocationService {

    public Location get(Long id);

    public List<Location> find(Location location);

    public Location create(Location location) throws ApiException;

}
