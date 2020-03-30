import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
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
        // Counter Map
        TreeMap<String, Integer> counter = new TreeMap<String, Integer>();
        // Initialize FileHandler
        FileHandler handler = new FileHandler(index, counter);

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
            // get counts
        }

        if (parser.hasFlag("-query")) {
            Path query = parser.getPath("-query");
            if (query != null) {
                // query file is provided
                if (parser.hasFlag("-exact")) {
                    // perform exact search
                } else {
                    // perform partial search
                }
                if (parser.hasFlag("-results")) {
                    Path results = parser.getPath("-results", Path.of("results.json"));
                    // store results
                }
            } // else do nothing
        }

        if (parser.hasFlag("-index")) {
            Path output = parser.getPath("-index", Path.of("index.json"));
            // TODO Move the try/catch and call to index.getIndex here
            // TODO Handler should only be for building
            handler.getIndexJson(index, output);
            return;
        }
        // calculate time elapsed and output
        Duration elapsed = Duration.between(start, Instant.now());
        double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
        System.out.printf("Elapsed: %f seconds%n", seconds);
    }
}
