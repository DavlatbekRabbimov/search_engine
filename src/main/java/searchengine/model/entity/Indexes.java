package searchengine.model.entity;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "`indexes`", indexes = {
        @Index(name = "idx_indexes_pageId", columnList = "page_id"),
        @Index(name = "idx_indexes_lemmaId", columnList = "lemma_id"),
        @Index(name = "idx_indexes_rank", columnList = "`rank`")
})
@RequiredArgsConstructor
@Getter
@Setter
public class Indexes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT", nullable = false)
    private int id;

    @JoinColumn(name = "page_id", nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private Pages page;

    @JoinColumn(name = "lemma_id", nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private Lemmas lemma;

    @Column(name = "`rank`", nullable = false, columnDefinition = "FLOAT")
    private float rank;

    public Indexes(Pages page, Lemmas lemma, float rank) {
        this.page = page;
        this.lemma = lemma;
        this.rank = rank;
    }
}