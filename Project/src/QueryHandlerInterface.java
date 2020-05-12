import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface used for query handling
 * 
 * @author stewartpowell
 *
 */
public interface QueryHandlerInterface {
    /**
     * Default method to read from the given path and then perform a search
     * 
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
     * helper function to {@link #handleQueries(Path path, boolean exact)}
     * 
     * @param query the line of queries to search for
     * @param exact flag for exact or partial search
     */
    public void handleQueries(String query, boolean exact);

    /**
     * Returns the results from the given query
     * 
     * @param query the query to get results form
     * @return an unmodifiable list of the results
     */
    public List<InvertedIndex.SearchResult> getResults(String query);

    /**
     * Outputs the search results to the given output file
     * 
     * @param output the path to output the results to
     * @throws IOException if unable to open output file
     */
    public void outputResults(Path output) throws IOException;

}
