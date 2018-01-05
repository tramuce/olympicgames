package br.com.olympicgames.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.olympicgames.controller.json.ApiResponse;
import br.com.olympicgames.repository.model.Competition;
import br.com.olympicgames.repository.model.Modality;
import br.com.olympicgames.service.api.ICompetitionService;
import br.com.olympicgames.service.exception.ApiException;

@RestController
@RequestMapping("/competition")
public class CompetitionController {

    @Autowired
    ICompetitionService competitionService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Object> create(@RequestBody Competition competition, UriComponentsBuilder uriBuilder) {
	try {
	    Competition newCompetition = competitionService.create(competition);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setLocation(uriBuilder.path("/competition/{id}").buildAndExpand(newCompetition.getId()).toUri());

	    return new ResponseEntity<Object>(newCompetition, headers, HttpStatus.CREATED);
	} catch (ApiException e) {
	    return new ResponseEntity<Object>(new ApiResponse(e.getCode(), e.getMessage()), e.getHttpStatus());
	}
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Object> find(@RequestParam(value = "modalityName", required = false) String modalityName,
	    @RequestParam(value = "modalityId", required = false) Long modalityId,
	    @RequestParam(value = "order", required = false) String order) {

	try {
	    Modality modality = new Modality();
	    modality.setId(modalityId);
	    modality.setName(modalityName);

	    Competition competition = new Competition();
	    competition.setModality(modality);

	    Sort.Direction enumOrder = order != null ? Sort.Direction.fromString(order) : null;

	    List<Competition> result = competitionService.find(competition, enumOrder);

	    return new ResponseEntity<Object>(result, HttpStatus.OK);
	} catch (Exception e) {
	    return new ResponseEntity<Object>(new ApiResponse(500, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> get(@PathVariable("id") Long id) {
	Competition competition = competitionService.get(id);

	if (competition == null)
	    return new ResponseEntity<Object>(new ApiResponse(400, "Id inválido"), HttpStatus.BAD_REQUEST);

	return new ResponseEntity<Object>(competition, HttpStatus.OK);
    }
}
