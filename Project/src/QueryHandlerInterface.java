import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Interface used for query handling
 * 
 * @author stewartpowell
 *
 */
public interface QueryHandlerInterface {
    /**
     * TODO Description here
     * @param path  the file containing the queries
     * @param exact flag for exact or partial search
     * @throws IOException if unable to open file
     */
    public default void handleQueries(Path path, boolean exact) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                handleQueries(line, exact);
            }
        }
    }

    /**
     * TODO Description here
     * @param query the line of queries to search for
     * @param exact flag for exact or partial search
     * @throws IOException if unable to
     */
    public void handleQueries(String query, boolean exact) throws IOException;

    /**
     * Outputs the search results to the given output file
     * 
     * @param output the path to output the results to
     */
    public void outputResults(Path output);

}
