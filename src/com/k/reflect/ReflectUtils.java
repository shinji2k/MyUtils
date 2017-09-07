/**
 * 
 */
package com.k.reflect;

import java.lang.reflect.Method;

/**
 * 反射用的工具集
 * @author zhaokai
 * 2017年9月7日 上午9:58:04
 */
public class ReflectUtils
{
	/**
	 * 获取属性的Set方法
	 * @param methods
	 * @param fieldName
	 * @return
	 * @author zhaokai
	 * 2017年9月7日 上午10:03:59
	 */
	public static Method getSetMethod(Method[] methods, String fieldName)
	{
		String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method setMethod = null;
		for (Method m : methods)
		{
			if (m.getName().equals(setMethodName))
			{
				setMethod = m;
				break;
			}
		}
		return setMethod;
	}
	
	/**
	 * 获取属性的Get方法
	 * @param methods
	 * @param fieldName
	 * @return
	 * @author zhaokai
	 * 2017年9月7日 上午10:04:23
	 */
	public static Method getGetMethod(Method[] methods, String fieldName)
	{
		String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method getMethod = null;
		for (Method m : methods)
		{
			if (m.getName().equals(getMethodName))
			{
				getMethod = m;
				break;
			}
		}
		return getMethod;
	}
}
