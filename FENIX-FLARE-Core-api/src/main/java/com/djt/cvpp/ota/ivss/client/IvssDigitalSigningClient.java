/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.ivss.client;

import com.djt.cvpp.ota.ivss.exception.IvssException;

/**
*
* @author tmyers1@yahoo.com (Tom Myers)
*
*/
public interface IvssDigitalSigningClient {

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
