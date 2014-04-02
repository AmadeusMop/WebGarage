

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SecureArea
 */
@WebServlet("/SecureArea")
public class SecureArea extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<Integer, String> sessionMap;
	private UserDBConnection connection;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
    public SecureArea() throws SQLException {
        super();
        connection = new UserDBConnection();
        sessionMap = connection.getSessionMap();
    }
    
	protected void doGet(HttpServletRequest requestParam, HttpServletResponse responseParam) throws ServletException, IOException {
		request = requestParam;
		response = responseParam;
		
		if(!sessionValid()) {
			response.sendRedirect("TestHandler");
		} else {
			response.setContentType("text/html");
			
			PageBuilder pb = new PageBuilder();
			
			pb.addTag("h2", String.format("User %s is currently logged in.", getUser()), true);
			pb.addTag("br", "", false);
			
			String form = "";
			form += pb.createTag("hidden", "", false, "name", "action", "value", "logOut");
			form += pb.createTag("submit", "", false, "action", "Log Out");
			form = "\n" + form;
			form = pb.createTag("form", form, true, "id", "logOutForm", "method", "get", "action", "SecureArea");
			form = pb.createTag("h1", "Log Out", true) + form;
			form += "\n";
			pb.addTag("div", form, true, "id", "logOutDiv");
			
			response.getWriter().println(pb.toString());
		}
	}

	protected void doPost(HttpServletRequest requestParam, HttpServletResponse responseParam) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private boolean sessionValid() {
		if(request == null) throw new IllegalStateException();
		
		int sessionID = getSessionID();
		if(sessionID == -1) return false;
		return sessionMap.containsKey(sessionID);
	}
	
	private String getUser() {
		if(request == null) throw new IllegalStateException();
		return sessionMap.get(getSessionID());
	}
	
	private int getSessionID() {
		if(request == null) throw new IllegalStateException();
		
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("sessionID")) {
				return Integer.parseInt(cookie.getValue());
			}
		}
		
		return -1;
	}
}
