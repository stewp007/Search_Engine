import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class Driver {
    /**
     * Initializes the classes necessary based on the provided command-line
     * arguments. This includes (but is not limited to) how to build or search an
     * inverted index.
     *
     * @param args flag/value pairs used to start this program
     * @throws Exception thrown by server
     */
    public static void main(String[] args) throws Exception {
        // store initial start time
        Instant start = Instant.now();

        // Check if enough arguments are provided
        if (args.length < 1) {
            System.out.println("Please provide necessary arguments.");
            return;
        }

        ArgumentParser parser = new ArgumentParser(args);
        int limit = 0;
        if (parser.hasFlag("-limit")) {
            try {
                limit = Integer.parseInt(parser.getString("-limit", "50"));
                if (limit <= 0) {
                    limit = 50;
                }
            } catch (NumberFormatException e) {
                limit = 50;
            }
        }

        WorkQueue queue = null;
        InvertedIndex index;
        IndexHandler indexHandler = null;
        QueryHandlerInterface queryHandler;
        WebCrawler crawler = null;
        String seed = null;
        int numThreads = 0;

        if (parser.hasFlag("-threads") || parser.hasFlag("-url") || parser.hasFlag("-port")) {
            try {
                numThreads = Integer.parseInt(parser.getString("-threads", "5"));
                if (numThreads <= 0) {
                    numThreads = 5;
                }
            } catch (NumberFormatException e) {
                numThreads = 5;
            }
            ThreadedInvertedIndex threadSafe = new ThreadedInvertedIndex();
            index = threadSafe;
            queue = new WorkQueue(numThreads);
            indexHandler = new ThreadedIndexHandler(threadSafe, queue);
            queryHandler = new ThreadedQueryHandler(threadSafe, queue);
            if (parser.hasFlag("-url")) {
                if (parser.hasFlag("-limit")) {
                    try {
                        limit = Integer.parseInt(parser.getString("-limit", "50"));
                        if (limit <= 0) {
                            limit = 50;
                        }
                    } catch (NumberFormatException e) {
                        limit = 50;
                    }
                }
                seed = parser.getString("-url");
                crawler = new WebCrawler(threadSafe, queue, limit);
                crawler.crawlWeb(seed);

            }
            if (parser.hasFlag("-port")) {
                int port;
                try {
                    port = Integer.parseInt(parser.getString("-port", "8080"));
                    if (port <= 0) {
                        port = 8080;
                    }
                } catch (NumberFormatException e) {
                    port = 8080;
                }
                Server server = new Server();

                ServerConnector connector = new ServerConnector(server);
                connector.setHost("localhost");
                connector.setPort(port);
                // add static resource holders to web server
                // this indicates where web files are accessible on the file system
                ResourceHandler resourceHandler = new ResourceHandler();
                resourceHandler.setResourceBase("photo");
                resourceHandler.setDirectoriesListed(true);

                // only serve static resources in the "/photo" context directory
                // this indicates where web files are accessible via the web server
                ContextHandler resourceContext = new ContextHandler("/photo");
                resourceContext.setHandler(resourceHandler);

                // all other requests should be handled by the gallery servlet
                ServletContextHandler servletContext = new ServletContextHandler();
                servletContext.setContextPath("/");
                servletContext.addServlet(new ServletHolder(new HomeServlet()), "/home");
                servletContext.addServlet(new ServletHolder(new BuildServlet(threadSafe, queue, 50)), "/build");
                servletContext.addServlet(new ServletHolder(new SearchServlet((ThreadedQueryHandler) queryHandler)),
                        "/search");
                servletContext.addServlet(new ServletHolder(new IndexServlet(threadSafe)), "/index");
                servletContext.addServlet(new ServletHolder(new CounterServlet(threadSafe)), "/counter");
                servletContext.addServlet(new ServletHolder(new ShutdownServlet(server)), "/shutdown");

                /*
                 * ServletHandler handler = new ServletHandler();
                 * handler.addServletWithMapping(new ServletHolder(new HomeServlet()),
                 * "/build"); try { handler.addServletWithMapping(new ServletHolder(new
                 * SearchServlet(threadSafe, queue, 50)), "/search"); } catch (IOException e) {
                 * System.out.println("Error with Server."); }
                 */
                // setup handlers (and handler order)
                HandlerList handlers = new HandlerList();
                handlers.addHandler(resourceContext);
                handlers.addHandler(servletContext);
                server.addConnector(connector);
                server.setHandler(handlers);

                server.start();
                server.join();
            }
        } else {
            index = new InvertedIndex();
            indexHandler = new IndexHandler(index);
            queryHandler = new QueryHandler(index);
        }

        if (parser.hasFlag("-path")) {
            Path path = parser.getPath("-path");
            if (path != null) {
                try {
                    indexHandler.handleFiles(path);
                } catch (IOException e) {
                    System.out.println("Error handling file: " + path);
                }
            } else {
                System.out.println("Error: Forgot a value for -path");
            }
        }

        if (parser.hasFlag("-counts")) {
            Path counts = parser.getPath("-counts", Path.of("counts.json"));
            try {
                SimpleJsonWriter.asObject(index.getCounter(), counts);
            } catch (IOException e) {
                System.out.println("Unable to create a Json of the counter.");
            }

        }

        if (parser.hasFlag("-query")) {
            Path query = parser.getPath("-query");
            if (query != null) {

                try {
                    queryHandler.handleQueries(query, parser.hasFlag("-exact"));
                } catch (IOException e) {
                    System.out.println("Unable to Search those Queries.");
                }

            }
        }

        if (parser.hasFlag("-results")) {
            try {
                queryHandler.outputResults(parser.getPath("-results", Path.of("results.json")));
            } catch (IOException e) {
                System.out.println("Unable to output results to the given file.");
            }
        }

        if (parser.hasFlag("-index")) {
            Path output = parser.getPath("-index", Path.of("index.json"));
            try {
                index.getIndex(output);
            } catch (IOException e) {
                System.out.println("Error retrieving Json form of the Index.");
            }
            return;
        }

        if (queue != null) {
            queue.shutdown();
        }

        // calculate time elapsed and output
        Duration elapsed = Duration.between(start, Instant.now());
        double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
        System.out.printf("Elapsed: %f seconds%n", seconds);
    }
}
