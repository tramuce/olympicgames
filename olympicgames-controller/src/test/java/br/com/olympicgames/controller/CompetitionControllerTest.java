package br.com.olympicgames.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import br.com.olympicgames.controller.configuration.OlympicGamesApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OlympicGamesApplication.class)
@AutoConfigureMockMvc
public class CompetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void xpto() throws Exception {
	this.mockMvc.perform(get("/"));
    }

}
