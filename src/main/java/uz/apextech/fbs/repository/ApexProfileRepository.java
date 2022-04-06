package uz.apextech.fbs.repository;

import org.springframework.stereotype.Repository;
import uz.apextech.fbs.domain.Profile;

import java.util.Optional;

@Repository
public interface ApexProfileRepository extends ProfileRepository {

    Optional<Profile> findOneByPhone(String phone);
}
