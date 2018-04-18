package com.k.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.k.util.StringUtils;

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

	public List<Map<String, String>> doSelect(String sql) throws SQLException
	{
		List<Map<String, String>> res = new ArrayList<Map<String, String>>();
		try
		{
			if (con == null)
				this.connect(driver, url, userName, pwd);
			pre = con.prepareStatement(sql);// 实例化预编译语句
			result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
			ResultSetMetaData rsmd = result.getMetaData();
			while (result.next())
			{
				Map<String, String> row = new HashMap<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
				{
					row.put(rsmd.getColumnName(i).toLowerCase(), result.getString(rsmd.getColumnName(i)));
				}
				res.add(row);
			}
			
		}
		finally
		{
			result.close();
			clearStatement();
			if (!isLongConnection)
				this.closeDbConnect();
		}
		return res;
	}

	/**
	 * 实现实体类的自动注入。 目前要求实体类属性名必须与字段名一致
	 *
	 * @param sql
	 * @param t
	 * @return
	 * @author zhaokai
	 * @version 2017年7月25日 下午12:25:05
	 * @throws SQLException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public <T> List<T> doSelect(String sql, Class<T> t) throws SQLException, InstantiationException, InvocationTargetException, IllegalAccessException
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
							// System.out.println(fieldName);
							method.invoke(entity, result.getString(fieldName));
							break;
						}
					}
				}
				entityList.add(entity);
			}

		}
		catch (InstantiationException e)
		{
			System.out.println("创建实体类实例失败！！");
			throw e;
		}
		catch (SQLException e)
		{
			System.out.println("执行SQL失败！！");
			throw e;
		}
		catch (InvocationTargetException e)
		{
			System.out.println("调用实体类Set方法失败！！");
			throw e;
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("创建实体类实例失败！！");
			throw e;
		}
		catch (IllegalAccessException e)
		{
			System.out.println("创建实体类实例失败！！");
			throw e;
		}
		finally
		{
			result.close();
			clearStatement();
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
	 * @throws SQLException
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public <T> T doSelectSingle(String sql, Class<T> t) throws SQLException, InstantiationException, InvocationTargetException, IllegalAccessException
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
			System.out.println("执行SQL失败！！");
			throw e;
		}
		catch (InvocationTargetException e)
		{
			System.out.println("调用实体类Set方法失败！！");
			throw e;
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("创建实体类实例失败！！");
			throw e;
		}
		catch (IllegalAccessException e)
		{
			System.out.println("创建实体类实例失败！！");
			throw e;
		}
		catch (InstantiationException e)
		{
			System.out.println("创建实体类实例失败！！");
			throw e;
		}
		finally
		{
			result.close();
			clearStatement();
			if (!isLongConnection)
				this.closeDbConnect();
		}

		return entity;
	}

	/**
	 * 通过对象插入数据 List版。 限制： 1.数据库中主键名称必须为小写“id” 2.id必须有get/set方法 3.属性名称必须与字段一致
	 * 4.不插入ID的话，id在db中必须为自增或自动生成
	 * 
	 * @param tList-对象列表
	 *            tableName-db中表名 ifSkipId-是否不插入ID数据。true-不插入；false-插入ID
	 * @return
	 * @author zhaokai
	 * @version 2017年7月28日 下午4:00:47
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public <T> List<T> doInsert(List<T> tList, String tableName, Boolean ifSkipId)
			throws SQLException, IllegalAccessException, InvocationTargetException
	{
		List<T> returnList = new ArrayList<T>();

		for (T t : tList)
		{
			t = doInsert(t, tableName, ifSkipId);
			returnList.add(t);
		}

		return returnList;
	}

	/**
	 * 通过对象插入数据。 限制： 1.数据库中主键名称必须为小写“id” 2.id必须有get/set方法 3.属性名称必须与字段一致
	 * 4.不插入ID的话，id在db中必须为自增或自动生成
	 * 
	 * @param t-对象
	 *            tableName-db中表名 isUpdateId-是否反取ID。true-更新；false-不更新
	 * @return
	 * @author zhaokai
	 * @version 2017年7月28日 下午4:00:47
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public <T> T doInsert(T t, String tableName, Boolean isUpdateId)
			throws SQLException, IllegalAccessException, InvocationTargetException
	{
		try
		{
			String sql = getInsertSql(t, tableName);

			// System.out.println(sql);
			pre = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pre.executeUpdate();
			con.commit();
			if (isUpdateId)
			{
				Class<?> clazz = t.getClass();
				Field fields[] = clazz.getDeclaredFields();
				Method[] methods = clazz.getDeclaredMethods();
				String setMethodName = null;
				ResultSet result = pre.getGeneratedKeys();
				for (Field field : fields)
				{
					String fieldName = field.getName();
					if (fieldName.equals("id"))
					{
						setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
						break;
					}
				}
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
		}
		catch (SQLException e)
		{
			System.out.println("SQL执行错误！！");
			throw e;
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("调用对象的get/set方法错误！！！");
			throw e;
		}
		catch (IllegalAccessException e)
		{
			System.out.println("调用对象的get/set方法错误！！！");
			throw e;
		}
		catch (InvocationTargetException e)
		{
			System.out.println("调用对象的get/set方法错误！！！");
			throw e;
		}
		finally
		{
			result.close();
			clearStatement();
			if (!isLongConnection)
				this.closeDbConnect();
		}

		return t;
	}

	/**
	 * 实体类转为Insert语句。类属性与数据库字段名称必须一致
	 * 
	 * @param t
	 * @param tableName
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @author zhaokai
	 * @create 2018年4月3日 下午6:04:24
	 */
	public <T> String getInsertSql(T t, String tableName)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Class<?> clazz = t.getClass();
		Field fields[] = clazz.getDeclaredFields();
		Method[] methods = clazz.getDeclaredMethods();
		String insertSql = "Insert into " + tableName + " (";
		String valueSql = " values(";
		for (Field field : fields)
		{
			String fieldName = field.getName();
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
			if (StringUtils.isNullOrEmpty(value))
				continue;
			insertSql = insertSql + fieldName + ",";
			valueSql = valueSql + "'" + value + "',";
		}
		insertSql = insertSql.substring(0, insertSql.length() - 1) + ")";
		valueSql = valueSql.substring(0, valueSql.length() - 1) + ")";
		String sql = insertSql + valueSql;
		return sql;
	}

	/**
	 * 批量操作
	 *
	 * @param sql
	 * @author zhaokai
	 * @version 2017年7月25日 下午12:38:52
	 * @throws SQLException 
	 */
	public void doInsert(List<String> sqlList) throws SQLException
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
			s.clearBatch();
			s.close();
		}
		catch (SQLException e)
		{
			System.out.println("执行SQL错误！！");
			throw e;
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
	 * @throws SQLException
	 */
	public void doInsert(String sql) throws SQLException
	{
		try
		{
			if (con == null)
				this.connect(driver, url, userName, pwd);
			pre = con.prepareStatement(sql);
			pre.executeUpdate();
			con.commit();
		}
		finally
		{
			clearStatement();
			if (!isLongConnection)
				this.closeDbConnect();

		}
	}

	public void closeDbConnect()
	{
		try
		{
			if (result != null)
			{
				result.close();
				result = null;
			}
			if (pre != null)
			{
				pre.clearBatch();
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

	public void clearStatement() throws SQLException
	{
		if (pre != null)
		{
			pre.clearBatch();
			pre.close();
			pre = null;
		}
	}
}
