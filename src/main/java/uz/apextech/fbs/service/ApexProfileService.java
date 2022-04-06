package uz.apextech.fbs.service;

import com.github.dockerjava.api.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.apextech.fbs.domain.enumeration.ProfileStatus;
import uz.apextech.fbs.repository.ApexProfileRepository;
import uz.apextech.fbs.repository.ProfileRepository;
import uz.apextech.fbs.security.SecurityUtils;
import uz.apextech.fbs.service.dto.MeDTO;
import uz.apextech.fbs.service.dto.ProfileDTO;
import uz.apextech.fbs.service.mapper.ApexProfileMapper;
import uz.apextech.fbs.service.mapper.ProfileMapper;
import uz.apextech.fbs.web.rest.errors.BadRequestAlertException;

import java.util.Optional;

import static java.util.Optional.of;

@Service
public class ApexProfileService extends ProfileService {

    private final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final ApexProfileRepository apexProfileRepository;

    private final ApexProfileMapper apexProfileMapper;

    public ApexProfileService(ProfileRepository profileRepository,
                              ProfileMapper profileMapper,
                              ApexProfileRepository apexProfileRepository,
                              ApexProfileMapper apexProfileMapper) {
        super(profileRepository, profileMapper);
        this.apexProfileRepository = apexProfileRepository;
        this.apexProfileMapper = apexProfileMapper;
    }


    @Transactional(readOnly = true)
    public ProfileDTO findCurrentCustomer() {
        String customerPhone = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Optional<ProfileDTO> optionalProfile = findOneByPhone(customerPhone);
        if (optionalProfile.isEmpty()) {
            throw new BadRequestAlertException("Profile not exists", "profile", "notexists");
        }
        return optionalProfile.get();
    }

    public Optional<ProfileDTO> findOneByPhone(String phone) {
        log.debug("Request to get profile by phone: {}", phone);
        return apexProfileRepository.findOneByPhone(phone).map(apexProfileMapper::toDto);
    }

    public Optional<ProfileDTO> registerCustomer(String phone, String language) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setPhone(phone);
        profileDTO.setAccessToken(RandomUtil.generateAccessToken());
        profileDTO.setLangKey(language);
        profileDTO.setStatus(ProfileStatus.ACTIVE);
        profileDTO = save(profileDTO);
        return of(profileDTO);
    }

    public MeDTO updateMe(MeDTO me) {
        ProfileDTO profileDTO = findCurrentCustomer();
        profileDTO.setFirstName(me.getFirstName());
        profileDTO.setLastName(me.getLastName());
        profileDTO.setImage(me.getImage());
        profileDTO.setGender(me.getGender());
        save(profileDTO);
        return getMe();
    }

    @Transactional(readOnly = true)
    public MeDTO getMe() {
        ProfileDTO profileDTO = findCurrentCustomer();
        MeDTO me = MeDTO.createWith(profileDTO);
        // TODO: 06.04.2022  find exchanges for current profile and set them;
        // me.setExchanges();
        return me;
    }

}
