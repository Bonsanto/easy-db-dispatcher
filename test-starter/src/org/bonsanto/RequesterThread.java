package org.bonsanto;

import java.net.MalformedURLException;
import java.net.URL;

public class RequesterThread implements Runnable {
	private URL endpoint;
	private int intensity;

	public RequesterThread(String url, int intensity) throws MalformedURLException {
		this.endpoint = new URL(url);
		this.intensity = intensity;
	}

	@Override
	public void run() {
		Test service = new TestService(this.endpoint).getTestPort();
		service.startTest(this.intensity);
	}
}
