/**
 * File: PhoneResource.java Course materials (21S) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
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
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.Phone;

@Path("phone")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PhoneResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;
	
	@GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getPhones() {
		LOG.debug("retrieving all phones ...");
		List<Phone> phones = service.getAll(Phone.class, "Phone.findAll");
		LOG.debug("Phones found = {}", phones);
		Response response = Response.ok(phones).build();
		return response;
	}
	
	@GET
	@RolesAllowed({ADMIN_ROLE, USER_ROLE})
	@Path("/{phoneID}")
	public Response getPhoneById(@PathParam("phoneID") int phoneId) {
		LOG.debug("Retrieving phone with id = {}", phoneId);
		Phone phone = service.getById(Phone.class, Phone.PHONES_QUERY_BY_ID, phoneId);
		Response response = Response.ok(phone).build();
		return response;
	}
	
	@RolesAllowed({ADMIN_ROLE})
	@POST
	public Response addPhone(Phone newPhone) {
		LOG.debug("Adding a new phone = {}", newPhone);
		Phone tempPhone = service.persistPhone(newPhone);
		return Response.ok( tempPhone).build();
	}
	
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path("/{phoneID}")
	public Response deletePhone(@PathParam("phoneID") int phoneID) {
		LOG.debug("Deleting a specific phone with id = {}", phoneID);
		Phone phone = service.deletePhoneById(phoneID);
		Response response = Response.status(phone == null ? Status.NOT_FOUND : Status.OK).entity(phone).build();
		return response;
	}
}

