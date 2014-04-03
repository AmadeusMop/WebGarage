

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
			response.setContentType("text/plain");
			
			PageBuilder pb = new PageBuilder();
			
			pb.getBody().addTags(
				pb.createTag("h2").setContent(String.format("User %s is currently logged in.", getUser())),
				pb.createTag("br", false),
				pb.createTag("div").setAttributes("id", "logOutDiv").setSubTags(
					pb.createTag("form").setAttributes("id", "logOutForm", "method", "get", "action", "SecureArea").setSubTags(
							pb.createTag("input").setAttributes("type", "hidden", "name", "action", "value", "logOut"),
							pb.createTag("input").setAttributes("type", "submit", "action", "Log Out"),
							pb.createTag("h1").setContent("Log Out"),
							pb.createTag("br", false)
					)
				)
			);
			
			response.getWriter().println(pb.toString());
		}
		
		request = null;
		response = null;
	}

	protected void doPost(HttpServletRequest requestParam, HttpServletResponse responseParam) throws ServletException, IOException {
		request = requestParam;
		response = responseParam;
		
		response.sendRedirect("TestHandler");
		
		request = null;
		response = null;
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
		if(cookies == null) return -1;
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("sessionID")) {
				return Integer.parseInt(cookie.getValue());
			}
		}
		
		return -1;
	}
}
