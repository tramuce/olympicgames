package br.com.olympicgames.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.olympicgames.repository.api.IModalityRepository;
import br.com.olympicgames.repository.model.Modality;
import br.com.olympicgames.service.api.IModalityService;
import br.com.olympicgames.service.exception.ApiException;

/**
 * @author tramuce
 * 
 *         Classe de que implementa a interface IModalityService.
 *
 */
@Service
public class ModalityService implements IModalityService {

    @Autowired
    private IModalityRepository modalityRepository;

    @Override
    public Modality get(Long id) {
	Optional<Modality> optModality = modalityRepository.findById(id);

	if (optModality.isPresent())
	    return optModality.get();

	return null;
    }

    @Override
    public List<Modality> find(Modality modality) {
	// O ExampleMatcher estar servindo para fazer um ignoreCase no campo
	// 'name' informado pelo cliente, uma vez que 'Basquete', 'BASQUETE' ou
	// 'basquete' serão considerados iguais nesse cenário.
	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("name");

	// Esta sendo utilizada a consulta com Example do Spring Data, ou seja,
	// a consulta irá filtrar a consulta com todos os valores informados no
	// objeto Modality, utilizando o ExampleMatcher informado.
	return modalityRepository.findAll(Example.of(modality, mather));
    }

    @Override
    public Modality create(Modality modality) throws ApiException {

	if (modality.getName() == null || modality.getName().trim().isEmpty())
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo nome é obrigatório");

	modality.setId(null);

	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("name");

	if (modalityRepository.exists(Example.of(modality, mather)))
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "Já existe uma modalidade com esse nome");

	return modalityRepository.save(modality);
    }

}
