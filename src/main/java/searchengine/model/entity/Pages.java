package searchengine.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "`pages`", indexes = {
        @Index(name = "idx_pages_path", columnList = "path"),
        @Index(name = "idx_pages_siteId", columnList = "site_id")})
@RequiredArgsConstructor
@Getter
@Setter
public class Pages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT", nullable = false)
    private int id;

    @JoinColumn(name = "site_id")
    @ManyToOne(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private Sites site;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String path;

    @Column(columnDefinition = "INT", nullable = false)
    private int code;

    @Type(type = "org.hibernate.type.TextType")
    @Column(nullable = false)
    private String content;

    public Pages(Sites site, String path, int code, String content) {
        this.site = site;
        this.path = path;
        this.code = code;
        this.content = content;
    }

}
