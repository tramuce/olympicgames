package br.com.olympicgames.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.olympicgames.repository.api.ILocationRepository;
import br.com.olympicgames.repository.model.Location;
import br.com.olympicgames.service.api.ILocationService;
import br.com.olympicgames.service.exception.ApiException;

@Service
public class LocationService implements ILocationService {

    @Autowired
    private ILocationRepository locationRepository;

    @Override
    public Location get(Long id) {
	Optional<Location> optLocation = locationRepository.findById(id);

	if (optLocation.isPresent())
	    return optLocation.get();

	return null;
    }

    @Override
    public List<Location> find(Location location) {
	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("description");

	return locationRepository.findAll(Example.of(location, mather));
    }

    @Override
    public Location create(Location location) throws ApiException {
	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("description");

	if (locationRepository.exists(Example.of(location, mather)))
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "Já existe um local com essa descrição");

	if (location.getDescription() == null || location.getDescription().trim().isEmpty())
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo descrição é obrigatório");

	location.setId(null);

	return locationRepository.save(location);
    }

}
