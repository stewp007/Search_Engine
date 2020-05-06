import java.io.IOException;
import java.nio.file.Path;

/**
 * 
 */

/**
 * @author stewartpowell
 *
 */
public interface QueryHandlerInterface {
    /**
     * 
     * @param path  the file containing the queries
     * @param exact flag for exact or partial search
     */
    public default void handlerQueries(Path path, boolean exact) {

    }

    /**
     * @param line  the line of queries to search for
     * @param exact flag for exact or partial search
     * @throws IOException if unable to
     */
    public void handleQueries(String line, boolean exact) throws IOException;

    /**
     * @param output the path to output the results to
     */
    public void outputResults(Path output);

}
