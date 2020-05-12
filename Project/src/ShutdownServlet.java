import java.io.IOException;

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
     * 
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
        // Check to make sure the browser is not requesting favicon.ico
        if (request.getRequestURI().endsWith("favicon.ico")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            server.stop();
        } catch (Exception e) {
            System.out.println("Server unable to shutdown.");
        }

    }
}
