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
	// ExampleMatcher mather =
	// ExampleMatcher.matching().withIgnoreCase("name");

	return competitionRepository.findAll(Example.of(competition),
		new Sort(order == null ? Sort.Direction.ASC : order, "initDate"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Competition create(Competition competition) throws ApiException {

	try {
	    validateCompetitionObject(competition);

	    validateCompetitionRules(competition);

	    return competitionRepository.save(competition);

	} catch (ApiException apiEx) {
	    throw apiEx;
	} catch (Exception e) {
	    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage());
	}
    }

    private void validateCompetitionRules(Competition competition) throws ApiException {
	if (competition.getInitDate() == null || competition.getEndDate() == null) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400, "É preciso informar a data início e fim da competição");
	} else if (competition.getInitDate().isAfter(competition.getEndDate())) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "A data fim da competição deve ser maior que a data de início");
	} else if (Long.valueOf(ChronoUnit.MINUTES.between(competition.getInitDate(), competition.getEndDate()))
		.compareTo(Long.valueOf("30")) < 0) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "A competição deve ter a duração mínima de 30 minutos");
	}

	long countConflicts = competitionRepository.countCompetitionsSameModalityLocationAndPeriod(
		competition.getModality().getId(),
		competition.getLocation().getId(), competition.getInitDate(), competition.getEndDate());

	if (Long.valueOf(countConflicts).compareTo(Long.valueOf("0")) > 0) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "Não pode haver mais de uma competição da mesma modalidade, no mesmo local, no mesmo momento.");
	}

	if (!competition.getPhase().isAllowSameCountry()) {
	    Stream<Country> countries = competition.getCountries().stream();
	    List<Country> duplicatedCountries = countries
		    .filter(c -> Collections.frequency(competition.getCountries(), c) > 1).collect(Collectors.toList());

	    if (duplicatedCountries.size() > 0) {
		throw new ApiException(HttpStatus.BAD_REQUEST, 400,
			"O mesmo país não pode estar mais de uma vez na competição no etapa "
				+ competition.getPhase().getDescription());
	    }
	}

	OffsetDateTime startDate = OffsetDateTime.of(LocalDateTime.of(competition.getInitDate().getYear(),
		competition.getInitDate().getMonth(), competition.getInitDate().getDayOfMonth(), 0, 0),
		competition.getInitDate().getOffset());
	OffsetDateTime finalDate = OffsetDateTime.of(LocalDateTime.of(competition.getInitDate().getYear(),
		competition.getInitDate().getMonth(), competition.getInitDate().getDayOfMonth(), 23, 59),
		competition.getInitDate().getOffset());

	long countCompetitionLimit = competitionRepository.countByLocationAndInitDateBetween(competition.getLocation(),
		startDate, finalDate);

	if (Long.valueOf(countCompetitionLimit).compareTo(Long.valueOf("3")) > 0) {
	    throw new ApiException(HttpStatus.BAD_REQUEST, 400,
		    "Cada local só pode receber no máximo 4 competições por dia");
	}

    }

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
