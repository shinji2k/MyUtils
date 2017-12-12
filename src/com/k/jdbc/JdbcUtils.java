package com.k.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcUtils
{
	private Connection con = null;// 创建一个数据库连接
	private PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
	private ResultSet result = null;
	private Boolean isLongConnection;
	private String driver;
	private String url;
	private String userName;
	private String pwd;

	public JdbcUtils(String driver, String url, String userName, String pwd, Boolean isLongCon)
	{
		this.driver = driver;
		this.url = url;
		this.userName = userName;
		this.pwd = pwd;
		if (isLongCon)
			System.out.println("提示：长连接需要手动调用方法关闭连接！");
		this.connect(driver, url, userName, pwd);
		this.isLongConnection = isLongCon;
	}

	public void connect(String driver, String url, String userName, String pwd)
	{
		try
		{
			Class.forName(driver);// 加载驱动程序
			System.out.println("开始尝试连接数据库:" + url + "！");
			con = DriverManager.getConnection(url, userName, pwd);// 获取连接
			System.out.println("\t关闭自动提交！");
			con.setAutoCommit(false);
			System.out.println("\t连接成功！");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("\t连接数据库失败！！");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("\t未找到对应的数据库驱动包！！");
		}
	}

	public ResultSet doSelect(String sql)
	{
		try
		{
			if (con == null)
				this.connect(driver, url, userName, pwd);
			pre = con.prepareStatement(sql);// 实例化预编译语句
			result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (!isLongConnection)
				this.closeDbConnect();
		}
		return result;
	}

	/**
	 * 实现实体类的自动注入。 目前要求实体类属性名必须与字段名一致
	 *
	 * @param sql
	 * @param t
	 * @return
	 * @author zhaokai
	 * @version 2017年7月25日 下午12:25:05
	 */
	public <T> List<T> doSelect(String sql, Class<T> t)
	{

		List<T> entityList = new ArrayList<T>();
		T entity = null;
		try
		{
			if (con == null)
				this.connect(driver, url, userName, pwd);
			pre = con.prepareStatement(sql);// 实例化预编译语句
			result = pre.executeQuery();

			while (result.next())
			{
				entity = t.newInstance();
				Method[] methods = t.getDeclaredMethods();
				Field[] fields = t.getDeclaredFields();
				for (Field field : fields)
				{
					// TODO: 暂时不考虑按照类型去注入，实体类中统一按照String来装载
					// String fieldType = field.getType().getSimpleName();
					String fieldName = field.getName();
					String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					for (Method method : methods)
					{
						if (method.getName().equals(setMethodName))
						{
							method.invoke(entity, result.getString(fieldName));
							break;
						}
					}
				}
				entityList.add(entity);
			}
		}
		catch (InstantiationException  e)
		{
			e.printStackTrace();
			System.out.println("创建实体类实例失败！！");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("执行SQL失败！！");
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			System.out.println("调用实体类Set方法失败！！");
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			System.out.println("创建实体类实例失败！！");
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			System.out.println("创建实体类实例失败！！");
		}
		finally
		{
			if (!isLongConnection)
				this.closeDbConnect();
		}

		return entityList;
	}

	/**
	 * 实现实体类的自动注入。 目前要求实体类属性名必须与字段名一致 返回单记录版
	 * 
	 * @param sql
	 * @param t
	 * @return
	 * @author zhaokai
	 * @version 2017年7月25日 下午12:25:05
	 */
	public <T> T doSelectSingle(String sql, Class<T> t)
	{

		T entity = null;
		try
		{
			if (con == null)
				this.connect(driver, url, userName, pwd);
			pre = con.prepareStatement(sql);// 实例化预编译语句
			result = pre.executeQuery();

			while (result.next())
			{
				entity = t.newInstance();
				Method[] methods = t.getDeclaredMethods();
				Field[] fields = t.getDeclaredFields();
				for (Field field : fields)
				{
					// TODO: 暂时不考虑按照类型去注入，实体类中统一按照String来装载
					// String fieldType = field.getType().getSimpleName();
					String fieldName = field.getName();
					String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					for (Method method : methods)
					{
						if (method.getName().equals(setMethodName))
						{
							method.invoke(entity, result.getString(fieldName));
							break;
						}
					}
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("执行SQL失败！！");
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			System.out.println("调用实体类Set方法失败！！");
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			System.out.println("创建实体类实例失败！！");
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			System.out.println("创建实体类实例失败！！");
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			System.out.println("创建实体类实例失败！！");
		}
		finally
		{
			if (!isLongConnection)
				this.closeDbConnect();
		}

		return entity;
	}

	/**
	 * 通过对象插入数据 List版。限制： 1.主键名称必须为小写“id” 2.id必须有get/set方法 3.属性名称必须与字段一致
	 * 4.id必须为自增或自动生成
	 * 
	 *
	 * @param t
	 * @return
	 * @author zhaokai
	 * @version 2017年7月28日 下午4:00:47
	 */
	public <T> List<T> doInsert(List<T> tList, String tableName)
	{
		List<T> returnList = new ArrayList<T>();
		Class<?> clazz = tList.get(0).getClass();
		Field fields[] = clazz.getDeclaredFields();
		Method[] methods = clazz.getDeclaredMethods();
		String setMethodName = null;

		try
		{
			for (T t : tList)
			{
				String insertSql = "Insert into " + tableName + " (";
				String valueSql = " values(";
				for (Field field : fields)
				{
					String fieldName = field.getName();
					if (fieldName.equals("id"))
					{
						setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
						continue;
					}
					insertSql = insertSql + fieldName + ",";
					String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					String value = null;
					for (Method method : methods)
					{
						if (method.getName().equals(getMethodName))
						{
							value = (String) method.invoke(t);
							break;

						}
					}
					valueSql = valueSql + "'" + value + "',";
				}
				insertSql = insertSql.substring(0, insertSql.length() - 1) + ")";
				valueSql = valueSql.substring(0, valueSql.length() - 1) + ");";
				String sql = insertSql + valueSql;

				pre = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pre.executeUpdate();
				con.commit();
				ResultSet result = pre.getGeneratedKeys();
				if (result.next())
				{
					for (Method method : methods)
					{
						if (method.getName().equals(setMethodName))
						{
							method.invoke(t, result.getString(1));
							break;
						}
					}
				}
				returnList.add(t);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("SQL执行错误！！");
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			System.out.println("调用对象的get/set方法错误！！！");
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			System.out.println("调用对象的get/set方法错误！！！");
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			System.out.println("调用对象的get/set方法错误！！！");
		}
		finally
		{
			if (!isLongConnection)
				this.closeDbConnect();
		}

		return returnList;
	}

	/**
	 * 通过对象插入数据 限制： 1.主键名称必须为小写“id” 2.id必须有get/set方法 3.属性名称必须与字段一致
	 * 4.id必须为自增或自动生成
	 * 
	 *
	 * @param t
	 * @return
	 * @author zhaokai
	 * @version 2017年7月28日 下午4:00:47
	 */
	public <T> T doInsert(T t, String tableName)
	{
		Class<?> clazz = t.getClass();
		Field fields[] = clazz.getDeclaredFields();
		Method[] methods = clazz.getDeclaredMethods();
		String insertSql = "Insert into " + tableName + " (";
		String valueSql = " values(";
		String setMethodName = null;
		try
		{
			for (Field field : fields)
			{
				String fieldName = field.getName();
				if (fieldName.equals("id"))
				{
					setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					continue;
				}
				insertSql = insertSql + fieldName + ",";
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				String value = null;
				for (Method method : methods)
				{
					if (method.getName().equals(getMethodName))
					{
						value = (String) method.invoke(t);
						break;

					}
				}
				valueSql = valueSql + "'" + value + "',";
			}
			insertSql = insertSql.substring(0, insertSql.length() - 1) + ")";
			valueSql = valueSql.substring(0, valueSql.length() - 1) + ");";
			String sql = insertSql + valueSql;

			pre = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pre.executeUpdate();
			con.commit();
			ResultSet result = pre.getGeneratedKeys();
			if (result.next())
			{
				for (Method method : methods)
				{
					if (method.getName().equals(setMethodName))
					{
						method.invoke(t, result.getString(1));
						break;
					}
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("SQL执行错误！！");
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			System.out.println("调用对象的get/set方法错误！！！");
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			System.out.println("调用对象的get/set方法错误！！！");
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			System.out.println("调用对象的get/set方法错误！！！");
		}
		finally
		{
			if (!isLongConnection)
				this.closeDbConnect();
		}

		return t;
	}

	/**
	 * 批量操作
	 *
	 * @param sql
	 * @author zhaokai
	 * @version 2017年7月25日 下午12:38:52
	 */
	public void doInsert(List<String> sqlList)
	{
		Statement s = null;
		try
		{
			if (con == null)
				this.connect(driver, url, userName, pwd);
			s = con.createStatement();
			for (String sql : sqlList)
				s.addBatch(sql);
			s.executeBatch();
			con.commit();
			s.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("执行SQL错误！！");
		}
		finally
		{
			try
			{
				if (s != null)
					s.close();
				if (!isLongConnection)
					this.closeDbConnect();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 单条操作
	 *
	 * @param sql
	 * @author zhaokai
	 * @version 2017年7月25日 下午12:43:46
	 */
	public void doInsert(String sql)
	{
		try
		{
			if (con == null)
				this.connect(driver, url, userName, pwd);
			pre = con.prepareStatement(sql);
			pre.executeUpdate();
			con.commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (!isLongConnection)
				this.closeDbConnect();
		}
	}

	public void closeDbConnect()
	{
		try
		{
//			if (result != null)
//			{
//				result.close();
//				result = null;
//			}
			if (pre != null)
			{
				pre.close();
				pre = null;
			}
			if (con != null)
			{
				con.close();
				con = null;
			}
			System.out.println("数据库连接:" + url + " 已关闭！");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("关闭数据库连接:" + url + " 失败！！");
		}
	}
}
