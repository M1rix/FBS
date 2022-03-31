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
import uz.apextech.fbs.domain.Profile;
import uz.apextech.fbs.domain.enumeration.Gender;
import uz.apextech.fbs.domain.enumeration.ProfileStatus;
import uz.apextech.fbs.repository.ProfileRepository;
import uz.apextech.fbs.service.criteria.ProfileCriteria;
import uz.apextech.fbs.service.dto.ProfileDTO;
import uz.apextech.fbs.service.mapper.ProfileMapper;

/**
 * Integration tests for the {@link ProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProfileResourceIT {

    private static final String DEFAULT_PHONE = "AAAAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBBBB";

    private static final String DEFAULT_ACCESS_TOKEN =
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_ACCESS_TOKEN =
        "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_LANG_KEY = "AAAAAA";
    private static final String UPDATED_LANG_KEY = "BBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final Double DEFAULT_SCORE = 0D;
    private static final Double UPDATED_SCORE = 1D;
    private static final Double SMALLER_SCORE = 0D - 1D;

    private static final Long DEFAULT_LIKES = 0L;
    private static final Long UPDATED_LIKES = 1L;
    private static final Long SMALLER_LIKES = 0L - 1L;

    private static final ProfileStatus DEFAULT_STATUS = ProfileStatus.ACTIVE;
    private static final ProfileStatus UPDATED_STATUS = ProfileStatus.INACTIVE;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfileMockMvc;

    private Profile profile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createEntity(EntityManager em) {
        Profile profile = new Profile()
            .phone(DEFAULT_PHONE)
            .accessToken(DEFAULT_ACCESS_TOKEN)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .imageUrl(DEFAULT_IMAGE_URL)
            .langKey(DEFAULT_LANG_KEY)
            .gender(DEFAULT_GENDER)
            .score(DEFAULT_SCORE)
            .likes(DEFAULT_LIKES)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return profile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createUpdatedEntity(EntityManager em) {
        Profile profile = new Profile()
            .phone(UPDATED_PHONE)
            .accessToken(UPDATED_ACCESS_TOKEN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .imageUrl(UPDATED_IMAGE_URL)
            .langKey(UPDATED_LANG_KEY)
            .gender(UPDATED_GENDER)
            .score(UPDATED_SCORE)
            .likes(UPDATED_LIKES)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return profile;
    }

    @BeforeEach
    public void initTest() {
        profile = createEntity(em);
    }

    @Test
    @Transactional
    void createProfile() throws Exception {
        int databaseSizeBeforeCreate = profileRepository.findAll().size();
        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);
        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isCreated());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeCreate + 1);
        Profile testProfile = profileList.get(profileList.size() - 1);
        assertThat(testProfile.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testProfile.getAccessToken()).isEqualTo(DEFAULT_ACCESS_TOKEN);
        assertThat(testProfile.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testProfile.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testProfile.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProfile.getLangKey()).isEqualTo(DEFAULT_LANG_KEY);
        assertThat(testProfile.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testProfile.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testProfile.getLikes()).isEqualTo(DEFAULT_LIKES);
        assertThat(testProfile.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProfile.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testProfile.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testProfile.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testProfile.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createProfileWithExistingId() throws Exception {
        // Create the Profile with an existing ID
        profile.setId(1L);
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        int databaseSizeBeforeCreate = profileRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = profileRepository.findAll().size();
        // set the field null
        profile.setPhone(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAccessTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = profileRepository.findAll().size();
        // set the field null
        profile.setAccessToken(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkImageUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = profileRepository.findAll().size();
        // set the field null
        profile.setImageUrl(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLangKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = profileRepository.findAll().size();
        // set the field null
        profile.setLangKey(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = profileRepository.findAll().size();
        // set the field null
        profile.setStatus(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = profileRepository.findAll().size();
        // set the field null
        profile.setCreatedBy(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        restProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProfiles() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profile.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].accessToken").value(hasItem(DEFAULT_ACCESS_TOKEN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE.doubleValue())))
            .andExpect(jsonPath("$.[*].likes").value(hasItem(DEFAULT_LIKES.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getProfile() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get the profile
        restProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, profile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profile.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.accessToken").value(DEFAULT_ACCESS_TOKEN))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANG_KEY))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE.doubleValue()))
            .andExpect(jsonPath("$.likes").value(DEFAULT_LIKES.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getProfilesByIdFiltering() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        Long id = profile.getId();

        defaultProfileShouldBeFound("id.equals=" + id);
        defaultProfileShouldNotBeFound("id.notEquals=" + id);

        defaultProfileShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProfileShouldNotBeFound("id.greaterThan=" + id);

        defaultProfileShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProfileShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProfilesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where phone equals to DEFAULT_PHONE
        defaultProfileShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the profileList where phone equals to UPDATED_PHONE
        defaultProfileShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllProfilesByPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where phone not equals to DEFAULT_PHONE
        defaultProfileShouldNotBeFound("phone.notEquals=" + DEFAULT_PHONE);

        // Get all the profileList where phone not equals to UPDATED_PHONE
        defaultProfileShouldBeFound("phone.notEquals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllProfilesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultProfileShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the profileList where phone equals to UPDATED_PHONE
        defaultProfileShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllProfilesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where phone is not null
        defaultProfileShouldBeFound("phone.specified=true");

        // Get all the profileList where phone is null
        defaultProfileShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where phone contains DEFAULT_PHONE
        defaultProfileShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the profileList where phone contains UPDATED_PHONE
        defaultProfileShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllProfilesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where phone does not contain DEFAULT_PHONE
        defaultProfileShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the profileList where phone does not contain UPDATED_PHONE
        defaultProfileShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllProfilesByAccessTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where accessToken equals to DEFAULT_ACCESS_TOKEN
        defaultProfileShouldBeFound("accessToken.equals=" + DEFAULT_ACCESS_TOKEN);

        // Get all the profileList where accessToken equals to UPDATED_ACCESS_TOKEN
        defaultProfileShouldNotBeFound("accessToken.equals=" + UPDATED_ACCESS_TOKEN);
    }

    @Test
    @Transactional
    void getAllProfilesByAccessTokenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where accessToken not equals to DEFAULT_ACCESS_TOKEN
        defaultProfileShouldNotBeFound("accessToken.notEquals=" + DEFAULT_ACCESS_TOKEN);

        // Get all the profileList where accessToken not equals to UPDATED_ACCESS_TOKEN
        defaultProfileShouldBeFound("accessToken.notEquals=" + UPDATED_ACCESS_TOKEN);
    }

    @Test
    @Transactional
    void getAllProfilesByAccessTokenIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where accessToken in DEFAULT_ACCESS_TOKEN or UPDATED_ACCESS_TOKEN
        defaultProfileShouldBeFound("accessToken.in=" + DEFAULT_ACCESS_TOKEN + "," + UPDATED_ACCESS_TOKEN);

        // Get all the profileList where accessToken equals to UPDATED_ACCESS_TOKEN
        defaultProfileShouldNotBeFound("accessToken.in=" + UPDATED_ACCESS_TOKEN);
    }

    @Test
    @Transactional
    void getAllProfilesByAccessTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where accessToken is not null
        defaultProfileShouldBeFound("accessToken.specified=true");

        // Get all the profileList where accessToken is null
        defaultProfileShouldNotBeFound("accessToken.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByAccessTokenContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where accessToken contains DEFAULT_ACCESS_TOKEN
        defaultProfileShouldBeFound("accessToken.contains=" + DEFAULT_ACCESS_TOKEN);

        // Get all the profileList where accessToken contains UPDATED_ACCESS_TOKEN
        defaultProfileShouldNotBeFound("accessToken.contains=" + UPDATED_ACCESS_TOKEN);
    }

    @Test
    @Transactional
    void getAllProfilesByAccessTokenNotContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where accessToken does not contain DEFAULT_ACCESS_TOKEN
        defaultProfileShouldNotBeFound("accessToken.doesNotContain=" + DEFAULT_ACCESS_TOKEN);

        // Get all the profileList where accessToken does not contain UPDATED_ACCESS_TOKEN
        defaultProfileShouldBeFound("accessToken.doesNotContain=" + UPDATED_ACCESS_TOKEN);
    }

    @Test
    @Transactional
    void getAllProfilesByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where firstName equals to DEFAULT_FIRST_NAME
        defaultProfileShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the profileList where firstName equals to UPDATED_FIRST_NAME
        defaultProfileShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByFirstNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where firstName not equals to DEFAULT_FIRST_NAME
        defaultProfileShouldNotBeFound("firstName.notEquals=" + DEFAULT_FIRST_NAME);

        // Get all the profileList where firstName not equals to UPDATED_FIRST_NAME
        defaultProfileShouldBeFound("firstName.notEquals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultProfileShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the profileList where firstName equals to UPDATED_FIRST_NAME
        defaultProfileShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where firstName is not null
        defaultProfileShouldBeFound("firstName.specified=true");

        // Get all the profileList where firstName is null
        defaultProfileShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where firstName contains DEFAULT_FIRST_NAME
        defaultProfileShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the profileList where firstName contains UPDATED_FIRST_NAME
        defaultProfileShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where firstName does not contain DEFAULT_FIRST_NAME
        defaultProfileShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the profileList where firstName does not contain UPDATED_FIRST_NAME
        defaultProfileShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastName equals to DEFAULT_LAST_NAME
        defaultProfileShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the profileList where lastName equals to UPDATED_LAST_NAME
        defaultProfileShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByLastNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastName not equals to DEFAULT_LAST_NAME
        defaultProfileShouldNotBeFound("lastName.notEquals=" + DEFAULT_LAST_NAME);

        // Get all the profileList where lastName not equals to UPDATED_LAST_NAME
        defaultProfileShouldBeFound("lastName.notEquals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultProfileShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the profileList where lastName equals to UPDATED_LAST_NAME
        defaultProfileShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastName is not null
        defaultProfileShouldBeFound("lastName.specified=true");

        // Get all the profileList where lastName is null
        defaultProfileShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByLastNameContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastName contains DEFAULT_LAST_NAME
        defaultProfileShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the profileList where lastName contains UPDATED_LAST_NAME
        defaultProfileShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastName does not contain DEFAULT_LAST_NAME
        defaultProfileShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the profileList where lastName does not contain UPDATED_LAST_NAME
        defaultProfileShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllProfilesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where imageUrl equals to DEFAULT_IMAGE_URL
        defaultProfileShouldBeFound("imageUrl.equals=" + DEFAULT_IMAGE_URL);

        // Get all the profileList where imageUrl equals to UPDATED_IMAGE_URL
        defaultProfileShouldNotBeFound("imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByImageUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where imageUrl not equals to DEFAULT_IMAGE_URL
        defaultProfileShouldNotBeFound("imageUrl.notEquals=" + DEFAULT_IMAGE_URL);

        // Get all the profileList where imageUrl not equals to UPDATED_IMAGE_URL
        defaultProfileShouldBeFound("imageUrl.notEquals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where imageUrl in DEFAULT_IMAGE_URL or UPDATED_IMAGE_URL
        defaultProfileShouldBeFound("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL);

        // Get all the profileList where imageUrl equals to UPDATED_IMAGE_URL
        defaultProfileShouldNotBeFound("imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where imageUrl is not null
        defaultProfileShouldBeFound("imageUrl.specified=true");

        // Get all the profileList where imageUrl is null
        defaultProfileShouldNotBeFound("imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where imageUrl contains DEFAULT_IMAGE_URL
        defaultProfileShouldBeFound("imageUrl.contains=" + DEFAULT_IMAGE_URL);

        // Get all the profileList where imageUrl contains UPDATED_IMAGE_URL
        defaultProfileShouldNotBeFound("imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where imageUrl does not contain DEFAULT_IMAGE_URL
        defaultProfileShouldNotBeFound("imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);

        // Get all the profileList where imageUrl does not contain UPDATED_IMAGE_URL
        defaultProfileShouldBeFound("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByLangKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where langKey equals to DEFAULT_LANG_KEY
        defaultProfileShouldBeFound("langKey.equals=" + DEFAULT_LANG_KEY);

        // Get all the profileList where langKey equals to UPDATED_LANG_KEY
        defaultProfileShouldNotBeFound("langKey.equals=" + UPDATED_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllProfilesByLangKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where langKey not equals to DEFAULT_LANG_KEY
        defaultProfileShouldNotBeFound("langKey.notEquals=" + DEFAULT_LANG_KEY);

        // Get all the profileList where langKey not equals to UPDATED_LANG_KEY
        defaultProfileShouldBeFound("langKey.notEquals=" + UPDATED_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllProfilesByLangKeyIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where langKey in DEFAULT_LANG_KEY or UPDATED_LANG_KEY
        defaultProfileShouldBeFound("langKey.in=" + DEFAULT_LANG_KEY + "," + UPDATED_LANG_KEY);

        // Get all the profileList where langKey equals to UPDATED_LANG_KEY
        defaultProfileShouldNotBeFound("langKey.in=" + UPDATED_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllProfilesByLangKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where langKey is not null
        defaultProfileShouldBeFound("langKey.specified=true");

        // Get all the profileList where langKey is null
        defaultProfileShouldNotBeFound("langKey.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByLangKeyContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where langKey contains DEFAULT_LANG_KEY
        defaultProfileShouldBeFound("langKey.contains=" + DEFAULT_LANG_KEY);

        // Get all the profileList where langKey contains UPDATED_LANG_KEY
        defaultProfileShouldNotBeFound("langKey.contains=" + UPDATED_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllProfilesByLangKeyNotContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where langKey does not contain DEFAULT_LANG_KEY
        defaultProfileShouldNotBeFound("langKey.doesNotContain=" + DEFAULT_LANG_KEY);

        // Get all the profileList where langKey does not contain UPDATED_LANG_KEY
        defaultProfileShouldBeFound("langKey.doesNotContain=" + UPDATED_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllProfilesByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where gender equals to DEFAULT_GENDER
        defaultProfileShouldBeFound("gender.equals=" + DEFAULT_GENDER);

        // Get all the profileList where gender equals to UPDATED_GENDER
        defaultProfileShouldNotBeFound("gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllProfilesByGenderIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where gender not equals to DEFAULT_GENDER
        defaultProfileShouldNotBeFound("gender.notEquals=" + DEFAULT_GENDER);

        // Get all the profileList where gender not equals to UPDATED_GENDER
        defaultProfileShouldBeFound("gender.notEquals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllProfilesByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where gender in DEFAULT_GENDER or UPDATED_GENDER
        defaultProfileShouldBeFound("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER);

        // Get all the profileList where gender equals to UPDATED_GENDER
        defaultProfileShouldNotBeFound("gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllProfilesByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where gender is not null
        defaultProfileShouldBeFound("gender.specified=true");

        // Get all the profileList where gender is null
        defaultProfileShouldNotBeFound("gender.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where score equals to DEFAULT_SCORE
        defaultProfileShouldBeFound("score.equals=" + DEFAULT_SCORE);

        // Get all the profileList where score equals to UPDATED_SCORE
        defaultProfileShouldNotBeFound("score.equals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllProfilesByScoreIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where score not equals to DEFAULT_SCORE
        defaultProfileShouldNotBeFound("score.notEquals=" + DEFAULT_SCORE);

        // Get all the profileList where score not equals to UPDATED_SCORE
        defaultProfileShouldBeFound("score.notEquals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllProfilesByScoreIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where score in DEFAULT_SCORE or UPDATED_SCORE
        defaultProfileShouldBeFound("score.in=" + DEFAULT_SCORE + "," + UPDATED_SCORE);

        // Get all the profileList where score equals to UPDATED_SCORE
        defaultProfileShouldNotBeFound("score.in=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllProfilesByScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where score is not null
        defaultProfileShouldBeFound("score.specified=true");

        // Get all the profileList where score is null
        defaultProfileShouldNotBeFound("score.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where score is greater than or equal to DEFAULT_SCORE
        defaultProfileShouldBeFound("score.greaterThanOrEqual=" + DEFAULT_SCORE);

        // Get all the profileList where score is greater than or equal to (DEFAULT_SCORE + 1)
        defaultProfileShouldNotBeFound("score.greaterThanOrEqual=" + (DEFAULT_SCORE + 1));
    }

    @Test
    @Transactional
    void getAllProfilesByScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where score is less than or equal to DEFAULT_SCORE
        defaultProfileShouldBeFound("score.lessThanOrEqual=" + DEFAULT_SCORE);

        // Get all the profileList where score is less than or equal to SMALLER_SCORE
        defaultProfileShouldNotBeFound("score.lessThanOrEqual=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllProfilesByScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where score is less than DEFAULT_SCORE
        defaultProfileShouldNotBeFound("score.lessThan=" + DEFAULT_SCORE);

        // Get all the profileList where score is less than (DEFAULT_SCORE + 1)
        defaultProfileShouldBeFound("score.lessThan=" + (DEFAULT_SCORE + 1));
    }

    @Test
    @Transactional
    void getAllProfilesByScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where score is greater than DEFAULT_SCORE
        defaultProfileShouldNotBeFound("score.greaterThan=" + DEFAULT_SCORE);

        // Get all the profileList where score is greater than SMALLER_SCORE
        defaultProfileShouldBeFound("score.greaterThan=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllProfilesByLikesIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where likes equals to DEFAULT_LIKES
        defaultProfileShouldBeFound("likes.equals=" + DEFAULT_LIKES);

        // Get all the profileList where likes equals to UPDATED_LIKES
        defaultProfileShouldNotBeFound("likes.equals=" + UPDATED_LIKES);
    }

    @Test
    @Transactional
    void getAllProfilesByLikesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where likes not equals to DEFAULT_LIKES
        defaultProfileShouldNotBeFound("likes.notEquals=" + DEFAULT_LIKES);

        // Get all the profileList where likes not equals to UPDATED_LIKES
        defaultProfileShouldBeFound("likes.notEquals=" + UPDATED_LIKES);
    }

    @Test
    @Transactional
    void getAllProfilesByLikesIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where likes in DEFAULT_LIKES or UPDATED_LIKES
        defaultProfileShouldBeFound("likes.in=" + DEFAULT_LIKES + "," + UPDATED_LIKES);

        // Get all the profileList where likes equals to UPDATED_LIKES
        defaultProfileShouldNotBeFound("likes.in=" + UPDATED_LIKES);
    }

    @Test
    @Transactional
    void getAllProfilesByLikesIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where likes is not null
        defaultProfileShouldBeFound("likes.specified=true");

        // Get all the profileList where likes is null
        defaultProfileShouldNotBeFound("likes.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByLikesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where likes is greater than or equal to DEFAULT_LIKES
        defaultProfileShouldBeFound("likes.greaterThanOrEqual=" + DEFAULT_LIKES);

        // Get all the profileList where likes is greater than or equal to UPDATED_LIKES
        defaultProfileShouldNotBeFound("likes.greaterThanOrEqual=" + UPDATED_LIKES);
    }

    @Test
    @Transactional
    void getAllProfilesByLikesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where likes is less than or equal to DEFAULT_LIKES
        defaultProfileShouldBeFound("likes.lessThanOrEqual=" + DEFAULT_LIKES);

        // Get all the profileList where likes is less than or equal to SMALLER_LIKES
        defaultProfileShouldNotBeFound("likes.lessThanOrEqual=" + SMALLER_LIKES);
    }

    @Test
    @Transactional
    void getAllProfilesByLikesIsLessThanSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where likes is less than DEFAULT_LIKES
        defaultProfileShouldNotBeFound("likes.lessThan=" + DEFAULT_LIKES);

        // Get all the profileList where likes is less than UPDATED_LIKES
        defaultProfileShouldBeFound("likes.lessThan=" + UPDATED_LIKES);
    }

    @Test
    @Transactional
    void getAllProfilesByLikesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where likes is greater than DEFAULT_LIKES
        defaultProfileShouldNotBeFound("likes.greaterThan=" + DEFAULT_LIKES);

        // Get all the profileList where likes is greater than SMALLER_LIKES
        defaultProfileShouldBeFound("likes.greaterThan=" + SMALLER_LIKES);
    }

    @Test
    @Transactional
    void getAllProfilesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where status equals to DEFAULT_STATUS
        defaultProfileShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the profileList where status equals to UPDATED_STATUS
        defaultProfileShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProfilesByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where status not equals to DEFAULT_STATUS
        defaultProfileShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the profileList where status not equals to UPDATED_STATUS
        defaultProfileShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProfilesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultProfileShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the profileList where status equals to UPDATED_STATUS
        defaultProfileShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllProfilesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where status is not null
        defaultProfileShouldBeFound("status.specified=true");

        // Get all the profileList where status is null
        defaultProfileShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdBy equals to DEFAULT_CREATED_BY
        defaultProfileShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the profileList where createdBy equals to UPDATED_CREATED_BY
        defaultProfileShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdBy not equals to DEFAULT_CREATED_BY
        defaultProfileShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the profileList where createdBy not equals to UPDATED_CREATED_BY
        defaultProfileShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultProfileShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the profileList where createdBy equals to UPDATED_CREATED_BY
        defaultProfileShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdBy is not null
        defaultProfileShouldBeFound("createdBy.specified=true");

        // Get all the profileList where createdBy is null
        defaultProfileShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdBy contains DEFAULT_CREATED_BY
        defaultProfileShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the profileList where createdBy contains UPDATED_CREATED_BY
        defaultProfileShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdBy does not contain DEFAULT_CREATED_BY
        defaultProfileShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the profileList where createdBy does not contain UPDATED_CREATED_BY
        defaultProfileShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdDate equals to DEFAULT_CREATED_DATE
        defaultProfileShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the profileList where createdDate equals to UPDATED_CREATED_DATE
        defaultProfileShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultProfileShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the profileList where createdDate not equals to UPDATED_CREATED_DATE
        defaultProfileShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultProfileShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the profileList where createdDate equals to UPDATED_CREATED_DATE
        defaultProfileShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProfilesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where createdDate is not null
        defaultProfileShouldBeFound("createdDate.specified=true");

        // Get all the profileList where createdDate is null
        defaultProfileShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultProfileShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the profileList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultProfileShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultProfileShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the profileList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultProfileShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultProfileShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the profileList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultProfileShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedBy is not null
        defaultProfileShouldBeFound("lastModifiedBy.specified=true");

        // Get all the profileList where lastModifiedBy is null
        defaultProfileShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultProfileShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the profileList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultProfileShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultProfileShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the profileList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultProfileShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultProfileShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the profileList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultProfileShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedDate not equals to DEFAULT_LAST_MODIFIED_DATE
        defaultProfileShouldNotBeFound("lastModifiedDate.notEquals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the profileList where lastModifiedDate not equals to UPDATED_LAST_MODIFIED_DATE
        defaultProfileShouldBeFound("lastModifiedDate.notEquals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultProfileShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the profileList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultProfileShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllProfilesByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        // Get all the profileList where lastModifiedDate is not null
        defaultProfileShouldBeFound("lastModifiedDate.specified=true");

        // Get all the profileList where lastModifiedDate is null
        defaultProfileShouldNotBeFound("lastModifiedDate.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProfileShouldBeFound(String filter) throws Exception {
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profile.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].accessToken").value(hasItem(DEFAULT_ACCESS_TOKEN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE.doubleValue())))
            .andExpect(jsonPath("$.[*].likes").value(hasItem(DEFAULT_LIKES.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProfileShouldNotBeFound(String filter) throws Exception {
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProfile() throws Exception {
        // Get the profile
        restProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProfile() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        int databaseSizeBeforeUpdate = profileRepository.findAll().size();

        // Update the profile
        Profile updatedProfile = profileRepository.findById(profile.getId()).get();
        // Disconnect from session so that the updates on updatedProfile are not directly saved in db
        em.detach(updatedProfile);
        updatedProfile
            .phone(UPDATED_PHONE)
            .accessToken(UPDATED_ACCESS_TOKEN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .imageUrl(UPDATED_IMAGE_URL)
            .langKey(UPDATED_LANG_KEY)
            .gender(UPDATED_GENDER)
            .score(UPDATED_SCORE)
            .likes(UPDATED_LIKES)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        ProfileDTO profileDTO = profileMapper.toDto(updatedProfile);

        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(profileDTO))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
        Profile testProfile = profileList.get(profileList.size() - 1);
        assertThat(testProfile.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testProfile.getAccessToken()).isEqualTo(UPDATED_ACCESS_TOKEN);
        assertThat(testProfile.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testProfile.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testProfile.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProfile.getLangKey()).isEqualTo(UPDATED_LANG_KEY);
        assertThat(testProfile.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testProfile.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testProfile.getLikes()).isEqualTo(UPDATED_LIKES);
        assertThat(testProfile.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProfile.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testProfile.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProfile.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testProfile.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().size();
        profile.setId(count.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().size();
        profile.setId(count.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().size();
        profile.setId(count.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        int databaseSizeBeforeUpdate = profileRepository.findAll().size();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile
            .phone(UPDATED_PHONE)
            .accessToken(UPDATED_ACCESS_TOKEN)
            .firstName(UPDATED_FIRST_NAME)
            .langKey(UPDATED_LANG_KEY)
            .likes(UPDATED_LIKES)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProfile))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
        Profile testProfile = profileList.get(profileList.size() - 1);
        assertThat(testProfile.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testProfile.getAccessToken()).isEqualTo(UPDATED_ACCESS_TOKEN);
        assertThat(testProfile.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testProfile.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testProfile.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProfile.getLangKey()).isEqualTo(UPDATED_LANG_KEY);
        assertThat(testProfile.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testProfile.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testProfile.getLikes()).isEqualTo(UPDATED_LIKES);
        assertThat(testProfile.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProfile.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testProfile.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProfile.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testProfile.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        int databaseSizeBeforeUpdate = profileRepository.findAll().size();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile
            .phone(UPDATED_PHONE)
            .accessToken(UPDATED_ACCESS_TOKEN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .imageUrl(UPDATED_IMAGE_URL)
            .langKey(UPDATED_LANG_KEY)
            .gender(UPDATED_GENDER)
            .score(UPDATED_SCORE)
            .likes(UPDATED_LIKES)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProfile))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
        Profile testProfile = profileList.get(profileList.size() - 1);
        assertThat(testProfile.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testProfile.getAccessToken()).isEqualTo(UPDATED_ACCESS_TOKEN);
        assertThat(testProfile.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testProfile.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testProfile.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProfile.getLangKey()).isEqualTo(UPDATED_LANG_KEY);
        assertThat(testProfile.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testProfile.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testProfile.getLikes()).isEqualTo(UPDATED_LIKES);
        assertThat(testProfile.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProfile.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testProfile.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProfile.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testProfile.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().size();
        profile.setId(count.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().size();
        profile.setId(count.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().size();
        profile.setId(count.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(profileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfile() throws Exception {
        // Initialize the database
        profileRepository.saveAndFlush(profile);

        int databaseSizeBeforeDelete = profileRepository.findAll().size();

        // Delete the profile
        restProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, profile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
