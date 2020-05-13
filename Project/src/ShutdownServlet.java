import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;

/**
 * Test servlet
 * 
 * @author stewartpowell
 *
 */
public class ShutdownServlet extends HttpServlet {

    /**
     * default serial version
     */
    private static final long serialVersionUID = 1L;
    /**
     * title for webpage
     */
    private static final String TITLE = "Shutdown Server";

    /**
     * The server that will be shutdown
     */
    private final Server server;

    /**
     * Constructor for HomeServlet
     * 
     * @param server the server to shutdown
     */
    public ShutdownServlet(Server server) {
        super();
        this.server = server;

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK); // 200
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
        out.printf("<h2>Server Shutdown</h2>");
        out.printf("</body>%n");
        out.printf("<footer>");
        // Demonstrate that this servlet is called by different threads
        out.printf("<p>This request was handled by thread %s on %s</p>%n", Thread.currentThread().getName(), getDate());
        out.printf("</footer>");

        out.printf("</html>%n");
        new Thread(() -> {
            try {
                server.stop();
            } catch (Exception e) {
                System.out.println("Failed to stop server.");
            }
        }).start();

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
