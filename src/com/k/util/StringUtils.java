package com.k.util;

public class StringUtils
{
	/**
	 * 将C接口点分制ID转为整型
	 * @param sidString
	 * @return
	 * @author zhaokai
	 * @create 2018年3月21日 下午7:45:33
	 */
	public static int toScadaIdInt(String sidString)
	{
		String[] array = sidString.split("\\.");
		if (array.length != 4)
			return -1;
		int s1 = Integer.parseInt(array[0]);
		int s2 = Integer.parseInt(array[1]);
		int s3 = Integer.parseInt(array[2]);
		int s4 = Integer.parseInt(array[3]);
		
		int res = (s1 << 27) | (s2 << 17) | (s3 << 11) | s4;
		return res;
	}
	
	/**
	 * 将C接口整型ID转为点分制
	 * @param sidInt
	 * @return
	 * @author zhaokai
	 * @create 2018年3月21日 下午7:51:41
	 */
	public static String toScadaId(int sidInt)
	{
		String s1 = Integer.toString(sidInt >> 27);
		String s2 = Integer.toString((sidInt &  0x7FE0000) >> 17);
		String s3 = Integer.toString((sidInt & 0x1F800) >> 11);
		String s4 = Integer.toString(sidInt & 0x7FF);
		return s1 + "." + s2 + "." + s3 + "." + s4;
	}
	
	/**
	 * 判断输入源是否为正整数
	 * @param src
	 * @return 是-true；否-false
	 * @author zhaokai
	 * @create 2018年3月21日 上午11:28:26
	 */
	public static Boolean isPositiveInteger(String src)
	{
		char[] array = src.toCharArray();
		for (char c : array)
		{
			if (c < '0' || c > '9')
				return false;
		}
		int fi = Integer.parseInt(src);
		if (fi > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * 识别参数是否是点分IP格式。
	 * 当参数符合以下条件时，认为不是IP格式：
	 * 1.包含字母
	 * 2.小于0，大于254
	 * 3.包含连续两个点
	 * 4.首字符为0或点
	 * 5.尾字符为点
	 * 6.以点分不为4段
	 * 
	 * @param src
	 * @return
	 * @author zhaokai
	 * @create 2018年2月1日 下午1:01:04
	 */
	public static boolean isIp(String src)
	{
		if (isNullOrEmpty(src))
			return false;
		if (src.indexOf("..") != -1)
			return false;
		char firstChar = src.charAt(0);
		if (firstChar == '.' || firstChar == '0')
			return false;
		if (src.substring(src.length() - 1).equals("."))
			return false;
		String[] parts = src.split("\\.");
		if (parts.length != 4)
			return false;
		for (String part :parts)
		{
			for (char c : part.toCharArray())
			{
				if ("0123456789".indexOf(c) == -1)
					return false;
			}
			int partInt = Integer.parseInt(part); 
			if ( partInt > 255)
				return false;
		}
		
		return true;
	}
	
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
	 * @param sid
	 *            序列号
	 * @param len
	 *            补完以后的长度
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

	/**
	 * 获得Long型Ip
	 * 
	 * @param ip
	 * @return
	 * @author zhaokai
	 * @create 2018年1月29日 上午9:36:24
	 */
	public static Long getLongIp(String ip)
	{
		if (isNullOrEmpty(ip))
			return 0L;

		Long ips = 0L;
		String[] numbers = ip.split("\\.");

		if (numbers.length != 4)
			return 0L;

		for (int i = 0; i < 4; ++i)
		{
			int ipPart = Integer.parseInt(numbers[i]);
			if (ipPart > 255 || ipPart < 0)
				return 0L;
			ips = ips << 8 | ipPart;
		}
		return ips;
	}

	/**
	 * 获得字符串型（点分）IP
	 * @param ipLong
	 * @return
	 * @author zhaokai
	 * @create 2018年1月29日 上午9:37:45
	 */
	public static String getStringIp(Long ipLong)
	{
		StringBuffer sb = new StringBuffer("");  
        //直接右移24位  
        sb.append(String.valueOf((ipLong >>> 24)));  
        sb.append(".");  
        //将高8位置0，然后右移16位  
        sb.append(String.valueOf((ipLong & 0x00FFFFFF) >>> 16));  
        sb.append(".");  
        //将高16位置0，然后右移8位  
        sb.append(String.valueOf((ipLong & 0x0000FFFF) >>> 8));  
        sb.append(".");  
        //将高24位置0  
        sb.append(String.valueOf((ipLong & 0x000000FF)));  
        return sb.toString();  
	}
	
	public static void main(String[] args)
	{
		String ip = "192.168.1.1";
		System.out.println(getLongIp(ip));
		System.out.println(getStringIp(3232235777L));
	}
}
