import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Test servlet
 * 
 * @author stewartpowell
 *
 */
public class HomeServlet extends HttpServlet {

    /**
     * default serial version
     */
    private static final long serialVersionUID = 1L;

    /** The title to use for this webpage. */
    private static final String TITLE = "Stewart Powell";

    /**
     * Constructor for HomeServlet
     */
    public HomeServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check to make sure the browser is not requesting favicon.ico
        if (request.getRequestURI().endsWith("favicon.ico")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.printf("<html>%n");
        out.printf("<head><title>%s</title></head>%n", TITLE);
        out.printf(
                "<img src=\"/Project/src/Powell-Logo.png\" width=\"112\" height=\"100\" alt=\"Powell logo small\">%n");
        out.printf("<body>%n");
        out.printf("<br><br><h2>Home of Mediocre Performance and Bad Jokes</h1>%n");
        out.printf("<form action=\"/search\" method=\"GET\">%n");
        out.printf("<label for=\"build\">First Build an Index for Searching</label>");
        out.printf("<br><input type=\"text\" name=\"url\" id=\"url\" required/>");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"Build\"/>");
        out.printf("</form>%n");

        // Demonstrate that this servlet is called by different threads
        out.printf("<p>This request was handled by thread %s.</p>%n", Thread.currentThread().getName());

        out.printf("</body>%n");
        out.printf("</html>%n");

        response.setStatus(HttpServletResponse.SC_OK);
    }

}
