package br.com.itarocha.importadorjdbc;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Main {

	static Connection connection = null;
	static DatabaseMetaData metadata = null;

	// Static block for initialization
	static {
		try {
			connection = DBConnection.getConnection("firebird");
		} catch (SQLException e) {
			System.err.println("There was an error getting the connection: " + e.getMessage());
		}

		try {
			metadata = connection.getMetaData();
		} catch (SQLException e) {
			System.err.println("There was an error getting the metadata: " + e.getMessage());
		}
	}

	/**
	 * Prints in the console the general metadata.
	 * 
	 * @throws SQLException
	 */
	public static void printGeneralMetadata() throws SQLException {
		System.out.println("Database Product Name: " + metadata.getDatabaseProductName());
		System.out.println("Database Product Version: " + metadata.getDatabaseProductVersion());
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
	public static void buildMetadata(ArrayList<String> tables) throws SQLException, IOException {

		BufferedWriter bf = new BufferedWriter(new FileWriter("c:\\temp\\metadata.sql"));

		ResultSet rs = null;
		// Print the columns properties of the actual table
		Statement st = connection.createStatement();
		st = connection.createStatement();
		for (String actualTable : tables) {
			System.out.println(String.format("gerando metadata da tabela \"%s\"", actualTable));
			rs = st.executeQuery("SELECT * FROM " + actualTable);

			ResultSetMetaData rsMetaData = rs.getMetaData();

			int numberOfColumns = rsMetaData.getColumnCount();
			bf.write("\nCREATE TABLE " + actualTable.toLowerCase() + "(\n");
			bf.write(String.format("`%s_id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n", actualTable.toLowerCase()));

			for (int i = 1; i <= numberOfColumns; i++) {

				bf.write(traduzTipo(
						actualTable.toLowerCase(),
						rsMetaData.getColumnName(i).toLowerCase(),
						rsMetaData.getColumnTypeName(i).toLowerCase(), rsMetaData.getPrecision(i),
						rsMetaData.getScale(i), rsMetaData.isNullable(i)));

				// System.out.println("column MetaData ");
				// System.out.println("column number " + i);
				// indicates the designated column's normal maximum width in
				// characters
				/////////////// bf.write("getColumnDisplaySize()="+
				// rsMetaData.getColumnDisplaySize(i)+"\n");
				// gets the designated column's suggested title
				// for use in printouts and displays.
				// System.out.println("getColumnLabel
				// "+rsMetaData.getColumnLabel(i));
				// get the designated column's name.
				// bf.write("getColumnName()=" +
				// rsMetaData.getColumnName(i)+"\n");

				// get the designated column's SQL type.
				///////////// bf.write("getColumnType
				// "+rsMetaData.getColumnType(i)+"\n");

				// get the designated column's SQL type name.
				/////////////// bf.write("getColumnTypeName
				// "+rsMetaData.getColumnTypeName(i)+"\n"); // VARCHAR, CHAR,
				// INTEGER, SMALLINT, NUMERIC, DATE, TIME, TIMESTAMP

				// get the designated column's class name.
				///////////// System.out.println("getColumnClassName
				// "+rsMetaData.getColumnClassName(i));

				// get the designated column's table name.
				// System.out.println("getTableName
				// "+rsMetaData.getTableName(i));

				// get the designated column's number of decimal digits.
				/////////// bf.write("getPrecision()="+rsMetaData.getPrecision(i)+"\n");

				// gets the designated column's number of
				// digits to right of the decimal point.
				////////////// bf.write("getScale()="+rsMetaData.getScale(i)+"\n");

				// indicates whether the designated column is
				// automatically numbered, thus read-only.
				// System.out.println("isAutoIncrement
				// "+rsMetaData.isAutoIncrement(i));

				// indicates whether the designated column is a cash value.
				// System.out.println("isCurrency "+rsMetaData.isCurrency(i));

				// indicates whether a write on the designated
				// column will succeed.
				// System.out.println(rsMetaData.isWritable(i));

				// indicates whether a write on the designated
				// column will definitely succeed.
				// System.out.println(rsMetaData.isDefinitelyWritable(i));

				// indicates the nullability of values
				// in the designated column.
				//////////// bf.write("isNullable()="+rsMetaData.isNullable(i)+"\n");

				// Indicates whether the designated column
				// is definitely not writable.
				// System.out.println(rsMetaData.isReadOnly(i));

				// Indicates whether a column's case matters
				// in the designated column.
				// System.out.println(rsMetaData.isCaseSensitive(i));

				// Indicates whether a column's case matters
				// in the designated column.
				// System.out.println(rsMetaData.isSearchable(i));

				// indicates whether values in the designated
				// column are signed numbers.
				// System.out.println(rsMetaData.isSigned(i));

				// Gets the designated column's table's catalog name.
				// System.out.println("getCatalogName
				// "+rsMetaData.getCatalogName(i));

				// Gets the designated column's table's schema name.
				// System.out.println("getSchemaName
				// "+rsMetaData.getSchemaName(i));
				bf.write("\n");
			}

			// st.close();

			/*
			 * rs = metadata.getColumns(null, null, actualTable, null);
			 * 
			 * 
			 * System.out.println(actualTable.toUpperCase()); while (rs.next())
			 * { ResultSetMetaData rsMetaData = rs.getMetaData(); int
			 * numberOfColumns = rsMetaData.getColumnCount();
			 * System.out.println(rs.getString("COLUMN_NAME") + ";" +
			 * rs.getString("TYPE_NAME") + ";" //+ rs.getString("TYPE_SCHEM") +
			 * ";" +rs.getInt("ORDINAL_POSITION")+";" +
			 * rs.getString("COLUMN_SIZE")+";"+ + numberOfColumns); }
			 */

			bf.write(String.format("PRIMARY KEY (`%s_id`)", actualTable.toLowerCase()));
			bf.write(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");
			bf.write("\n");
		}
		bf.close();
		System.out.println("fim....");

	}

	public static void moveData(ArrayList<String> tables, 
								Config config) throws SQLException, IOException {

		int qtdRegistrosPorCommit = 10000;
		String campos;
		String values;
		//BufferedWriter bf = null;
		OutputStreamWriter bf = null;

		ResultSet rs = null;
		
		SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
		
		
		
		Calendar calendar = Calendar.getInstance();

		Date dateMax = calendar.getTime();		
		
		calendar.clear();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 1900);
		Date dateMin = calendar.getTime();		
		
		try {
			dateMin = formatoData.parse("01/01/1970");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Boolean umArquivoPorTabela = true;

		if (!config.getUmArquivoPorTabela()) {
			//bf = new BufferedWriter(new FileWriter("c:\\temp\\movedatanew.sql"));
			//bf = new OutputStreamWriter(new FileOutputStream("c:\\temp\\movedata.sql"),"UTF-8"); // WINDOWS-1252
			//bf = new OutputStreamWriter(new FileOutputStream("c:\\temp\\movedata.sql"),"WINDOWS-1252"); // WINDOWS-1252
			bf = new OutputStreamWriter(new FileOutputStream(String.format("%s%s.sql", config.getPath(), config.getFileNameData()) ),"WINDOWS-1252"); // WINDOWS-1252
			
			
			// latin1
			//bf.write("SET NAMES 'latin1';\n");
			//bf.write("SET character_set_connection=latin1;\n");
			//bf.write("SET character_set_client=latin1;\n");
			//bf.write("SET character_set_results=latin1;\n");
		}

		// Print the columns properties of the actual table
		Statement st = connection.createStatement();
		st = connection.createStatement();
		String insertCommand = "";
		for (String actualTable : tables) {
			if (actualTable.toLowerCase().startsWith("ppr")) {
				continue;
			}
			if (actualTable.toLowerCase().equalsIgnoreCase("audlwe")) {
				continue;
			}

			System.out.println(String.format("gerando sql da tabela \"%s\"", actualTable));
			if (config.getUmArquivoPorTabela()) {
				
				//bf = new OutputStreamWriter(new FileOutputStream(String.format("c:\\temp\\movedata_%s.sql", actualTable)),"UTF-8");
				//bf = new OutputStreamWriter(new FileOutputStream(String.format("c:\\temp\\movedata_%s.sql", actualTable)),"WINDOWS-1252");
				bf = new OutputStreamWriter(new FileOutputStream(String.format("%s%s_%s.sql", 
						config.getPath(), config.getFileNameData(), actualTable) ),
						"WINDOWS-1252"); // WINDOWS-1252

				
				// latin1
				//bf.write("SET NAMES 'latin1';\n");
				//bf.write("SET character_set_connection=latin1;\n");
				//bf.write("SET character_set_client=latin1;\n");
				//bf.write("SET character_set_results=latin1;\n");
				
				//bf = new BufferedWriter(new FileWriter(String.format("c:\\temp\\movedatanew_%s.sql", actualTable)));
				//bf = new BufferedWriter(new FileWriter(String.format("c:\\temp\\movedatanew_%s.sql", actualTable)));
			}
			rs = st.executeQuery("SELECT * FROM " + actualTable);

			ResultSetMetaData rsMetaData = rs.getMetaData();

			int numberOfColumns = rsMetaData.getColumnCount();
			campos = "";

			ArrayList<String> listaCampos = new ArrayList<String>();

			for (int i = 1; i <= numberOfColumns; i++) {
				listaCampos.add(rsMetaData.getColumnClassName(i));
				campos += rsMetaData.getColumnName(i).toLowerCase() + ",";
			}
			campos = campos.substring(0, campos.length() - 1);



			int qtdRecords = 0;
			int cont = 0;
			Boolean imprimiuInsert = false;
			while (rs.next()) {
				if (!imprimiuInsert) {

					// latin1
					bf.write("SET NAMES 'latin1';\n");
					bf.write("SET character_set_connection=latin1;\n");
					bf.write("SET character_set_client=latin1;\n");
					bf.write("SET character_set_results=latin1;\n");
					
					insertCommand = String.format("INSERT INTO %s (%s) VALUES ", actualTable.toLowerCase(), campos); 
					bf.write(insertCommand);
					imprimiuInsert = true;
				}

				cont++;
				qtdRecords++;
				values = "(";
				for (int i = 1; i <= numberOfColumns; i++) {

					if (rs.getObject(i) != null) {
						if (listaCampos.get(i - 1).equalsIgnoreCase("java.lang.integer")
								|| listaCampos.get(i - 1).equalsIgnoreCase("java.math.bigdecimal")) {
							values += (String.format("%d,", rs.getInt(i)));
						} else if (listaCampos.get(i - 1).equalsIgnoreCase("java.sql.timestamp")) {
							Timestamp t = rs.getTimestamp(i);
							
							
							
							if ( t.before(dateMin) ){
								//System.out.println("Olha que bosta: "+formatoData.format(t));
								t = new Timestamp(dateMin.getTime());
							} else 
							if ( t.after(dateMax) ){
								//System.out.println("Olha que bosta: "+formatoData.format(t)+" "+new Timestamp(dateMax.getTime()));
								t = new Timestamp(dateMax.getTime());
								//System.out.println("   VIROU: "+formatoData.format(t));
							} 
							
							//values += (String.format("'%s',", rs.getTimestamp(i)));
							values += (String.format("'%s',", t ));
						} else {
							values += (String.format("'%s',", limparString(rs.getString(i))));
						}
					} else {
						values += ("NULL,");
					}
				}
				// values = values.substring(0, values.length() - 1) + ")\n";
				values = values.substring(0, values.length() - 1) + ")";
				if (!rs.isLast() && (cont <= qtdRegistrosPorCommit)) {
					values += ",";
				}
				bf.write(values);
				
				if (cont > qtdRegistrosPorCommit) {
					//bf.write(";\n");
					bf.write(";");
					
					if (config.getAplicarCommit()) {
						//bf.write("commit;\n");
						bf.write("commit;");
					}
					
					bf.write("SET NAMES 'latin1';\n");
					bf.write("SET character_set_connection=latin1;\n");
					bf.write("SET character_set_client=latin1;\n");
					bf.write("SET character_set_results=latin1;\n");
					
					
					bf.write(insertCommand);
					cont = 0;
				}
				
			} // Para cada linha

			if (qtdRecords > 0) {
				bf.write(";\n");
				if (config.getAplicarCommit()) {
					//bf.write("commit;\n");
					bf.write("commit;");
				}
			}
			
			if (config.getUmArquivoPorTabela()) {
				bf.close();
			}
		}

		if (!config.getUmArquivoPorTabela()) {
			bf.close();
		}

		System.out.println("fim....");

	}
	
	public static String UTF8toISO(String str){
        //Charset utf8charset = Charset.forName("ISO-8859-1");
        Charset utf8charset = Charset.forName("WINDOWS-1252");
        Charset iso88591charset = Charset.forName("UTF-8");

        ByteBuffer inputBuffer = ByteBuffer.wrap(str.getBytes());

        // decode UTF-8
        CharBuffer data = utf8charset.decode(inputBuffer);

        // encode ISO-8559-1
        ByteBuffer outputBuffer = iso88591charset.encode(data);
        byte[] outputData = outputBuffer.array();

        return new String(outputData);
    }

	public static String traduzzz(String input){
		String output = "";
		try {
		    /* From ISO-8859-1 to UTF-8 */
		    //output = new String(input.getBytes("ISO-8859-1"), "UTF-8");
		    output = new String(input.getBytes("CP1252"), "UTF-8");
		    /* From UTF-8 to ISO-8859-1 */
		    //output = new String(input.getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}
		return output;
	}
	
	private static String limparString(String entrada) {
		String saida = entrada.replace("'", "''");
		saida = saida.replace("\\", "\\\\");
		
		//String s2 = new String(saida.getBytes("ISO-8859-1"), "utf-8");
		
		//saida = traduzzz(saida);
		//saida = UTF8toISO(saida);
		saida = saida.trim();
		
		//System.out.println(saida);
		//byte[] utf8 = new String(saida, "ISO-8859-1").getBytes("UTF-8");
		
		return saida;
	}

	private static String traduzTipo(String tableName, String nome, String tipo, Integer escala, Integer precisao, Integer idNullable) {
		String retorno = "";

		if (tipo.equalsIgnoreCase("char")) {
			retorno = String.format("`%s` char(%d) COLLATE utf8_unicode_ci", nome, escala);
		} else if (tipo.equalsIgnoreCase("varchar")) {
			
			
			if (tableName.equalsIgnoreCase("clidet") && nome.equalsIgnoreCase("endcli")) {
				System.out.println("clidet.endcli");
				escala = 64;
			} else
			if (tableName.equalsIgnoreCase("clidet") && nome.equalsIgnoreCase("nombai")) {
				System.out.println("clidet.nombai");
				escala = 64;
			} else
			if ((escala > 4) && (escala <= 7)){
				System.out.println(String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala));
				escala = 8;
				System.out.println(String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala));
			} else
			if ((escala > 8) && (escala <= 15)){
				System.out.println(String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala));
				escala = 32;
				System.out.println(String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala));
			} else
			if ((escala > 16) && (escala <= 31)){
				System.out.println(String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala));
				escala = 32;
				System.out.println(String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala));
			} else
			if ((escala > 32) && (escala <= 63)){
				System.out.println(String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala));
				escala = 64;
				System.out.println(String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala));
			}
				
				
			retorno = String.format("`%s` varchar(%d) COLLATE utf8_unicode_ci", nome, escala);
			
		} else if (tipo.equalsIgnoreCase("numeric")) {
			retorno = String.format("`%s` decimal(%d,%d)", nome, escala, precisao);
		} else if (tipo.equalsIgnoreCase("integer")) {
			retorno = String.format("`%s` int(11)", nome);
		} else if (tipo.equalsIgnoreCase("smallint")) {
			retorno = String.format("`%s` smallint(6)", nome);
		} else {
			retorno = String.format("`%s` %s", nome, tipo);
		}

		if (idNullable == 0) {
			retorno += " NOT NULL,";
		} else {
			retorno += ",";
		}

		return retorno;

	}

	// System.out.printf("%d: %s (%d)\n", i++, columns.getString("COLUMN_NAME"),
	// columns.getInt("ORDINAL_POSITION"));

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Config config = new Config();
		config.setAplicarCommit(true);
		config.setGerarMetaData(false);
		config.setUmArquivoPorTabela(false);
		config.setTodasAsTabelas(true);
		
		//config.setPath("c:\\temp\\teste\\");
		config.setPath("c:\\temp\\");
		//config.fileNameMetadata = "metadata";
		//config.fileNameData = "data";
		

		
		try {
			printGeneralMetadata();
			// Print all the tables of the database scheme, with their names and
			// structure
			//Boolean aplicarCommit = true;
			//Boolean gerarMetaData = false;
			//Boolean umArquivoPorTabela = false;
			//Boolean todasAsTabelas = true;
			
			if (config.getGerarMetaData()) {
				buildMetadata(getTablesMetadata());
			}

			//

			if (config.getTodasAsTabelas() ) {
				moveData(getTablesMetadata(), config);
			} else {
				ArrayList<String> a = new ArrayList<String>();
				// a.add("anmclncmp");
				// a.add("anmcln");
				// a.add("anmclnres");
				// a.add("anmclnresstd");
				// a.add("aso");
				// a.add("asoaut");
				// a.add("asoexa");
				// a.add("asofim");
				// a.add("asonot");
				// a.add("asoocp");
				// a.add("asopat");
				// a.add("asoprf");
				// a.add("asorsc");
				// a.add("asorscepi");
				// a.add("aud");
				// a.add("audapa");
				// a.add("audcls");
				// a.add("audmed");
				// a.add("audnot");
				// a.add("audref");
				// a.add("audres");
				// a.add("ava");
				// a.add("avagrprsc");
				// a.add("avaind");
				// a.add("avalmt");
				// a.add("avalmtfxa");
				// a.add("avarsc");
				// a.add("cid");
				// a.add("cli");
				// a.add("clidet");
				// a.add("cln");
				// a.add("clnexa");
				// a.add("clnpst");
				// a.add("clnpstexa");
				// a.add("clntipcns");
				// a.add("cnvexa");
				// a.add("cpsquipdt");
				// a.add("cta");
				// a.add("ctapagrec");
				// a.add("ctr");
				// a.add("ctrite");
				// a.add("ctrpcl");
				// a.add("elmqui");
				// a.add("emp");
				// a.add("empcln");
				// a.add("empclnexa");
				// a.add("empclnexahis");
				// a.add("empclntipcns");
				// a.add("empclntipcnshis");
				// a.add("empctt");
				// a.add("empend");
				// a.add("empexavld");
				// a.add("empfcn");
				// a.add("empftrrsc");
				// a.add("empmed");
				// a.add("empnot");
				// a.add("empprf");
				// a.add("empprfexa");
				// a.add("empprfrsc");
				// a.add("empprfrscepi");
				// a.add("empsec");
				// a.add("empsecmdc");
				// a.add("emptipcns");
				// a.add("epc");
				// a.add("epi");
				// a.add("eqpmdc");
				// a.add("eqptrb");
				// a.add("etd");
				// a.add("etdaca");
				// a.add("exa");
				// a.add("exaaud");
				// a.add("exares");
				// a.add("exavld");
				// a.add("fam");
				// a.add("famcmp");
				// a.add("famres");
				// a.add("famresstd");
				// a.add("fat");
				// a.add("fataso");
				// a.add("fatexa");
				// a.add("fatosvpcl");
				// a.add("fatpcl");
				// a.add("fatppp");
				// a.add("fatstt");
				// a.add("fcn");
				// a.add("frmpgt");
				// a.add("frmutlagt");
				// a.add("grpelmqui");
				// a.add("grpelmquiite");
				// a.add("grpemp");
				// a.add("grprsc");
				// a.add("idtava");
				// a.add("itpgrarsc");
				// a.add("lmtcalcomrps");
				// a.add("lmtcalsemrps");
				// a.add("med");
				// a.add("mov");
				// a.add("nac");
				// a.add("nivrsc");
				// a.add("ocp");
				// a.add("ocpres");
				// a.add("orgexp");
				// a.add("osv");
				// a.add("osvite");
				// a.add("osvpcl");
				// a.add("pat");
				// a.add("patres");
				// a.add("pcl");
				// a.add("pdt");
				// a.add("pdtcml");
				// a.add("pdtcmlexp");
				// a.add("pdtqui");
				// a.add("pln");
				// a.add("plnaca");
				// a.add("plnacaite");
				// a.add("ppp");
				// a.add("prf");
				// a.add("primdd");
				// a.add("req_exaaso");
				// a.add("req_exaaud");
				// a.add("req_exacln");
				// a.add("req_exapst");
				// a.add("req_exareq");
				//a.add("req_exares");
				a.add("req_exarlz");
				a.add("req_exastt");
				a.add("resaso");
				a.add("rgmtrb");
				a.add("rsc");
				a.add("rscepi");
				a.add("sec");
				a.add("sisemp");
				a.add("sttatd");
				a.add("sttfat");
				a.add("svc");
				a.add("syspar");
				a.add("sys_act");
				a.add("sys_adm");
				a.add("sys_ite");
				a.add("sys_iteact");
				a.add("sys_mod");
				a.add("sys_ope");
				a.add("sys_opeite");
				a.add("sys_opeiteact");
				a.add("sys_opemov");
				a.add("tabatu");
				a.add("tabexa");
				a.add("tabexaclnexa");
				a.add("tabtipcns");
				a.add("tabtipcnsdet");
				a.add("tecseg");
				a.add("tipatv");
				a.add("tipatvtaxmet");
				a.add("tipcns");
				a.add("tipcrtava");
				a.add("tipend");
				a.add("tipeqpmdc");
				a.add("tipexp");
				a.add("tsffcn");
				a.add("vdd");		
				moveData(a, config);
			}

		} catch (SQLException e) {
			System.err.println("There was an error retrieving the metadata properties: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



/*
 * Statement stmt = connection.createStatement(); try { DatabaseMetaData
 * metaData = connection.getMetaData(); ResultSet tables = metaData.getTables(
 * null, null, "customer", new String[]{"TABLE"}); if (!tables.next())
 * stmt.execute("CREATE TABLE customer(" +
 * "customerId INTEGER NOT NULL PRIMARY KEY, " +
 * "firstName VARCHAR(20) NOT NULL, " + "lastName VARCHAR(40) NOT NULL)"); }
 * finally { stmt.close(); }
 */

/*
 * public void save(List<Entity> entities) throws SQLException { try (
 * Connection connection = database.getConnection(); PreparedStatement statement
 * = connection.prepareStatement(SQL_INSERT); ) { int i = 0;
 * 
 * for (Entity entity : entities) { statement.setString(1,
 * entity.getSomeProperty()); // ...
 * 
 * statement.addBatch(); i++;
 * 
 * if (i % 1000 == 0 || i == entities.size()) { statement.executeBatch(); //
 * Execute every 1000 items. } } } }
 * 
 */
