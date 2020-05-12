import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Handles the Queries used for Search
 * 
 * @author stewartpowell
 *
 */
public class ThreadedQueryHandler implements QueryHandlerInterface {

    /**
     * the thread safe inverted index
     */
    private final ThreadedInvertedIndex index;
    /**
     * the map of all the search results and search queries
     */
    private final TreeMap<String, List<InvertedIndex.SearchResult>> allResults;
    /**
     * the work queue used to delegate tasks to each thread
     */
    private final WorkQueue queue;

    /**
     * constructor for QueryHandler
     * 
     * @param index the inverted Index
     * @param queue the workqueue used to delegate tasks
     */
    public ThreadedQueryHandler(ThreadedInvertedIndex index, WorkQueue queue) {
        this.index = index;
        this.allResults = new TreeMap<>();
        this.queue = queue;
    }

    /**
     * Reads a file of queries and adds them to a work queue
     * 
     * @param path  the path of the Query file
     * @param exact flag for partial or exact search
     * @throws IOException throws an IOException
     */
    @Override
    public void handleQueries(Path path, boolean exact) throws IOException {
        QueryHandlerInterface.super.handleQueries(path, exact);
        queue.finish();
    }

    /**
     * helper method to handleQueries
     * 
     * @param line  the line of queries
     * @param exact whether exact or partial search will be performed
     */
    @Override
    public void handleQueries(String line, boolean exact) {
        queue.execute(new IndexSearcher(line, exact));
    }

    /**
     * outputs the results into pretty Json format to the given path
     * 
     * @param output the path to output the results to
     */
    @Override
    public void outputResults(Path output) throws IOException {
        synchronized (allResults) {
            this.index.getIndex(output);
            // SimpleJsonWriter.writeSearchResultsToFile(allResults, output);
        }
    }

    /**
     * Returns the results from the given query
     * 
     * @param query the query to get results form
     * @return an unmodifiable list of the results
     */
    @Override
    public List<InvertedIndex.SearchResult> getResults(String query) {
        TreeSet<String> stemmed = TextFileStemmer.uniqueStems(query);
        if (stemmed.isEmpty()) {
            return Collections.emptyList();
        }
        query = String.join(" ", stemmed);
        synchronized (allResults) {
            if (allResults.containsKey(query)) {
                return Collections.unmodifiableList(this.allResults.get(query));
            }
            return Collections.emptyList();

        }

    }

    /**
     * Runnable Object used for building an index
     * 
     * @author stewartpowell
     *
     */
    private class IndexSearcher implements Runnable {

        /** The line of queries */
        private final String line;

        /** The flag for exact/partial Search */
        private final boolean exact;

        /**
         * Constructor for IndexBuilder
         * 
         * @param line  the line of queries
         * @param exact whether exact or partial search will be performed
         */
        public IndexSearcher(String line, boolean exact) {
            this.line = line;
            this.exact = exact;
        }

        @Override
        public void run() {
            TreeSet<String> cleaned = TextFileStemmer.uniqueStems(line);
            String joined = String.join(" ", cleaned);
            synchronized (allResults) {
                if (cleaned.isEmpty() || allResults.containsKey(joined)) {
                    return;
                }
            }

            List<InvertedIndex.SearchResult> results = index.search(cleaned, exact);

            synchronized (allResults) {
                allResults.putIfAbsent(joined, results);
            }

        }

    }

}
