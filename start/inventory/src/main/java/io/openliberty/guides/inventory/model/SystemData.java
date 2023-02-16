// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2022, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory.model;

import java.util.Properties;

public class SystemData {

	public String hostname;
    public Properties properties;
    
    public SystemData() {
      }

    public SystemData(String hostname, Properties properties) {
      this.hostname = hostname;
      this.properties = properties;
    }

    public String getHostname() {
      return hostname;
    }

    public Properties getProperties() {
      return properties;
    }

    @Override
    public boolean equals(Object host) {
      if (host instanceof SystemData) {
        return hostname.equals(((SystemData) host).getHostname());
      }
      return false;
    }
}
