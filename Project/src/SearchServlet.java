import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
public class SearchServlet extends HttpServlet {

    /** Class version for serialization, in [YEAR][TERM] format (unused). */
    private static final long serialVersionUID = 202020;

    /** The title to use for this webpage. */
    private static final String TITLE = "Messages";

    /** The logger to use for this servlet. */
    private static Logger log = Log.getRootLogger();

    /** The thread-safe data structure to use for storing messages. */
    private final ThreadedInvertedIndex index;

    private final WebCrawler webCrawler;

    /** Template for HTML. **/
    private final String htmlTemplate;

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
    public SearchServlet(ThreadedInvertedIndex index, WorkQueue queue, int limit) throws IOException {
        super();
        this.index = index;
        webCrawler = new WebCrawler(index, queue, limit);
        htmlTemplate = Files.readString(Path.of("src", "index.html"), StandardCharsets.UTF_8);
    }

    /*
     * @Override protected void doGet(HttpServletRequest request,
     * HttpServletResponse response) throws ServletException, IOException {
     * response.setContentType("text/html");
     * 
     * log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");
     * 
     * // used to substitute values in our templates Map<String, String> values =
     * new HashMap<>(); values.put("title", TITLE); values.put("thread",
     * Thread.currentThread().getName());
     * 
     * // setup form values.put("method", "POST"); values.put("action",
     * request.getServletPath());
     * 
     * // generate html from template StringSubstitutor replacer = new
     * StringSubstitutor(values); String html = replacer.replace(htmlTemplate);
     * 
     * // output generated html PrintWriter out = response.getWriter();
     * out.println(html); out.flush();
     * 
     * response.setStatus(HttpServletResponse.SC_OK); }
     */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        log.info("SearchServlet ID " + this.hashCode() + " handling POST request.");

        String url = request.getParameter("url");
        System.out.println(url);

        url = url == null ? "https://www.cs.usfca.edu/~cs212/birds/" : url;

        // avoid xss attacks using apache commons text
        // comment out if you don't have this library installed
        url = StringEscapeUtils.escapeHtml4(url);

        webCrawler.crawlWeb(url);

        PrintWriter out = response.getWriter();
        out.printf("<html>%n");
        out.printf("<head><title>%s</title></head>%n", TITLE);
        out.printf("<img src=\"/Project/src/Powell-Logo.png\" width=\"112\" height=\"100\" alt=\"Powell Logo\">%n");
        out.printf("<body>%n");
        out.printf("<h2>Built an index from URL: %s</h2>%n", url);
        out.printf("<ul>%n");
        for (String location : index.getCounter().keySet()) {
            out.printf("<li>%s</li>", location);
        }
        out.printf("<ul>%n");

        // Demonstrate that this servlet is called by different threads
        out.printf("<p>This request was handled by thread %s.</p>%n", Thread.currentThread().getName());

        out.printf("</body>%n");
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