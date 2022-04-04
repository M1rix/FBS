package uz.apextech.fbs.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.apextech.fbs.repository.ExchangeRepository;
import uz.apextech.fbs.service.ExchangeQueryService;
import uz.apextech.fbs.service.ExchangeService;
import uz.apextech.fbs.service.criteria.ExchangeCriteria;
import uz.apextech.fbs.service.dto.ExchangeDTO;
import uz.apextech.fbs.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.apextech.fbs.domain.Exchange}.
 */
@RestController
@RequestMapping("/api")
public class ExchangeResource {

    private final Logger log = LoggerFactory.getLogger(ExchangeResource.class);

    private static final String ENTITY_NAME = "exchange";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExchangeService exchangeService;

    private final ExchangeRepository exchangeRepository;

    private final ExchangeQueryService exchangeQueryService;

    public ExchangeResource(
        ExchangeService exchangeService,
        ExchangeRepository exchangeRepository,
        ExchangeQueryService exchangeQueryService
    ) {
        this.exchangeService = exchangeService;
        this.exchangeRepository = exchangeRepository;
        this.exchangeQueryService = exchangeQueryService;
    }

    /**
     * {@code POST  /exchanges} : Create a new exchange.
     *
     * @param exchangeDTO the exchangeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new exchangeDTO, or with status {@code 400 (Bad Request)} if the exchange has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/exchanges")
    public ResponseEntity<ExchangeDTO> createExchange(@Valid @RequestBody ExchangeDTO exchangeDTO) throws URISyntaxException {
        log.debug("REST request to save Exchange : {}", exchangeDTO);
        if (exchangeDTO.getId() != null) {
            throw new BadRequestAlertException("A new exchange cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ExchangeDTO result = exchangeService.save(exchangeDTO);
        return ResponseEntity
            .created(new URI("/api/exchanges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /exchanges/:id} : Updates an existing exchange.
     *
     * @param id the id of the exchangeDTO to save.
     * @param exchangeDTO the exchangeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exchangeDTO,
     * or with status {@code 400 (Bad Request)} if the exchangeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exchangeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/exchanges/{id}")
    public ResponseEntity<ExchangeDTO> updateExchange(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExchangeDTO exchangeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Exchange : {}, {}", id, exchangeDTO);
        if (exchangeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exchangeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!exchangeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ExchangeDTO result = exchangeService.save(exchangeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exchangeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /exchanges/:id} : Partial updates given fields of an existing exchange, field will ignore if it is null
     *
     * @param id the id of the exchangeDTO to save.
     * @param exchangeDTO the exchangeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exchangeDTO,
     * or with status {@code 400 (Bad Request)} if the exchangeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the exchangeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the exchangeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/exchanges/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExchangeDTO> partialUpdateExchange(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExchangeDTO exchangeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Exchange partially : {}, {}", id, exchangeDTO);
        if (exchangeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exchangeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!exchangeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExchangeDTO> result = exchangeService.partialUpdate(exchangeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exchangeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /exchanges} : get all the exchanges.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of exchanges in body.
     */
    @GetMapping("/exchanges")
    public ResponseEntity<List<ExchangeDTO>> getAllExchanges(
        ExchangeCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Exchanges by criteria: {}", criteria);
        Page<ExchangeDTO> page = exchangeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /exchanges/count} : count all the exchanges.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/exchanges/count")
    public ResponseEntity<Long> countExchanges(ExchangeCriteria criteria) {
        log.debug("REST request to count Exchanges by criteria: {}", criteria);
        return ResponseEntity.ok().body(exchangeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /exchanges/:id} : get the "id" exchange.
     *
     * @param id the id of the exchangeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exchangeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/exchanges/{id}")
    public ResponseEntity<ExchangeDTO> getExchange(@PathVariable Long id) {
        log.debug("REST request to get Exchange : {}", id);
        Optional<ExchangeDTO> exchangeDTO = exchangeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(exchangeDTO);
    }

    /**
     * {@code DELETE  /exchanges/:id} : delete the "id" exchange.
     *
     * @param id the id of the exchangeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/exchanges/{id}")
    public ResponseEntity<Void> deleteExchange(@PathVariable Long id) {
        log.debug("REST request to delete Exchange : {}", id);
        exchangeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
