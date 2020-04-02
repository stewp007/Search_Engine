import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

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
     * static variable flag used for exact or partial searches
     */
    public static boolean exact = false;

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
        // parse arguments into a Map
        ArgumentParser parser = new ArgumentParser(args);
        // Initialize the InvertedIndex
        InvertedIndex index = new InvertedIndex();
        // Initialize FileHandler
        FileHandler handler = new FileHandler(index);

        if (parser.hasFlag("-path")) {
            Path path = parser.getPath("-path");
            if (path != null) {
                try {
                    handler.handleFiles(path);
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
            boolean result = false;
            if (query != null) {
                // query file is provided
                if (parser.hasFlag("-exact")) {
                    exact = true;
                }
                try {
                    if (parser.hasFlag("-results")) {
                        result = true;
                    }
                    Path results = parser.getPath("-results", Path.of("results.json"));
                    handler.handleQueries(query, exact, results, result);
                } catch (IOException e) {
                    System.out.println("Unable to perform the search for the given Query file: " + query);
                }
            } // else do nothing
        } else if (parser.hasFlag("-results")) {
            Path empty = Path.of("results.json");
            TreeMap<String, List<SearchResult>> emptyResults = new TreeMap<String, List<SearchResult>>();
            try {
                SimpleJsonWriter.writeSearchResultsToFile(emptyResults, empty);
            } catch (IOException e) {
                System.out.println("Unable to write default empty results.");
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
        // calculate time elapsed and output
        Duration elapsed = Duration.between(start, Instant.now());
        double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
        System.out.printf("Elapsed: %f seconds%n", seconds);
    }
}
