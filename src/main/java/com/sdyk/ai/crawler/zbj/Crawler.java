package com.sdyk.ai.crawler.zbj;

import com.sdyk.ai.crawler.zbj.task.Task;
import com.sdyk.ai.crawler.zbj.task.scanTask.ProjectScanTask;
import com.sdyk.ai.crawler.zbj.task.scanTask.ScanTask;
import it.sauronsoftware.cron4j.Scheduler;
import one.rewind.io.requester.chrome.ChromeDriverRequester;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

import static spark.route.HttpMethod.get;

public class Crawler {

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Crawler.class.getName());

	protected static Crawler instance;

	public static Crawler getInstance() {

		if (instance == null) {

			synchronized (Crawler.class) {
				if (instance == null) {
					instance = new Crawler();
				}
			}
		}

		return instance;
	}

	// 项目频道参数
	public String[] project_channels = {
			"t-pxfw",
			"t-consult",
			"t-paperwork",
			"t-ppsj",
			"t-sign",
			"t-ad",
			"t-dhsjzbj",
			"t-video",
			"t-xcpzzzbj",
			"t-uisheji",
			"t-rjkf",
			"t-ydyykf",
			"t-wzkf",
			"t-vrthreed",
			"t-hkaifa",
			"t-zhjjfazbjzbj",
			"t-wxptkf",
			"t-dianlu",
			"t-xxtg",
			"t-yxtg",

	};

	// 服务商频道参数
	public static String[] service_supplier_channels = {
			"pxfw",
			"consult",
			"paperwork",
			"ppsj",
			"sign",
			"ad",
			"dhsjzbj",
			"video",
			"xcpzzzbj",
			"uisheji",
			"rjkf",
			"ydyykf",
			"wzkf",
			"vrthreed",
			"hkaifa",
			"zhjjfazbjzbj",
			"wxptkf",
			"dianlu",
			"xxtg",
			"yxtg",
	};

	/**
	 * TODO 初始化ChromeDriverAgent
	 * 增加Exception Callbacks
	 */
	public Crawler() {

	}

	/**
	 *
	 * @param backtrace
	 * @return
	 */
	public List<ScanTask> getTask(boolean backtrace) {

		List<ScanTask> tasks = new ArrayList<>();

		for(String channel : project_channels) {
			ScanTask t = ProjectScanTask.generateTask(channel, 1);
			t.backtrace = backtrace;
			tasks.add(t);
			System.out.println("PROJECT:" + channel);
		}

		/*if (backtrace == true) {
			for (String channel : service_supplier_channels) {
				ScanTask t = ServiceScanTask.generateTask(channel, 1, null);
				t.backtrace = backtrace;
				tasks.add(t);
				System.out.println("SERVICE:" + channel);
			}
		}*/

		return tasks;
	}

	/**
	 * 获取历史数据
	 */
	public void getHistoricalData() {

		// 需求
		for (Task task : getTask(true)) {
			ChromeDriverRequester.getInstance().submit(task);
		}
	}

	/**
	 * 监控调度
	 */
	public void monitor() {

		try {

			Scheduler s = new Scheduler();

			// 每隔十分钟，生成实时扫描任务
			s.schedule("*/10 * * * *", new Runnable() {

				public void run() {

					for (Task task : getTask(false)) {
						ChromeDriverRequester.getInstance().submit(task);
					}
				}
			});

			s.start();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 1 && args[0].equals("H")){
			// 获取历史数据
			logger.info("历史数据");
			Crawler.getInstance().getHistoricalData();

		}
		else {
			// 监控数据
			logger.info("监控数据");
			Crawler.getInstance().monitor();
		}
	}

}
