import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Handles the Queries used for Search
 * 
 * @author stewartpowell
 *
 */
public class QueryHandler implements QueryHandlerInterface {
    /**
     * Class Member to reference the index
     */
    private final InvertedIndex index;

    /**
     * the List of all the search results that will be created
     */
    private final TreeMap<String, List<InvertedIndex.SearchResult>> allResults;

    /**
     * constructor for QueryHandler
     * 
     * @param index the inverted Index
     */
    public QueryHandler(InvertedIndex index) {
        this.index = index;
        this.allResults = new TreeMap<String, List<InvertedIndex.SearchResult>>();
    }

    /**
     * helper method to handleQueries
     * 
     * @param line  the line of queries
     * @param exact whether exact or partial search will be performed
     */
    @Override
    public void handleQueries(String line, boolean exact) {
        TreeSet<String> cleaned = TextFileStemmer.uniqueStems(line);
        String joined = String.join(" ", cleaned);
        if (cleaned.isEmpty() || allResults.containsKey(joined)) {
            return;
        }

        allResults.put(joined, this.index.search(cleaned, exact));

    }

    /**
     * outputs the results into pretty Json format to the given path
     * 
     * @param output the path to output the results to
     */
    @Override
    public void outputResults(Path output) {
        try {
            SimpleJsonWriter.writeSearchResultsToFile(allResults, output);
        } catch (IOException e) {
            System.out.println("Unable to write the results to the given output file: " + output);
        }
    }
}
