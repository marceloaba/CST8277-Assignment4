/**
 * File: ClientErrorExceptionMapper.java
 * Course materials (21S) CST 8277
 * 
 * @author Teddy Yap
 * 
 * @author Shariar (Shawn) Emami
 * @date Mar 31, 2021
 * 
 * @author Mike Norman
 * @date 2020 10
 * 
 * Students:
 * 	@author Chrishanthi Michael
 * 	@author Marcelo Monteiro da Silva
 * 	@author Janio Mendonca Junior
 * 	@author Parnoor Singh Gill
 * 
 * @date 13/08/2021
 */
package bloodbank.rest;

import bloodbank.rest.resource.HttpErrorResponse;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Create a Jackson exception instead of the default Payara HTML response.<br>
 * This exception is mapped using "@{@link Provider}".<br>
 * This is not needed, optional design.
 * 
 * 
 * @see <a href="https://javaee.github.io/javaee-spec/javadocs/javax./ws/rs/ClientErrorException.html">JavaEE 8 ClientErrorException</a>
 * @see <a href="https://javaee.github.io/javaee-spec/javadocs/javax./ws/rs/ext/ExceptionMapper.html">JavaEE 8 ExceptionMapper</a>
 */
@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {
    
    @Override
    public Response toResponse(ClientErrorException exception) {
      Response response = exception.getResponse();
      Response.StatusType statusType = response.getStatusInfo();
      int statusCode = statusType.getStatusCode();
      String reasonPhrase = statusType.getReasonPhrase();
      HttpErrorResponse entity = new HttpErrorResponse(statusCode, reasonPhrase);
      return Response.status(statusCode).entity(entity).build();
    }
}