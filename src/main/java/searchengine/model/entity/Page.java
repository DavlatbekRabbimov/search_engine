package searchengine.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "pages")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT", nullable = false)
    private Long id;

    @JoinColumn(name = "site_id")
    @ManyToOne(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private Site site;

    @Column(columnDefinition = "TEXT NOT NULL, INDEX (path(512))")
    private String path;

    @Column(columnDefinition = "INT", nullable = false)
    private int code;

    @Type(type = "org.hibernate.type.TextType")
    @Column(nullable = false)
    private String content;

    public Page(Site site, String path, int code, String content) {
        this.site = site;
        this.path = path;
        this.code = code;
        this.content = content;
    }

}
