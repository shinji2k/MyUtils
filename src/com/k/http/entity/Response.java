package com.k.http.entity;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhaokai 2018年1月2日 下午6:07:02
 */
public class Response
{
	private String body;
	private Map<String, List<String>> respHeaderMap;

	/**
	 * @return the body
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body)
	{
		this.body = body;
	}

	/**
	 * @return the respHeaderMap
	 */
	public Map<String, List<String>> getRespHeaderMap()
	{
		return respHeaderMap;
	}

	/**
	 * @param respHeaderMap
	 *            the respHeaderMap to set
	 */
	public void setRespHeaderMap(Map<String, List<String>> respHeaderMap)
	{
		this.respHeaderMap = respHeaderMap;
	}
}
