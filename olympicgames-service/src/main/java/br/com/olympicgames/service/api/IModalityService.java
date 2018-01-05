package br.com.olympicgames.service.api;

import java.util.List;

import br.com.olympicgames.repository.model.Modality;
import br.com.olympicgames.service.exception.ApiException;

public interface IModalityService {

    public Modality get(Long id);

    public List<Modality> find(Modality modality);

    public Modality create(Modality modality) throws ApiException;

}
