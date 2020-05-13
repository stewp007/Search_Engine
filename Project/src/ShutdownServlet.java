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
     * The server that will be shutdown
     */
    private final Server server;

    /**
     * Constructor for HomeServlet
     * 
     * @param server the server to shutdown
     * @param secret secret code to signal shutdown
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
        new Thread(() -> {
            try {
                server.stop();
            } catch (Exception e) {
                System.out.println("Failed to stop server.");
            }
        }).start();

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.doPost(request, response);

    }
}
