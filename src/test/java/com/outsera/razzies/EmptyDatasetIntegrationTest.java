package com.outsera.razzies;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "app.csv.localizacao=classpath:datasets/empty.csv")
class EmptyDatasetIntegrationTest extends AbstractAwardIntervalsIntegrationTest {

    @Test
    void shouldReturnEmptyListsWhenThereAreNoIntervalsToCompute() throws Exception {
        buscarIntervalosPremiacao()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min.length()").value(0))
                .andExpect(jsonPath("$.max.length()").value(0));
    }
}
