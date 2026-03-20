package com.outsera.razzies.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "movies",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_movie_business_key",
                columnNames = {"award_year", "title", "studios", "winner"}
        )
)
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "award_year", nullable = false)
    private Integer anoPremiacao;

    @Column(name = "title", nullable = false, length = 500)
    private String titulo;

    @Column(name = "studios", nullable = false, length = 500)
    private String estudios;

    @Column(name = "winner", nullable = false)
    private boolean vencedor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_producers",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "producer_id")
    )
    private Set<ProducerEntity> produtores = new LinkedHashSet<>();

    protected MovieEntity() {
    }

    public MovieEntity(Integer anoPremiacao, String titulo, String estudios, boolean vencedor) {
        this.anoPremiacao = anoPremiacao;
        this.titulo = titulo;
        this.estudios = estudios;
        this.vencedor = vencedor;
    }

    public void adicionarProdutor(ProducerEntity produtor) {
        produtores.add(produtor);
        produtor.getFilmes().add(this);
    }

    public Long getId() {
        return id;
    }

    public Integer getAnoPremiacao() {
        return anoPremiacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEstudios() {
        return estudios;
    }

    public boolean isVencedor() {
        return vencedor;
    }

    public Set<ProducerEntity> getProdutores() {
        return produtores;
    }
}
