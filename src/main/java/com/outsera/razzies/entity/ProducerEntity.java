package com.outsera.razzies.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "producers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_producer_normalized_name",
                columnNames = "normalized_name"
        )
)
public class ProducerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(name = "normalized_name", nullable = false, length = 255)
    private String nomeNormalizado;

    @ManyToMany(mappedBy = "produtores")
    private Set<MovieEntity> filmes = new LinkedHashSet<>();

    protected ProducerEntity() {
    }

    public ProducerEntity(String nome, String nomeNormalizado) {
        this.nome = nome;
        this.nomeNormalizado = nomeNormalizado;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeNormalizado() {
        return nomeNormalizado;
    }

    public Set<MovieEntity> getFilmes() {
        return filmes;
    }
}
