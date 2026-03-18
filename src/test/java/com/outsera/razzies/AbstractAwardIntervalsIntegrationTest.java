package com.outsera.razzies;

import com.outsera.razzies.repository.MovieRepository;
import com.outsera.razzies.repository.ProducerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractAwardIntervalsIntegrationTest {

    protected static final String ENDPOINT = "/api/v1/producers/award-intervals";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected MovieRepository movieRepository;

    @Autowired
    protected ProducerRepository producerRepository;

    protected ResultActions buscarIntervalosPremiacao() throws Exception {
        return mockMvc.perform(get(ENDPOINT));
    }
}
