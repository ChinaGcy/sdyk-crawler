package com.sdyk.ai.crawler.util.test;

import com.google.common.collect.ImmutableMap;
import com.sdyk.ai.crawler.ServiceWrapper;
import com.sdyk.ai.crawler.account.AccountManager;
import com.sdyk.ai.crawler.account.model.AccountImpl;
import com.sdyk.ai.crawler.specific.mihuashi.task.modelTask.ProjectTask;
import com.sdyk.ai.crawler.specific.zbj.AuthorizedRequester;
import com.sdyk.ai.crawler.specific.zbj.task.modelTask.GetProjectContactTask;
import one.rewind.io.requester.account.Account;
import one.rewind.io.requester.chrome.ChromeDriverAgent;
import one.rewind.io.requester.chrome.action.LoginWithGeetestAction;
import one.rewind.io.requester.task.ChromeTask;
import org.junit.Test;
import spark.Spark;

import java.util.List;

import static spark.Spark.port;
import static spark.route.HttpMethod.get;

public class SparkJavaTest {

	@Test
	public void post() throws InterruptedException {
		/*post("/", (request, respones) -> {
			return ;
		});*/

		port(80);

		// matches "GET /hello/foo" and "GET /hello/bar"
		// request.params(":name") is 'foo' or 'bar'
		/*Spark.get("/hello/:name", (request, response) -> {
			request.body();

			return "Hello: " + request.params(":name")+ response.body();
		});*/

		Spark.post("/hello/:name", (request, response) -> {

		return "Hello: " + request.params(":name")+ response.body();
	});

		Thread.sleep(500000);
	}

	@Test
	public void getPhone() throws Exception {

		Account account = AccountManager.getInstance().getAccountsByDomain("zbj.com", "select");

		ChromeTask task = new ProjectTask("https://www.zbj.com");

		task.addAction(new LoginWithGeetestAction().setAccount(account));

		// 不使用代理
		ChromeDriverAgent agent = new ChromeDriverAgent();

		// agent 添加异常回调
		agent.addAccountFailedCallback((a, c)->{

		}).addTerminatedCallback((a)->{

		}).addNewCallback((a)->{

			try {
				a.submit(task, true);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		AuthorizedRequester.getInstance().addAgent(agent);
		agent.start();


		/*ChromeTaskHolder holder = ChromeTask.buildHolder(GetProjectContactTask.class, ImmutableMap.of("user_id", String.class,"case_id", String.class));

		AuthorizedRequester.getInstance().submit(holder);*/

		ServiceWrapper.getInstance();

		Thread.sleep(1000000);
	}

	@Test
	public void testRount() throws InterruptedException {
		ServiceWrapper.getInstance();
		Thread.sleep(1000000);
	}

	@Test
	public void testDao() throws Exception {


		List<AccountImpl> accounts = (List<AccountImpl>) AccountManager.getInstance().getAccountsByDomain("zbj.com", null);

		//Project project = DaoManager.getDao(Project.class).queryForId("984f9e1224cad0f27bcbaea18aef85d4");

		System.err.println(accounts.size());
	}
}
