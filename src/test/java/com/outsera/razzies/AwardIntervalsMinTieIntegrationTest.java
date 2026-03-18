package com.outsera.razzies;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "app.csv.localizacao=classpath:datasets/tie-min.csv")
class AwardIntervalsMinTieIntegrationTest extends AbstractAwardIntervalsIntegrationTest {

    @Test
    void shouldReturnAllProducersTiedForMinimumInterval() throws Exception {
        buscarIntervalosPremiacao()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min.length()").value(2))
                .andExpect(jsonPath("$.min[0].producer").value("Producer A"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.min[1].producer").value("Producer B"))
                .andExpect(jsonPath("$.min[1].interval").value(1))
                .andExpect(jsonPath("$.max.length()").value(1))
                .andExpect(jsonPath("$.max[0].producer").value("Producer C"))
                .andExpect(jsonPath("$.max[0].interval").value(10));
    }
}
