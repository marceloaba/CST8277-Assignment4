/**
 * File: RecordService.java
 * Course materials (21S) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by Students:
 * 	@author Chrishanthi Michael
 * 	@author Marcelo Monteiro da Silva
 * 	@author Janio Mendonca Junior
 * 	@author Parnoor Singh Gill
 * 
 * Creation date: 13/08/2021
 */
package bloodbank.ejb;

import static bloodbank.entity.BloodBank.SPECIFIC_BLOODBANKS_QUERY_NAME;
import static bloodbank.entity.BloodBank.IS_DUPLICATE_QUERY_NAME;
import static bloodbank.entity.SecurityRole.ROLE_BY_NAME_QUERY;
import static bloodbank.entity.SecurityUser.USER_FOR_OWNING_PERSON_QUERY;
import static bloodbank.utility.MyConstants.DEFAULT_KEY_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.DEFAULT_SALT_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PARAM1;
import static bloodbank.utility.MyConstants.PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.PROPERTY_KEYSIZE;
import static bloodbank.utility.MyConstants.PROPERTY_SALTSIZE;
import static bloodbank.utility.MyConstants.PU_NAME;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.Contact;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.Phone;
import bloodbank.entity.SecurityRole;
import bloodbank.entity.SecurityUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Stateless Singleton ejb Bean - BloodBankService
 */
