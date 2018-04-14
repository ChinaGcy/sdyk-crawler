package com.sdyk.ai.crawler.zbj.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import one.rewind.db.DBName;
import one.rewind.db.OrmLiteDaoManager;
import one.rewind.io.requester.proxy.IpDetector;
import one.rewind.util.NetworkUtil;

import java.util.Date;

@DBName(value = "crawler")
@DatabaseTable(tableName = "crawler_stats")
public class CrawlerStat {

	public static String LOCAL_IP = IpDetector.getIp() + " :: " + NetworkUtil.getLocalIp();

	@DatabaseField(dataType = DataType.DATE, canBeNull = false, id = true)
	public Date insert_time = new Date();

	@DatabaseField(dataType = DataType.STRING, width = 32, canBeNull = false)
	public String ip = LOCAL_IP;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, defaultValue = "0")
	public int request_count = 0;

	public CrawlerStat() {}

	public CrawlerStat(int request_count) {
		this.request_count = request_count;
	}

	public boolean insert() throws Exception{

		Dao<CrawlerStat, String> dao = OrmLiteDaoManager.getDao(CrawlerStat.class);

		if (dao.create(this) == 1) {
			return true;
		}

		return false;
	}
}
