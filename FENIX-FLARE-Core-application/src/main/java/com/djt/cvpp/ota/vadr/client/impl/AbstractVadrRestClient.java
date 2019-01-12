/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.vadr.client.impl;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author tmyers28 (Tom Myers)
 * 
 * https://stackoverflow.com/questions/17619871/access-https-rest-service-using-spring-resttemplate
 *
 */
public class AbstractVadrRestClient {
	
	@Value("${vadr.domain.base.url}")
	protected String baseUri;
	
	@Value("${client.orfin.username}")
	protected String username;
	
	@Value("${client.orfin.password}")
	protected String password;
	
	@Value("${client.orfin.trustAllCerts}")
	protected Boolean trustAllCerts = Boolean.TRUE;
	
	public AbstractVadrRestClient() {
	}
	
	protected String buildEndpointUri(String endpointUri, String operationUri) {
		return new StringBuilder().append(this.baseUri).append(endpointUri).append(operationUri).toString();
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		
		CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);		
	    return restTemplate;
	}	
}
