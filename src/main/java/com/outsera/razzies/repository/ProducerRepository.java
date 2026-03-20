package com.outsera.razzies.repository;

import com.outsera.razzies.entity.ProducerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProducerRepository extends JpaRepository<ProducerEntity, Long> {

    Optional<ProducerEntity> findByNomeNormalizado(String nomeNormalizado);
}
