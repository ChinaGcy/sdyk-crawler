package com.sdyk.ai.crawler;

import com.sdyk.ai.crawler.zbj.task.Task;
import com.sdyk.ai.crawler.zbj.task.modelTask.ProjectTask;
import net.lightbody.bmp.BrowserMobProxyServer;
import one.rewind.io.docker.model.ChromeDriverDockerContainer;
import one.rewind.io.docker.model.DockerHost;
import one.rewind.io.requester.account.AccountImpl;
import one.rewind.io.requester.chrome.ChromeDriverAgent;
import one.rewind.io.requester.chrome.ChromeDriverRequester;
import one.rewind.io.requester.chrome.action.LoginWithGeetestAction;
import one.rewind.io.requester.exception.ChromeDriverException;
import one.rewind.io.requester.proxy.Proxy;
import one.rewind.io.requester.proxy.ProxyImpl;
import org.junit.Test;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static one.rewind.io.requester.chrome.ChromeDriverRequester.buildBMProxy;

public class RemoteDriverTest {

	@Test
	public void batchTest() throws Exception {
		for(int i=0; i<20; i++) {
			remoteTest();
		}
	}


	@Test
	public void remoteTest() throws Exception {

		DockerHost host = new DockerHost("10.0.0.62", 22, "root");
		host.delAllDockerContainers();

		//
		List<ChromeDriverDockerContainer> containers = new ArrayList<>();
		for(int i=0; i<1; i++) {
			containers.add(host.createChromeDriverDockerContainer());
		}

		ChromeDriverRequester requester = ChromeDriverRequester.getInstance();

		//
		for(ChromeDriverDockerContainer container : containers) {

			final Proxy proxy = new ProxyImpl("10.0.0.51", 59998, "tfelab", "TfeLAB2@15");
			final URL remoteAddress = container.getRemoteAddress();

			new Thread(() -> {
				try {

					proxy.validate();

					ChromeDriverAgent agent = new ChromeDriverAgent(remoteAddress, container, proxy);
					//ChromeDriverAgent agent = new ChromeDriverAgent(remoteAddress);

					Task task = new Task("http://zbj.com");
					AccountImpl account = new AccountImpl("zbj.com", "17600668061", "gcy116149");
					task.addAction(new LoginWithGeetestAction(account));

					agent.addNewCallback(()->{
						try {
							agent.submit(task);
						} catch (ChromeDriverException.IllegalStatusException e) {
							e.printStackTrace();
						}
					});

					requester.addAgent(agent);

					agent.start();

				} catch (ChromeDriverException.IllegalStatusException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}).start();

		}

		/*for(int i=0; i<100; i++) {

			// Task task = new Task("https://www.google.com.sg/search?q=1" + (1050 + i));

			Task task = new Task("https://zbj.com");
			requester.submit(task);
		}*/

		Thread.sleep(600000);

		//requester.close();
	}

	@Test
	public void testBuildProxyServer() throws InterruptedException, UnknownHostException {

		Proxy proxy = new ProxyImpl("scisaga.net", 60103, "tfelab", "TfeLAB2@15");
		BrowserMobProxyServer ps = buildBMProxy(proxy);
		System.err.println(ps.getClientBindAddress());
		System.err.println(ps.getPort());
		Thread.sleep(100000);
	}

	@Test
	public void testGetLocalAddress() throws UnknownHostException {
		System.err.println(InetAddress.getLocalHost());
	}

	@Test
	public void ContainerTest() throws Exception {

		AccountImpl account = new AccountImpl("zbj", "17600668061", "gcy116149");

		DockerHost host = new DockerHost("10.0.0.62", 22, "root");
		host.delAllDockerContainers();
		ChromeDriverDockerContainer container = host.createChromeDriverDockerContainer();
		ChromeDriverRequester requester = ChromeDriverRequester.getInstance();
		final Proxy proxy = new ProxyImpl("10.0.0.51", 59998, "tfelab", "TfeLAB2@15");
		final URL remoteAddress = container.getRemoteAddress();

		ChromeDriverAgent agent = new ChromeDriverAgent(remoteAddress, container, proxy);
		requester.addAgent(agent);

		agent.start();

		Task task = new Task("http://zbj.com");
		task.addAction(new LoginWithGeetestAction(account));
		agent.submit(task);

		// https://task.zbj.com/13369135/
		agent.submit(new ProjectTask("https://task.zbj.com/13371103/"));

		//agent.submit(new ProjectTask("https://task.zbj.com/13369135/"));

		Thread.sleep(10000);

	}

	/**
	 * 每次执行task任务时都会报异常，使chromedriver重新启动，
	 * 在执行到最后一个task也会重新启动一个chromedriver
	 * @throws Exception
	 */
	@Test
	public void RemoteChromeDriverTest() throws Exception {

		DockerHost host = new DockerHost("10.0.0.62", 22, "root");
		host.delAllDockerContainers();
		ChromeDriverDockerContainer container = host.createChromeDriverDockerContainer();
		ChromeDriverRequester requester = ChromeDriverRequester.getInstance();
		final URL remoteAddress = container.getRemoteAddress();
		ChromeDriverAgent agent = new ChromeDriverAgent(remoteAddress, container);
		requester.addAgent(agent);

		agent.start();
		Task task = new Task("http://www.baidu.com/s");

		for (int i = 0; i < 5; i++) {
			requester.submit(task);
		}
		Thread.sleep(240000);
	}

	/**
	 *
	 */
}
