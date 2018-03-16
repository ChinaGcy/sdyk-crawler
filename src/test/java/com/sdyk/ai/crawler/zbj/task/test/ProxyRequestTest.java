package com.sdyk.ai.crawler.zbj.task.test;

import com.sdyk.ai.crawler.zbj.model.Proxy;
import org.junit.Test;
import org.tfelab.io.requester.Task;
import org.tfelab.io.requester.chrome.ChromeDriverAgent;
import org.tfelab.io.requester.proxy.ProxyWrapperImpl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class ProxyRequestTest {



	@Test
	public void testRequestByProxy() throws MalformedURLException, URISyntaxException {

		Task t = new Task("https://zbj.com");

		/*Proxy pw = new Proxy();
		pw.host = "scisaga.net";
		pw.port = 60103;*/

		Proxy pw_ = new Proxy();
		pw_.host = "118.190.133.34";
		pw_.port = 59998;
		pw_.username = "tfelab";
		pw_.password = "TfeLAB2@15";

		/*t.setProxyWrapper(pw);
		ChromeDriverAgent agent = new ChromeDriverAgent();
		agent.fetch(t);*/

		ChromeDriverAgent agent_ = new ChromeDriverAgent();
		t.setProxyWrapper(pw_);
		agent_.fetch(t);
		try {
			Thread.sleep(10*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
