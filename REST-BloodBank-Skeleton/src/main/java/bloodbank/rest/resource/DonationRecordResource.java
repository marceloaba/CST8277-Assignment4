package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.CUSTOMER_ADDRESS_RESOURCE_PATH;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.Address;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.SecurityUser;

@Path("donationrecord")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DonationRecordResource {
	
	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getDonationRecords() {
		LOG.debug("retrieving all records ...");
		List<DonationRecord> records = service.getAll(DonationRecord.class,  DonationRecord.ALL_RECORDS_QUERY_NAME);
		Response response = Response.ok(records).build();
		return response;
	}

	@GET
	@RolesAllowed({ADMIN_ROLE, USER_ROLE})
	@Path("/{donationRecordID}")
	public Response getDonationRecordById(@PathParam("donationRecordID") int id) {
		LOG.debug("try to retrieve specific record " + id);
		
		DonationRecord record = service.getById(DonationRecord.class, DonationRecord.ID_RECORD_QUERY_NAME, id);
		Response response = Response.ok(record).build();
		return response;
	}

	@POST
	@RolesAllowed({ADMIN_ROLE})
	@Path("/person/{personID}/blooddonation/{donationID}")
	public Response addDonationRecord(DonationRecord newRecord, @PathParam("personID") int personID, @PathParam("donationID") int donationID) {
		LOG.debug("Adding a new doantionRecord = {}", newRecord);
		
		
		
		DonationRecord record = service.persistDonationRecord(newRecord,personID,donationID);
		return Response.ok( record).build();
	}

	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path("/{donationRecordID}")
	public Response deleteAddress(@PathParam("donationRecordID") int recordID) {
		LOG.debug("Deleting a specific record with id = {}", recordID);
		DonationRecord donationRecord = service.getById(DonationRecord.class, DonationRecord.ID_RECORD_QUERY_NAME, recordID);
		service.deleteDonationRecordById(recordID);
		Response response = Response.ok(donationRecord).build();
		return response;
	}
	
	

}
