package uz.apextech.fbs.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.apextech.fbs.domain.Exchange} entity.
 */
public class ExchangeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String createdBy;

    private Instant createdDate;

    @Size(max = 50)
    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private ProfileDTO formProfile;

    private ProfileDTO toProfile;

    private BookDTO book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public ProfileDTO getFormProfile() {
        return formProfile;
    }

    public void setFormProfile(ProfileDTO formProfile) {
        this.formProfile = formProfile;
    }

    public ProfileDTO getToProfile() {
        return toProfile;
    }

    public void setToProfile(ProfileDTO toProfile) {
        this.toProfile = toProfile;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExchangeDTO)) {
            return false;
        }

        ExchangeDTO exchangeDTO = (ExchangeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, exchangeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExchangeDTO{" +
            "id=" + getId() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", formProfile=" + getFormProfile() +
            ", toProfile=" + getToProfile() +
            ", book=" + getBook() +
            "}";
    }
}
