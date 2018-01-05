package br.com.olympicgames.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import br.com.olympicgames.repository.api.IPhaseRepository;
import br.com.olympicgames.repository.model.Phase;
import br.com.olympicgames.service.api.IPhaseService;

/**
 * @author tramuce
 * 
 *         Classe de que implementa a interface IPhaseService.
 *
 */
@Service
public class PhaseService implements IPhaseService {

    @Autowired
    private IPhaseRepository phaseRepository;

    @Override
    public Phase get(Long id) {
	Optional<Phase> optPhase = phaseRepository.findById(id);

	if (optPhase.isPresent())
	    return optPhase.get();

	return null;
    }

    @Override
    public List<Phase> find(Phase phase) {
	// O ExampleMatcher estar servindo para fazer um ignoreCase no campo
	// 'tag' informado pelo cliente, uma vez que 'Final', 'FINAl' ou 'final'
	// serão considerados iguais nesse cenário.
	ExampleMatcher mather = ExampleMatcher.matching().withIgnoreCase("tag");

	// Esta sendo utilizada a consulta com Example do Spring Data, ou seja,
	// a consulta irá filtrar a consulta com todos os valores informados no
	// objeto Phase, utilizando o ExampleMatcher informado.
	return phaseRepository.findAll(Example.of(phase, mather));
    }

}
