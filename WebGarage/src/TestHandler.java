

import java.io.IOException;
import java.io.PrintWriter;
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
	private Map<String, String> passwordMap = new HashMap<String, String>();
	private Map<Integer, String> sessionMap = new HashMap<Integer, String>();
	int session = 0;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    int sessionID = getSessionID(request.getCookies());
	    String requestType = request.getParameter("action");
	    if(requestType == null) {
	    	
	    } else if(requestType.equals("logOut")) {
	    	tryLogOut(sessionID);
	    }
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
	    if(requestType.equals("createUser")) {
	    	tryCreateUser(request, response, out);
	    } else if(sessionMap.containsKey(sessionID)) {
			out.printf("User '%s' currently logged in", sessionMap.get(sessionID));
	    } else if(requestType.equals("logIn")) {
	    	tryLogIn(request, response, out);
	    } else {
	    	out.printf("Error: Request type '%s' not valid", requestType);
	    }
	}
	
	private void tryCreateUser(HttpServletRequest request, HttpServletResponse response, PrintWriter output) {
		String un = request.getParameter(USERNAME_PARAM);
		if(!un.equals("")) {
			if(!passwordMap.containsKey(un)) {
				String pw = request.getParameter(PASSWORD_PARAM);
				if(!pw.equals("")) {
					passwordMap.put(un, pw);
					output.printf("User %s successfully created!\n", un);
					tryLogIn(un, pw, response, output);
				} else {
					output.println("Error: No password entered.");
				}
			}  else {
				output.printf("Error: User '%s' already exists.\n", un);
			}
		} else {
			output.println("Error: No username entered.");
		}
	}
	
	private void tryLogIn(HttpServletRequest request, HttpServletResponse response, PrintWriter output) {
		String un = request.getParameter(USERNAME_PARAM);
		if(!un.equals("")) {
			if(passwordMap.containsKey(un)) {
				String pw = request.getParameter(PASSWORD_PARAM);
				if(!pw.equals("")) {
					if(passwordMap.get(un).equals(pw)) {
						int sessionID = getNewSessionID(un);
						response.addCookie(getNewSessionCookie(sessionID));
						output.printf("User %s successfully logged in!\n", un);
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
	
	private void tryLogIn(String un, String pw, HttpServletResponse response, PrintWriter output) {
		if(passwordMap.containsKey(un)) {
			if(passwordMap.get(un).equals(pw)) {
			} else {
				output.printf("Error: Invalid password for user %s.", un);
			}
		} else {
			output.printf("Error: User '%s' does not yet exist.", un);
		}
	}
	
	private void tryLogOut(int id) {
		sessionMap.remove(id);
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
	
	private boolean sessionValid() {
		return false;
	}
	
	private int getNewSessionID(String un) {
		session++;
		sessionMap.put(session, un);
		return session;
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
