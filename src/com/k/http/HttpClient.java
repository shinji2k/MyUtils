package com.k.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.k.http.entity.Response;
import com.k.util.CollectionUtils;

/**
 * 
 * @author zhaokai 2017年12月28日 下午6:33:04
 */
public class HttpClient
{
	/**
	 * 发送post请求，返回响应头内容和响应体。参数的格式化请根据需要在外部格式化
	 * 
	 * @param url
	 * @param param
	 * @param requestProperty
	 * @return
	 * @author zhaokai
	 * @throws Exception 
	 * @create 2018年1月3日 上午10:16:21
	 */
	public static Response sendPost(String url, String param, Map<String, String> requestProperty) throws Exception
	{
		InputStream in = null;
		OutputStream out = null;
		String res = null;

		Response response = new Response();
		try
		{
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();

			// 设置通用的请求属性
			if (requestProperty == null || requestProperty.keySet().size() == 0)
				return null;
			for (String key : requestProperty.keySet())
			{
				conn.setRequestProperty(key, requestProperty.get(key));
			}

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);

			// 判断是不是需要压缩并字节化Post参数
			byte[] postParamBytes = null;
			if (requestProperty.keySet().contains("Content-Encoding")
					&& requestProperty.get("Content-Encoding").toLowerCase().trim().equals("gzip"))
			{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				GZIPOutputStream gout = new GZIPOutputStream(bout);
				gout.write(param.getBytes("UTF-8"));
				gout.close();
				postParamBytes = bout.toByteArray();
			}
			else
			{
				postParamBytes = param.getBytes("UTF-8");
			}

			// 获取URLConnection对象对应的输出流
			out = conn.getOutputStream();
			out.write(postParamBytes);

			// 输出流的缓冲
			out.flush();

			// 定义BufferedReader输入流来读取URL的响应
			in = conn.getInputStream();

			// 获取所有响应头字段并整理、遍历
			Map<String, List<String>> respHeaderMap = conn.getHeaderFields();

			int cttLen = -1;
			String charSet = "UTF-8";
			String cttEncoder = null;
			Map<String, String> responseHeader = new HashMap<String, String>();
			for (String key : respHeaderMap.keySet())
			{
				// System.out.println(key + "--->" + respHeaderMap.get(key));
				if (key == null)
					continue;
				switch (respHeaderMap.get(key).size())
				{
					case 0:
						responseHeader.put(key, "");
						break;
					case 1:
						responseHeader.put(key, respHeaderMap.get(key).get(0));
						break;
					default:
						List<String> headerValueList = respHeaderMap.get(key);
						StringBuffer sb = new StringBuffer();
						for (String headerValue : headerValueList)
							sb.append(headerValue + ";");
						responseHeader.put(key, sb.toString());
				}

				// 获取收取内容的长度（不包含Header）
				if (key.equals("Content-Length"))
					cttLen = Integer.parseInt(responseHeader.get(key));
				// 获取字符集
				if (key.equals("Content-Type"))
				{
					String cttType = responseHeader.get(key);
					if (cttType.indexOf("charset=") > -1)
					{
						String charSetStr = cttType.substring(cttType.indexOf("charset=") + 8);
						charSet = charSetStr
								.substring(0,
										charSetStr.indexOf(";") == -1 ? charSetStr.length() : charSetStr.indexOf(";"))
								.trim().toUpperCase();
					}
				}
				// 获取编码类型
				if (key.equals("Content-Encoding"))
					cttEncoder = responseHeader.get("Content-Encoding").toLowerCase().trim();
			}

			response.setRespHeaderMap(responseHeader);
			
			int len = 0;
			if (cttLen == -1)
				cttLen = 1024;
			// 以字节收取返回信息
			byte[] bytes = new byte[cttLen];
			List<Byte> bodyBytes = new ArrayList<Byte>();
			while (-1 != (len = in.read(bytes)))
				CollectionUtils.copyArrayToList(bodyBytes, bytes, len);

			len = bodyBytes.size();
			bytes = CollectionUtils.toByteArray(bodyBytes);

			// 是否需要解码
			if (cttEncoder != null && cttEncoder.equals("gzip"))
			{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
				GZIPInputStream ungzip = new GZIPInputStream(bin);
				byte[] buffer = new byte[256];
				int n;
				while ((n = ungzip.read(buffer)) >= 0)
				{
					bout.write(buffer, 0, n);
				}

				res = new String(bout.toByteArray(), charSet);
			}
			else
			{
				res = new String(bytes, 0, len, charSet);
			}

			response.setBody(res);
		}
		catch (Exception e)
		{
			throw e;
		}
		// 使用finally块来关闭输出流、输入流
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return response;
	}

	/**
	 * 发送Get请求，参数的格式化请在外部进行
	 * @param url
	 * @param param
	 * @param requestProperty
	 * @return
	 * @author zhaokai
	 * @create 2018年1月3日 上午10:52:42
	 */
	public static Response sendGet(String url, String param, Map<String, String> requestProperty)
	{
		InputStream in = null;
		String res = null;
		Response response = new Response();
		try
		{
			String urlName = url + "?" + param;
			URL realUrl = new URL(urlName);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			if (requestProperty == null || requestProperty.keySet().size() == 0)
				return null;
			for (String key : requestProperty.keySet())
			{
				conn.setRequestProperty(key, requestProperty.get(key));
			}
			// 建立实际的连接
			conn.connect();
			// 获取所有响应头字段
			Map<String, List<String>> respHeaderMap = conn.getHeaderFields();
			int cttLen = 0;
			String charSet = "UTF-8";
			String cttEncoder = null;
			Map<String, String> responseHeader = new HashMap<String, String>();
			for (String key : respHeaderMap.keySet())
			{
				// System.out.println(key + "--->" + respHeaderMap.get(key));
				if (key == null)
					continue;
				switch (respHeaderMap.get(key).size())
				{
					case 0:
						responseHeader.put(key, "");
						break;
					case 1:
						responseHeader.put(key, respHeaderMap.get(key).get(0));
						break;
					default:
						List<String> headerValueList = respHeaderMap.get(key);
						StringBuffer sb = new StringBuffer();
						for (String headerValue : headerValueList)
							sb.append(headerValue + ";");
						responseHeader.put(key, sb.toString());
				}

				// 获取收取内容的长度（不包含Header）
				if (key.equals("Content-Length"))
					cttLen = Integer.parseInt(responseHeader.get(key));
				// 获取字符集
				if (key.equals("Content-Type"))
				{
					String cttType = responseHeader.get(key);
					if (cttType.indexOf("charset=") > -1)
					{
						String charSetStr = cttType.substring(cttType.indexOf("charset=") + 8);
						charSet = charSetStr
								.substring(0,
										charSetStr.indexOf(";") == -1 ? charSetStr.length() : charSetStr.indexOf(";"))
								.trim().toUpperCase();
					}
				}
				// 获取编码类型
				if (key.equals("Content-Encoding"))
					cttEncoder = responseHeader.get("Content-Encoding").toLowerCase().trim();
			}
			
			response.setRespHeaderMap(responseHeader);

			// 获取返回流
			in = conn.getInputStream();
			int len = 0;
			if (cttLen == -1)
				cttLen = 1024;
			// 以字节收取返回信息
			byte[] bytes = new byte[cttLen];
			List<Byte> bodyBytes = new ArrayList<Byte>();
			while (-1 != (len = in.read(bytes)))
				CollectionUtils.copyArrayToList(bodyBytes, bytes, len);

			len = bodyBytes.size();
			bytes = CollectionUtils.toByteArray(bodyBytes);
			// 是否需要解码
			if (cttEncoder != null && cttEncoder.equals("gzip"))
			{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
				GZIPInputStream ungzip = new GZIPInputStream(bin);
				byte[] buffer = new byte[256];
				int n;
				while ((n = ungzip.read(buffer)) >= 0)
				{
					bout.write(buffer, 0, n);
				}

				res = new String(bout.toByteArray(), charSet);
			}
			else
			{
				res = new String(bytes, 0, len, charSet);
			}

			response.setBody(res);
		}
		catch (Exception e)
		{
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return response;
	}

	/**
	 * URLEncoder格式化，除jdk的格式化外还格式化了“+”，“-”，“.”等
	 * @param param
	 * @return
	 * @author zhaokai
	 * @create 2018年1月3日 上午10:53:28
	 */
	public static String urlEncoder(String param)
	{
		String res = null;
		try
		{
			res = URLEncoder.encode(param, "UTF-8").replace("+", "%20").replace("-", "%2D").replace(".", "%2E");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return res;
	}

}
