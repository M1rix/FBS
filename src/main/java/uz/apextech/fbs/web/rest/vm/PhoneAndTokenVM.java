package uz.apextech.fbs.web.rest.vm;


import uz.apextech.fbs.config.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class PhoneAndTokenVM {

    @NotBlank
    @Pattern(regexp = Constants.PROFILE_PHONE_REGEX)
    private String phone;

    @NotBlank
    private String token;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
