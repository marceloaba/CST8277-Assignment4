/***************************************************************************
 * File: TestBloodBankSystem.java Course materials (21F) CST 8277
 * 
 * Created by Students:
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

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
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
import bloodbank.entity.BloodBankExtends;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.BloodType;
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
    public int phoneSize;
    static public Integer newAddressId;
    static public Integer newPhoneId;
    static public Integer newBloodBankId;
    static public Integer newRecordId;
    static public Integer newDonationId;
    static public Integer newPersonId;

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

    //#############
    //#  GET ALL  #
    //#############
    @Test
    public void test01_all_blooddonation_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("blooddonation")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<BloodDonation> blooddonation = response.readEntity(new GenericType<List<BloodDonation>>(){});
        assertThat(blooddonation, is(not(empty())));
        assertThat(blooddonation, hasSize(2));
    }
    
    @Test
    public void test02_get_all_persons_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PERSON_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Person> persons = response.readEntity(new GenericType<List<Person>>(){});
        assertThat(persons, is(not(empty())));
//        assertThat(persons, hasSize(1));
    }
    
    @Test
    public void test03_get_all_address_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Address> addresses = response.readEntity(new GenericType<List<Address>>(){});
        assertThat(addresses, is(not(empty())));
        assertThat(addresses, hasSize(1));
    }
    
    @Test
    public void test04_get_all_phones_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("phone")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Phone> phones = response.readEntity(new GenericType<List<Phone>>(){});
        phoneSize = phones.size();
        assertThat(phones, is(not(empty())));
        assertThat(phones, hasSize(2));
    }
    
    @Test
    public void test05_all_bloodbank_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("bloodbank")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<BloodBank> bloodbanks = response.readEntity(new GenericType<List<BloodBank>>(){});
        assertThat(bloodbanks, is(not(empty())));
        assertThat(bloodbanks, hasSize(2));
    }
    
    @Test
    public void test06_all_contact_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("contact")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Contact> contacts = response.readEntity(new GenericType<List<Contact>>(){});
        assertThat(contacts, is(not(empty())));
        assertThat(contacts, hasSize(2));
    }
    
    @Test
    public void test07_all_donationRecord_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("donationrecord")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<DonationRecord> donationRecords = response.readEntity(new GenericType<List<DonationRecord>>(){});
        assertThat(donationRecords, is(not(empty())));
        assertThat(donationRecords, hasSize(2));
    }
    
    //#############
    //# GET BY ID #
    //#############
    @Test
    public void test08_get_blooddonationById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("blooddonation").path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        BloodDonation blooddonation = response.readEntity(new GenericType<BloodDonation>(){});
        assertEquals(10, blooddonation.getMilliliters()); 
    }
    
    @Test
    public void test09_get_personById_with_adminrole() throws JsonMappingException, JsonProcessingException {
     	Response response = webTarget
            .register(adminAuth)
            .path("person/").path("1")
            .resolveTemplate(HOST, APPLICATION_CONTEXT_ROOT)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Person person = response.readEntity(new GenericType<Person>(){});
        assertEquals("Teddy", person.getFirstName());
        assertEquals("Yap", person.getLastName());
      
    }
    
    @Test
    public void test10_get_addressById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME).path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Address address = response.readEntity(new GenericType<Address>(){});
        assertEquals("123", address.getStreetNumber());
        assertEquals("ottawa", address.getCity());
        assertEquals("CA", address.getCountry());
        assertEquals("ON", address.getProvince());
        assertEquals("abcd Dr.W", address.getStreet());
        assertEquals("A1B2C3", address.getZipcode());
        
    }
    
    @Test
    public void test11_get_phoneByID_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
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
    public void test12_get_bloodbankById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("bloodbank").path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        BloodBank bloodbank = response.readEntity(new GenericType<BloodBank>(){});
        assertEquals("Bloody Bank", bloodbank.getName());
        
    }
    
    @Test
    public void test13_get_contactById_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    		.register(adminAuth)
    		.path("contact").path("1")
    		.request()
    		.get();
    	assertThat(response.getStatus(), is(200));
      	Contact contact = response.readEntity(new GenericType<Contact>(){});
      	assertEquals("test@test.com", contact.getEmail());
      	assertEquals("Home", contact.getContactType());
      
    }
    
    @Test
    public void test14_get_donationRecordById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("donationrecord").path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        DonationRecord donationRecord = response.readEntity(new GenericType<DonationRecord>(){});
        assertEquals(1, donationRecord.getTested());
      
    }
    
    //########################
    //#    POST AND DELETE   #
    //########################   
    @Test
    public void test15_addressPOST_with_adminrole() throws JsonMappingException, JsonProcessingException {
        
    	Address addressTest = new Address();
    	addressTest.setAddress("222", "ponta do mel", "natal", "rn", "br", "590955");
    	
    	Response response = webTarget
            .register(adminAuth)
            .path("address")
            .request()
            .post(Entity.json(addressTest));
    	
    	assertThat(response.getStatus(), is(200));
    	assertEquals(response.hasEntity(), true);
    	Address newAddress = response.readEntity(new GenericType<Address>(){});
    	newAddressId = newAddress.getId();
        
    }
    
    @Test
    public void test16_get_new_addressById_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME).path(newAddressId.toString())
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Address address = response.readEntity(new GenericType<Address>(){});
        assertEquals("222", address.getStreetNumber());
        assertEquals("natal", address.getCity());
        assertEquals("br", address.getCountry());
        assertEquals("rn", address.getProvince());
        assertEquals("ponta do mel", address.getStreet());
        assertEquals("590955", address.getZipcode());
        
    }
    
    @Test
    public void test17_addressDELETE_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    		.register(userAuth)
            .path("address").path(newAddressId.toString())
            .request()
            .delete();
    	assertThat(response.getStatus(), is(401));
  }
    
    @Test
    public void test18_addressDELETE_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    		.register(adminAuth)
            .path("address").path(newAddressId.toString())
            .request()
            .delete();
    	assertThat(response.getStatus(), is(200));
    	assertEquals(response.hasEntity(), true);
  }
    
  @Test
  public void test19_phonePOST_with_adminrole() throws JsonMappingException, JsonProcessingException {
      
  	Phone phoneTest = new Phone();
  	phoneTest.setAreaCode("000");
  	phoneTest.setCountryCode("1");
  	phoneTest.setNumber("2222222");
  	
  	
  	Response response = webTarget
          .register(adminAuth)
          .path("phone")
          .request()
          .post(Entity.json(phoneTest));
  	 
  	 assertThat(response.getStatus(), is(200));
  	 assertEquals(response.hasEntity(), true);
  	 Phone newPhone = response.readEntity(new GenericType<Phone>(){});
  	 newPhoneId = newPhone.getId();
      
  }
  
  @Test
  public void test20_get_new_phoneByID_with_adminrole() throws JsonMappingException, JsonProcessingException {
      Response response = webTarget
          .register(adminAuth)
          .path("phone").path(newPhoneId.toString())
          .request()
          .get();
      assertThat(response.getStatus(), is(200));
      Phone phone = response.readEntity(new GenericType<Phone>(){});
      assertEquals("000", phone.getAreaCode());
      assertEquals("1", phone.getCountryCode());
      assertEquals("2222222", phone.getNumber());
      
  }
  
  @Test
  public void test21_phoneDELETE_with_userrole() throws JsonMappingException, JsonProcessingException {
  	Response response = webTarget
  		.register(userAuth)
          .path("phone").path(newAddressId.toString())
          .request()
          .delete();
  	assertThat(response.getStatus(), is(401));
  }
  
  @Test
  public void test22_phoneDELETE_with_adminrole() throws JsonMappingException, JsonProcessingException {
  	Response response = webTarget
  		.register(adminAuth)
        .path("phone").path(newPhoneId.toString())
        .request()
        .delete();
  	assertThat(response.getStatus(), is(200));
  	assertEquals(response.hasEntity(), true);
  }
  
  @Test
  public void test23_bloodDonationPOST_with_adminrole() throws JsonMappingException, JsonProcessingException {
  	
  	BloodType type = new BloodType();
  	type.setType("A", "+");
  	
  	BloodDonation donation = new BloodDonation();
  	donation.setMilliliters(9);
  	donation.setBloodType(type);
  	
  	Response response = webTarget
  	.register(adminAuth)
  	.path("blooddonation/bank/1")
  	.request()
  	.post(Entity.json(donation));
  	
      assertThat(response.getStatus(), is(200));
      BloodDonation readDonation = response.readEntity(new GenericType<BloodDonation>(){});
      assertEquals(type, readDonation.getBloodType());
      newDonationId = readDonation.getId();
    
      Response checkTotalDonations = webTarget
              .register(adminAuth)
              .path("blooddonation")
              .request()
              .get();
          assertThat(response.getStatus(), is(200));
          List<BloodDonation> donations = checkTotalDonations.readEntity(new GenericType<List<BloodDonation>>(){});
          assertThat(donations, is(not(empty())));
          assertThat(donations, hasSize(3));
  }
  
  @Test
  public void test24_get_new_blooddonationById_with_adminrole() throws JsonMappingException, JsonProcessingException {
      Response response = webTarget
          .register(adminAuth)
          .path("blooddonation").path(newDonationId.toString())
          .request()
          .get();
      assertThat(response.getStatus(), is(200));
      BloodDonation blooddonation = response.readEntity(new GenericType<BloodDonation>(){});
      assertEquals(9, blooddonation.getMilliliters()); 
      assertEquals("A", blooddonation.getBloodType().getBloodGroup());
      assertEquals(0b1, blooddonation.getBloodType().getRhd()); 
  }
  
  
  @Test
  public void test25_addDonationRecord_with_adminrole() throws JsonMappingException, JsonProcessingException {
      
  	DonationRecord record=new DonationRecord();
  	record.setTested(true);
  	
  	Response response = webTarget
          .register(adminAuth)
          .path("donationrecord/person/1/blooddonation/"+newDonationId)
          .request()
          .post(Entity.json(record));
      assertThat(response.getStatus(), is(200));
      DonationRecord donationRecord = response.readEntity(new GenericType<DonationRecord>(){});
      assertEquals(1, donationRecord.getTested());
      newRecordId = donationRecord.getId();
      
      Response checkTotalRecords = webTarget
              .register(adminAuth)
              .path("donationrecord")
              .request()
              .get();
          assertThat(response.getStatus(), is(200));
          List<DonationRecord> donationRecords = checkTotalRecords.readEntity(new GenericType<List<DonationRecord>>(){});
          assertThat(donationRecords, is(not(empty())));
          assertThat(donationRecords, hasSize(3));
  }
  
  @Test
  public void test26_get_new_donationRecordById_with_adminrole() throws JsonMappingException, JsonProcessingException {
      Response response = webTarget
          .register(adminAuth)
          .path("donationrecord").path(newDonationId.toString())
          .request()
          .get();
      assertThat(response.getStatus(), is(200));
      DonationRecord donationRecord = response.readEntity(new GenericType<DonationRecord>(){});
      assertEquals(1, donationRecord.getTested());
    
  }
  
  @Test
  public void test27_deleteDonationRecord_with_userrole() throws JsonMappingException, JsonProcessingException {
      Response response = webTarget
          .register(userAuth)
          .path("donationrecord/"+newRecordId)
          .request()
          .delete();
      assertThat(response.getStatus(), is(401));
  }
  
  @Test
  public void test28_deleteDonationRecord_with_adminrole() throws JsonMappingException, JsonProcessingException {
      Response response = webTarget
          .register(adminAuth)
          .path("donationrecord/"+newRecordId)
          .request()
          .delete();
      assertThat(response.getStatus(), is(200));
      DonationRecord record = response.readEntity(new GenericType<DonationRecord>(){});
      assertEquals(newRecordId, record.getId());
      
      Response checkTotalRecords = webTarget
              .register(adminAuth)
              //.register(adminAuth)
              .path("donationrecord")
              .request()
              .get();
          assertThat(response.getStatus(), is(200));
          List<DonationRecord> donationRecords = checkTotalRecords.readEntity(new GenericType<List<DonationRecord>>(){});
          assertThat(donationRecords, is(not(empty())));
          assertThat(donationRecords, hasSize(2));
  }
  
  @Test
  public void test29_deleteBloodDonation_with_userrole() throws JsonMappingException, JsonProcessingException {
      Response response = webTarget
          .register(userAuth)
          .path("blooddonation/"+newDonationId)
          .request()
          .delete();
      assertThat(response.getStatus(), is(401));
  }
  
  @Test
  public void test30_deleteBloodDonation_with_adminrole() throws JsonMappingException, JsonProcessingException {
      Response response = webTarget
          .register(adminAuth)
          .path("blooddonation/"+newDonationId)
          .request()
          .delete();
      assertThat(response.getStatus(), is(200));
      BloodDonation donation = response.readEntity(new GenericType<BloodDonation>(){});
      assertEquals(newDonationId, donation.getId());
      
      Response checkTotalDonations = webTarget
              .register(adminAuth)
              .path("blooddonation")
              .request()
              .get();
          assertThat(response.getStatus(), is(200));
          List<BloodDonation> donations = checkTotalDonations.readEntity(new GenericType<List<BloodDonation>>(){});
          assertThat(donations, is(not(empty())));
          assertThat(donations, hasSize(2));
  }
  
  @Test
  public void test31_bloodbankPOST_with_adminrole() throws JsonMappingException, JsonProcessingException {

      BloodBankExtends bb = new BloodBankExtends();
      bb.setName("TEST blood Bank");
      bb.isPublic();

      Response response = webTarget
          .register(adminAuth)
          .path("bloodbank")
          .request()
          .post(Entity.json(bb));

      assertThat(response.getStatus(), is(200));
      assertEquals(response.hasEntity(), true);
 	  
      BloodBank newBloodBank = response.readEntity(new GenericType<BloodBank>(){});
 	  newBloodBankId = newBloodBank.getId();
  }

  @Test
  public void test32_get_new_bloodBankById_with_adminrole() throws JsonMappingException, JsonProcessingException {
      Response response = webTarget
          .register(adminAuth)
          .path("bloodbank").path(newBloodBankId.toString())
          .request()
          .get();
      assertThat(response.getStatus(), is(200));
      BloodBank bloodbank = response.readEntity(new GenericType<BloodBank>(){});
      assertEquals("TEST blood Bank", bloodbank.getName());  
  }
  
  @Test
  public void test33_bloodBankDELETE_with_userrole() throws JsonMappingException, JsonProcessingException {
  	Response response = webTarget
  		.register(userAuth)
          .path("bloodbank").path(newBloodBankId.toString())
          .request()
          .delete();
  	assertThat(response.getStatus(), is(401));
  }
  
  @Test
  public void test34_bloodBankDELETE_with_adminrole() throws JsonMappingException, JsonProcessingException {
  	Response response = webTarget
  		.register(adminAuth)
          .path("bloodbank").path(newBloodBankId.toString())
          .request()
          .delete();
  	assertThat(response.getStatus(), is(200));
  	assertEquals(response.hasEntity(), true);
  }
    
//  @Test
//  public void test35_personPOST_with_adminrole() throws JsonMappingException, JsonProcessingException {
//      
//  	Person personTest = new Person();
//  	personTest.setFullName("Test", "JUnit");
//
//  	Response response = webTarget
//          .register(adminAuth)
//          .path("person")
//          .request()
//          .post(Entity.json(personTest));
//  	 
//  	assertThat(response.getStatus(), is(200));
//  	assertEquals(response.hasEntity(), true);
//     
//  	Person newBloodBank = response.readEntity(new GenericType<Person>(){});
//	newPersonId = newBloodBank.getId();
//  
//  }
//  
//  @Test
//  public void test36_get_new_personById_with_adminrole() throws JsonMappingException, JsonProcessingException {
//
//   	Response response = webTarget
//          .register(adminAuth)
//          .path("person/").path("2")
//          .resolveTemplate(HOST, APPLICATION_CONTEXT_ROOT)
//          .request()
//          .get();
//      assertThat(response.getStatus(), is(200));
//      Person person = response.readEntity(new GenericType<Person>(){});
//      assertEquals("Test", person.getFirstName());
//      assertEquals("JUnit", person.getLastName());
//  }

}