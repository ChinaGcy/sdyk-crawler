package com.sdyk.ai.crawler.specific.mihuashi.task.modelTask;

import com.sdyk.ai.crawler.model.ServiceProvider;
import com.sdyk.ai.crawler.specific.clouderwork.util.CrawlerAction;
import com.sdyk.ai.crawler.specific.zbj.task.Task;
import one.rewind.io.requester.exception.AccountException;
import one.rewind.io.requester.exception.ChromeDriverException;
import one.rewind.io.requester.exception.ProxyException;
import org.jsoup.nodes.Document;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class ServiceSupplierTask extends com.sdyk.ai.crawler.task.Task {

    ServiceProvider serviceProvider;

    public ServiceSupplierTask(String url) throws MalformedURLException, URISyntaxException {

        super(url);
        this.setPriority(Priority.MEDIUM);
        this.setBuildDom();
        this.addDoneCallback(() -> {
            Document doc = getResponse().getDoc();
            try {
                crawlerJob(doc);
            } catch (ChromeDriverException.IllegalStatusException e) {
                logger.info("error on crawlerJob",e);
            }

        });
    }

    public void crawlerJob(Document doc) throws ChromeDriverException.IllegalStatusException {

        serviceProvider = new ServiceProvider(getUrl());
        List<Task> tasks = new ArrayList<Task>();
        String[] url = getUrl().split("users");
        try {
            serviceProvider.origin_id = URLDecoder.decode(url[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //名字
        String name = doc.select("#users-show > div.container-fluid > div.profile__container > aside > section.profile__avatar-wrapper > h5").text();
        String renzhang = doc.select("#users-show > div.container-fluid > div.profile__container > aside > section.profile__avatar-wrapper > h5 > span").text();
        serviceProvider.name = name.replace(renzhang,"");
        //介绍
        serviceProvider.content = doc.select("#users-show > div.container-fluid > div.profile__container > aside > section.profile__summary-wrapper").html();
        //评论数
        String ratNum = doc.select("#users-show > div.container-fluid > div.profile__container > aside > section.profile__avatar-wrapper > section.credit > p")
                .text().replace("共","").replace("条评价","");
        ratNum = CrawlerAction.getNumbers(ratNum);
        if(ratNum!=null&&!"".equals(ratNum)){
            serviceProvider.rating_num = Integer.valueOf(ratNum);
        }
        //领域
        serviceProvider.category = doc.select("#users-show > div.container-fluid > div.profile__container > aside > section.profile__skill-wrapper").text();
        serviceProvider.insert();
    }

    @Override
    public one.rewind.io.requester.Task validate() throws ProxyException.Failed, AccountException.Failed, AccountException.Frozen {
        return null;
    }
}
