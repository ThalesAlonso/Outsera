package com.outsera.razzies;

import com.outsera.razzies.entity.MovieEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultCsvBootstrapIntegrationTest extends AbstractAwardIntervalsIntegrationTest {

    @Test
    void shouldLoadMoviesAndWinnersFromApplicationDefaultCsvIntoH2() {
        assertThat(movieRepository.count()).isEqualTo(206);
        assertThat(movieRepository.countByVencedorTrue()).isEqualTo(42);
        assertThat(producerRepository.count()).isGreaterThan(0);
    }

    @Test
    void shouldParseMultipleProducersFromSingleField() {
        MovieEntity movie = movieRepository.findByTitulo("Under the Cherry Moon").orElseThrow();

        assertThat(movie.getProdutores())
                .extracting(produtor -> produtor.getNome())
                .containsExactlyInAnyOrder("Bob Cavallo", "Joe Ruffalo", "Steve Fargnoli");
    }

    @Test
    void shouldParseProducerListsWithOxfordComma() {
        MovieEntity movie = movieRepository.findByTitulo("Cats").orElseThrow();

        assertThat(movie.getProdutores())
                .extracting(produtor -> produtor.getNome())
                .containsExactlyInAnyOrder("Debra Hayward", "Tim Bevan", "Eric Fellner", "Tom Hooper");
    }
}
