package searchengine.model.entity;

import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;

@Entity
@Table(name = "`lemmas`", indexes = {
        @Index(name = "idx_lemmas_lemma", columnList = "lemma"),
        @Index(name = "idx_lemmas_siteId", columnList = "site_id")})
@RequiredArgsConstructor
@Getter
@Setter
public class Lemmas implements Comparable<Lemmas>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "INT", nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private Sites site;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    private String lemma;

    @Column(nullable = false)
    private int frequency;

    @Override
    public int compareTo(Lemmas o) {
        return Integer.compare(getFrequency(), o.getFrequency());
    }

    public Lemmas(Sites site, String lemma, int frequency) {
        this.site = site;
        this.lemma = lemma;
        this.frequency = frequency;
    }

}