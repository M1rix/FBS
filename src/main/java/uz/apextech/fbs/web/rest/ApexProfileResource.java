package uz.apextech.fbs.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.apextech.fbs.domain.enumeration.ProfileStatus;
import uz.apextech.fbs.service.ApexProfileService;
import uz.apextech.fbs.service.dto.MeDTO;
import uz.apextech.fbs.service.dto.ProfileDTO;
import uz.apextech.fbs.web.rest.util.HeaderUtil;
import uz.apextech.fbs.web.rest.vm.PhoneAndTokenVM;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ApexProfileResource {

    private final Logger log = LoggerFactory.getLogger(ApexProfileResource.class);

    private static final String ENTITY_NAME = "profile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApexProfileService apexProfileService;

    public ApexProfileResource(ApexProfileService apexProfileService) {
        this.apexProfileService = apexProfileService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AccessToken> authenticate(@Valid @RequestBody PhoneAndTokenVM phoneAndToken,
                                                 HttpServletRequest request) {
        log.debug("Rest request to authenticate profile");
        Optional<ProfileDTO> optionalProfile = apexProfileService.findOneByPhone(phoneAndToken.getPhone());
        if (optionalProfile.isEmpty()) {
            optionalProfile = apexProfileService.registerCustomer(phoneAndToken.getPhone(), HeaderUtil.resolveLanguage(request));
        }
        ProfileDTO profileDTO = optionalProfile.get();
        if (ProfileStatus.ACTIVE != profileDTO.getStatus()) {
            profileDTO.setAccessToken(null);
        }
        return ResponseEntity.ok(new AccessToken(profileDTO));
    }

    @GetMapping("/getMe")
    public ResponseEntity<MeDTO> getMe(){
        MeDTO result = apexProfileService.getMe();
        return ResponseEntity.ok().body(result);
    }

    static class AccessToken {

        private String accessToken;

        private ProfileStatus status;

        AccessToken(ProfileDTO profileDTO) {
            this.accessToken = profileDTO.getAccessToken();
            this.status = profileDTO.getStatus();
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public ProfileStatus getStatus() {
            return status;
        }

        public void setStatus(ProfileStatus status) {
            this.status = status;
        }
    }

}
