/**
 * 
 */
package com.k.datetime;

import java.text.SimpleDateFormat;

/**
 * @author zhaokai
 *
 * 2017年9月12日 下午2:24:13
 */
public class TimeUtils
{
	public static String getTimeStamp()
	{
		return getTimeStamp("yyyy-MM-dd HH:mm:ss");
	}
	
	public static String getTimeStamp(String format)
	{
		SimpleDateFormat date = new SimpleDateFormat(format);
		return date.format(System.currentTimeMillis());
	}
}
