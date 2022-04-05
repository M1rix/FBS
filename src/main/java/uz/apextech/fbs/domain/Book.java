package uz.apextech.fbs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import uz.apextech.fbs.domain.enumeration.BookStatus;

/**
 * A Book.
 */
@Entity
@Table(name = "apex_book")
public class Book extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 255)
    @Column(name = "name", length = 255)
    private String name;

    @Min(value = 0)
    @Column(name = "pages")
    private Integer pages;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookStatus status;

    @Min(value = 0L)
    @Column(name = "likes")
    private Long likes;

    @OneToOne
    @JoinColumn(unique = true)
    private Image image;

    @ManyToOne
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Category category;

    @OneToMany(mappedBy = "book")
    @JsonIgnoreProperties(value = { "image", "book" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Book name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPages() {
        return this.pages;
    }

    public Book pages(Integer pages) {
        this.setPages(pages);
        return this;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public BookStatus getStatus() {
        return this.status;
    }

    public Book status(BookStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Long getLikes() {
        return this.likes;
    }

    public Book likes(Long likes) {
        this.setLikes(likes);
        return this;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Book createdBy(String createdBy) {
        setCreatedBy(createdBy);
        return this;
    }

    public Book createdDate(Instant createdDate) {
        setCreatedDate(createdDate);
        return this;
    }

    public Book lastModifiedBy(String lastModifiedBy) {
        setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public Book lastModifiedDate(Instant lastModifiedDate) {
        setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Book image(Image image) {
        this.setImage(image);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Book category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public void setAuthors(Set<Author> authors) {
        if (this.authors != null) {
            this.authors.forEach(i -> i.setBook(null));
        }
        if (authors != null) {
            authors.forEach(i -> i.setBook(this));
        }
        this.authors = authors;
    }

    public Book authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Book addAuthor(Author author) {
        this.authors.add(author);
        author.setBook(this);
        return this;
    }

    public Book removeAuthor(Author author) {
        this.authors.remove(author);
        author.setBook(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", pages=" + getPages() +
            ", status='" + getStatus() + "'" +
            ", likes=" + getLikes() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
