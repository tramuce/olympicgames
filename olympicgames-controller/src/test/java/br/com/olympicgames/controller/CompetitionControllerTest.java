package br.com.olympicgames.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import br.com.olympicgames.CompetitionController;
import br.com.olympicgames.repository.model.Competition;
import br.com.olympicgames.repository.model.Country;
import br.com.olympicgames.repository.model.Location;
import br.com.olympicgames.repository.model.Modality;
import br.com.olympicgames.repository.model.Phase;
import br.com.olympicgames.service.exception.ApiException;
import br.com.olympicgames.service.impl.CompetitionService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CompetitionController.class)
public class CompetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompetitionService competitionService;

    @MockBean
    private CompetitionController competitionController;

    private ObjectMapper mapper = new ObjectMapper();

    private Competition mockCompetition01;
    private Competition mockCompetition02;

    private Location mockLocation01;
    private Location mockLocation02;
    private Modality mockModality01;
    private Modality mockModality02;
    private Phase mockPhase01;
    private Phase mockPhase02;
    private Country mockCountry01;
    private Country mockCountry02;

    @Before
    public void setup() throws ApiException {

	JavaTimeModule module = new JavaTimeModule();
	LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
	mapper = Jackson2ObjectMapperBuilder.json().modules(module)
		.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();

	mockLocation01 = new Location();
	mockLocation01.setDescription("Estádio");
	mockLocation02 = new Location();
	mockLocation02.setDescription("Ginásio");

	mockModality01 = new Modality();
	mockModality01.setName("Futebol");
	mockModality02 = new Modality();
	mockModality02.setName("Basquete");

	mockPhase01 = new Phase();
	mockPhase01.setTag("final");
	mockPhase02 = new Phase();
	mockPhase02.setTag("eliminatories");

	mockCountry01 = new Country();
	mockCountry01.setName("Brasil");
	mockCountry02 = new Country();
	mockCountry02.setName("Argentina");

	mockCompetition01 = new Competition();
	mockCompetition01.setLocation(mockLocation01);
	mockCompetition01.setModality(mockModality01);
	mockCompetition01.setPhase(mockPhase02);
	mockCompetition01.setCountries(Arrays.asList(mockCountry01, mockCountry02));
	mockCompetition01.setInitDate(OffsetDateTime.now());
	mockCompetition01.setEndDate(OffsetDateTime.now().plusHours(2));

	competitionService.create(mockCompetition01);
    }
    
    @Test
    public void getCompetition() throws Exception {
	when(competitionService.get(Long.valueOf("1"))).thenReturn(mockCompetition01);
	this.mockMvc.perform(get("/competition/1").contentType("application/json")
		.content(mapper.writeValueAsString(mockCompetition01)))
		.andExpect(status().isOk());
    }

    @Test
    public void findCompetition() throws Exception {

	List<Competition> competitionList = new ArrayList<Competition>();
	competitionList.add(mockCompetition01);

	mockCompetition02 = new Competition();
	when(competitionService.find(mockCompetition02, null)).thenReturn(competitionList);
	this.mockMvc.perform(get("/competition").contentType("application/json")
		.content(mapper.writeValueAsString(mockCompetition02))).andExpect(status().isOk());
    }

    @Test
    public void createCompetition() throws Exception {
	mockCompetition02 = new Competition();
	mockCompetition02.setLocation(mockLocation01);
	mockCompetition02.setModality(mockModality01);
	mockCompetition02.setPhase(mockPhase02);
	mockCompetition02.setCountries(Arrays.asList(mockCountry01, mockCountry02));
	mockCompetition02.setInitDate(OffsetDateTime.now().minusHours(2));
	mockCompetition02.setEndDate(OffsetDateTime.now().minusHours(2));

	when(competitionService.create(mockCompetition02)).thenReturn(mockCompetition02);
	this.mockMvc.perform(post("/competition").contentType("application/json")
		.content(mapper.writeValueAsString(mockCompetition02)))
		.andExpect(status().isOk());
    }

}
