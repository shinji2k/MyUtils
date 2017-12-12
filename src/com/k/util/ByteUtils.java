/**
 * byte类型相关工具类
 */
package com.k.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author zhaokai 2016年9月30日 下午4:52:11
 */
public class ByteUtils
{
	/**
	 * 动环SU中点分ID到byte[]的转换
	 * @param scadaId
	 * @return
	 * @author zhaokai
	 * @create 2017年12月5日 下午4:54:45
	 */
	public static byte[] scadaIdToBytes(String scadaId)
	{
		String[] idParts = scadaId.split("\\.");
		if (idParts.length != 4)	//点分格式错误
			return null;
		int aa = Integer.parseInt(idParts[0]) << 27;
		int bbb = Integer.parseInt(idParts[1]) << 17;
		int cc = Integer.parseInt(idParts[2]) << 11;
		int ddd = Integer.parseInt(idParts[3]) & 0x7FF;
		
		int intId = aa | bbb | cc | ddd;
		
		byte[] b = getBytes(intId);
		return b;
	}
	
	/**
	 * byte[]到动环点分ID的转换
	 * @param src
	 * @return
	 * @author zhaokai
	 * @create 2017年12月6日 下午7:58:00
	 */
	public static String bytesToScadaId(byte[] src)
	{
		if (src.length != 4)
			return null;
		int intId = (src[0] << 24) | (src[1]) << 16 | (src[2] << 8) | src[3]; 
		String aa = "" + ((intId & 0xF8000000) >> 27);
		String bbb = "" + ((intId & 0x7FE0000) >> 17);
		String cc = "" + ((intId & 0x1F800) >> 11);
		String ddd = "" + (intId & 0x7FF);
		
		String scadaIdString = aa + "." + bbb + "." + cc + "." + ddd;
		return scadaIdString;
	}
	
