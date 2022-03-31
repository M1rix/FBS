package uz.apextech.fbs.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import uz.apextech.fbs.domain.*; // for static metamodels
import uz.apextech.fbs.domain.Exchange;
import uz.apextech.fbs.repository.ExchangeRepository;
import uz.apextech.fbs.service.criteria.ExchangeCriteria;
import uz.apextech.fbs.service.dto.ExchangeDTO;
import uz.apextech.fbs.service.mapper.ExchangeMapper;

/**
 * Service for executing complex queries for {@link Exchange} entities in the database.
 * The main input is a {@link ExchangeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ExchangeDTO} or a {@link Page} of {@link ExchangeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExchangeQueryService extends QueryService<Exchange> {

    private final Logger log = LoggerFactory.getLogger(ExchangeQueryService.class);

    private final ExchangeRepository exchangeRepository;

    private final ExchangeMapper exchangeMapper;

    public ExchangeQueryService(ExchangeRepository exchangeRepository, ExchangeMapper exchangeMapper) {
        this.exchangeRepository = exchangeRepository;
        this.exchangeMapper = exchangeMapper;
    }

    /**
     * Return a {@link List} of {@link ExchangeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ExchangeDTO> findByCriteria(ExchangeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Exchange> specification = createSpecification(criteria);
        return exchangeMapper.toDto(exchangeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ExchangeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ExchangeDTO> findByCriteria(ExchangeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Exchange> specification = createSpecification(criteria);
        return exchangeRepository.findAll(specification, page).map(exchangeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExchangeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Exchange> specification = createSpecification(criteria);
        return exchangeRepository.count(specification);
    }

    /**
     * Function to convert {@link ExchangeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Exchange> createSpecification(ExchangeCriteria criteria) {
        Specification<Exchange> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Exchange_.id));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Exchange_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Exchange_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Exchange_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Exchange_.lastModifiedDate));
            }
            if (criteria.getFormProfileId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFormProfileId(),
                            root -> root.join(Exchange_.formProfile, JoinType.LEFT).get(Profile_.id)
                        )
                    );
            }
            if (criteria.getToProfileId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getToProfileId(),
                            root -> root.join(Exchange_.toProfile, JoinType.LEFT).get(Profile_.id)
                        )
                    );
            }
            if (criteria.getBookId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getBookId(), root -> root.join(Exchange_.book, JoinType.LEFT).get(Book_.id))
                    );
            }
        }
        return specification;
    }
}
