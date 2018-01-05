package br.com.olympicgames.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.olympicgames.repository.api.ICompetitionRepository;
import br.com.olympicgames.repository.model.Competition;
import br.com.olympicgames.repository.model.Country;
import br.com.olympicgames.repository.model.Location;
import br.com.olympicgames.repository.model.Modality;
import br.com.olympicgames.repository.model.Phase;
import br.com.olympicgames.service.api.ICompetitionService;
import br.com.olympicgames.service.api.ICountryService;
import br.com.olympicgames.service.api.ILocationService;
import br.com.olympicgames.service.api.IModalityService;
import br.com.olympicgames.service.api.IPhaseService;
import br.com.olympicgames.service.exception.ApiException;

/**
 * @author tramuce
 * 
 *         Classe de que implementa a interface ICompetitionService. O serviço
 *         será responsável por criar, e consultar as competições. Ele também
 *         irá solicitar a criação das outras entidades, Modality, Location e
 *         Country, se os mesmos não existirem, porém quem de fato faz a
 *         inclusão são seus respectivos serviços, mantendo assim as
 *         responsabilidades.
 *
 */
@Service
public class CompetitionService implements ICompetitionService {

    @Autowired
    private ILocationService locationService;

    @Autowired
    private IModalityService modalityService;

    @Autowired
    private IPhaseService phaseService;

    @Autowired
    private ICountryService countryService;

    @Autowired
    private ICompetitionRepository competitionRepository;

    @Override
    public Competition get(Long id) {
	Optional<Competition> optCompetition = competitionRepository.findById(id);

	if (optCompetition.isPresent())
	    return optCompetition.get();

	return null;
    }

