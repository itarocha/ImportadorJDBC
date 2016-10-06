package br.com.itarocha.importadorjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	//private static String DB_URL = "jdbc:mysql://localhost:3306/sakila";
	//private static String DB_USER = "root";
	//private static String DB_PASSWORD = "root";
	
	//private static String DB_URL = "jdbc:firebirdsql: localhost/3050:d:/database/soft.fdb";
	//private static String DB_USER = "SYSDBA";
	//private static String DB_PASSWORD = "masterkey";
	
	

	public static Connection getConnection(String tipo) throws SQLException {
		String DB_URL;
		String DB_USER;
		String DB_PASSWORD;

		Connection connection;
		if (tipo.toLowerCase() == "mysql") { 
			DB_URL = "jdbc:mysql://localhost:3306/newpetra";
			DB_USER = "root";
			DB_PASSWORD = "root";
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} else {
			DB_URL = "jdbc:firebirdsql:localhost/3050:d:/database/petra.fdb";
			DB_USER = "SYSDBA";
			DB_PASSWORD = "masterkey";
			Properties props = new Properties();
			 props.setProperty("user", "SYSDBA");
			 props.setProperty("password", "masterkey");
			 props.setProperty("encoding", "WIN1252");
			 //props.setProperty("encoding", "UNICODE_FSS");
			 props.setProperty("sqldialect", "3");
			 //props.setProperty("lc_ctype", "WIN1252");
			 connection = DriverManager.getConnection(DB_URL,props);
			
		}
		System.err.println("The connection is successfully obtained");
		return connection;
	}
}
