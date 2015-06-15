package org.bonsanto;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Starter {
	public static void main(String[] argv) {
		//Read the endpoints.properties to get the endpoints to call.
		Properties endpoints = new Properties();
		InputStream stream;
		int intensity = 10;

		try {
			stream = new FileInputStream("endpoints.properties");
			endpoints.load(stream);

			//Get the endpoints
			String[] urls = endpoints.getProperty("endpoints").split(",");

			for (String url : urls) {
				Test service = new TestService(new URL(url)).getTestPort();
				service.startTest(intensity);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
