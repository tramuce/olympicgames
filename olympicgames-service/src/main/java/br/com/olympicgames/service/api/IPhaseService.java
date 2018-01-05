package br.com.olympicgames.service.api;

import java.util.List;

import br.com.olympicgames.repository.model.Phase;

public interface IPhaseService {

    public Phase get(Long id);

    public List<Phase> find(Phase phase);

}
