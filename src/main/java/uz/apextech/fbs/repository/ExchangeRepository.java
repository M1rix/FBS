package uz.apextech.fbs.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.apextech.fbs.domain.Exchange;

/**
 * Spring Data SQL repository for the Exchange entity.
 */
@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long>, JpaSpecificationExecutor<Exchange> {
    default Optional<Exchange> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Exchange> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Exchange> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct exchange from Exchange exchange left join fetch exchange.book",
        countQuery = "select count(distinct exchange) from Exchange exchange"
    )
    Page<Exchange> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct exchange from Exchange exchange left join fetch exchange.book")
    List<Exchange> findAllWithToOneRelationships();

    @Query("select exchange from Exchange exchange left join fetch exchange.book where exchange.id =:id")
    Optional<Exchange> findOneWithToOneRelationships(@Param("id") Long id);
}