	/**
	 * 将byte[]转为asc码，每字节采用2位16进制数计算.16进制中的字母将转为大写计算asc值
	 * 
	 * @param src
	 * @return
	 * zhaokai
	 * 2017年9月24日 上午11:32:56
	 */
	public static byte[] byteToAsc(byte[] src)
	{
		if (src == null || src.length == 0)
			return null;
		StringBuffer hexBuffer = new StringBuffer();
		for (int i = 0; i < src.length; i++)
		{
			if (src[i] == 0)
			{
				hexBuffer.append("00");
			}
			else
			{
				String hexString = byteToHexString(src[i]).toUpperCase();
				if (hexString.length() == 1)
					hexString = "0" + hexString;
				hexBuffer.append(hexString);
			}
		}
		
		char[] ascArray = hexBuffer.toString().toCharArray();
		byte[] ret = new byte[ascArray.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = (byte) ascArray[i];
		return ret;
	}

	/**
	 * 将16位的int高低位互换，返回2字节长度的byte数组。 主要应用于大端机报文发送时调整字节顺序
	 *
	 * @param src
	 * @return
	 * @author zhaokai
	 * @version 2017年4月2日 上午10:49:07
	 */
	public static byte[] swapHighLow(int src)
	{
		return new byte[]
		{ (byte) (src & 0xFF00), (byte) (src & 0x00FF) };
	}

	/**
	 * 将byte数组(重载)转为内容为16进制显示的字符串返回，各元素空格分割
	 * 
	 * @param src
	 * @return
	 * @author zhaokai
	 * @date 2016年9月30日 下午4:57:17
	 */
	public static String byteToHexString(byte[] src)
	{
		if (src.length == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < src.length; i++)
			sb.append(" " + byteToHexString(src[i]));
		return sb.toString().substring(1);
	}
	
	/**
	 * 将byte数组(重载)转为内容为16进制显示的字符串返回，各元素空格分割
	 * @param src
	 * @return
	 * @author ken_8
	 * 2017年9月21日 下午10:26:01
	 */
	public static String byteToHexString(List<Byte> src)
	{
		if (src.size() == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < src.size(); i++)
			sb.append(" " + byteToHexString(src.get(i)));
		return sb.substring(1).toString();
	}
	
	public static String byteToHexString(byte src)
	{
		return Integer.toHexString(src & 0xFF);
	}

	/**
	 * 将16进制字符串数组转为byte数组。字符串数组中每个元素是一个16进制数的字符串格式
	 * @param hexStringArray 例如：{"1f","2f","3f"}
	 * @return
	 */
	public static byte[] hexStringArrayToBytes(String[] hexStringArray)
	{
		byte[] res = new byte[hexStringArray.length];
		for (int i = 0; i < hexStringArray.length; i++)
		{
			res[i] = hexStringToByte(hexStringArray[i]);
		}
		
		return res;
	}
	
	/**
	 * 将16进制字符转为byte
	 *
	 * @param hexString
	 * @return
	 * @author zhaokai
	 * @version 2016年11月9日 下午1:55:28
	 */
	public static byte hexStringToByte(String hexString)
	{
		byte d = -1;
		if (hexString == null || hexString.equals(""))
		{
			System.out.println("hexString is null or empty");
			return d;
		}
		
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	
	/**
	 * 将16进制字符串转为byte数组
	 *
	 * @param hexString 例：1f2f3f4f
	 * @return
	 * @author zhaokai
	 * @version 2016年11月9日 下午1:55:28
	 */
	public static byte[] hexStringToBytes(String hexString)
	{
		if (hexString == null || hexString.equals(""))
		{
			System.out.println("hexString is null or empty");
			return null;
		}
		
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		byte[] d = new byte[length];
		char[] hexChars = hexString.toCharArray();
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 字符型转byte
	 *
	 * @param c
	 * @return
	 * @author zhaokai
	 * @version 2016年11月9日 下午1:56:54
	 */
	private static byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static byte[] getBytes(short data)
	{
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] getBytes(char data)
	{
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data);
		bytes[1] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] getBytes(int data)
	{
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((data >> 24) & 0xFF);
		bytes[1] = (byte) ((data >> 16) & 0xFF);
		bytes[2] = (byte) ((data >> 8) & 0xFF);
		bytes[3] = (byte) (data & 0xFF);
		return bytes;
	}

	public static byte[] getBytes(long data)
	{
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] getBytes(float data)
	{
		int intBits = Float.floatToIntBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(double data)
	{
		long intBits = Double.doubleToLongBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(String data, String charsetName)
	{
		Charset charset = Charset.forName(charsetName);
		return data.getBytes(charset);
	}

	/**
	 * 以默认的GBK编码格式将字符串转为byte数组。 若指定编码格式，请使用getBytes(String data, String
	 * charsetName)方法
	 *
	 * @param data
	 * @return
	 * @author zhaokai
	 * @version 2017年4月12日 下午3:58:17
	 */
	public static byte[] getBytes(String data)
	{
		return getBytes(data, "GBK");
	}

	public static short getShort(byte[] bytes)
	{
		return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
	}

	public static char getChar(byte[] bytes)
	{
		return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
	}

	/**
	 * byte[]数组转int
	 * @param bytes
	 * @return
	 * @author zhaokai
	 * @create 2017年12月7日 下午5:20:38
	 */
	public static int getInt(byte[] bytes)
	{
		return (int) (((bytes[0] & 0xFF) << 24) | ((bytes[0 + 1] & 0xFF) << 16) | ((bytes[0 + 2] & 0xFF) << 8)
				| (bytes[0 + 3] & 0xFF));
	}

	public static long getLong(byte[] bytes)
	{
		long res = 0;
		if (bytes.length == 8)
		{
			res = (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16))
					| (0xff000000L & ((long) bytes[3] << 24)) | (0xff00000000L & ((long) bytes[4] << 32))
					| (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48))
					| (0xff00000000000000L & ((long) bytes[7] << 56));
		}
		else
		{
			res = (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16))
			| (0xff000000L & ((long) bytes[3] << 24));
		}
			
		return res;
	}

	public static float getFloat(byte[] bytes)
	{
		return Float.intBitsToFloat(getInt(bytes));
	}

	public static double getDouble(byte[] bytes)
	{
		long l = getLong(bytes);
		System.out.println(l);
		return Double.longBitsToDouble(l);
	}

	/**
	 * 字节转换为浮点
	 * 
	 * @param b
	 *            字节（至少4个字节）
	 * @param index
	 *            开始位置
	 * @return
	 */
	public static float byte2float(byte[] b, int index)
	{
		int l;
		l = b[index + 0];
		l &= 0xff;
		l |= ((long) b[index + 1] << 8);
		l &= 0xffff;
		l |= ((long) b[index + 2] << 16);
		l &= 0xffffff;
		l |= ((long) b[index + 3] << 24);
		return Float.intBitsToFloat(l);

		// int accum = 0;
		// accum = accum|(b[0] & 0xff) << 0;
		// accum = accum|(b[1] & 0xff) << 8;
		// accum = accum|(b[2] & 0xff) << 16;
		// accum = accum|(b[3] & 0xff) << 24;
		// System.out.println(accum);
		// return Float.intBitsToFloat(accum);
	}

	/**
	 * byte数组转为字符串，使用指定的编码进行转换
	 *
	 * @param b
	 * @param charSet
	 * @return
	 * @author zhaokai
	 * @version 2017年4月12日 下午3:55:39
	 */
	public static String byteArrayToString(byte[] b, String charSet)
	{
		String res = null;
		try
		{
			res = new String(b, charSet);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 将byte数组的内容转为字符串，默认采用UTF-8的编码格式
	 *
	 * @param b
	 * @return
	 * @author zhaokai
	 * @version 2017年1月6日 下午2:08:19
	 */
	public static String byteArrayToString(byte[] b)
	{
		return byteArrayToString(b, "GBK");
	}

	/**
	 * 整型转换至byte[4]
	 *
	 * @param integer
	 * @return
	 * @author zhaokai
	 * @version 2017年3月3日 下午5:12:23
	 */
	public static byte[] intToByteArray(final int integer)
	{
		int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[4];

		for (int n = 0; n < byteNum; n++)
			byteArray[3 - n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}
}