    @Override
    public List<Competition> find(Competition competition, Sort.Direction order) {
	return competitionRepository.findAll(Example.of(competition),
		new Sort(order == null ? Sort.Direction.ASC : order, "initDate"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.com.olympicgames.service.api.ICompetitionService#create(br.com.
     * olympicgames.repository.model.Competition)
     * 
     * Na implementação foi definida uma @Transactional, de modo a permitir o
     * rollback da transação no caso de lançamento de exception por parte do
     * método.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Competition create(Competition competition) throws ApiException {

	try {
	    // Valida o objeto informado.
	    validateCompetitionObject(competition);

	    // Valida as regras de negócio
	    validateCompetitionRules(competition);

	    return competitionRepository.save(competition);
	} catch (ApiException apiEx) {
	    throw apiEx;
	} catch (Exception e) {
	    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
	}
    }

    /**
     * @param competition
     * @throws ApiException
     * 
     *             Método responsável por validar as regras de negócio da
     *             aplicação
     */
    private void validateCompetitionRules(Competition competition) throws ApiException {
	// É evidente que as datas são obrigatórias, e a data final deve ser
	// maior ou igual a data inicial.
	if (competition.getInitDate() == null || competition.getEndDate() == null) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "É preciso informar a data início e fim da competição");
	} else if (competition.getInitDate().isAfter(competition.getEndDate())) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "A data fim da competição deve ser maior que a data de início");
	    // O método ChronoUnit.MINUTES.between retorna a diferença em
	    // minutos entre as datas, e devem ser maior ou igual à 30.
	} else if (Long.valueOf(ChronoUnit.MINUTES.between(competition.getInitDate(), competition.getEndDate()))
		.compareTo(Long.valueOf("30")) < 0) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "A competição deve ter a duração mínima de 30 minutos");
	}

	// Consulta que retorna a quantidade de competições para uma mesma
	// modalidade, localização, que tenham intersecção de datas.
	long countConflicts = competitionRepository.countCompetitionsSameModalityLocationAndPeriod(
		competition.getModality().getId(),
		competition.getLocation().getId(), competition.getInitDate(), competition.getEndDate());

	// Se já existir alguma competição, então não pode-se criar a nova.
	if (Long.valueOf(countConflicts).compareTo(Long.valueOf("0")) > 0) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "Não pode haver mais de uma competição da mesma modalidade, no mesmo local, no mesmo momento.");
	}

	// Foi criado no banco um atributo informando se a etapa permite ou não
	// o mesmo país mais de uma vez na competição, essa regra só verifica as
	// etapas que não permitem.
	if (!competition.getPhase().isAllowSameCountry()) {
	    // Com o uso de stream.filter e Collections.frequency, conseguimos
	    // retornar todos os países que aparecem mais de uma vez na
	    // competição.
	    Stream<Country> countries = competition.getCountries().stream();
	    List<Country> duplicatedCountries = countries
		    .filter(c -> Collections.frequency(competition.getCountries(), c) > 1).collect(Collectors.toList());

	    // Caso exista alguma repetição, não podemos prosseguir.
	    if (duplicatedCountries.size() > 0) {
		throw new ApiException(HttpStatus.BAD_REQUEST, 400,
			"O mesmo país não pode estar mais de uma vez na competição no etapa "
				+ competition.getPhase().getDescription());
	    }
	}

	// Baseada na data início da nova competição, vamos definir as datas
	// limites do dia (00:00 - 23:59) e informar para a consulta.
	OffsetDateTime startDate = OffsetDateTime.of(LocalDateTime.of(competition.getInitDate().getYear(),
		competition.getInitDate().getMonth(), competition.getInitDate().getDayOfMonth(), 0, 0),
		competition.getInitDate().getOffset());
	OffsetDateTime finalDate = OffsetDateTime.of(LocalDateTime.of(competition.getInitDate().getYear(),
		competition.getInitDate().getMonth(), competition.getInitDate().getDayOfMonth(), 23, 59),
		competition.getInitDate().getOffset());

	// Consulta que retorna quantas competições já existem na mesma
	// localização naquele dia.
	long countCompetitionLimit = competitionRepository.countByLocationAndInitDateBetween(competition.getLocation(),
		startDate, finalDate);

	// O limite definido foi de 4 competições.
	if (Long.valueOf(countCompetitionLimit).compareTo(Long.valueOf("3")) > 0) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "Cada local só pode receber no máximo 4 competições por dia");
	}

    }

    /**
     * @param competition
     * @throws ApiException
     * 
     *             Método que irá validar a estrutura do objeto Competition
     *             informado, carregando as entidades internas do mesmo.
     */
    private void validateCompetitionObject(Competition competition) throws ApiException {
	competition.setId(null);
	competition.setLocation(validateAndCreateLocation(competition.getLocation()));
	competition.setModality(validateAndCreateModality(competition.getModality()));
	competition.setPhase(validatePhase(competition.getPhase()));

	if (competition.getCountries() != null) {
	    List<Country> validatedCountries = new ArrayList<>();
	    for (Country country : competition.getCountries()) {
		validatedCountries.add(validateAndCreateCountry(country));
	    }
	    competition.setCountries(validatedCountries);
	}
    }

    /**
     * @param location
     * @return
     * @throws ApiException
     * 
     *             Valida o objeto Location informado. Caso seja informado o Id,
     *             deve-se recuperar obrigatoriamente o mesmo do banco. Caso
     *             tenha sido informado uma descrição, consulta o mesmo para
     *             verificar a existencia, no caso negativo, insere a nova
     *             Location no banco e retorna o novo objeto.
     */
    private Location validateAndCreateLocation(Location location) throws ApiException {
	if (location == null) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo localização deve ser informado");
	}

	if (location.getId() != null) {
	    Location loc = locationService.get(location.getId());

	    if (loc == null) {
		throw new ApiException(HttpStatus.BAD_REQUEST, 400, "Id da localização inválido");
	    }

	    return loc;
	} else if (location.getDescription() != null && !location.getDescription().trim().isEmpty()) {
	    List<Location> locationList = locationService.find(location);

	    if (locationList.size() > 0) {
		return locationList.get(0);
	    } else {
		return locationService.create(location);
	    }
	} else {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "O campo id ou descrição deve ser informado para a localização");
	}
    }

    /**
     * @param modality
     * @return
     * @throws ApiException
     * 
     *             Valida o objeto Modality informado. Caso seja informado o Id,
     *             deve-se recuperar obrigatoriamente o mesmo do banco. Caso
     *             tenha sido informado um nome, consulta o mesmo para verificar
     *             a existencia, no caso negativo, insere a nova Modality no
     *             banco e retorna o novo objeto.
     */
    private Modality validateAndCreateModality(Modality modality) throws ApiException {
	if (modality == null) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo modalidade deve ser informado");
	}

	if (modality.getId() != null) {
	    Modality mod = modalityService.get(modality.getId());

	    if (mod == null) {
		throw new ApiException(HttpStatus.BAD_REQUEST, 400, "Id da modalidade inválido");
	    }

	    return mod;
	} else if (modality.getName() != null && !modality.getName().trim().isEmpty()) {
	    List<Modality> modalityList = modalityService.find(modality);
	    if (modalityList.size() > 0) {
		return modalityList.get(0);
	    } else {
		return modalityService.create(modality);
	    }
	} else {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "O campo id ou nome deve ser informado para a modalidade");
	}
    }

    /**
     * @param phase
     * @return
     * @throws ApiException
     *
     *             Valida o objeto Phase informado. Caso seja informado o Id,
     *             deve-se recuperar obrigatoriamente o mesmo do banco. Caso
     *             tenha sido informado uma tag, consulta o mesmo para verificar
     *             a existencia. Esse objeto nunca é criado por se tratar de uma
     *             tabela apenas de consulta
     */
    private Phase validatePhase(Phase phase) throws ApiException {
	if (phase == null) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo etapa deve ser informado");
	}

	Phase ph = null;

	if (phase.getId() != null) {
	    ph = phaseService.get(phase.getId());
	} else if (phase.getTag() != null && !phase.getTag().trim().isEmpty()) {
	    List<Phase> phaseList = phaseService.find(phase);
	    ph = phaseList.size() > 0 ? phaseList.get(0) : null;
	} else {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo id ou tag deve ser informado para a etapa");
	}

	if (ph == null) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "Etapa inválida");
	}

	return ph;
    }

    /**
     * @param country
     * @return
     * @throws ApiException
     *             Valida o objeto Country informado. Caso seja informado o Id,
     *             deve-se recuperar obrigatoriamente o mesmo do banco. Caso
     *             tenha sido informado um nome, consulta o mesmo para verificar
     *             a existencia, no caso negativo, insere a nova Country no
     *             banco e retorna o novo objeto.
     */
    private Country validateAndCreateCountry(Country country) throws ApiException {
	if (country.getId() != null) {
	    Country crt = countryService.get(country.getId());

	    if (crt == null) {
		throw new ApiException(HttpStatus.BAD_REQUEST, 400, "Id do país inválido");
	    }

	    return crt;
	} else if (country.getName() != null && !country.getName().trim().isEmpty()) {
	    List<Country> countryList = countryService.find(country);
	    if (countryList.size() > 0) {
		return countryList.get(0);
	    } else {
		return countryService.create(country);
	    }
	} else {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "O campo id ou nome deve ser informado para o país");
	}
    }
}
