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
import org.springframework.util.Base64Utils;
import uz.apextech.fbs.IntegrationTest;
import uz.apextech.fbs.domain.Image;
import uz.apextech.fbs.repository.ImageRepository;
import uz.apextech.fbs.service.criteria.ImageCriteria;
import uz.apextech.fbs.service.dto.ImageDTO;
import uz.apextech.fbs.service.mapper.ImageMapper;

/**
 * Integration tests for the {@link ImageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ImageResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_DATA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DATA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DATA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DATA_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImageMockMvc;

    private Image image;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createEntity(EntityManager em) {
        Image image = new Image()
            .name(DEFAULT_NAME)
            .data(DEFAULT_DATA)
            .dataContentType(DEFAULT_DATA_CONTENT_TYPE)
            .url(DEFAULT_URL)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return image;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createUpdatedEntity(EntityManager em) {
        Image image = new Image()
            .name(UPDATED_NAME)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .url(UPDATED_URL)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return image;
    }

    @BeforeEach
    public void initTest() {
        image = createEntity(em);
    }

    @Test
    @Transactional
    void createImage() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().size();
        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);
        restImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isCreated());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate + 1);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testImage.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testImage.getDataContentType()).isEqualTo(DEFAULT_DATA_CONTENT_TYPE);
        assertThat(testImage.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testImage.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testImage.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testImage.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testImage.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createImageWithExistingId() throws Exception {
        // Create the Image with an existing ID
        image.setId(1L);
        ImageDTO imageDTO = imageMapper.toDto(image);

        int databaseSizeBeforeCreate = imageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageRepository.findAll().size();
        // set the field null
        image.setCreatedBy(null);

        // Create the Image, which fails.
        ImageDTO imageDTO = imageMapper.toDto(image);

        restImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isBadRequest());

        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllImages() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList
        restImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].dataContentType").value(hasItem(DEFAULT_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA))))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get the image
        restImageMockMvc
            .perform(get(ENTITY_API_URL_ID, image.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(image.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.dataContentType").value(DEFAULT_DATA_CONTENT_TYPE))
            .andExpect(jsonPath("$.data").value(Base64Utils.encodeToString(DEFAULT_DATA)))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getImagesByIdFiltering() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        Long id = image.getId();

        defaultImageShouldBeFound("id.equals=" + id);
        defaultImageShouldNotBeFound("id.notEquals=" + id);

        defaultImageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultImageShouldNotBeFound("id.greaterThan=" + id);

        defaultImageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultImageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllImagesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where name equals to DEFAULT_NAME
        defaultImageShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the imageList where name equals to UPDATED_NAME
        defaultImageShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllImagesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where name not equals to DEFAULT_NAME
        defaultImageShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the imageList where name not equals to UPDATED_NAME
        defaultImageShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllImagesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where name in DEFAULT_NAME or UPDATED_NAME
        defaultImageShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the imageList where name equals to UPDATED_NAME
        defaultImageShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllImagesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where name is not null
        defaultImageShouldBeFound("name.specified=true");

        // Get all the imageList where name is null
        defaultImageShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllImagesByNameContainsSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where name contains DEFAULT_NAME
        defaultImageShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the imageList where name contains UPDATED_NAME
        defaultImageShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllImagesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where name does not contain DEFAULT_NAME
        defaultImageShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the imageList where name does not contain UPDATED_NAME
        defaultImageShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllImagesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where url equals to DEFAULT_URL
        defaultImageShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the imageList where url equals to UPDATED_URL
        defaultImageShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllImagesByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where url not equals to DEFAULT_URL
        defaultImageShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the imageList where url not equals to UPDATED_URL
        defaultImageShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllImagesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where url in DEFAULT_URL or UPDATED_URL
        defaultImageShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the imageList where url equals to UPDATED_URL
        defaultImageShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllImagesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where url is not null
        defaultImageShouldBeFound("url.specified=true");

        // Get all the imageList where url is null
        defaultImageShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllImagesByUrlContainsSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where url contains DEFAULT_URL
        defaultImageShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the imageList where url contains UPDATED_URL
        defaultImageShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllImagesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where url does not contain DEFAULT_URL
        defaultImageShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the imageList where url does not contain UPDATED_URL
        defaultImageShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdBy equals to DEFAULT_CREATED_BY
        defaultImageShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the imageList where createdBy equals to UPDATED_CREATED_BY
        defaultImageShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdBy not equals to DEFAULT_CREATED_BY
        defaultImageShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the imageList where createdBy not equals to UPDATED_CREATED_BY
        defaultImageShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultImageShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the imageList where createdBy equals to UPDATED_CREATED_BY
        defaultImageShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdBy is not null
        defaultImageShouldBeFound("createdBy.specified=true");

        // Get all the imageList where createdBy is null
        defaultImageShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllImagesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdBy contains DEFAULT_CREATED_BY
        defaultImageShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the imageList where createdBy contains UPDATED_CREATED_BY
        defaultImageShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdBy does not contain DEFAULT_CREATED_BY
        defaultImageShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the imageList where createdBy does not contain UPDATED_CREATED_BY
        defaultImageShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdDate equals to DEFAULT_CREATED_DATE
        defaultImageShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the imageList where createdDate equals to UPDATED_CREATED_DATE
        defaultImageShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultImageShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the imageList where createdDate not equals to UPDATED_CREATED_DATE
        defaultImageShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultImageShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the imageList where createdDate equals to UPDATED_CREATED_DATE
        defaultImageShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllImagesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where createdDate is not null
        defaultImageShouldBeFound("createdDate.specified=true");

        // Get all the imageList where createdDate is null
        defaultImageShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultImageShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the imageList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultImageShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultImageShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the imageList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultImageShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultImageShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the imageList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultImageShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedBy is not null
        defaultImageShouldBeFound("lastModifiedBy.specified=true");

        // Get all the imageList where lastModifiedBy is null
        defaultImageShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultImageShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the imageList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultImageShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultImageShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the imageList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultImageShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultImageShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the imageList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultImageShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedDate not equals to DEFAULT_LAST_MODIFIED_DATE
        defaultImageShouldNotBeFound("lastModifiedDate.notEquals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the imageList where lastModifiedDate not equals to UPDATED_LAST_MODIFIED_DATE
        defaultImageShouldBeFound("lastModifiedDate.notEquals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultImageShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the imageList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultImageShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllImagesByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where lastModifiedDate is not null
        defaultImageShouldBeFound("lastModifiedDate.specified=true");

        // Get all the imageList where lastModifiedDate is null
        defaultImageShouldNotBeFound("lastModifiedDate.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImageShouldBeFound(String filter) throws Exception {
        restImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].dataContentType").value(hasItem(DEFAULT_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA))))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImageShouldNotBeFound(String filter) throws Exception {
        restImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingImage() throws Exception {
        // Get the image
        restImageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Update the image
        Image updatedImage = imageRepository.findById(image.getId()).get();
        // Disconnect from session so that the updates on updatedImage are not directly saved in db
        em.detach(updatedImage);
        updatedImage
            .name(UPDATED_NAME)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .url(UPDATED_URL)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        ImageDTO imageDTO = imageMapper.toDto(updatedImage);

        restImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, imageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(imageDTO))
            )
            .andExpect(status().isOk());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testImage.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testImage.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
        assertThat(testImage.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testImage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testImage.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testImage.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testImage.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, imageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(imageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(imageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImageWithPatch() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Update the image using partial update
        Image partialUpdatedImage = new Image();
        partialUpdatedImage.setId(image.getId());

        partialUpdatedImage
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .url(UPDATED_URL)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedImage))
            )
            .andExpect(status().isOk());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testImage.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testImage.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
        assertThat(testImage.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testImage.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testImage.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testImage.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testImage.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateImageWithPatch() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Update the image using partial update
        Image partialUpdatedImage = new Image();
        partialUpdatedImage.setId(image.getId());

        partialUpdatedImage
            .name(UPDATED_NAME)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .url(UPDATED_URL)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedImage))
            )
            .andExpect(status().isOk());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testImage.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testImage.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
        assertThat(testImage.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testImage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testImage.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testImage.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testImage.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, imageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(imageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(imageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();
        image.setId(count.incrementAndGet());

        // Create the Image
        ImageDTO imageDTO = imageMapper.toDto(image);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        int databaseSizeBeforeDelete = imageRepository.findAll().size();

        // Delete the image
        restImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, image.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
