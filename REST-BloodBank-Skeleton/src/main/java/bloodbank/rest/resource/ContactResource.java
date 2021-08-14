/***************************************************************************
 * File: ContactResource.java Course materials (21F) CST 8277
 * 
 * Created by Students:
 * 	@author Chrishanthi Michael
 * 	@author Marcelo Monteiro da Silva
 * 	@author Janio Mendonca Junior
 * 	@author Parnoor Singh Gill
 * 
 * @date 13/08/2021
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.Contact;

@Path("contact")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContactResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;
	
	@GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getContacts() {
		LOG.debug("retrieving all contacts ...");
		List<Contact> contacts = service.getAll(Contact.class, "Contact.findAll");
		LOG.debug("Contacts found = {}", contacts);
		Response response = Response.ok(contacts).build();
		return response;
	}
	
	@GET
	@RolesAllowed({ADMIN_ROLE, USER_ROLE})
	@Path("/{contactID}")
	public Response getContactById(@PathParam("contactID") int contactID) {
		LOG.debug("Retrieving contacts with id = {}", contactID);
		Contact contact = service.getById(Contact.class, Contact.ADDRESS_FOR_OWNING_PERSON_CONTACT, contactID);
		Response response = Response.ok(contact).build();
		return response;
	}
	
	@RolesAllowed({ADMIN_ROLE})
	@POST
	public Response addContact(Contact newContact) {
		LOG.debug("Adding a new contacts = {}", newContact);
		Contact tempContact = service.persistContact(newContact);
		return Response.ok( newContact).build();
	}
	
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path("/{contactID}")
	public Response deleteContact(@PathParam("contactID") int contactID) {
		LOG.debug("Deleting a specific contact with id = {}", contactID);
		Contact contact = service.getById(Contact.class, Contact.ADDRESS_FOR_OWNING_PERSON_CONTACT, contactID);
		service.deleteContactById(contactID);
		Response response = Response.ok(contact).build();
		return response;
	}
	
}