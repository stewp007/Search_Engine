import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// https://commons.apache.org/proper/commons-lang/download_lang.cgi

/**
 * The servlet class responsible for setting up a simple message board.
 *
 */
public class SearchServlet extends HttpServlet {

    /** Class version for serialization, in [YEAR][TERM] format (unused). */
    private static final long serialVersionUID = 202020;

    /** The title to use for this webpage. */
    private static final String TITLE = "Stewart Powell";

    /** The logger to use for this servlet. */
    private static Logger log = Log.getRootLogger();

    /** The thread-safe Query Handler to do the searching. */
    private final ThreadedQueryHandler handler;

    /**
     * Initializes this message board. Each message board has its own collection of
     * messages.
     * 
     * @param handler query handler to handle searching
     * 
     * @throws IOException if unable to read template
     */
    public SearchServlet(ThreadedQueryHandler handler) throws IOException {
        super();

        this.handler = handler;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        log.info("SearchServlet ID " + this.hashCode() + " handling POST request.");

        String queries = request.getParameter("queries");
        // avoid xss attacks using apache commons text
        // comment out if you don't have this library installed
        queries = StringEscapeUtils.escapeHtml4(queries);
        handler.handleQueries(queries, false);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PrintWriter out = response.getWriter();
        out.printf("<html>%n");
        out.printf("<head>");
        out.printf("<title>%s</title>", TITLE);
        out.printf("<link href=\"/photo/searchEngine.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.printf("</head>%n");
        out.printf("<header>%n");
        out.printf("<img src=\"/photo/Powell-Logo.png\" width=\"112\" height=\"100\" alt=\"Powell Logo\">");
        out.printf("<h2>  Home of Mediocre Performance</h2>%n          <h3>  and Bad Jokes</h3>%n");
        out.printf("</header>%n");
        out.printf("<body>%n");
        out.printf("<form action=\"/home\" method=\"GET\">%n");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"New Search\"/>");
        out.printf("</form>");
        out.printf("<h2>Searched For: %s</h2>%n", queries);
        out.printf("<h3>Found: </h3%n");
        out.printf("<ul>%n");

        if (handler.getResults(queries).isEmpty()) {
            out.printf("<p> No Results found</p>");
        } else {

            for (InvertedIndex.SearchResult query : handler.getResults(queries)) {
                System.out.println(query);
                out.printf("<li><a href=%s>%s</a></li>", query.getWhere(), query.getWhere());
            }
        }

        out.printf("<ul>%n");

        out.printf("</body>%n");
        out.printf("<footer>");
        // Demonstrate that this servlet is called by different threads
        out.printf("<p>This request was handled by thread %s on %s</p>%n", Thread.currentThread().getName(), getDate());
        out.printf("</footer>");

        out.printf("</html>%n");

        response.setStatus(HttpServletResponse.SC_OK);
        // response.sendRedirect(request.getServletPath());
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