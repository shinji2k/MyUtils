package com.k.util;

public class StringUtils
{
	public static Boolean isNullOrEmpty(String str)
	{
		if (str == null)
			return true;
		
		if (str.equals(""))
			return true;
		
		return false;
	}

	/**
	 * 左补0
	 *
	 * @param sid 序列号
	 * @param len 补完以后的长度
	 * @return
	 * @author zhaokai
	 * @version 2017年7月25日 下午2:36:50
	 */
	public static String leftPlus0(int sid, int len)
	{
		String res = Integer.toString(sid);
		for (int i = Integer.toString(sid).length(); i < len; i++)
			res = "0" + res;
		return res;
	}


}
