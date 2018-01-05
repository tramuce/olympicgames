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

/**
 * @author tramuce
 * 
 *         Classe de que implementa a interface ILocationService.
 *
 */
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
	// O ExampleMatcher estar servindo para fazer um ignoreCase no campo
	// 'description' informado pelo cliente, uma vez que 'Estário 01', 'ESTÁDIO 01' ou
	// 'estádio 01' serão considerados iguais nesse cenário.
	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("description");

	// Esta sendo utilizada a consulta com Example do Spring Data, ou seja,
	// a consulta irá filtrar a consulta com todos os valores informados no
	// objeto Location, utilizando o ExampleMatcher informado.
	return locationRepository.findAll(Example.of(location, mather));
    }

    @Override
    public Location create(Location location) throws ApiException {
	if (location.getDescription() == null || location.getDescription().trim().isEmpty())
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo descrição é obrigatório");

	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("description");

	location.setId(null);

	if (locationRepository.exists(Example.of(location, mather)))
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "Já existe um local com essa descrição");

	return locationRepository.save(location);
    }

}
