package edu.njust.sem.wa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TimeUtil {
	private final static SimpleDateFormat SDF_M = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private final static SimpleDateFormat SDF_D = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final int LITTLE_TIME = 10000;

	/**
	 * 对以下四种日期格式化为标准格式---- “5秒前” “5分钟前” “今天 10:38” “3月23日 03:51”
	 * 
	 * @return
	 */
	public static String formmatDate(String time) {
		long now = System.currentTimeMillis();
		int num = ParseUtil.getNum(time);
		if (time.contains("秒")) {
			Date datetime = new Date(now - num * 1000);
			return SDF_M.format(datetime);
		} else if (time.contains("分钟前")) {
			Date datetime = new Date(now - num * 60 * 1000);
			return SDF_M.format(datetime);
		} else if (time.contains("今天")) {
			String today = SDF_D.format(new Date(now));
			return time.replace("今天", today);
		} else if (time.contains("月")) {
			time = "2014年" + time;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			try {
				Date date = sdf.parse(time);
				return SDF_M.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return time;
	}

	public static String getCurrDate() {
		Date now = new Date();
		return SDF_M.format(now);
	}

	/**
	 * 休息随机时间（0到Max毫秒）
	 * 
	 * @param max
	 *            休息时间的最大毫秒数
	 */
	public static void sleep(int max) {
		Random r = new Random();
		int num = r.nextInt(max);
		Logger.log("休息" + num + "毫秒,即"+(float)num/(60000)+"分钟");
		try {
			Thread.sleep(num);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
