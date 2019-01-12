/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.ivss.service;

import com.djt.cvpp.ota.ivss.exception.IvssException;

/**
*
* @author tmyers1@yahoo.com (Tom Myers)
*
*/
public interface IvssDigitalSigningAdapterService {

	/** Used for unique identification of exceptions thrown */
	String BOUNDED_CONTEXT_NAME = "IVSS";
	
	/** Used for unique identification of exceptions thrown */
	String SERVICE_NAME = "COMMUNICATOR";
	
	/**
	 * 
	 * @param fesn
	 * @param payload
	 * @return
	 * @throws IvssException
	 */
	byte[] signBytes(
		String fesn,
		byte[] payload)
	throws 
		IvssException;
}
