package api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
public class ApiResource {
	
	TA35 TA35;
	
	public ApiResource() {
		TA35 = TA35.getInstance();
	}
	
	@Path("/ta35")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getData() {
		return String.valueOf(TA35.getData());
	}

}
