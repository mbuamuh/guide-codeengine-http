// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SystemEndpointIT {

    private static String systemUrl;

    private Client client;
    private Response response;

    @BeforeAll
    public static void oneTimeSetup() {
        String systemHostname = System.getProperty("system.host");
       
        systemUrl = "https://" + systemHostname + "/system/properties/";
    }

    @BeforeEach
    public void setup() {
        response = null;
        client = ClientBuilder.newBuilder()
                    .hostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();
    }

    @AfterEach
    public void teardown() {
        client.close();
    }

    @Test
    @Order(1)
    public void testPodNameNotNull() {
        response = this.getResponse(systemUrl);
        this.assertResponse(systemUrl, response);
        String greeting = response.getHeaderString("server");

        assertNotNull(greeting,
            "Container name should not be null but it was."
            + "The service is probably not running inside a container");
    }

    @Test
    @Order(2)
    public void testGetProperties() {
        Client client = ClientBuilder.newClient();

        WebTarget target = client.target(systemUrl);
        Response response = target.request().get();

        assertEquals(200, response.getStatus(),
                     "Incorrect response code from " + systemUrl);
        response.close();
    }

    private Response getResponse(String url) {
        return client
            .target(url)
            .request()
            .header("Host", System.getProperty("system.host"))
            .get();
    }

    private void assertResponse(String url, Response response) {
        assertEquals(200, response.getStatus(),
            "Incorrect response code from " + url);
    }
}
