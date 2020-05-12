import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private static final String TITLE = "Home";

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
        out.printf("<head>");
        out.printf("<title>%s</title>", TITLE);
        out.printf("<link href=\"/photo/searchEngine.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.printf("</head>%n");
        out.printf("<header>%n");
        out.printf("<img src=\"/photo/Powell-Logo.png\" width=\"112\" height=\"100\" alt=\"Powell Logo\">%n");
        out.printf("<h2>  Home of Mediocre Performance</h2>%n<h3>  and Bad Jokes</h3>%n");
        out.printf("</header>%n");
        out.printf("<body>%n");
        out.printf("<div>");
        out.printf("<form action=\"/index\" method=\"GET\">");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"View Index\"/>");
        out.printf("</form>");
        out.printf("</div>");
        out.printf("<div>");
        out.printf("<form action=\"/counter\" method=\"GET\">");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"View Locations\"/>");
        out.printf("</form>%n");
        out.printf("</div>%n");
        out.printf("<div>");
        out.printf("<form action=\"/shutdown\" method=\"GET\">");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"Shutdown Server\"/>");
        out.printf("</form>%n");
        out.printf("</div>%n");
        out.printf("<br><br><form action=\"/search\" method=\"GET\">%n");
        out.printf("<label for=\"search\">What are you looking for?</label>");
        out.printf("<br><input type=\"text\" name=\"queries\" id=\"queries\" required/>");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"queries\" value=\"Search\"/>");
        out.printf("<input type=\"checkbox\" name=\"exact\" id=\"exact\" value=\"Exact\"/>");
        out.printf("<label for=\"Exact\">Exact</label>");
        out.printf("</form>%n");
        out.printf("<form action=\"/build\" method=\"GET\">%n");
        out.printf("<label for=\"build\">Add a new Crawl</label>");
        out.printf("<br><input type=\"text\" name=\"url\" id=\"url\"/>");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"url\" value=\"Build\"/>");
        out.printf("</form>%n");

        out.printf("</body>%n");
        out.printf("<footer>");
        // Demonstrate that this servlet is called by different threads
        out.printf("<p>This request was handled by thread %s on %s</p>%n", Thread.currentThread().getName(), getDate());
        out.printf("</footer>");
        out.printf("</html>%n");

        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Returns the date and time in a long format. For example: "12:00 am on
     * Saturday, January 01 2000".
     *
     * @return current date and time
     */
    private static String getDate() {
        String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(new Date());
    }
}
