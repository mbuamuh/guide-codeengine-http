// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.client.SystemClient;

@RequestScoped
@Path("/systems")
public class InventoryResource {

	@Inject
	InventoryManager manager;

	@Inject
	SystemClient systemClient;

	@POST
	@Path("/{hostname}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPropertiesToInventory(@PathParam("hostname") String hostname, List<String> dataQuery) {
		// Get properties for host
		Properties systemAppProperties = systemClient.getProperties(hostname);
		if (systemAppProperties == null) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("{ \"error\" : \"Unknown hostname or the system service " + "may not be running on "
							+ hostname + "\" }")
					.build();
		}
		if (dataQuery == null || dataQuery.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity(
					"{ \"error\" : \"Method has been called with no property keys to query from the system service\" }")
					.build();
		}

		/**
		 * Retrieve only queried properties, inorder to add these to the inventory
		 * 
		 */
		HashMap<String, String> queriedProperties = new HashMap<String, String>();
		for (String key : dataQuery) {
			String noValue = "The property with key " +key+ " does not exist in the system application";
			queriedProperties.put(key, systemAppProperties.getProperty(key, noValue));
		}
		
		// Add os.name and user.name system properties
		queriedProperties.put("os.name", systemAppProperties.getProperty("os.name"));
		queriedProperties.put("user.name", systemAppProperties.getProperty("user.name"));
		
		// Add to queried properties to inventory and return them from the POST method
		manager.add(hostname, queriedProperties);
		return Response.ok(queriedProperties).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public InventoryList listContents() {
		return manager.list();
	}

	@POST
	@Path("/reset")
	public void reset() {
		manager.reset();
	}
}