@Singleton
public class BloodBankService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Person> getAllPeople() {
    	//Added the following code
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Person> cq = cb.createQuery(Person.class);
    	cq.select(cq.from(Person.class));
    	return em.createQuery(cq).getResultList();
    }

    public Person getPersonId(int id) {
    	return em.find(Person.class, id);
    }

    @Transactional
    public Person persistPerson(Person newPerson) {
    	em.persist(newPerson);
    	return newPerson;
    }

    @Transactional
    public void buildUserForNewPerson(Person newPerson) {
        SecurityUser userForNewPerson = new SecurityUser();
        userForNewPerson.setUsername(
            DEFAULT_USER_PREFIX + "_" + newPerson.getFirstName() + "." + newPerson.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewPerson.setPwHash(pwHash);
        userForNewPerson.setPerson(newPerson);
        SecurityRole userRole = em.createNamedQuery(ROLE_BY_NAME_QUERY, SecurityRole.class)
            .setParameter(PARAM1, USER_ROLE).getSingleResult();
        userForNewPerson.getRoles().add(userRole);
        userRole.getUsers().add(userForNewPerson);
        em.persist(userForNewPerson);
    }

    @Transactional
    public Address setAddressForPersonPhone(int personId, int phoneId, Address newAddress) {
    	Person personToBeUpdated = em.find(Person.class, personId);
    	if (personToBeUpdated != null) { // Person exists
    		Set<Contact> contacts = personToBeUpdated.getContacts();
    		contacts.forEach(c -> {
    			if (c.getPhone().getId() == phoneId) {
    				if (c.getAddress() != null) { // Address exists
    					Address addr = em.find(Address.class, c.getAddress().getId());
    					addr.setAddress(newAddress.getStreetNumber(),
    							        newAddress.getStreet(),
    							        newAddress.getCity(),
    							        newAddress.getProvince(),
    							        newAddress.getCountry(),
    							        newAddress.getZipcode());
    					em.merge(addr);
    					
    				}
    				else { // Address does not exist
    					c.setAddress(newAddress);
    					em.merge(personToBeUpdated);
    				}
    			}
    		});
    		return newAddress;
    	}
    	else // Person doesn't exist
    		return null;
    }

    /**
     * to update a person
     * 
     * @param id - id of entity to update
     * @param personWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Person updatePersonById(int id, Person personWithUpdates) {
        Person personToBeUpdated = getPersonId(id);
        if (personToBeUpdated != null) {
            em.refresh(personToBeUpdated);
            em.merge(personWithUpdates);
            em.flush();
        }
        return personToBeUpdated;
    }

    /**
     * to delete a person by id
     * 
     * @param id - person id to delete
     */
    @Transactional
    public void deletePersonById(int id) {
        Person person = getPersonId(id);
        if (person != null) {
            em.refresh(person);
            TypedQuery<SecurityUser> findUser = em
                .createNamedQuery(USER_FOR_OWNING_PERSON_QUERY, SecurityUser.class)
                .setParameter(PARAM1, person.getId());
            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(person);
        }
    }
    
    public List<BloodBank> getAllBloodBanks() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<BloodBank> cq = cb.createQuery(BloodBank.class);
    	cq.select(cq.from(BloodBank.class));
    	return em.createQuery(cq).getResultList();
    }
    
   
    public BloodBank getBloodBankById(int bloodBankId) {
    	TypedQuery<BloodBank> specificBloodBankQuery = em.createNamedQuery(SPECIFIC_BLOODBANKS_QUERY_NAME, BloodBank.class);
    	specificBloodBankQuery.setParameter(PARAM1, bloodBankId);
        return specificBloodBankQuery.getSingleResult();
    }
    
    // These 2 methods are more generic.
    // BEGIN
    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
    	TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
    	return allQuery.getResultList();
    }
    
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
    	TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
    	allQuery.setParameter(PARAM1, id);
    	T result = null;
    	try{
    		result = allQuery.getSingleResult();
    	}
    	catch(NoResultException ex) {
    		
    	}
    	return result;
    }
    
    @Transactional
    public BloodBank deleteBloodBank(int id) {
//    	BloodBank bb = getBloodBankById(id);
    	 BloodBank bb = getById(BloodBank.class, BloodBank.SPECIFIC_BLOODBANKS_QUERY_NAME, id);
    	
    	if (bb != null) {
    		Set<BloodDonation> donations = bb.getDonations();
        	
        	// You can either delete using a new named query and delete all blood donations with the specified blood bank id (id)
        	// Or you can loop through the list and manually remove them as I am doing below
        	
        	List<BloodDonation> list = new LinkedList<>();
        	donations.forEach(list::add);
        	
        	list.forEach(bd -> {
        		if (bd.getRecord() != null) {
        			DonationRecord dr = getById(DonationRecord.class, DonationRecord.ID_RECORD_QUERY_NAME, bd.getRecord().getId());
        			dr.setDonation(null);
        		}
        		bd.setRecord(null);
        		em.merge(bd);
        	});
        	
        	em.remove(bb);
    	}
    	
    	return bb;

    }
    
    // Please try to understand and test the below:
    public boolean isDuplicated(BloodBank newBloodBank) {
        TypedQuery<Long> allBloodBankQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allBloodBankQuery.setParameter(PARAM1, newBloodBank.getName());
        return (allBloodBankQuery.getSingleResult() >= 1);
    }

    @Transactional
    public BloodBank persistBloodBank(BloodBank newBloodBank) {
        em.persist(newBloodBank);
        return newBloodBank;
    }

    @Transactional
    public BloodBank updateBloodBank(int id, BloodBank updatingBloodBank) {
        BloodBank bloodBankToBeUpdated = getBloodBankById(id);
        if (bloodBankToBeUpdated != null) {
            em.refresh(bloodBankToBeUpdated);
            em.merge(updatingBloodBank);
            em.flush();
        }
        return bloodBankToBeUpdated;
    }
    
    @Transactional
    public BloodDonation persistBloodDonation(BloodDonation newBloodDonation) {
        em.persist(newBloodDonation);
        return newBloodDonation;
    }

    public BloodDonation getBloodDonationById(int prodId) {
        TypedQuery<BloodDonation> allBloodDonationQuery = em.createNamedQuery(BloodDonation.FIND_BY_ID, BloodDonation.class);
        allBloodDonationQuery.setParameter(PARAM1, prodId);
        return allBloodDonationQuery.getSingleResult();
    }

    @Transactional
    public BloodDonation updateBloodDonation(int id, BloodDonation bloodDonationWithUpdates) {
        BloodDonation bloodDonationToBeUpdated = getBloodDonationById(id);
        if (bloodDonationToBeUpdated != null) {
            em.refresh(bloodDonationToBeUpdated);
            em.merge(bloodDonationWithUpdates);
            em.flush();
        }
        return bloodDonationToBeUpdated;
    }
    
    // ADDRESSES Services
    @Transactional
    public Address persistAddress(Address newAddress) {
        em.persist(newAddress);
        return newAddress;
    }
    
    @Transactional
    public DonationRecord persistDonationRecord(DonationRecord newRecord, int personID, int donationID) {
    	
    	
		Person person = getPersonId(personID);
		BloodDonation donation = getBloodDonationById(donationID);
    	
    	newRecord.setOwner(person);
		newRecord.setDonation(donation);
		//donation.setRecord(newRecord);
		//em.merge(person);
		//Set<DonationRecord> records = new HashSet<>();
		//records.add(newRecord);
		//person.setDonations( records);
		//em.merge(person);
		//em.merge(donation);
    	
        em.persist(newRecord);
        return newRecord;
    }
    
    @Transactional
    public Address deleteAddressById(int addressID) {
    	Address address = getById(Address.class, Address.SPECIFIC_ADDRESSES_QUERY_NAME, addressID);
    	Contact contact = null;
//    	em.refresh(address);
        if (address != null) {
            em.refresh(address);
            TypedQuery<Contact> findContact = em
                .createNamedQuery(Contact.ADDRESS_FOR_OWNING_PERSON_CONTACT, Contact.class)
                .setParameter(PARAM1, address.getId());
            try {
            	contact = findContact.getSingleResult();
            }
            catch(NoResultException ex) {
            	
            }
            LOG.debug("Contact found = {}", contact);
            if (contact != null) {
            	contact.setAddress(null);
                em.merge(contact);
            }
            em.remove(address);
            LOG.debug("Address deleted = {}", address);
            
        }
        return address;
    }    
    
    @Transactional
    public void deleteDonationRecordById(int donationId) {
    	DonationRecord record = getById(DonationRecord.class, DonationRecord.ID_RECORD_QUERY_NAME, donationId);
        if (record != null) {
            em.remove(record);
        }
    }
    
    @Transactional
    public void deleteBloodDonationById(int donationID) {
    	
    	//DonationRecord record = getById(DonationRecord.class, DonationRecord.ID_RECORD_QUERY_NAME, donationID);
    	BloodDonation donation= getById(BloodDonation.class, BloodDonation.FIND_BY_ID, donationID);
        if (donation != null) {
        	
        	if(donation.getRecord()!=null)
                donation.getRecord().setDonation(null);
                
                
            em.remove(donation);
        }
    }
    
    @Transactional
    public void deletePhoneById2(int phoneID) {
    	Phone phone = getById(Phone.class,"select ", phoneID);
        if (phone != null) {
            em.refresh(phone);
            TypedQuery<Contact> findContact = em
                .createNamedQuery(Contact.ADDRESS_FOR_OWNING_PERSON_CONTACT, Contact.class)
                .setParameter(PARAM1, phone.getId());
            Contact contact = findContact.getSingleResult();
            contact.setPhone(null);
            em.merge(contact);
            em.remove(phone);
        }
    }
    
    @Transactional
    public Phone deletePhoneById(int phoneID) {
    	Phone phone = getById(Phone.class, Phone.PHONES_QUERY_BY_ID, phoneID);
    	Contact contact = null;
        if (phone != null) {
            em.refresh(phone);
            TypedQuery<Contact> findContact = em
                .createNamedQuery(Contact.ADDRESS_FOR_OWNING_PERSON_CONTACT, Contact.class)
                .setParameter(PARAM1, phone.getId());
            try {
            	contact = findContact.getSingleResult();
            }
            catch(NoResultException ex) {
            	
            }
            LOG.debug("Contact found = {}", contact);
            if (contact != null) {
            	contact.setPhone(null);
            	em.merge(contact);
            }
            em.remove(phone);
            LOG.debug("Phone deleted = {}", phone);
        }
        return phone;
    }
     
    @Transactional
    public Phone persistPhone(Phone newPhone) {
    	em.persist(newPhone);
    	return newPhone;
    }
    
    @Transactional
    public void deleteContactById(int contactID) {
    	Contact contact = getById(Contact.class, Contact.ADDRESS_FOR_OWNING_PERSON_CONTACT, contactID);
        if (contact != null) {
            contact.setPhone(null);
            contact.setAddress(null);
            em.merge(contact);
            em.remove(contact);
        }
    }
    
    @Transactional
    public Contact persistContact(Contact newContact) {
    	em.persist(newContact);
    	return newContact;
    }
}