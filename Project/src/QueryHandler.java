import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
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
public class QueryHandler {

    /**
     * Class Member to reference the index
     */

    private final ThreadedInvertedIndex index;

    /**
     * the List of all the search results that will be created
     */
    private final TreeMap<String, List<InvertedIndex.SearchResult>> allResults;

    /**
     * the workqueue
     */
    private WorkQueue queue;

    /**
     * constructor for QueryHandler
     * 
     * @param index      the inverted Index
     * @param numThreads the number of threads to use
     */
    public QueryHandler(ThreadedInvertedIndex index, int numThreads) {
        this.index = index;
        this.queue = new WorkQueue(numThreads);
        this.allResults = new TreeMap<String, List<InvertedIndex.SearchResult>>();
    }

    /**
     * Cleans and parses queries from the given Path
     * 
     * @param path       the path of the Query file
     * @param exact      flag for partial or exact search
     * @param numThreads the number of threads used for searching
     * @param output     the file to output the Json results
     * @param result     whether to ouptut the search to a file or not
     * @throws IOException throws an IOException
     */
    public void handleQueries(Path path, boolean exact, int numThreads) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                queue.execute(new IndexSearcher(line, exact));
            }
            try {
                queue.finish();
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted in QueryHandler");
                Thread.currentThread().interrupt();
            }
            queue.shutdown();
        }
    }

    /**
     * helper method to handleQueries
     * 
     * @param line  the line of queries
     * @param exact whether exact or partial search will be performed
     * @throws IOException throws an IOException
     */
    public void handleQueries(String line, boolean exact) throws IOException {
        TreeSet<String> cleaned = TextFileStemmer.uniqueStems(line);
        String joined = String.join(" ", cleaned);
        if (!cleaned.isEmpty() && !allResults.containsKey(joined)) {
            allResults.put(joined, index.search(cleaned, exact));
        }
    }

    /**
     * outputs the results into pretty Json format to the given path
     * 
     * @param output the path to output the results to
     */
    public void outputResults(Path output) {
        try {
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
         * Constructer for IndexBuilder
         * 
         * @param line       the line of queries
         * @param index      the Thread Safe InvertedIndex used to search
         * @param allResults the Map of queries and search results
         * @param exact      whether exact or partial search will be performed
         */
        public IndexSearcher(String line, boolean exact) {
            this.line = line;
            this.exact = exact;
        }

        @Override
        public void run() {
            synchronized (queue) {
                try {
                    handleQueries(line, exact);
                } catch (IOException e) {
                    System.out.println("Warning: IOexception while searching those queries");
                }
            }

        }

    }

}
