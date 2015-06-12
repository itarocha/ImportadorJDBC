package br.com.itarocha.importadorjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

		if (tipo.toLowerCase() == "mysql") { 
			DB_URL = "jdbc:mysql://localhost:3306/sakila";
			DB_USER = "root";
			DB_PASSWORD = "root";
		} else {
			DB_URL = "jdbc:firebirdsql: localhost/3050:d:/database/soft.fdb";
			DB_USER = "SYSDBA";
			DB_PASSWORD = "masterkey";
		}
		Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		System.err.println("The connection is successfully obtained");
		return connection;
	}
}
