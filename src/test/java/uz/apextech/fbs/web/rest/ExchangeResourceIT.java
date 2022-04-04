package uz.apextech.fbs.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.apextech.fbs.IntegrationTest;
import uz.apextech.fbs.domain.Book;
import uz.apextech.fbs.domain.Exchange;
import uz.apextech.fbs.domain.Profile;
import uz.apextech.fbs.repository.ExchangeRepository;
import uz.apextech.fbs.service.criteria.ExchangeCriteria;
import uz.apextech.fbs.service.dto.ExchangeDTO;
import uz.apextech.fbs.service.mapper.ExchangeMapper;

/**
 * Integration tests for the {@link ExchangeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExchangeResourceIT {

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/exchanges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private ExchangeMapper exchangeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExchangeMockMvc;

    private Exchange exchange;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exchange createEntity(EntityManager em) {
        Exchange exchange = new Exchange()
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return exchange;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exchange createUpdatedEntity(EntityManager em) {
        Exchange exchange = new Exchange()
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return exchange;
    }

    @BeforeEach
    public void initTest() {
        exchange = createEntity(em);
    }

    @Test
    @Transactional
    void createExchange() throws Exception {
        int databaseSizeBeforeCreate = exchangeRepository.findAll().size();
        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);
        restExchangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exchangeDTO)))
            .andExpect(status().isCreated());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeCreate + 1);
        Exchange testExchange = exchangeList.get(exchangeList.size() - 1);
        assertThat(testExchange.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testExchange.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testExchange.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testExchange.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createExchangeWithExistingId() throws Exception {
        // Create the Exchange with an existing ID
        exchange.setId(1L);
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        int databaseSizeBeforeCreate = exchangeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExchangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exchangeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = exchangeRepository.findAll().size();
        // set the field null
        exchange.setCreatedBy(null);

        // Create the Exchange, which fails.
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        restExchangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exchangeDTO)))
            .andExpect(status().isBadRequest());

        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExchanges() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList
        restExchangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exchange.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getExchange() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get the exchange
        restExchangeMockMvc
            .perform(get(ENTITY_API_URL_ID, exchange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exchange.getId().intValue()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getExchangesByIdFiltering() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        Long id = exchange.getId();

        defaultExchangeShouldBeFound("id.equals=" + id);
        defaultExchangeShouldNotBeFound("id.notEquals=" + id);

        defaultExchangeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultExchangeShouldNotBeFound("id.greaterThan=" + id);

        defaultExchangeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultExchangeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdBy equals to DEFAULT_CREATED_BY
        defaultExchangeShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the exchangeList where createdBy equals to UPDATED_CREATED_BY
        defaultExchangeShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdBy not equals to DEFAULT_CREATED_BY
        defaultExchangeShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the exchangeList where createdBy not equals to UPDATED_CREATED_BY
        defaultExchangeShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultExchangeShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the exchangeList where createdBy equals to UPDATED_CREATED_BY
        defaultExchangeShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdBy is not null
        defaultExchangeShouldBeFound("createdBy.specified=true");

        // Get all the exchangeList where createdBy is null
        defaultExchangeShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdBy contains DEFAULT_CREATED_BY
        defaultExchangeShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the exchangeList where createdBy contains UPDATED_CREATED_BY
        defaultExchangeShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdBy does not contain DEFAULT_CREATED_BY
        defaultExchangeShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the exchangeList where createdBy does not contain UPDATED_CREATED_BY
        defaultExchangeShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdDate equals to DEFAULT_CREATED_DATE
        defaultExchangeShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the exchangeList where createdDate equals to UPDATED_CREATED_DATE
        defaultExchangeShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultExchangeShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the exchangeList where createdDate not equals to UPDATED_CREATED_DATE
        defaultExchangeShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultExchangeShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the exchangeList where createdDate equals to UPDATED_CREATED_DATE
        defaultExchangeShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllExchangesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where createdDate is not null
        defaultExchangeShouldBeFound("createdDate.specified=true");

        // Get all the exchangeList where createdDate is null
        defaultExchangeShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultExchangeShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the exchangeList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultExchangeShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultExchangeShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the exchangeList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultExchangeShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultExchangeShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the exchangeList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultExchangeShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedBy is not null
        defaultExchangeShouldBeFound("lastModifiedBy.specified=true");

        // Get all the exchangeList where lastModifiedBy is null
        defaultExchangeShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultExchangeShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the exchangeList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultExchangeShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultExchangeShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the exchangeList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultExchangeShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultExchangeShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the exchangeList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultExchangeShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedDate not equals to DEFAULT_LAST_MODIFIED_DATE
        defaultExchangeShouldNotBeFound("lastModifiedDate.notEquals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the exchangeList where lastModifiedDate not equals to UPDATED_LAST_MODIFIED_DATE
        defaultExchangeShouldBeFound("lastModifiedDate.notEquals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultExchangeShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the exchangeList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultExchangeShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllExchangesByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        // Get all the exchangeList where lastModifiedDate is not null
        defaultExchangeShouldBeFound("lastModifiedDate.specified=true");

        // Get all the exchangeList where lastModifiedDate is null
        defaultExchangeShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllExchangesByFormProfileIsEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);
        Profile formProfile;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            formProfile = ProfileResourceIT.createEntity(em);
            em.persist(formProfile);
            em.flush();
        } else {
            formProfile = TestUtil.findAll(em, Profile.class).get(0);
        }
        em.persist(formProfile);
        em.flush();
        exchange.setFormProfile(formProfile);
        exchangeRepository.saveAndFlush(exchange);
        Long formProfileId = formProfile.getId();

        // Get all the exchangeList where formProfile equals to formProfileId
        defaultExchangeShouldBeFound("formProfileId.equals=" + formProfileId);

        // Get all the exchangeList where formProfile equals to (formProfileId + 1)
        defaultExchangeShouldNotBeFound("formProfileId.equals=" + (formProfileId + 1));
    }

    @Test
    @Transactional
    void getAllExchangesByToProfileIsEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);
        Profile toProfile;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            toProfile = ProfileResourceIT.createEntity(em);
            em.persist(toProfile);
            em.flush();
        } else {
            toProfile = TestUtil.findAll(em, Profile.class).get(0);
        }
        em.persist(toProfile);
        em.flush();
        exchange.setToProfile(toProfile);
        exchangeRepository.saveAndFlush(exchange);
        Long toProfileId = toProfile.getId();

        // Get all the exchangeList where toProfile equals to toProfileId
        defaultExchangeShouldBeFound("toProfileId.equals=" + toProfileId);

        // Get all the exchangeList where toProfile equals to (toProfileId + 1)
        defaultExchangeShouldNotBeFound("toProfileId.equals=" + (toProfileId + 1));
    }

    @Test
    @Transactional
    void getAllExchangesByBookIsEqualToSomething() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            book = BookResourceIT.createEntity(em);
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        exchange.setBook(book);
        exchangeRepository.saveAndFlush(exchange);
        Long bookId = book.getId();

        // Get all the exchangeList where book equals to bookId
        defaultExchangeShouldBeFound("bookId.equals=" + bookId);

        // Get all the exchangeList where book equals to (bookId + 1)
        defaultExchangeShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExchangeShouldBeFound(String filter) throws Exception {
        restExchangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exchange.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restExchangeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultExchangeShouldNotBeFound(String filter) throws Exception {
        restExchangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restExchangeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingExchange() throws Exception {
        // Get the exchange
        restExchangeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExchange() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();

        // Update the exchange
        Exchange updatedExchange = exchangeRepository.findById(exchange.getId()).get();
        // Disconnect from session so that the updates on updatedExchange are not directly saved in db
        em.detach(updatedExchange);
        updatedExchange
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(updatedExchange);

        restExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exchangeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
        Exchange testExchange = exchangeList.get(exchangeList.size() - 1);
        assertThat(testExchange.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testExchange.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testExchange.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testExchange.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exchangeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exchangeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExchangeWithPatch() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();

        // Update the exchange using partial update
        Exchange partialUpdatedExchange = new Exchange();
        partialUpdatedExchange.setId(exchange.getId());

        partialUpdatedExchange.createdDate(UPDATED_CREATED_DATE).lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExchange.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExchange))
            )
            .andExpect(status().isOk());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
        Exchange testExchange = exchangeList.get(exchangeList.size() - 1);
        assertThat(testExchange.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testExchange.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testExchange.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testExchange.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateExchangeWithPatch() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();

        // Update the exchange using partial update
        Exchange partialUpdatedExchange = new Exchange();
        partialUpdatedExchange.setId(exchange.getId());

        partialUpdatedExchange
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExchange.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExchange))
            )
            .andExpect(status().isOk());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
        Exchange testExchange = exchangeList.get(exchangeList.size() - 1);
        assertThat(testExchange.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testExchange.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testExchange.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testExchange.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, exchangeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExchange() throws Exception {
        int databaseSizeBeforeUpdate = exchangeRepository.findAll().size();
        exchange.setId(count.incrementAndGet());

        // Create the Exchange
        ExchangeDTO exchangeDTO = exchangeMapper.toDto(exchange);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExchangeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(exchangeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exchange in the database
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExchange() throws Exception {
        // Initialize the database
        exchangeRepository.saveAndFlush(exchange);

        int databaseSizeBeforeDelete = exchangeRepository.findAll().size();

        // Delete the exchange
        restExchangeMockMvc
            .perform(delete(ENTITY_API_URL_ID, exchange.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Exchange> exchangeList = exchangeRepository.findAll();
        assertThat(exchangeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
