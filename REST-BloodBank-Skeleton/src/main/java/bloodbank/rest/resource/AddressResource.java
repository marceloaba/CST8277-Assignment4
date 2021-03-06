/***************************************************************************
 * File: AddressResource.java Course materials (21F) CST 8277
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
import bloodbank.entity.Address;

@Path("address")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AddressResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;
	
	@GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getAddresses() {
		LOG.debug("retrieving all addresses ...");
		List<Address> addresses = service.getAll(Address.class, "Address.findAll");
		LOG.debug("Addresses found = {}", addresses);
		Response response = Response.ok(addresses).build();
		return response;
	}
	
	@GET
	@RolesAllowed({ADMIN_ROLE, USER_ROLE})
	@Path("/{addressID}")
	public Response getAddressById(@PathParam("addressID") int addressId) {
		LOG.debug("Retrieving addresse with id = {}", addressId);
		Address address = service.getById(Address.class, Address.SPECIFIC_ADDRESSES_QUERY_NAME, addressId);
		Response response = Response.status(address == null ? Status.NOT_FOUND : Status.OK).entity(address).build();
		return response;
	}
	
	@RolesAllowed({ADMIN_ROLE})
	@POST
	public Response addAddress(Address newAddress) {
		LOG.debug("Adding a new address = {}", newAddress);
		Address tempAddress = service.persistAddress(newAddress);
		return Response.ok( tempAddress).build();
	}
	
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path("/{addressID}")
	public Response deleteAddress(@PathParam("addressID") int addressID) {
		LOG.debug("Deleting a specific address with id = {}", addressID);
		Address address = service.deleteAddressById(addressID);
		Response response = Response.status(address == null ? Status.NOT_FOUND : Status.OK).entity(address).build();
		return response;
	}
	
}
