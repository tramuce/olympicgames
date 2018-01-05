package br.com.olympicgames.service.api;

import java.util.List;

import org.springframework.data.domain.Sort;

import br.com.olympicgames.repository.model.Competition;
import br.com.olympicgames.service.exception.ApiException;

/**
 * @author tramuce
 * 
 *         Definição na interface padrão para os serviço Competition
 *
 */
public interface ICompetitionService {

    /**
     * @param competition
     * @return
     * @throws ApiException
     * 
     *             Método que irá cadastrar a competição, e as demais entidades
     *             caso necessário. Sempre retorna uma ApiException, tratando
     *             erros não esperados para retornar uma exception conhecida ao
     *             controller.
     */
    public Competition create(Competition competition) throws ApiException;

    public Competition get(Long id);

    public List<Competition> find(Competition competition, Sort.Direction order);

}
