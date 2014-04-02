import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class UserDBConnection {
	private Connection con = null;
	private final String serverName = "fourwaylo.com";
	private final String portNumber = "8889";
	private final String databaseName = "csproj";
	private final String userName = "csproj";
	private final String password = "DoYourHomework";
	
	private static final String SELECT_TEMPLATE = "SELECT * FROM %s;";
	private static final String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s);";
	private static final String REMOVE_TEMPLATE = "DELETE FROM %s WHERE ID=%d;";
	private static final String UPDATE_TEMPLATE = "UPDATE %s SET %s WHERE %s;";
	
	private static final String USERS_TABLE = "cillian.Users";
	private static final String USERS_PARAMS = "Username, Password";
	
	public UserDBConnection() {}
	
	public Map<String, String> getUserMap() throws SQLException {
		Map<String, String> userMap = new HashMap<String, String>();
		getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(String.format(SELECT_TEMPLATE, USERS_TABLE));
		while(rs.next()) {
			userMap.put(rs.getString(1).trim(), rs.getString(2).trim());
		}
		rs.close();
		stmt.close();
		closeConnection();
		return userMap;
	}
	
	public Map<Integer, String> getSessionMap() throws SQLException {
		Map<Integer, String> sessionMap = new HashMap<Integer, String>();
		getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(String.format(SELECT_TEMPLATE, USERS_TABLE));
		while(rs.next()) {
			sessionMap.put(rs.getInt(3), rs.getString(1).trim());
		}
		rs.close();
		stmt.close();
		closeConnection();
		return sessionMap;
	}
	
	public void addNewUser(String un, String pw) throws SQLException {
		String query = String.format(INSERT_TEMPLATE, USERS_TABLE, USERS_PARAMS, String.format("'%s', '%s'", un, pw));
		getConnection();
		Statement stmt = con.createStatement();
		stmt.execute(query);
		stmt.close();
		closeConnection();
	}
	
	public int createNewSession(String un) throws SQLException {
		int sessionID = new Random().nextInt(9);
		String query = String.format(UPDATE_TEMPLATE, USERS_TABLE, String.format("SessionID=%d", sessionID), String.format("Username='%s'", un));
		getConnection();
		Statement stmt = con.createStatement();
		stmt.execute(query);
		stmt.close();
		closeConnection();
		return sessionID;
	}
	
	private void getConnection() {
		SQLServerDataSource ds = new SQLServerDataSource();
	    ds.setUser(userName);
	    ds.setPassword(password);
	    ds.setServerName(serverName);
	    ds.setPortNumber(Integer.parseInt(portNumber));
	    ds.setDatabaseName(databaseName);
		Connection localCon = null;
		try {
			localCon = ds.getConnection();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error Trace in getConnection(): " + e.getMessage());
		}
		this.con = localCon;
	}
	
	public static String repeatString(String s, int n) {
		return new String(new char[n]).replace("\0", s);
	}
	
	private static void printResults(Statement statement) throws SQLException {
		ResultSet rs;
		do {
			rs = statement.getResultSet();
			if(rs == null) return;
			printTable(rs);
			rs.close();
			System.out.println();
			System.out.println();
		} while(statement.getMoreResults());
		rs.close();
		rs = null;
	}
	
	public static void printTable(ResultSet rs) throws SQLException {
		if(rs == null) return;
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		Object[] o = new Object[columns];
		for(int i = 1; i <= columns; i++) {
			System.out.print(md.getColumnName(i) + "\t");
		}
		while(rs.next()) {
			System.out.println();
			for(int i = 1; i <= columns; i++) {
				System.out.print(rs.getString(i) + "\t");
			}
		}
		md = null;
	}
	
	private void closeConnection() {
		try {
			if(con != null) {
				con.close();
				con = null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
