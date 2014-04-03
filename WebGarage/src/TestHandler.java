

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

/**
 * Servlet implementation class TestHandler
 */
@WebServlet("/TestHandler")
public class TestHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String USERNAME_PARAM = "un";
	private static final String PASSWORD_PARAM = "pw";
	private Map<String, String> passwordMap;
	private Map<Integer, String> sessionMap = new HashMap<Integer, String>();
	UserDBConnection connection;
       
    /**
     * @throws SQLException 
     * @see HttpServlet#HttpServlet()
     */
    public TestHandler() throws SQLException {
        super();
        connection = new UserDBConnection();
        passwordMap = connection.getUserMap();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.sendRedirect("testForm.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Set a cookie for the user, so that the counter does not increate
	    // every time the user press refresh
	    HttpSession session = request.getSession(true);
	    // Set the session valid for 5 secs
	    session.setMaxInactiveInterval(5);
	    response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
	    int sessionID = getSessionID(request.getCookies());
	    
	    String requestType = request.getParameter("action");
	    if(sessionMap.containsKey(sessionID)) {
			out.printf("Error: User '%s' is currently logged in.", sessionMap.get(sessionID));
	    } else if(requestType.equals("createUser")) {
	    	try {
				tryCreateUser(request, response, out);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } else if(requestType.equals("logIn")) {
	    	try {
				tryLogIn(request, response, out);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } else {
	    	out.printf("Error: Request type '%s' not valid.", requestType);
	    }
	}
	
	private void tryCreateUser(HttpServletRequest request, HttpServletResponse response, PrintWriter output) throws SQLException, IOException {
		String un = request.getParameter(USERNAME_PARAM);
		if(!un.equals("")) {
			if(!un.contains(" ")) {
				if(!passwordMap.containsKey(un)) {
					String pw = hash(request.getParameter(PASSWORD_PARAM));
					if(!pw.equals("")) {
						connection.addNewUser(un, pw);
						passwordMap = connection.getUserMap();
						output.printf("User %s successfully created!\n", un);
						tryLogIn(request, response, output);
					} else {
						output.println("Error: No password entered.");
					}
				}  else {
					output.printf("Error: User '%s' already exists.\n", un);
				}
			} else {
				output.printf("Error: User name may not contain spaces.");
			}
		} else {
			output.println("Error: No username entered.");
		}
	}
	
	private void tryLogIn(HttpServletRequest request, HttpServletResponse response, PrintWriter output) throws SQLException, IOException {
		String un = request.getParameter(USERNAME_PARAM);
		if(!un.equals("")) {
			if(passwordMap.containsKey(un)) {
				String pw = hash(request.getParameter(PASSWORD_PARAM));
				if(!pw.equals("")) {
					if(passwordMap.get(un).equals(pw)) {
						int sessionID = connection.createNewSession(un);
						response.addCookie(getNewSessionCookie(sessionID));
						response.sendRedirect("SecureArea");
					} else {
						output.printf("Error: Invalid password for user %s.\n", un);
					}
				} else {
					output.print("Error: No password entered.\n");
				}
			}  else {
				output.printf("Error: User '%s' does not yet exist.\n", un);
			}
		} else {
			output.println("Error: No username entered.");
		}
	}
	
	private int getSessionID(Cookie[] cookies) {
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("sessionID")) {
					return Integer.parseInt(cookie.getValue());
				}
			}
		}
		return 0;
	}
	
	private Cookie getNewSessionCookie(int sessionID) {
		Cookie cookie = new Cookie("sessionID", Integer.toString(sessionID));
		cookie.setMaxAge(60*60);
		return cookie;
	}
	
	private String hash(String s) {
		return s; //Eh, I'll do this eventually.
	}
}
