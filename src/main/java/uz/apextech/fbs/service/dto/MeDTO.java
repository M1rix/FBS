package uz.apextech.fbs.service.dto;

import uz.apextech.fbs.domain.enumeration.Gender;
import uz.apextech.fbs.domain.enumeration.ProfileStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class MeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 12, max = 12)
    private String phone;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotNull
    @Size(min = 2, max = 6)
    private String langKey;

    private ImageDTO image;

    private Instant createdDate;

    private ProfileStatus status;

    private Gender gender;

    private List<ExchangeDTO> exchanges;

    public static MeDTO createWith(ProfileDTO profileDTO) {
        MeDTO meDTO = new MeDTO();
        meDTO.setPhone(profileDTO.getPhone());
        meDTO.setFirstName(profileDTO.getFirstName());
        meDTO.setLastName(profileDTO.getLastName());
        meDTO.setImage(profileDTO.getImage());
        meDTO.setLangKey(profileDTO.getLangKey());
        meDTO.setCreatedDate(profileDTO.getCreatedDate());
        meDTO.setStatus(profileDTO.getStatus());
        meDTO.setGender(profileDTO.getGender());
        return meDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ImageDTO getImage() {
        return image;
    }

    public void setImage(ImageDTO image) {
        this.image = image;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public ProfileStatus getStatus() {
        return status;
    }

    public void setStatus(ProfileStatus status) {
        this.status = status;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MeDTO meDTO = (MeDTO) o;
        if (meDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), meDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MeDTO{" +
            "id=" + id +
            ", phone='" + phone + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", langKey='" + langKey + '\'' +
            ", image=" + image +
            ", createdDate=" + createdDate +
            ", status=" + status +
            ", gender=" + gender +
            '}';
    }
}

