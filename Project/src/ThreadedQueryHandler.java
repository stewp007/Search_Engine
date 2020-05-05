import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

/*
 * TODO Create a QueryHandlerInterface that both Threaded and QueryHandler
 * implement.
 * 
 * Include all the common methods into the interface, and have a default
 * implementation of the handleQueries(Path, boolean).
 * 
 * Add your own index and treemap to this class.
 */

/**
 * Handles the Queries used for Search
 * 
 * @author stewartpowell
 *
 */
public class ThreadedQueryHandler extends QueryHandler {

    /**
     * the workqueue used to delegate tasks to each thread
     */
    private WorkQueue queue;

    /**
     * constructor for QueryHandler
     * 
     * @param index      the inverted Index
     * @param numThreads the number of threads to use
     */
    public ThreadedQueryHandler(ThreadedInvertedIndex index, int numThreads) {
        super(index);
        this.queue = new WorkQueue(numThreads); // TODO pass in the work queue from driver
    }

    /**
     * Cleans and parses queries from the given Path
     * 
     * @param path       the path of the Query file
     * @param exact      flag for partial or exact search
     * @param numThreads the number of threads used for searching
     * @throws IOException throws an IOException
     */
    public void handleQueries(Path path, boolean exact, int numThreads) throws IOException {
      /*
       * TODO QueryHandlerInterface.super.handleQueries(...)
       * queue.finish();
       */
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
            queue.shutdown(); // TODO This will be called in Driver instead
        }
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
      /*
       * TODO Move the implementation into the run method. This method should
       * just add a task to the work queue.
       */
        TreeSet<String> cleaned = TextFileStemmer.uniqueStems(line);
        String joined = String.join(" ", cleaned);
        synchronized (allResults) {
            if (cleaned.isEmpty() || allResults.containsKey(joined)) {
                return;
            }
            allResults.put(joined, this.index.search(cleaned, exact));
        }
        
        /* TODO 
        synchronized (allResults) {
          if (cleaned.isEmpty() || allResults.containsKey(joined)) {
              return;
          }
        }
        
        var blah = this.index.search(cleaned, exact);
        
        synchronized (allResults) {
          allResults.put(joined, blah);
        }
        */
    }

    /**
     * outputs the results into pretty Json format to the given path
     * 
     * @param output the path to output the results to
     */
    public void outputResults(Path output) {
        super.outputResults(output);
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
