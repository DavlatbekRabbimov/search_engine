package searchengine.model.entity;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "`indexes`", indexes = {
        @javax.persistence.Index(name = "index_page_id", columnList = "page_id"),
        @javax.persistence.Index(name = "index_lemma_id", columnList = "lemma_id")})
public class Index {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT", nullable = false)
    private Long id;

    @JoinColumn(name = "page_id", nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private Page page;

    @JoinColumn(name = "lemma_id", nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private Lemma lemma;

    @Column(name = "`rank`", nullable = false, columnDefinition = "FLOAT")
    private float rank;

    public Index(Page page, Lemma lemma, float rank) {
        this.page = page;
        this.lemma = lemma;
        this.rank = rank;
    }
}