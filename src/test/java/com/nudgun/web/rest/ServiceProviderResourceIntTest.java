package com.nudgun.web.rest;

import com.nudgun.NudgunApp;

import com.nudgun.domain.ServiceProvider;
import com.nudgun.repository.ServiceProviderRepository;
import com.nudgun.service.ServiceProviderService;
import com.nudgun.service.dto.ServiceProviderDTO;
import com.nudgun.service.mapper.ServiceProviderMapper;
import com.nudgun.web.rest.errors.ExceptionTranslator;
import com.nudgun.service.dto.ServiceProviderCriteria;
import com.nudgun.service.ServiceProviderQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;


import static com.nudgun.web.rest.TestUtil.sameInstant;
import static com.nudgun.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ServiceProviderResource REST controller.
 *
 * @see ServiceProviderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NudgunApp.class)
public class ServiceProviderResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PROFILE_PIC = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE_PIC = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICES = "AAAAAAAAAA";
    private static final String UPDATED_SERVICES = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_FACEBOOK = "AAAAAAAAAA";
    private static final String UPDATED_FACEBOOK = "BBBBBBBBBB";

    private static final String DEFAULT_INSTRAGRAM = "AAAAAAAAAA";
    private static final String UPDATED_INSTRAGRAM = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACCEPT_CREDIT_CARD = false;
    private static final Boolean UPDATED_ACCEPT_CREDIT_CARD = true;

    private static final Boolean DEFAULT_PARKING_AVAILABLE = false;
    private static final Boolean UPDATED_PARKING_AVAILABLE = true;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_SERVICE_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SERVICE_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_SERVICE_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SERVICE_END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_PHONE_2 = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_3 = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_3 = "BBBBBBBBBB";

    private static final String DEFAULT_PARKING_DETAIL = "AAAAAAAAAA";
    private static final String UPDATED_PARKING_DETAIL = "BBBBBBBBBB";

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private ServiceProviderQueryService serviceProviderQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restServiceProviderMockMvc;

    private ServiceProvider serviceProvider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ServiceProviderResource serviceProviderResource = new ServiceProviderResource(serviceProviderService, serviceProviderQueryService);
        this.restServiceProviderMockMvc = MockMvcBuilders.standaloneSetup(serviceProviderResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceProvider createEntity(EntityManager em) {
        ServiceProvider serviceProvider = new ServiceProvider()
            .name(DEFAULT_NAME)
            .profile_pic(DEFAULT_PROFILE_PIC)
            .services(DEFAULT_SERVICES)
            .address(DEFAULT_ADDRESS)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .facebook(DEFAULT_FACEBOOK)
            .instragram(DEFAULT_INSTRAGRAM)
            .acceptCreditCard(DEFAULT_ACCEPT_CREDIT_CARD)
            .parkingAvailable(DEFAULT_PARKING_AVAILABLE)
            .description(DEFAULT_DESCRIPTION)
            .serviceStart(DEFAULT_SERVICE_START)
            .serviceEnd(DEFAULT_SERVICE_END)
            .phone2(DEFAULT_PHONE_2)
            .phone3(DEFAULT_PHONE_3)
            .parkingDetail(DEFAULT_PARKING_DETAIL);
        return serviceProvider;
    }

    @Before
    public void initTest() {
        serviceProvider = createEntity(em);
    }

    @Test
    @Transactional
    public void createServiceProvider() throws Exception {
        int databaseSizeBeforeCreate = serviceProviderRepository.findAll().size();

        // Create the ServiceProvider
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);
        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isCreated());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeCreate + 1);
        ServiceProvider testServiceProvider = serviceProviderList.get(serviceProviderList.size() - 1);
        assertThat(testServiceProvider.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServiceProvider.getProfile_pic()).isEqualTo(DEFAULT_PROFILE_PIC);
        assertThat(testServiceProvider.getServices()).isEqualTo(DEFAULT_SERVICES);
        assertThat(testServiceProvider.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testServiceProvider.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testServiceProvider.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testServiceProvider.getFacebook()).isEqualTo(DEFAULT_FACEBOOK);
        assertThat(testServiceProvider.getInstragram()).isEqualTo(DEFAULT_INSTRAGRAM);
        assertThat(testServiceProvider.isAcceptCreditCard()).isEqualTo(DEFAULT_ACCEPT_CREDIT_CARD);
        assertThat(testServiceProvider.isParkingAvailable()).isEqualTo(DEFAULT_PARKING_AVAILABLE);
        assertThat(testServiceProvider.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testServiceProvider.getServiceStart()).isEqualTo(DEFAULT_SERVICE_START);
        assertThat(testServiceProvider.getServiceEnd()).isEqualTo(DEFAULT_SERVICE_END);
        assertThat(testServiceProvider.getPhone2()).isEqualTo(DEFAULT_PHONE_2);
        assertThat(testServiceProvider.getPhone3()).isEqualTo(DEFAULT_PHONE_3);
        assertThat(testServiceProvider.getParkingDetail()).isEqualTo(DEFAULT_PARKING_DETAIL);
    }

    @Test
    @Transactional
    public void createServiceProviderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = serviceProviderRepository.findAll().size();

        // Create the ServiceProvider with an existing ID
        serviceProvider.setId(1L);
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setName(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkProfile_picIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setProfile_pic(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkServicesIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setServices(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setAddress(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setPhone(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setEmail(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAcceptCreditCardIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setAcceptCreditCard(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkServiceStartIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setServiceStart(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkServiceEndIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceProviderRepository.findAll().size();
        // set the field null
        serviceProvider.setServiceEnd(null);

        // Create the ServiceProvider, which fails.
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        restServiceProviderMockMvc.perform(post("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllServiceProviders() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList
        restServiceProviderMockMvc.perform(get("/api/service-providers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceProvider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].profile_pic").value(hasItem(DEFAULT_PROFILE_PIC.toString())))
            .andExpect(jsonPath("$.[*].services").value(hasItem(DEFAULT_SERVICES.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].facebook").value(hasItem(DEFAULT_FACEBOOK.toString())))
            .andExpect(jsonPath("$.[*].instragram").value(hasItem(DEFAULT_INSTRAGRAM.toString())))
            .andExpect(jsonPath("$.[*].acceptCreditCard").value(hasItem(DEFAULT_ACCEPT_CREDIT_CARD.booleanValue())))
            .andExpect(jsonPath("$.[*].parkingAvailable").value(hasItem(DEFAULT_PARKING_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].serviceStart").value(hasItem(sameInstant(DEFAULT_SERVICE_START))))
            .andExpect(jsonPath("$.[*].serviceEnd").value(hasItem(sameInstant(DEFAULT_SERVICE_END))))
            .andExpect(jsonPath("$.[*].phone2").value(hasItem(DEFAULT_PHONE_2.toString())))
            .andExpect(jsonPath("$.[*].phone3").value(hasItem(DEFAULT_PHONE_3.toString())))
            .andExpect(jsonPath("$.[*].parkingDetail").value(hasItem(DEFAULT_PARKING_DETAIL.toString())));
    }
    
    @Test
    @Transactional
    public void getServiceProvider() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get the serviceProvider
        restServiceProviderMockMvc.perform(get("/api/service-providers/{id}", serviceProvider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(serviceProvider.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.profile_pic").value(DEFAULT_PROFILE_PIC.toString()))
            .andExpect(jsonPath("$.services").value(DEFAULT_SERVICES.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.facebook").value(DEFAULT_FACEBOOK.toString()))
            .andExpect(jsonPath("$.instragram").value(DEFAULT_INSTRAGRAM.toString()))
            .andExpect(jsonPath("$.acceptCreditCard").value(DEFAULT_ACCEPT_CREDIT_CARD.booleanValue()))
            .andExpect(jsonPath("$.parkingAvailable").value(DEFAULT_PARKING_AVAILABLE.booleanValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.serviceStart").value(sameInstant(DEFAULT_SERVICE_START)))
            .andExpect(jsonPath("$.serviceEnd").value(sameInstant(DEFAULT_SERVICE_END)))
            .andExpect(jsonPath("$.phone2").value(DEFAULT_PHONE_2.toString()))
            .andExpect(jsonPath("$.phone3").value(DEFAULT_PHONE_3.toString()))
            .andExpect(jsonPath("$.parkingDetail").value(DEFAULT_PARKING_DETAIL.toString()));
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where name equals to DEFAULT_NAME
        defaultServiceProviderShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the serviceProviderList where name equals to UPDATED_NAME
        defaultServiceProviderShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where name in DEFAULT_NAME or UPDATED_NAME
        defaultServiceProviderShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the serviceProviderList where name equals to UPDATED_NAME
        defaultServiceProviderShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where name is not null
        defaultServiceProviderShouldBeFound("name.specified=true");

        // Get all the serviceProviderList where name is null
        defaultServiceProviderShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByProfile_picIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where profile_pic equals to DEFAULT_PROFILE_PIC
        defaultServiceProviderShouldBeFound("profile_pic.equals=" + DEFAULT_PROFILE_PIC);

        // Get all the serviceProviderList where profile_pic equals to UPDATED_PROFILE_PIC
        defaultServiceProviderShouldNotBeFound("profile_pic.equals=" + UPDATED_PROFILE_PIC);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByProfile_picIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where profile_pic in DEFAULT_PROFILE_PIC or UPDATED_PROFILE_PIC
        defaultServiceProviderShouldBeFound("profile_pic.in=" + DEFAULT_PROFILE_PIC + "," + UPDATED_PROFILE_PIC);

        // Get all the serviceProviderList where profile_pic equals to UPDATED_PROFILE_PIC
        defaultServiceProviderShouldNotBeFound("profile_pic.in=" + UPDATED_PROFILE_PIC);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByProfile_picIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where profile_pic is not null
        defaultServiceProviderShouldBeFound("profile_pic.specified=true");

        // Get all the serviceProviderList where profile_pic is null
        defaultServiceProviderShouldNotBeFound("profile_pic.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServicesIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where services equals to DEFAULT_SERVICES
        defaultServiceProviderShouldBeFound("services.equals=" + DEFAULT_SERVICES);

        // Get all the serviceProviderList where services equals to UPDATED_SERVICES
        defaultServiceProviderShouldNotBeFound("services.equals=" + UPDATED_SERVICES);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServicesIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where services in DEFAULT_SERVICES or UPDATED_SERVICES
        defaultServiceProviderShouldBeFound("services.in=" + DEFAULT_SERVICES + "," + UPDATED_SERVICES);

        // Get all the serviceProviderList where services equals to UPDATED_SERVICES
        defaultServiceProviderShouldNotBeFound("services.in=" + UPDATED_SERVICES);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServicesIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where services is not null
        defaultServiceProviderShouldBeFound("services.specified=true");

        // Get all the serviceProviderList where services is null
        defaultServiceProviderShouldNotBeFound("services.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where address equals to DEFAULT_ADDRESS
        defaultServiceProviderShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the serviceProviderList where address equals to UPDATED_ADDRESS
        defaultServiceProviderShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultServiceProviderShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the serviceProviderList where address equals to UPDATED_ADDRESS
        defaultServiceProviderShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where address is not null
        defaultServiceProviderShouldBeFound("address.specified=true");

        // Get all the serviceProviderList where address is null
        defaultServiceProviderShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone equals to DEFAULT_PHONE
        defaultServiceProviderShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the serviceProviderList where phone equals to UPDATED_PHONE
        defaultServiceProviderShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultServiceProviderShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the serviceProviderList where phone equals to UPDATED_PHONE
        defaultServiceProviderShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone is not null
        defaultServiceProviderShouldBeFound("phone.specified=true");

        // Get all the serviceProviderList where phone is null
        defaultServiceProviderShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where email equals to DEFAULT_EMAIL
        defaultServiceProviderShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the serviceProviderList where email equals to UPDATED_EMAIL
        defaultServiceProviderShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultServiceProviderShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the serviceProviderList where email equals to UPDATED_EMAIL
        defaultServiceProviderShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where email is not null
        defaultServiceProviderShouldBeFound("email.specified=true");

        // Get all the serviceProviderList where email is null
        defaultServiceProviderShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByFacebookIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where facebook equals to DEFAULT_FACEBOOK
        defaultServiceProviderShouldBeFound("facebook.equals=" + DEFAULT_FACEBOOK);

        // Get all the serviceProviderList where facebook equals to UPDATED_FACEBOOK
        defaultServiceProviderShouldNotBeFound("facebook.equals=" + UPDATED_FACEBOOK);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByFacebookIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where facebook in DEFAULT_FACEBOOK or UPDATED_FACEBOOK
        defaultServiceProviderShouldBeFound("facebook.in=" + DEFAULT_FACEBOOK + "," + UPDATED_FACEBOOK);

        // Get all the serviceProviderList where facebook equals to UPDATED_FACEBOOK
        defaultServiceProviderShouldNotBeFound("facebook.in=" + UPDATED_FACEBOOK);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByFacebookIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where facebook is not null
        defaultServiceProviderShouldBeFound("facebook.specified=true");

        // Get all the serviceProviderList where facebook is null
        defaultServiceProviderShouldNotBeFound("facebook.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByInstragramIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where instragram equals to DEFAULT_INSTRAGRAM
        defaultServiceProviderShouldBeFound("instragram.equals=" + DEFAULT_INSTRAGRAM);

        // Get all the serviceProviderList where instragram equals to UPDATED_INSTRAGRAM
        defaultServiceProviderShouldNotBeFound("instragram.equals=" + UPDATED_INSTRAGRAM);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByInstragramIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where instragram in DEFAULT_INSTRAGRAM or UPDATED_INSTRAGRAM
        defaultServiceProviderShouldBeFound("instragram.in=" + DEFAULT_INSTRAGRAM + "," + UPDATED_INSTRAGRAM);

        // Get all the serviceProviderList where instragram equals to UPDATED_INSTRAGRAM
        defaultServiceProviderShouldNotBeFound("instragram.in=" + UPDATED_INSTRAGRAM);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByInstragramIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where instragram is not null
        defaultServiceProviderShouldBeFound("instragram.specified=true");

        // Get all the serviceProviderList where instragram is null
        defaultServiceProviderShouldNotBeFound("instragram.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByAcceptCreditCardIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where acceptCreditCard equals to DEFAULT_ACCEPT_CREDIT_CARD
        defaultServiceProviderShouldBeFound("acceptCreditCard.equals=" + DEFAULT_ACCEPT_CREDIT_CARD);

        // Get all the serviceProviderList where acceptCreditCard equals to UPDATED_ACCEPT_CREDIT_CARD
        defaultServiceProviderShouldNotBeFound("acceptCreditCard.equals=" + UPDATED_ACCEPT_CREDIT_CARD);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByAcceptCreditCardIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where acceptCreditCard in DEFAULT_ACCEPT_CREDIT_CARD or UPDATED_ACCEPT_CREDIT_CARD
        defaultServiceProviderShouldBeFound("acceptCreditCard.in=" + DEFAULT_ACCEPT_CREDIT_CARD + "," + UPDATED_ACCEPT_CREDIT_CARD);

        // Get all the serviceProviderList where acceptCreditCard equals to UPDATED_ACCEPT_CREDIT_CARD
        defaultServiceProviderShouldNotBeFound("acceptCreditCard.in=" + UPDATED_ACCEPT_CREDIT_CARD);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByAcceptCreditCardIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where acceptCreditCard is not null
        defaultServiceProviderShouldBeFound("acceptCreditCard.specified=true");

        // Get all the serviceProviderList where acceptCreditCard is null
        defaultServiceProviderShouldNotBeFound("acceptCreditCard.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByParkingAvailableIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where parkingAvailable equals to DEFAULT_PARKING_AVAILABLE
        defaultServiceProviderShouldBeFound("parkingAvailable.equals=" + DEFAULT_PARKING_AVAILABLE);

        // Get all the serviceProviderList where parkingAvailable equals to UPDATED_PARKING_AVAILABLE
        defaultServiceProviderShouldNotBeFound("parkingAvailable.equals=" + UPDATED_PARKING_AVAILABLE);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByParkingAvailableIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where parkingAvailable in DEFAULT_PARKING_AVAILABLE or UPDATED_PARKING_AVAILABLE
        defaultServiceProviderShouldBeFound("parkingAvailable.in=" + DEFAULT_PARKING_AVAILABLE + "," + UPDATED_PARKING_AVAILABLE);

        // Get all the serviceProviderList where parkingAvailable equals to UPDATED_PARKING_AVAILABLE
        defaultServiceProviderShouldNotBeFound("parkingAvailable.in=" + UPDATED_PARKING_AVAILABLE);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByParkingAvailableIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where parkingAvailable is not null
        defaultServiceProviderShouldBeFound("parkingAvailable.specified=true");

        // Get all the serviceProviderList where parkingAvailable is null
        defaultServiceProviderShouldNotBeFound("parkingAvailable.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where description equals to DEFAULT_DESCRIPTION
        defaultServiceProviderShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the serviceProviderList where description equals to UPDATED_DESCRIPTION
        defaultServiceProviderShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultServiceProviderShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the serviceProviderList where description equals to UPDATED_DESCRIPTION
        defaultServiceProviderShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where description is not null
        defaultServiceProviderShouldBeFound("description.specified=true");

        // Get all the serviceProviderList where description is null
        defaultServiceProviderShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceStartIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceStart equals to DEFAULT_SERVICE_START
        defaultServiceProviderShouldBeFound("serviceStart.equals=" + DEFAULT_SERVICE_START);

        // Get all the serviceProviderList where serviceStart equals to UPDATED_SERVICE_START
        defaultServiceProviderShouldNotBeFound("serviceStart.equals=" + UPDATED_SERVICE_START);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceStartIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceStart in DEFAULT_SERVICE_START or UPDATED_SERVICE_START
        defaultServiceProviderShouldBeFound("serviceStart.in=" + DEFAULT_SERVICE_START + "," + UPDATED_SERVICE_START);

        // Get all the serviceProviderList where serviceStart equals to UPDATED_SERVICE_START
        defaultServiceProviderShouldNotBeFound("serviceStart.in=" + UPDATED_SERVICE_START);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceStart is not null
        defaultServiceProviderShouldBeFound("serviceStart.specified=true");

        // Get all the serviceProviderList where serviceStart is null
        defaultServiceProviderShouldNotBeFound("serviceStart.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceStartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceStart greater than or equals to DEFAULT_SERVICE_START
        defaultServiceProviderShouldBeFound("serviceStart.greaterOrEqualThan=" + DEFAULT_SERVICE_START);

        // Get all the serviceProviderList where serviceStart greater than or equals to UPDATED_SERVICE_START
        defaultServiceProviderShouldNotBeFound("serviceStart.greaterOrEqualThan=" + UPDATED_SERVICE_START);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceStartIsLessThanSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceStart less than or equals to DEFAULT_SERVICE_START
        defaultServiceProviderShouldNotBeFound("serviceStart.lessThan=" + DEFAULT_SERVICE_START);

        // Get all the serviceProviderList where serviceStart less than or equals to UPDATED_SERVICE_START
        defaultServiceProviderShouldBeFound("serviceStart.lessThan=" + UPDATED_SERVICE_START);
    }


    @Test
    @Transactional
    public void getAllServiceProvidersByServiceEndIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceEnd equals to DEFAULT_SERVICE_END
        defaultServiceProviderShouldBeFound("serviceEnd.equals=" + DEFAULT_SERVICE_END);

        // Get all the serviceProviderList where serviceEnd equals to UPDATED_SERVICE_END
        defaultServiceProviderShouldNotBeFound("serviceEnd.equals=" + UPDATED_SERVICE_END);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceEndIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceEnd in DEFAULT_SERVICE_END or UPDATED_SERVICE_END
        defaultServiceProviderShouldBeFound("serviceEnd.in=" + DEFAULT_SERVICE_END + "," + UPDATED_SERVICE_END);

        // Get all the serviceProviderList where serviceEnd equals to UPDATED_SERVICE_END
        defaultServiceProviderShouldNotBeFound("serviceEnd.in=" + UPDATED_SERVICE_END);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceEnd is not null
        defaultServiceProviderShouldBeFound("serviceEnd.specified=true");

        // Get all the serviceProviderList where serviceEnd is null
        defaultServiceProviderShouldNotBeFound("serviceEnd.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceEndIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceEnd greater than or equals to DEFAULT_SERVICE_END
        defaultServiceProviderShouldBeFound("serviceEnd.greaterOrEqualThan=" + DEFAULT_SERVICE_END);

        // Get all the serviceProviderList where serviceEnd greater than or equals to UPDATED_SERVICE_END
        defaultServiceProviderShouldNotBeFound("serviceEnd.greaterOrEqualThan=" + UPDATED_SERVICE_END);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByServiceEndIsLessThanSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where serviceEnd less than or equals to DEFAULT_SERVICE_END
        defaultServiceProviderShouldNotBeFound("serviceEnd.lessThan=" + DEFAULT_SERVICE_END);

        // Get all the serviceProviderList where serviceEnd less than or equals to UPDATED_SERVICE_END
        defaultServiceProviderShouldBeFound("serviceEnd.lessThan=" + UPDATED_SERVICE_END);
    }


    @Test
    @Transactional
    public void getAllServiceProvidersByPhone2IsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone2 equals to DEFAULT_PHONE_2
        defaultServiceProviderShouldBeFound("phone2.equals=" + DEFAULT_PHONE_2);

        // Get all the serviceProviderList where phone2 equals to UPDATED_PHONE_2
        defaultServiceProviderShouldNotBeFound("phone2.equals=" + UPDATED_PHONE_2);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByPhone2IsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone2 in DEFAULT_PHONE_2 or UPDATED_PHONE_2
        defaultServiceProviderShouldBeFound("phone2.in=" + DEFAULT_PHONE_2 + "," + UPDATED_PHONE_2);

        // Get all the serviceProviderList where phone2 equals to UPDATED_PHONE_2
        defaultServiceProviderShouldNotBeFound("phone2.in=" + UPDATED_PHONE_2);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByPhone2IsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone2 is not null
        defaultServiceProviderShouldBeFound("phone2.specified=true");

        // Get all the serviceProviderList where phone2 is null
        defaultServiceProviderShouldNotBeFound("phone2.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByPhone3IsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone3 equals to DEFAULT_PHONE_3
        defaultServiceProviderShouldBeFound("phone3.equals=" + DEFAULT_PHONE_3);

        // Get all the serviceProviderList where phone3 equals to UPDATED_PHONE_3
        defaultServiceProviderShouldNotBeFound("phone3.equals=" + UPDATED_PHONE_3);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByPhone3IsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone3 in DEFAULT_PHONE_3 or UPDATED_PHONE_3
        defaultServiceProviderShouldBeFound("phone3.in=" + DEFAULT_PHONE_3 + "," + UPDATED_PHONE_3);

        // Get all the serviceProviderList where phone3 equals to UPDATED_PHONE_3
        defaultServiceProviderShouldNotBeFound("phone3.in=" + UPDATED_PHONE_3);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByPhone3IsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where phone3 is not null
        defaultServiceProviderShouldBeFound("phone3.specified=true");

        // Get all the serviceProviderList where phone3 is null
        defaultServiceProviderShouldNotBeFound("phone3.specified=false");
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByParkingDetailIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where parkingDetail equals to DEFAULT_PARKING_DETAIL
        defaultServiceProviderShouldBeFound("parkingDetail.equals=" + DEFAULT_PARKING_DETAIL);

        // Get all the serviceProviderList where parkingDetail equals to UPDATED_PARKING_DETAIL
        defaultServiceProviderShouldNotBeFound("parkingDetail.equals=" + UPDATED_PARKING_DETAIL);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByParkingDetailIsInShouldWork() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where parkingDetail in DEFAULT_PARKING_DETAIL or UPDATED_PARKING_DETAIL
        defaultServiceProviderShouldBeFound("parkingDetail.in=" + DEFAULT_PARKING_DETAIL + "," + UPDATED_PARKING_DETAIL);

        // Get all the serviceProviderList where parkingDetail equals to UPDATED_PARKING_DETAIL
        defaultServiceProviderShouldNotBeFound("parkingDetail.in=" + UPDATED_PARKING_DETAIL);
    }

    @Test
    @Transactional
    public void getAllServiceProvidersByParkingDetailIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList where parkingDetail is not null
        defaultServiceProviderShouldBeFound("parkingDetail.specified=true");

        // Get all the serviceProviderList where parkingDetail is null
        defaultServiceProviderShouldNotBeFound("parkingDetail.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultServiceProviderShouldBeFound(String filter) throws Exception {
        restServiceProviderMockMvc.perform(get("/api/service-providers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceProvider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].profile_pic").value(hasItem(DEFAULT_PROFILE_PIC.toString())))
            .andExpect(jsonPath("$.[*].services").value(hasItem(DEFAULT_SERVICES.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].facebook").value(hasItem(DEFAULT_FACEBOOK.toString())))
            .andExpect(jsonPath("$.[*].instragram").value(hasItem(DEFAULT_INSTRAGRAM.toString())))
            .andExpect(jsonPath("$.[*].acceptCreditCard").value(hasItem(DEFAULT_ACCEPT_CREDIT_CARD.booleanValue())))
            .andExpect(jsonPath("$.[*].parkingAvailable").value(hasItem(DEFAULT_PARKING_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].serviceStart").value(hasItem(sameInstant(DEFAULT_SERVICE_START))))
            .andExpect(jsonPath("$.[*].serviceEnd").value(hasItem(sameInstant(DEFAULT_SERVICE_END))))
            .andExpect(jsonPath("$.[*].phone2").value(hasItem(DEFAULT_PHONE_2.toString())))
            .andExpect(jsonPath("$.[*].phone3").value(hasItem(DEFAULT_PHONE_3.toString())))
            .andExpect(jsonPath("$.[*].parkingDetail").value(hasItem(DEFAULT_PARKING_DETAIL.toString())));

        // Check, that the count call also returns 1
        restServiceProviderMockMvc.perform(get("/api/service-providers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultServiceProviderShouldNotBeFound(String filter) throws Exception {
        restServiceProviderMockMvc.perform(get("/api/service-providers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restServiceProviderMockMvc.perform(get("/api/service-providers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingServiceProvider() throws Exception {
        // Get the serviceProvider
        restServiceProviderMockMvc.perform(get("/api/service-providers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateServiceProvider() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();

        // Update the serviceProvider
        ServiceProvider updatedServiceProvider = serviceProviderRepository.findById(serviceProvider.getId()).get();
        // Disconnect from session so that the updates on updatedServiceProvider are not directly saved in db
        em.detach(updatedServiceProvider);
        updatedServiceProvider
            .name(UPDATED_NAME)
            .profile_pic(UPDATED_PROFILE_PIC)
            .services(UPDATED_SERVICES)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .facebook(UPDATED_FACEBOOK)
            .instragram(UPDATED_INSTRAGRAM)
            .acceptCreditCard(UPDATED_ACCEPT_CREDIT_CARD)
            .parkingAvailable(UPDATED_PARKING_AVAILABLE)
            .description(UPDATED_DESCRIPTION)
            .serviceStart(UPDATED_SERVICE_START)
            .serviceEnd(UPDATED_SERVICE_END)
            .phone2(UPDATED_PHONE_2)
            .phone3(UPDATED_PHONE_3)
            .parkingDetail(UPDATED_PARKING_DETAIL);
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(updatedServiceProvider);

        restServiceProviderMockMvc.perform(put("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isOk());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);
        ServiceProvider testServiceProvider = serviceProviderList.get(serviceProviderList.size() - 1);
        assertThat(testServiceProvider.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServiceProvider.getProfile_pic()).isEqualTo(UPDATED_PROFILE_PIC);
        assertThat(testServiceProvider.getServices()).isEqualTo(UPDATED_SERVICES);
        assertThat(testServiceProvider.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testServiceProvider.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testServiceProvider.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testServiceProvider.getFacebook()).isEqualTo(UPDATED_FACEBOOK);
        assertThat(testServiceProvider.getInstragram()).isEqualTo(UPDATED_INSTRAGRAM);
        assertThat(testServiceProvider.isAcceptCreditCard()).isEqualTo(UPDATED_ACCEPT_CREDIT_CARD);
        assertThat(testServiceProvider.isParkingAvailable()).isEqualTo(UPDATED_PARKING_AVAILABLE);
        assertThat(testServiceProvider.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testServiceProvider.getServiceStart()).isEqualTo(UPDATED_SERVICE_START);
        assertThat(testServiceProvider.getServiceEnd()).isEqualTo(UPDATED_SERVICE_END);
        assertThat(testServiceProvider.getPhone2()).isEqualTo(UPDATED_PHONE_2);
        assertThat(testServiceProvider.getPhone3()).isEqualTo(UPDATED_PHONE_3);
        assertThat(testServiceProvider.getParkingDetail()).isEqualTo(UPDATED_PARKING_DETAIL);
    }

    @Test
    @Transactional
    public void updateNonExistingServiceProvider() throws Exception {
        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();

        // Create the ServiceProvider
        ServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toDto(serviceProvider);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceProviderMockMvc.perform(put("/api/service-providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceProviderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteServiceProvider() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        int databaseSizeBeforeDelete = serviceProviderRepository.findAll().size();

        // Get the serviceProvider
        restServiceProviderMockMvc.perform(delete("/api/service-providers/{id}", serviceProvider.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceProvider.class);
        ServiceProvider serviceProvider1 = new ServiceProvider();
        serviceProvider1.setId(1L);
        ServiceProvider serviceProvider2 = new ServiceProvider();
        serviceProvider2.setId(serviceProvider1.getId());
        assertThat(serviceProvider1).isEqualTo(serviceProvider2);
        serviceProvider2.setId(2L);
        assertThat(serviceProvider1).isNotEqualTo(serviceProvider2);
        serviceProvider1.setId(null);
        assertThat(serviceProvider1).isNotEqualTo(serviceProvider2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceProviderDTO.class);
        ServiceProviderDTO serviceProviderDTO1 = new ServiceProviderDTO();
        serviceProviderDTO1.setId(1L);
        ServiceProviderDTO serviceProviderDTO2 = new ServiceProviderDTO();
        assertThat(serviceProviderDTO1).isNotEqualTo(serviceProviderDTO2);
        serviceProviderDTO2.setId(serviceProviderDTO1.getId());
        assertThat(serviceProviderDTO1).isEqualTo(serviceProviderDTO2);
        serviceProviderDTO2.setId(2L);
        assertThat(serviceProviderDTO1).isNotEqualTo(serviceProviderDTO2);
        serviceProviderDTO1.setId(null);
        assertThat(serviceProviderDTO1).isNotEqualTo(serviceProviderDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(serviceProviderMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(serviceProviderMapper.fromId(null)).isNull();
    }
}
