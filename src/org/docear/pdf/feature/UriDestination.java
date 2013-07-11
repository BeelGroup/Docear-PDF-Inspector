package org.docear.pdf.feature;

import java.net.URI;

public class UriDestination extends APDObjectDestination {
	
	private final URI uri;
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	public UriDestination(URI dest) {
		if(dest == null) {
			throw new IllegalArgumentException("NULL");
		}
		this.uri = dest;
	}
	
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	public URI getUri() {
		return this.uri;
	}
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
