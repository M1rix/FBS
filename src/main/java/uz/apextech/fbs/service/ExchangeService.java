package uz.apextech.fbs.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.apextech.fbs.domain.Exchange;
import uz.apextech.fbs.repository.ExchangeRepository;
import uz.apextech.fbs.service.dto.ExchangeDTO;
import uz.apextech.fbs.service.mapper.ExchangeMapper;

/**
 * Service Implementation for managing {@link Exchange}.
 */
@Service
@Transactional
public class ExchangeService {

    private final Logger log = LoggerFactory.getLogger(ExchangeService.class);

    private final ExchangeRepository exchangeRepository;

    private final ExchangeMapper exchangeMapper;

    public ExchangeService(ExchangeRepository exchangeRepository, ExchangeMapper exchangeMapper) {
        this.exchangeRepository = exchangeRepository;
        this.exchangeMapper = exchangeMapper;
    }

    /**
     * Save a exchange.
     *
     * @param exchangeDTO the entity to save.
     * @return the persisted entity.
     */
    public ExchangeDTO save(ExchangeDTO exchangeDTO) {
        log.debug("Request to save Exchange : {}", exchangeDTO);
        Exchange exchange = exchangeMapper.toEntity(exchangeDTO);
        exchange = exchangeRepository.save(exchange);
        return exchangeMapper.toDto(exchange);
    }

    /**
     * Partially update a exchange.
     *
     * @param exchangeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ExchangeDTO> partialUpdate(ExchangeDTO exchangeDTO) {
        log.debug("Request to partially update Exchange : {}", exchangeDTO);

        return exchangeRepository
            .findById(exchangeDTO.getId())
            .map(existingExchange -> {
                exchangeMapper.partialUpdate(existingExchange, exchangeDTO);

                return existingExchange;
            })
            .map(exchangeRepository::save)
            .map(exchangeMapper::toDto);
    }

    /**
     * Get all the exchanges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ExchangeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Exchanges");
        return exchangeRepository.findAll(pageable).map(exchangeMapper::toDto);
    }

    /**
     * Get one exchange by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ExchangeDTO> findOne(Long id) {
        log.debug("Request to get Exchange : {}", id);
        return exchangeRepository.findById(id).map(exchangeMapper::toDto);
    }

    /**
     * Delete the exchange by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Exchange : {}", id);
        exchangeRepository.deleteById(id);
    }
}
