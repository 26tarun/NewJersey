package com.example;

import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class WrapperResource {
	private static final ConcurrentHashMap<Integer, AsyncResponse> suspended = new ConcurrentHashMap<Integer, AsyncResponse>(
			500);
	private String thisID;

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt(String id, @Suspended final AsyncResponse asyncResponse) {

		thisID = id;
		try {

			// suspended.put(Integer.valueOf(thisID), asyncResponse);

			new Thread(new Runnable() {
				@Override
				public void run() {
					String result = veryExpensiveOperation();
					asyncResponse.resume(result);
				}

				private String veryExpensiveOperation() {
					Client client = ClientBuilder.newClient();
					WebTarget wt = client.target("http://localhost:9000/").path("mainApp/core");
					thisID = wt.request(MediaType.TEXT_PLAIN).get(String.class);
					System.out.println("Request is being processed asynchronously.:" + thisID);
					return thisID;
				}
			}).start();
		} catch (Exception e) {

		}
		return thisID;
	}

	// @POST
	// @Produces(MediaType.TEXT_PLAIN)
	// public String postIt(final String message) {
	// try {
	// System.out.println("this is the message " + message);
	// final AsyncResponse ar = suspended.get(Integer.valueOf(message));
	// ar.resume(message); // resumes the processing of one GET request
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return "Message sent";
	// }

}
