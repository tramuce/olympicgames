package br.com.olympicgames.service.api;

import java.util.List;

import br.com.olympicgames.repository.model.Country;
import br.com.olympicgames.service.exception.ApiException;

public interface ICountryService {

    public Country get(Long id);

    public List<Country> find(Country country);

    public Country create(Country country) throws ApiException;

}
