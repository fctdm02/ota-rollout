/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.djt.cvpp.ota.flare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author tmyers1@yahoo.com (Tom Myers)
 *
 */
@SpringBootApplication
@ComponentScan("com.djt.cvpp.ota.flare,com.djt.cvpp.ota.orfin.client")
public class FenixFlareCoreApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(FenixFlareCoreApplication.class, args);
	}
}
