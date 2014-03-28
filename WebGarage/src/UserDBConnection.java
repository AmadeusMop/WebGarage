import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class UserDBConnection {
	private Connection con = null;
	private final String url = "jdbc:sqlserver://";
	private final String serverName = "fourwaylo.com";
	private final String portNumber = "8889";
	private final String databaseName = "csproj";
	private final String userName = "csproj";
	private final String password = "DoYourHomework";
	
	private final String selectMethod = "cursor";
	
	private static final String SELECT_TEMPLATE = "SELECT * FROM %s;";
	private static final String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s);";
	private static final String REMOVE_TEMPLATE = "REMOVE FROM %s WHERE ID=%d;";
	private static final String USERS_TABLE = "cillian.Users";
	private static final String USERS_PARAMS = "Username, Password";
	
	public UserDBConnection() {}
	
	private String getConnectionURL() {
		return String.format("%s%s:%s;databaseName=%s;selectMethod=%s;", url, serverName, portNumber, databaseName, selectMethod);
	}
	
	public Map<String, String> getUserMap() throws SQLException {
		Map<String, String> userMap = new HashMap<String, String>();
		con = getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(String.format(SELECT_TEMPLATE, USERS_TABLE));
		while(rs.next()) {
			userMap.put(rs.getString(1), rs.getString(2));
		}
		rs.close();
		stmt.close();
		con.close();
		return userMap;
	}
	
	public void addNewUser(String un, String pw) throws SQLException {
		String query = String.format(INSERT_TEMPLATE, USERS_TABLE, String.format("%s, %s", un, pw), USERS_PARAMS);
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		stmt.execute(query);
		stmt.close();
		con.close();
	}
	
	private Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerConnection");
			con = DriverManager.getConnection(getConnectionURL(), userName, password);
			if(con != null) System.out.println("Connection Successful!");
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error Trace in getConnection(): " + e.getMessage());
		}
		return con;
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
