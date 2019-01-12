/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.ivss.service.impl;

import com.djt.cvpp.ota.ivss.client.IvssDigitalSigningClient;
import com.djt.cvpp.ota.ivss.exception.IvssException;
import com.djt.cvpp.ota.ivss.service.IvssDigitalSigningAdapterService;

/**
*
* @author tmyers1@yahoo.com (Tom Myers)
*
*/
public class IvssDigitalSigningAdapterServiceImpl implements IvssDigitalSigningAdapterService {

	private IvssDigitalSigningClient ivssDigitalSigningClient;

	
	public IvssDigitalSigningAdapterServiceImpl() {
	}

	public IvssDigitalSigningAdapterServiceImpl(IvssDigitalSigningClient ivssDigitalSigningClient) {
		this.ivssDigitalSigningClient = ivssDigitalSigningClient;
	}
	
	/**
	 * 
	 * @param fesn
	 * @param payload
	 * @return
	 * @throws IvssException
	 */
	public byte[] signBytes(
		String fesn,
		byte[] payload)
	throws 
		IvssException {
		
		return this.ivssDigitalSigningClient.signBytes(fesn, payload);
	}
}
