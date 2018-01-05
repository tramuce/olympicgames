package br.com.olympicgames.service.api;

import java.util.List;

import org.springframework.data.domain.Sort;

import br.com.olympicgames.repository.model.Competition;
import br.com.olympicgames.service.exception.ApiException;

public interface ICompetitionService {

    public Competition create(Competition competition) throws ApiException;

    public Competition get(Long id);

    public List<Competition> find(Competition competition, Sort.Direction order);

}
