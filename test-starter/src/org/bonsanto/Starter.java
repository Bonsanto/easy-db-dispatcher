package org.bonsanto;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Starter {
	public static void main(String[] argv) {
		//Read the endpoints.properties to get the endpoints to call.
		Properties endpoints = new Properties();
		FileInputStream stream;
		int intensity = 10;

		try {
			stream = new FileInputStream("endpoints.properties");
			endpoints.load(stream);

			//Get the endpoints
			String[] urls = endpoints.getProperty("endpoints").split(",");

			for (String url : urls) {
				Runnable requester = new RequesterThread(url, intensity);
				new Thread(requester).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
