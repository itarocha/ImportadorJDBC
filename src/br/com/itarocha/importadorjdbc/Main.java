package br.com.itarocha.importadorjdbc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Main {

	static Connection connection = null;
	static DatabaseMetaData metadata = null;

	// Static block for initialization
	static {
		try {
			connection = DBConnection.getConnection("firebird");
		} catch (SQLException e) {
			System.err.println("There was an error getting the connection: "
					+ e.getMessage());
		}

		try {
			metadata = connection.getMetaData();
		} catch (SQLException e) {
			System.err.println("There was an error getting the metadata: "
					+ e.getMessage());
		}
	}

	/**
	 * Prints in the console the general metadata.
	 * 
	 * @throws SQLException
	 */
	public static void printGeneralMetadata() throws SQLException {
		System.out.println("Database Product Name: "
				+ metadata.getDatabaseProductName());
		System.out.println("Database Product Version: "
				+ metadata.getDatabaseProductVersion());
		System.out.println("Logged User: " + metadata.getUserName());
		System.out.println("JDBC Driver: " + metadata.getDriverName());
		System.out.println("Driver Version: " + metadata.getDriverVersion());
		System.out.println("\n");
	}

	/**
	 * 
	 * @return Arraylist with the table's name
	 * @throws SQLException
	 */
	public static ArrayList getTablesMetadata() throws SQLException {
		String table[] = { "TABLE" };
		ResultSet rs = null;
		ArrayList tables = null;
		// receive the Type of the object in a String array.
		rs = metadata.getTables(null, null, null, table);
		tables = new ArrayList();
		while (rs.next()) {
			tables.add(rs.getString("TABLE_NAME"));
		}
		return tables;
	}

	/**
	 * Prints in the console the columns metadata, based in the Arraylist of
	 * tables passed as parameter.
	 * 
	 * @param tables
	 * @throws SQLException
	 */
	public static void buildMetadata(ArrayList<String> tables)
			throws SQLException, IOException {
		
		BufferedWriter bf = new BufferedWriter(new FileWriter("c:\\temp\\metadata.sql"));
		
		ResultSet rs = null;
		// Print the columns properties of the actual table
		Statement st = connection.createStatement();
		st = connection.createStatement();
		for (String actualTable : tables) {
			System.out.println(String.format("gerando metadata da tabela \"%s\"",actualTable));
			rs = st.executeQuery("SELECT * FROM "+actualTable);	
			
			ResultSetMetaData rsMetaData = rs.getMetaData();

		    int numberOfColumns = rsMetaData.getColumnCount();
		    bf.write("\nCREATE TABLE "+actualTable.toLowerCase()+"(\n");
		    bf.write(String.format("`%s_id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n",actualTable.toLowerCase()));
			
		    for (int i = 1; i <= numberOfColumns; i++) {
		    	
		    	bf.write(traduzTipo(rsMetaData.getColumnName(i).toLowerCase(),
		    						rsMetaData.getColumnTypeName(i).toLowerCase(),
		    						rsMetaData.getPrecision(i),
		    						rsMetaData.getScale(i),
		    						rsMetaData.isNullable(i)) );
		    	
		        //System.out.println("column MetaData ");
		        //System.out.println("column number " + i);
		        // indicates the designated column's normal maximum width in
		        // characters
		        ///////////////bf.write("getColumnDisplaySize()="+ rsMetaData.getColumnDisplaySize(i)+"\n");
		        // gets the designated column's suggested title
		        // for use in printouts and displays.
		        //System.out.println("getColumnLabel "+rsMetaData.getColumnLabel(i));
		        // get the designated column's name.
		        //bf.write("getColumnName()=" + rsMetaData.getColumnName(i)+"\n");

		        // get the designated column's SQL type.
		        /////////////bf.write("getColumnType "+rsMetaData.getColumnType(i)+"\n");

		        // get the designated column's SQL type name.
		        ///////////////bf.write("getColumnTypeName "+rsMetaData.getColumnTypeName(i)+"\n"); // VARCHAR, CHAR, INTEGER, SMALLINT, NUMERIC, DATE, TIME, TIMESTAMP 

		        // get the designated column's class name.
		        /////////////System.out.println("getColumnClassName "+rsMetaData.getColumnClassName(i));

		        // get the designated column's table name.
		        //System.out.println("getTableName "+rsMetaData.getTableName(i));

		        // get the designated column's number of decimal digits.
		        ///////////bf.write("getPrecision()="+rsMetaData.getPrecision(i)+"\n");

		        // gets the designated column's number of
		        // digits to right of the decimal point.
		        //////////////bf.write("getScale()="+rsMetaData.getScale(i)+"\n");

		        // indicates whether the designated column is
		        // automatically numbered, thus read-only.
		        //System.out.println("isAutoIncrement "+rsMetaData.isAutoIncrement(i));

		        // indicates whether the designated column is a cash value.
		        //System.out.println("isCurrency "+rsMetaData.isCurrency(i));

		        // indicates whether a write on the designated
		        // column will succeed.
		        //System.out.println(rsMetaData.isWritable(i));

		        // indicates whether a write on the designated
		        // column will definitely succeed.
		        //System.out.println(rsMetaData.isDefinitelyWritable(i));

		        // indicates the nullability of values
		        // in the designated column.
		        ////////////bf.write("isNullable()="+rsMetaData.isNullable(i)+"\n");

		        // Indicates whether the designated column
		        // is definitely not writable.
		        //System.out.println(rsMetaData.isReadOnly(i));

		        // Indicates whether a column's case matters
		        // in the designated column.
		        //System.out.println(rsMetaData.isCaseSensitive(i));

		        // Indicates whether a column's case matters
		        // in the designated column.
		        //System.out.println(rsMetaData.isSearchable(i));

		        // indicates whether values in the designated
		        // column are signed numbers.
		        //System.out.println(rsMetaData.isSigned(i));

		        // Gets the designated column's table's catalog name.
		        //System.out.println("getCatalogName "+rsMetaData.getCatalogName(i));

		        // Gets the designated column's table's schema name.
		        //System.out.println("getSchemaName "+rsMetaData.getSchemaName(i));
		        bf.write("\n");
		      }
		    

		      //st.close();		    
			
			/*
			rs = metadata.getColumns(null, null, actualTable, null);
			
			
			System.out.println(actualTable.toUpperCase());
			while (rs.next()) {
				ResultSetMetaData rsMetaData = rs.getMetaData();
				int numberOfColumns = rsMetaData.getColumnCount();
				System.out.println(rs.getString("COLUMN_NAME") + ";"
						+ rs.getString("TYPE_NAME") + ";"
						//+ rs.getString("TYPE_SCHEM") + ";"
						+rs.getInt("ORDINAL_POSITION")+";"
						+ rs.getString("COLUMN_SIZE")+";"+
						+ numberOfColumns);
			}
			*/

		    bf.write(String.format("PRIMARY KEY (`%s_id`)",actualTable.toLowerCase()));
		    bf.write(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");		    
		    bf.write("\n");
		}
		bf.close();
		System.out.println("fim....");

	}
	
	public static void moveData(ArrayList<String> tables)
			throws SQLException, IOException {
		
		String campos;
		String values;
		BufferedWriter bf;
		
		ResultSet rs = null;
		// Print the columns properties of the actual table
		Statement st = connection.createStatement();
		st = connection.createStatement();
		for (String actualTable : tables) {
			if (actualTable.toLowerCase().startsWith("ppr")){
				continue;
			}
			
			System.out.println(String.format("gerando sql da tabela \"%s\"",actualTable));
			bf = new BufferedWriter(new FileWriter(String.format("c:\\temp\\movedata_%s.sql",actualTable)));
			rs = st.executeQuery("SELECT * FROM "+actualTable);	
			
			ResultSetMetaData rsMetaData = rs.getMetaData();

		    int numberOfColumns = rsMetaData.getColumnCount();
		    campos = "";
			
		    ArrayList<String> listaCampos = new ArrayList<String>(); 
		    
		    for (int i = 1; i <= numberOfColumns; i++) {
		    	listaCampos.add(rsMetaData.getColumnClassName(i));
		    	campos += rsMetaData.getColumnName(i).toLowerCase()+",";
		      }
		      campos = campos.substring(0,campos.length()-1);
		      
		      bf.write(String.format("INSERT INTO %s (%s) VALUES ",actualTable.toLowerCase(), campos));
		      
		      while (rs.next()){
		    	  values = "(";
		    	  for (int i = 1; i <= numberOfColumns; i++) {
		    		  
		    		  //values+= String.format("[%s]",listaCampos.get(i-1));
		    		  
		    		  if (rs.getObject(i) != null) {
		    			  
		    			  if (listaCampos.get(i-1).equalsIgnoreCase("java.lang.integer") ||
		    				 listaCampos.get(i-1).equalsIgnoreCase("java.math.bigdecimal") )
		    			  {
		    				  values+=(String.format("%d,", rs.getInt(i) ) );
		    			  } else 
			    		  if (listaCampos.get(i-1).equalsIgnoreCase("java.sql.timestamp") ){
			    			  values+=(String.format("'%s',", rs.getTimestamp(i) ) );
			    		  }
		    			  else {
		    				  values+=(String.format("'%s',", limparString( rs.getString(i) ) ) );
		    			  }
		    			  
		    			  
		    		  } else{
		    			  values+=("NULL,");
		    		  }
		    		  //values+=(String.format("'%s',", new String(rs.getBytes(i), Charset.forName("Cp1252") ))  );
		    		  //values+=(String.format("'%s',", new String(rs.getBytes(i), "Cp1252")) );
		    		  
		    		    
		    	  }
	    		  values=values.substring(0,values.length()-1)+")\n";  
		    	  if (!rs.isLast()) {
		    		  values+=",";  
		    	  }
		    	  bf.write(values);
		      }
		      
		      bf.write(";\n");
		      bf.close();

		      //st.close();		    
			
			/*
			rs = metadata.getColumns(null, null, actualTable, null);
			
			
			System.out.println(actualTable.toUpperCase());
			while (rs.next()) {
				ResultSetMetaData rsMetaData = rs.getMetaData();
				int numberOfColumns = rsMetaData.getColumnCount();
				System.out.println(rs.getString("COLUMN_NAME") + ";"
						+ rs.getString("TYPE_NAME") + ";"
						//+ rs.getString("TYPE_SCHEM") + ";"
						+rs.getInt("ORDINAL_POSITION")+";"
						+ rs.getString("COLUMN_SIZE")+";"+
						+ numberOfColumns);
			}
			*/

		    //bf.write(String.format("PRIMARY KEY (`%s_id`)",actualTable.toLowerCase()));
		    //bf.write(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");		    
		    //bf.write("\n");
		}
		
		
		System.out.println("fim....");

	}
	
	private static String limparString(String entrada){
		String saida = entrada.replace("'", "''");
		saida = saida.replace("\\","\\\\");
		return saida;
	}

	private static String traduzTipo(String nome, String tipo, Integer escala, Integer precisao, Integer idNullable){
		String retorno = "";
		
		if (tipo.equalsIgnoreCase("char")) {
			retorno = String.format("`%s` char(%d) COLLATE utf8_unicode_ci", nome, escala);
		} else
		if (tipo.equalsIgnoreCase("varchar")) {
			retorno = String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala);
		} else
		if (tipo.equalsIgnoreCase("numeric")) {
			retorno = String.format("`%s` decimal(%d,%d)", nome, escala, precisao);
		} else 
		if (tipo.equalsIgnoreCase("integer")) {
			retorno = String.format("`%s` int(11)", nome);
		} else 
		if (tipo.equalsIgnoreCase("smallint")) {
			retorno = String.format("`%s` smallint(6)", nome);
		} else {
			retorno = String.format("`%s` %s", nome,tipo);
		}
		
		if (idNullable == 0){
			retorno += " NOT NULL,"; 
		} else {
			retorno += ",";
		}
		
		return retorno;
		
	}
	
	//System.out.printf("%d: %s (%d)\n", i++, columns.getString("COLUMN_NAME"), columns.getInt("ORDINAL_POSITION"));

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			printGeneralMetadata();
			// Print all the tables of the database scheme, with their names and
			// structure
			buildMetadata(getTablesMetadata());
			//moveData(getTablesMetadata());
			
			ArrayList<String> a = new ArrayList<String>();
			a.add("emp");
			moveData(a);
			
		} catch (SQLException e) {
			System.err
					.println("There was an error retrieving the metadata properties: "
							+ e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/*
Statement stmt = connection.createStatement();
try {
 DatabaseMetaData metaData = connection.getMetaData();
 ResultSet tables = metaData.getTables(
 null, null, "customer", new String[]{"TABLE"});
 if (!tables.next())
 stmt.execute("CREATE TABLE customer(" +
 "customerId INTEGER NOT NULL PRIMARY KEY, " +
 "firstName VARCHAR(20) NOT NULL, " +
 "lastName VARCHAR(40) NOT NULL)");
} finally {
 stmt.close();
}
*/

/*
public void save(List<Entity> entities) throws SQLException {
    try (
        Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
    ) {
        int i = 0;

        for (Entity entity : entities) {
            statement.setString(1, entity.getSomeProperty());
            // ...

            statement.addBatch();
            i++;

            if (i % 1000 == 0 || i == entities.size()) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }
    }
} 
  
 */
 