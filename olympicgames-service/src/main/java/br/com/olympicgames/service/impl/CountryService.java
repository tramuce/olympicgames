package br.com.olympicgames.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.olympicgames.repository.api.ICountryRepository;
import br.com.olympicgames.repository.model.Country;
import br.com.olympicgames.service.api.ICountryService;
import br.com.olympicgames.service.exception.ApiException;

/**
 * @author tramuce
 * 
 *         Classe de que implementa a interface ICountryService.
 *
 */
@Service
public class CountryService implements ICountryService {

    @Autowired
    private ICountryRepository countryRepository;

    @Override
    public Country get(Long id) {
	Optional<Country> optCountry = countryRepository.findById(id);

	if (optCountry.isPresent())
	    return optCountry.get();

	return null;
    }

    @Override
    public List<Country> find(Country country) {
	// O ExampleMatcher estar servindo para fazer um ignoreCase no campo
	// 'name' informado pelo cliente, uma vez que 'Brasil', 'BRASIL' ou
	// 'brasil' serão considerados iguais nesse cenário.
	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("name");

	// Esta sendo utilizada a consulta com Example do Spring Data, ou seja,
	// a consulta irá filtrar a consulta com todos os valores informados no
	// objeto Country, utilizando o ExampleMatcher informado.
	return countryRepository.findAll(Example.of(country, mather));
    }

    @Override
    public Country create(Country country) throws ApiException {

	if (country.getName() == null || country.getName().trim().isEmpty())
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo nome é obrigatório");

	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("name");

	country.setId(null);

	if (countryRepository.exists(Example.of(country, mather)))
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "Já existe um país com esse nome");

	return countryRepository.save(country);
    }

}

