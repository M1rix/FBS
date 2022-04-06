package uz.apextech.fbs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Exchange.
 */
@Entity
@Table(name = "apex_exchange")
public class Exchange extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties(value = { "image" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Profile formProfile;

    @JsonIgnoreProperties(value = { "image" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Profile toProfile;

    @JsonIgnoreProperties(value = { "image", "category", "authors" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exchange id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exchange createdBy(String createdBy) {
        setCreatedBy(createdBy);
        return this;
    }

    public Exchange createdDate(Instant createdDate) {
        setCreatedDate(createdDate);
        return this;
    }

    public Exchange lastModifiedBy(String lastModifiedBy) {
        setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public Exchange lastModifiedDate(Instant lastModifiedDate) {
        setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public Profile getFormProfile() {
        return this.formProfile;
    }

    public void setFormProfile(Profile profile) {
        this.formProfile = profile;
    }

    public Exchange formProfile(Profile profile) {
        this.setFormProfile(profile);
        return this;
    }

    public Profile getToProfile() {
        return this.toProfile;
    }

    public void setToProfile(Profile profile) {
        this.toProfile = profile;
    }

    public Exchange toProfile(Profile profile) {
        this.setToProfile(profile);
        return this;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Exchange book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exchange)) {
            return false;
        }
        return id != null && id.equals(((Exchange) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exchange{" +
            "id=" + getId() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
