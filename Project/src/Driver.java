import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

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
     */
    public static void main(String[] args) {
        // store initial start time
        Instant start = Instant.now();

        // Check if enough arguments are provided
        if (args.length < 1) {
            System.out.println("Please provide necessary arguments.");
            return;
        }

        ArgumentParser parser = new ArgumentParser(args);
        WorkQueue queue = null;
        InvertedIndex index;
        IndexHandler indexHandler;
        QueryHandlerInterface queryHandler;

        if (parser.hasFlag("-threads")) {
            int numThreads;
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
            queryHandler.outputResults(parser.getPath("-results", Path.of("results.json")));
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
