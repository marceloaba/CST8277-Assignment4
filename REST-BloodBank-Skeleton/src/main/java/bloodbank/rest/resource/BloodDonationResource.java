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
import bloodbank.entity.BloodDonation;
import bloodbank.entity.DonationRecord;

@Path("blooddonation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BloodDonationResource {
	
	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getBloodDonations() {
		LOG.debug("retrieving all blood donations ...");
		List<BloodDonation> donations = service.getAll(BloodDonation.class,  BloodDonation.FIND_ALL);
		Response response = Response.ok(donations).build();
		return response;
	}

	@GET
	@RolesAllowed({ADMIN_ROLE, USER_ROLE})
	@Path("/{bloodDonationID}")
	public Response getBloodDonationById(@PathParam("bloodDonationID") int id) {
		LOG.debug("try to retrieve specific donation " + id);
		
		BloodDonation donation = service.getById(BloodDonation.class, BloodDonation.FIND_BY_ID, id);
		Response response = Response.ok(donation).build();
		return response;
	}

	@POST
	@RolesAllowed({ADMIN_ROLE})
	public Response addBloodDonation(BloodDonation newDonation) {
		LOG.debug("Adding a new doantion = {}", newDonation);
		BloodDonation donation = service.persistBloodDonation(newDonation);
		return Response.ok( donation).build();
	}

	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path("/{bloodDonationID}")
	public Response deleteBloodDonation(@PathParam("bloodDonationID") int donationID) {
		LOG.debug("Deleting a specific donation with id = {}", donationID);
		BloodDonation donation = service.getById(BloodDonation.class, BloodDonation.FIND_BY_ID, donationID);
		service.deleteBloodDonationById(donationID);
		Response response = Response.ok(donation).build();
		return response;
	}

}
