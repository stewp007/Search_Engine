import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

// TODO Fix warning setup in Eclipse

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

        /*
         * TODO ArgumentParser parser = new ArgumentParser(args); WorkQueue queue =
         * null; InvertedIndex index; IndexHandler handler; QueryHandlerInterface query;
         * 
         * if (-threads) { ThreadedInvertedIndex threadSafe = new ... index =
         * threadSafe;
         * 
         * queue = ...
         * 
         * handler = ThreadedIndexHandler(threadSafe, queue); query = ... } else { index
         * = new InvertedIndex(); }
         * 
         * 
         * all the same if/else blocks from project 2
         * 
         * if queue != null, shutdown
         */

        // parse arguments into a Map
        ArgumentParser parser = new ArgumentParser(args);

        int numThreads;
        if (parser.hasFlag("-threads")) {
            try {
                numThreads = Integer.parseInt(parser.getString("-threads", "5"));
                if (numThreads <= 0) {
                    numThreads = 5;
                }
            } catch (NumberFormatException e) {
                numThreads = 5;
            }
        } else {
            numThreads = 1;
        }

        ThreadedInvertedIndex threadedIndex = null;
        ThreadedIndexHandler threadedIndexHandler = null;
        ThreadedQueryHandler threadedQueryHandler = null;

        InvertedIndex singleIndex = null;
        IndexHandler singleIndexHandler = null;
        QueryHandler singleQueryHandler = null;

        if (numThreads > 1) {
            threadedIndex = new ThreadedInvertedIndex();
            // Initialize ThreadedIndexHandler
            threadedIndexHandler = new ThreadedIndexHandler(threadedIndex, numThreads);
            // Initialize ThreadedQueryHandler
            threadedQueryHandler = new ThreadedQueryHandler(threadedIndex, numThreads);
        } else {
            singleIndex = new InvertedIndex();
            singleIndexHandler = new IndexHandler(singleIndex);
            singleQueryHandler = new QueryHandler(singleIndex);
        }

        if (parser.hasFlag("-path")) {
            Path path = parser.getPath("-path");
            if (path != null) {
                try {
                    if (numThreads > 1) {
                        threadedIndexHandler.handleFiles(path, numThreads);
                    } else {
                        singleIndexHandler.handleFiles(path);
                    }

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
                if (numThreads > 1) {
                    SimpleJsonWriter.asObject(threadedIndex.getCounter(), counts);
                } else {
                    SimpleJsonWriter.asObject(singleIndex.getCounter(), counts);
                }
            } catch (IOException e) {
                System.out.println("Unable to create a Json of the counter.");
            }

        }

        if (parser.hasFlag("-query")) {
            Path query = parser.getPath("-query");
            if (query != null) {
                try {
                    if (numThreads > 1) {
                        threadedQueryHandler.handleQueries(query, parser.hasFlag("-exact"));
                    } else {
                        singleQueryHandler.handleQueries(query, parser.hasFlag("-exact"));
                    }

                } catch (IOException e) {
                    System.out.println("Unable to Search those Queries.");
                }
            }
        }

        if (parser.hasFlag("-results")) {
            if (numThreads > 1) {
                threadedQueryHandler.outputResults(parser.getPath("-results", Path.of("results.json")));
            } else {
                singleQueryHandler.outputResults(parser.getPath("-results", Path.of("results.json")));
            }
        }

        if (parser.hasFlag("-index")) {
            Path output = parser.getPath("-index", Path.of("index.json"));
            try {
                if (numThreads > 1) {
                    threadedIndex.getIndex(output);
                } else {
                    singleIndex.getIndex(output);
                }
            } catch (IOException e) {
                System.out.println("Error retrieving Json form of the Index.");
            }
            return;
        }
        // calculate time elapsed and output
        Duration elapsed = Duration.between(start, Instant.now());
        double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
        System.out.printf("Elapsed: %f seconds%n", seconds);
    }
}
