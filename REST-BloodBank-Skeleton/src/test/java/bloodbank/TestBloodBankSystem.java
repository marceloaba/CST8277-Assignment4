/**
 * File: OrderSystemTestSuite.java
 * Course materials (21S) CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 * 
 * Updated by Students:
 * 	@author Chrishanthi Michael
 * 	@author Marcelo Monteiro da Silva
 * 	@author Janio Mendonca Junior
 * 	@author Parnoor Singh Gill
 * 
 * @date 13/08/2021
 */
package bloodbank;

import static bloodbank.utility.MyConstants.APPLICATION_API_VERSION;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PERSON_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.CUSTOMER_ADDRESS_SUBRESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.Contact;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.Phone;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestBloodBankSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String APPLICATION_CONTEXT_ROOT = "REST-BloodBank-Skeleton";
    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER_PREFIX, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }

    @Test
    public void test01_all_persons_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(PERSON_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Person> persons = response.readEntity(new GenericType<List<Person>>(){});
        assertThat(persons, is(not(empty())));
        assertThat(persons, hasSize(1));
    }
    
    @Test
    public void test01_personById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        //String id = "1";
     	Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path("person/").path("1")
            //.queryParam("id", id)
            .resolveTemplate(HOST, APPLICATION_CONTEXT_ROOT)
            //.path("person/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Person person = response.readEntity(new GenericType<Person>(){});
        assertEquals("Teddy", person.getFirstName());
        assertEquals("Yap", person.getLastName());
        //List<Person> persons = response.readEntity(new GenericType<List<Person>>(){});
        //assertThat(person, is(not(empty())));
        //assertThat(person, hasSize(1));
      
    }
    
    @Test
    public void test02_all_address_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Address> addresses = response.readEntity(new GenericType<List<Address>>(){});
        assertThat(addresses, is(not(empty())));
        assertThat(addresses, hasSize(1));
    }
    
    @Test
    public void test02_addressById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME).path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Address address = response.readEntity(new GenericType<Address>(){});
        assertEquals("ottawa", address.getCity());
        assertEquals("CA", address.getCountry());
        assertEquals("ON", address.getProvince());
        assertEquals("abcd Dr.W", address.getStreet());
        assertEquals("123", address.getStreetNumber());
        assertEquals("A1B2C3", address.getZipcode());
        
    }
    
    @Test
    public void test03_all_phones_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path("phone")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Phone> phones = response.readEntity(new GenericType<List<Phone>>(){});
        assertThat(phones, is(not(empty())));
        assertThat(phones, hasSize(2));
    }
    
    @Test
    public void test03_phoneByID_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path("phone").path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Phone phone = response.readEntity(new GenericType<Phone>(){});
        assertEquals("234", phone.getAreaCode());
        assertEquals("0", phone.getCountryCode());
        assertEquals("5678900", phone.getNumber());
        
    }
    
    @Test
    public void test04_all_bloodbank_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path("bloodbank")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<BloodBank> bloodbanks = response.readEntity(new GenericType<List<BloodBank>>(){});
        assertThat(bloodbanks, is(not(empty())));
        assertThat(bloodbanks, hasSize(2));
    }
    
    @Test
    public void test04_bloodbankById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path("bloodbank").path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        BloodBank bloodbank = response.readEntity(new GenericType<BloodBank>(){});
        assertEquals("Bloody Bank", bloodbank.getName());
        
    }
    
    @Test
    public void test05_all_contact_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path("contact")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Contact> contacts = response.readEntity(new GenericType<List<Contact>>(){});
        assertThat(contacts, is(not(empty())));
        assertThat(contacts, hasSize(2));
    }
    
    @Test
    public void test05_contactById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path("contact").path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Contact contact = response.readEntity(new GenericType<Contact>(){});
        assertEquals("test@test.com", contact.getEmail());
        assertEquals("Home", contact.getContactType());
        
    }
    
    @Test
    public void test06_all_donationRecord_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path("donationrecord")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<DonationRecord> donationRecords = response.readEntity(new GenericType<List<DonationRecord>>(){});
        assertThat(donationRecords, is(not(empty())));
        assertThat(donationRecords, hasSize(2));
    }
    
    @Test
    public void test06_donationRecordById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            //.register(adminAuth)
            .path("donationrecord").path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        DonationRecord donationRecord = response.readEntity(new GenericType<DonationRecord>(){});
        assertEquals(1, donationRecord.getTested());
      
    }
    
}