package com.k.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


import com.k.util.filehelper.FileHelper;

public class JdbcUpdate
{
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	static final String DB_URL = "jdbc:oracle:thin:@192.168.2.131:1521:orcl";

	// Database credentials
	static final String USER = "gsmr";
	static final String PASS = "gsmr";

	public static void main(String[] args)
	{
		Connection conn = null;
		try
		{
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Updating data...");
			String sql = "update T_GSMR_BASE_STATION_BASE_P set group_ring = ?, bsic = ?, carrier_num = ?,  "
					+ "base_type = (select pk_id from T_DICTIONARY where code = ?), "
					+ "bsc_id = (select pk_id from T_GSMR_NET_BASIC where data_status = '2' and equipment_name = ?) where pk_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			FileHelper fh = new FileHelper("src.txt");
			List<String> dataList = fh.read();

			for (String line : dataList)
			{
				String[] param = line.split(",");
				pstmt.setString(1, param[1]);
				pstmt.setString(2, param[2]);
				pstmt.setString(3, param[3]);
				pstmt.setString(4, param[4]);
				pstmt.setString(5, param[5]);
				pstmt.setString(6, param[0]);
				pstmt.addBatch(); // 添加一次预定义参数
			}
			// 批量执行预定义SQL
			pstmt.executeBatch();

			conn.close();
		}
		catch (SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch (Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		finally
		{
			// finally block used to close resources
			try
			{
				if (conn != null)
					conn.close();
			}
			catch (SQLException se)
			{
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");
	}

}
