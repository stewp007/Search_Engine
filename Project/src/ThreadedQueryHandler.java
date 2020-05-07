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
    private WorkQueue queue; // TODO final

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
     * @throws IOException throws an IOException
     */
    @Override
    public void handleQueries(String line, boolean exact) throws IOException {
        queue.execute(new IndexSearcher(line, exact));
    }

    /**
     * outputs the results into pretty Json format to the given path
     * 
     * @param output the path to output the results to
     */
    @Override
    public void outputResults(Path output) { // TODO Change this in interface to throw the exception to Driver instead
        try {
            // TODO Need to protect this read of shared data!
            SimpleJsonWriter.writeSearchResultsToFile(allResults, output);
        } catch (IOException e) {
            System.out.println("Unable to write the results to the given output file: " + output);
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
                allResults.put(joined, results);
            }

        }

    }

}
