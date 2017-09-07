package com.k.util.filehelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.k.util.StringUtils;

public class FormatFileCharacterSet
{
	private File rootDir;
	private File destDir;
	private String key;
	private String srcCharSet;
	private String destCharSet;

	public FormatFileCharacterSet(String rootDir, String destDir,
			String srcCharSet, String destCharSet, String key)
	{
		this.setRootDir(rootDir);
		this.setDestDir(destDir);
		this.setSrcCharSet(srcCharSet);
		this.setDestCharSet(destCharSet);
		this.setKey(key);
	}

	public FormatFileCharacterSet(String rootDir, String destDir,
			String srcCharSet, String destCharSet)
	{
		this.setRootDir(rootDir);
		this.setDestDir(destDir);
		this.setSrcCharSet(srcCharSet);
		this.setDestCharSet(destCharSet);
		this.setKey(null);
	}

	/**
	 * 暂时不用, 之后解决自动判断源文件字符集的问题
	 * 
	 * @param rootDir
	 * @param destDir
	 * @param destCharSet
	 */
	@SuppressWarnings(value =
	{ "unused" })
	private FormatFileCharacterSet(String rootDir, String destDir,
			String destCharSet)
	{
		this.setRootDir(rootDir);
		this.setDestDir(destDir);
		this.setSrcCharSet(null);
		this.setDestCharSet(destCharSet);
		this.setKey(null);
	}

	public FormatFileCharacterSet()
	{
	}

	/**
	 * 设置源目录
	 * 
	 * @param rootDir
	 *            源目录绝对路径
	 * @author zhaokai
	 * @version : 2015年11月4日 上午10:40:35
	 */
	public void setRootDir(String rootDir)
	{
		this.rootDir = new File(rootDir);
	}

	/**
	 * 设置目标目录
	 * 
	 * @param destDir
	 *            目标目录绝对路径
	 * @author zhaokai
	 * @version : 2015年11月4日 上午10:40:58
	 */
	public void setDestDir(String destDir)
	{
		this.destDir = new File(destDir);
	}

	private void init() throws Exception
	{
		if (!rootDir.exists())
		{
			System.out.println("源目录不存在");
			throw new Exception("源目录不存在");
		}
		if (this.checkEmpty(rootDir))
		{
			System.out.println("源目录为空");
			throw new Exception("源目录为空");
		}
		if (!destDir.exists())
		{
			System.out.println("目标目录不存在, 创建目录");
			if (!destDir.mkdir())
			{
				System.out.println("目标目录创建失败");
				throw new Exception("目标目录创建失败");
			}
		}
	}

	/**
	 * 检查目录下是否存在文件, 若存在返回false
	 * 
	 * @param src
	 *            需要检查的目录对象
	 * 
	 * @author zhaokai
	 * @version : 2015年10月30日 下午12:21:45
	 */
	public Boolean checkEmpty(File src)
	{
		File[] child = src.listFiles();
		Boolean result = true;
		if (child.length != 0)
		{
			for (int i = 0; i < child.length; i++)
			{
				if (!child[i].isDirectory())
					result = false;
				else
					result = checkEmpty(child[i]);

				if (!result)
					break;
			}
		}
		return result;
	}

	public void copyFile(File[] rootFileArray, String destRootPath)
	{
		for (File file : rootFileArray)
		{
			String destPath = destRootPath + "\\" + file.getName();
			File destFile = new File(destPath);
			if (file.isDirectory())
			{
				if (!destFile.exists())
				{
					System.out.println("创建目录: " + destPath);
					destFile.mkdir();
				}
				File[] fileArray = file.listFiles();
				if (fileArray.length > 0)
				{
					copyFile(fileArray, destPath);
				}
			}
			else
			{
				if (!file
						.getName()
						.substring(file.getName().length() - 5,
								file.getName().length()).toLowerCase()
						.equals(this.key.toLowerCase()))
					continue;

				if (destFile.exists())
					destFile.delete();
				try
				{
					destFile.createNewFile();
					this.formatFile(file, destFile);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void formatFile(File srcFile, File destFile) throws IOException
	{
		FileInputStream in = new FileInputStream(srcFile);
		FileOutputStream out = new FileOutputStream(destFile);

		byte[] buf = new byte[1024];
		int len = 0;
		String content = "";
		while ((len = in.read(buf)) > 0)
		{
			content = new String(buf, 0, len, this.srcCharSet);
			out.write(content.getBytes(this.destCharSet));
			out.flush();
		}
		in.close();
		out.close();
	}

	/**
	 * 将源目录中的文件编码转为UTF8格式.
	 * 
	 * @author zhaokai
	 * @version : 2015年11月4日 上午10:40:18
	 */
	public void toUtf8()
	{
		if (StringUtils.isNullOrEmpty(this.srcCharSet))
		{
			System.out.println("没有设置源文件的字符集");
			return;
		}
		
		this.setDestCharSet("UTF8");
		try
		{
			this.init();
		}
		catch (Exception e)
		{
			return;
		}
		File[] fileArray = this.rootDir.listFiles();
		copyFile(fileArray, this.destDir.getAbsolutePath());

	}

	/**
	 * 将源目录中的文件编码转为UTF8格式.
	 * 
	 * @param destDir
	 *            目标目录
	 * @param key
	 *            过滤文件的扩展名, 为空时处理所有类型的文件
	 * @author zhaokai
	 * @version : 2015年11月4日 上午10:39:25
	 */
	public void toUtf8(String destDir, String key)
	{
		this.setDestDir(destDir);
		this.setKey(key);
		this.toUtf8();
	}

	/**
	 * 将源目录中的文件编码转为UTF8格式.
	 * 
	 * @param destDir
	 *            目标目录
	 * @author zhaokai
	 * @version : 2015年11月4日 上午10:39:25
	 */
	public void toUtf8(String destDir)
	{
		this.setDestDir(destDir);
		this.setKey(null);
		this.toUtf8();
	}

	public static void main(String[] args)
	{
		FormatFileCharacterSet test = new FormatFileCharacterSet();
		test.setRootDir("D:\\WorkSpace\\Java\\MyEclipse\\Test\\src");
		test.setDestDir("C:\\Users\\zhaokai\\Desktop\\test");
		test.setKey("java");
		test.setSrcCharSet("UTF8");
		test.toUtf8();
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		if (StringUtils.isNullOrEmpty(key))
		{
			this.key = "*";
			return;
		}
		if (key.substring(0, 1).equals("."))
			this.key = key;
		else
			this.key = "." + key;
	}

	public String getSrcCharSet()
	{
		return srcCharSet;
	}

	public void setSrcCharSet(String srcCharSet)
	{
		this.srcCharSet = srcCharSet;
	}

	public String getDestCharSet()
	{
		return destCharSet;
	}

	public void setDestCharSet(String destCharSet)
	{
		this.destCharSet = destCharSet;
	}
}
