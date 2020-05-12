import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class BuildServlet extends HttpServlet {

    /** Class version for serialization, in [YEAR][TERM] format (unused). */
    private static final long serialVersionUID = 202020;

    /** The title to use for this webpage. */
    private static final String TITLE = "New Crawl";

    /** The logger to use for this servlet. */
    private static Logger log = Log.getRootLogger();

    /** The thread-safe data structure to use for storing messages. */
    private final ThreadedInvertedIndex index;

    /**
     * The webcrawler ued to build the index
     */
    private final WebCrawler webCrawler;

    /**
     * Initializes this message board. Each message board has its own collection of
     * messages.
     * 
     * @param index the indexHandler to build the InvertedIndex
     * @param queue the work queue used with the ThreadedIndexHandler
     * @param limit the liit of urls
     * 
     * @throws IOException if unable to read template
     */
    public BuildServlet(ThreadedInvertedIndex index, WorkQueue queue, int limit) throws IOException {
        super();
        this.index = index;
        webCrawler = new WebCrawler(index, queue, limit);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        log.info("SearchServlet ID " + this.hashCode() + " handling POST request.");

        String url = request.getParameter("url");
        url = url == null ? "https://www.cs.usfca.edu/~cs212/birds/" : url;

        // avoid xss attacks using apache commons text
        // comment out if you don't have this library installed
        url = StringEscapeUtils.escapeHtml4(url);
        webCrawler.crawlWeb(url);
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
        out.printf("<h1>Added to index from URL: %s</h1>%n", url);
        out.printf("<form action=\"/home\" method=\"GET\">%n");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"New Search\"/>");
        out.printf("</form>");

        out.printf("<ul>%n");
        for (String location : index.getCounter().keySet()) {
            out.printf("<li>%s</li>", location);
        }
        out.printf("</ul>%n");

        out.printf("</body>%n");
        out.printf("<footer>");
        // Demonstrate that this servlet is called by different threads
        out.printf("<p>This request was handled by thread %s on %s</p>%n", Thread.currentThread().getName(), getDate());
        out.printf("</footer>");
        out.printf("</html>%n");

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        log.info("SearchServlet ID " + this.hashCode() + " handling POST request.");

        String url = request.getParameter("url");
        url = url == null ? "https://www.cs.usfca.edu/~cs212/birds/" : url;

        // avoid xss attacks using apache commons text
        // comment out if you don't have this library installed
        url = StringEscapeUtils.escapeHtml4(url);

        webCrawler.crawlWeb(url);

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
        out.printf("<form action=\"/search\" method=\"GET\">%n");
        out.printf("<label for=\"build\">What would you like to search?</label>");
        out.printf("<br><input type=\"text\" name=\"queries\" id=\"queries\" required/>");
        out.printf("<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"Search\"/>");
        out.printf("</form>%n");
        out.printf("<ul>%n");
        for (String location : index.getCounter().keySet()) {
            out.printf("<li>%s</li>", location);
        }
        out.printf("<ul>%n");

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