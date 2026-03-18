package com.outsera.razzies;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "app.csv.localizacao=classpath:datasets/winner-filter.csv")
class WinnerFilteringIntegrationTest extends AbstractAwardIntervalsIntegrationTest {

    @Test
    void shouldIgnoreNonWinningRowsWhenCalculatingIntervals() throws Exception {
        buscarIntervalosPremiacao()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min.length()").value(1))
                .andExpect(jsonPath("$.min[0].producer").value("Producer B"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.max.length()").value(1))
                .andExpect(jsonPath("$.max[0].producer").value("Producer A"))
                .andExpect(jsonPath("$.max[0].interval").value(10))
                .andExpect(jsonPath("$.max[0].previousWin").value(2000))
                .andExpect(jsonPath("$.max[0].followingWin").value(2010));
    }
}
