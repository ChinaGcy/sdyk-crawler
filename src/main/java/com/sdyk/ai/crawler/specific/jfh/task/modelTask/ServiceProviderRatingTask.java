package com.sdyk.ai.crawler.specific.jfh.task.modelTask;

import com.google.common.collect.ImmutableMap;
import com.sdyk.ai.crawler.HttpTaskPoster;
import com.sdyk.ai.crawler.model.ServiceProviderRating;
import com.sdyk.ai.crawler.specific.clouderwork.util.CrawlerAction;
import com.sdyk.ai.crawler.specific.jfh.task.Task;
import one.rewind.txt.DateFormatUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ServiceProviderRatingTask extends Task {

	static {
		registerBuilder(
				ServiceProviderRatingTask.class,
				"http://shop.jfh.com/{{user_id}}",
				ImmutableMap.of("user_id", String.class),
				ImmutableMap.of("user_id", "")
		);
	}

	public ServiceProviderRatingTask(String url) throws MalformedURLException, URISyntaxException {

		super(url);

		this.setBuildDom();

		this.setPriority(Priority.HIGH);

		this.addDoneCallback((t) -> {

			String src = getResponse().getText();

			Document doc = getResponse().getDoc();

			//页面发生错误
			if( src.contains("关闭中") || src.contains("稍后再试") || src.contains("404") ){
				return;
			}
			//页面正确
			else {
				crawler(doc);
			}

		});

	}

	public void crawler(Document doc){

		List<Task> task = new ArrayList<>();

		int i =0;

		String uId = one.rewind.txt.StringUtil.byteArrayToHex(
				one.rewind.txt.StringUtil.uuid(getUrl() + "/bu"));

		Elements ratingElement = doc.select("div.companyInfomation");

		for(Element rating : ratingElement){

			ServiceProviderRating serviceProviderRating = new ServiceProviderRating(getUrl() + "?rating=" + "i");

			serviceProviderRating.service_provider_id = uId;

			//项目名称
			serviceProviderRating.project_name = rating.select("div.companyInfomationCenter > a").text();

			//价格
			String price = CrawlerAction.getNumbers(rating.select("div.companyInfomationCenter > span").text());
			if( !"".equals(price) ){
				serviceProviderRating.price = Double.valueOf(price);
			}

			//内容
			serviceProviderRating.content = rating.select("div.companyInfomationCenter > p:nth-child(4) > span:nth-child(2)")
					.toString();

			//时间
			String time = rating.select("div.companyInfomationLeft").text();
			try {
				serviceProviderRating.pubdate = DateFormatUtil.parseTime(time);
			} catch (ParseException e) {
				logger.error("error for String to Date", e);
			}

			//甲方
			serviceProviderRating.tenderer_name = rating.select("div.companyInfomationLeft > p:nth-child(1)").text();

			try{
				serviceProviderRating.insert();
			} catch (Exception e) {
				logger.error("error for serviceProviderRating.insert", e);
			}

		}

		//服务
		Elements cases = doc.select("#productServerContainer > a");
		for( Element element : cases ){

			String url = element.attr("href");

			try {
				HttpTaskPoster.getInstance().submit(CaseTask.class,
						ImmutableMap.of("servicer_id", url));
			} catch (ClassNotFoundException | MalformedURLException | URISyntaxException | UnsupportedEncodingException e) {

				logger.error("error fro HttpTaskPoster.submit CaseTask.class", e);
			}

		}

	}

}