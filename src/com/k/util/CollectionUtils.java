package com.k.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合相关的工具类
 * 
 * @author zhaokai
 * @version 2017年2月14日 下午5:05:00
 */
public class CollectionUtils
{
	/**
	 * 拷贝一个List到另一个List。若dest list为空则返回一个新List
	 * 
	 * @param src
	 * @param dest
	 * @return
	 * @author zhaokai
	 * @create 2017年10月31日 下午6:37:53
	 */
	public static <E> List<E> copyList(List<E> src, List<E> dest)
	{
		List<E> res = null;
		if (dest == null)
			res = new ArrayList<E>();
		for (int i = 0; i < src.size(); i++)
			res.add(src.get(i));
		return res;
	}

	/**
	 * 对传入List操作，移除index前面的内容。包括index
	 * 
	 * @param src
	 * @param index
	 * @author zhaokai 2017年9月21日 下午6:06:21
	 */
	public static <E> void removeBefore(List<E> src, int index)
	{
		for (int i = index; i > -1; i--)
			src.remove(i);
	}

	/**
	 * 对传入List操作，移除index后面的内容。包括index
	 * 
	 * @param src
	 * @param index
	 * @author zhaokai 2017年9月21日 下午6:06:21
	 */
	public static <E> void removeAfter(List<E> src, int index)
	{
		for (int i = index; i < src.size(); i++)
			src.remove(i);
	}

	/**
	 * 主要针对基本类型byte的数组将内容追加至List中
	 *
	 * @param list
	 * @param array
	 * @author zhaokai
	 * @version 2017年2月14日 下午5:07:10
	 */
	public static List<Byte> copyArrayToList(List<Byte> list, byte[] array)
	{
		if (array == null || array.length == 0)
			return list;
		if (list == null)
			list = new ArrayList<Byte>();
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		return list;
	}

	/**
	 * 将byte[]数组装箱为Byte[]
	 *
	 * @param b
	 * @return
	 * @author zhaokai
	 * @version 2017年2月16日 下午1:58:16
	 */
	public static Byte[] byteToByte(byte[] b)
	{
		if (b == null)
			return null;
		Byte[] res = new Byte[b.length];
		for (int i = 0; i < b.length; i++)
			res[i] = b[i];
		return res;
	}

	/**
	 * 将Byte[]数组拆箱为byte[]
	 * 
	 * @param b
	 * @return
	 * @author zhaokai 2017年9月14日 下午4:49:48
	 */
	public static byte[] ByteTobyte(Byte[] b)
	{
		byte[] res = new byte[b.length];
		for (int i = 0; i < b.length; i++)
			res[i] = b[i];
		return res;
	}

	/**
	 * 将byte的list转为byte数组
	 * 
	 * @param list
	 * @return
	 * @author zhaokai 2017年9月14日 下午4:51:47
	 */
	public static byte[] toByteArray(List<Byte> list)
	{
		if (list.size() == 0)
			return null;
		byte[] res = new byte[list.size()];
		for (int i = 0; i < list.size(); i++)
			res[i] = list.get(i);
		return res;
	}

	/**
	 * 判断a、b两个数组是否相等。byte重载
	 *
	 * @param a
	 * @param b
	 * @return
	 * @author zhaokai
	 * @version 2017年5月11日 下午4:51:33
	 */
	public static Boolean isSameArray(byte[] a, byte[] b)
	{
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++)
		{
			if (a[i] != b[i])
				return false;
		}
		return true;
	}

	/**
	 * 在src截取[start,stop]的子数组,byte重载
	 *
	 * @param src
	 * @param start
	 *            from 0
	 * @param stop
	 *            from 0
	 * @return
	 * @author zhaokai
	 * @version 2017年5月11日 下午5:25:56
	 */
	public static byte[] subArray(byte[] src, int start, int stop)
	{
		byte[] res = new byte[stop - start + 1];
		int pos = start;
		for (int i = 0; i < res.length; i++, pos++)
			res[i] = src[pos];
		return res;
	}

	/**
	 * 在src截取[start,stop]的子数组,List<Byte>重载
	 * 
	 * @param src
	 * @param start
	 *            from 0
	 * @param stop
	 *            from 0
	 * @return
	 * @author ken_8 2017年9月21日 上午12:01:26
	 */
	public static byte[] subArray(List<Byte> src, int start, int stop)
	{
		byte[] res = new byte[stop - start + 1];
		int pos = start;
		for (int i = 0; i < res.length; i++, pos++)
			res[i] = src.get(pos);
		return res;
	}

	public static <E> List<E> subList(List<E> src, int start, int stop)
	{
		stop = stop + 1;
		if (start < 0 || stop > src.size())
			return null;
		List<E> ret = new ArrayList<E>();
		for (int i = start; i < stop; i++)
			ret.add(src.get(i));
		return ret;
	}

	/**
	 * 获取target在source中的索引位置。同String.indexOf()；
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @author ken_8 2017年9月20日 下午11:58:44
	 */
	public static int indexOf(List<Byte> source, byte[] target)
	{
		return indexOf(source, 0, source.size(), target, 0, target.length, 0);
	}

	/**
	 * 获取target在source中的索引位置。带偏移量重载
	 * @param source
	 * @param target
	 * @param fromIndex
	 * @return
	 * @author zhaokai
	 * @create 2017年11月21日 上午9:55:27
	 */
	public static int indexOf(List<Byte> source, byte[] target, int fromIndex)
	{
		return indexOf(source, 0, source.size(), target, 0, target.length, fromIndex);
	}

	/**
	 * 获取target在source中的索引位置，同String.indexOf()。基础方法供其他方法调用
	 * 
	 * @param source
	 *            源
	 * @param sourceOffset
	 *            源偏移量，即从源的第几位开始查找，与sourceCount配合使用
	 * @param sourceCount
	 *            在源中进行查找的长度，若源为abcde，sourceCount为3的话即为在abc中查找
	 * @param target
	 *            目标
	 * @param targetOffset
	 *            目标偏移量
	 * @param targetCount
	 *            目标串中用来匹配的长度，同sourceCount
	 * @param fromIndex
	 *            查找偏移量，即从第几位开始查找
	 * @return
	 * @author zhaokai
	 * @create 2017年11月21日 上午9:46:09
	 */
	public static int indexOf(List<Byte> source, int sourceOffset, int sourceCount, byte[] target, int targetOffset,
			int targetCount, int fromIndex)
	{
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		if (targetCount == 0)
		{
			return fromIndex;
		}

		byte first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++)
		{
			/* Look for first character. */
			if (source.get(i) != first)
			{
				while (++i <= max && source.get(i) != first)
					;
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source.get(j) == target[k]; j++, k++)
					;

				if (j == end)
				{
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

}
