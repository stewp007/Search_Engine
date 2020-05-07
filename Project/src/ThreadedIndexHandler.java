import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Helper Class for CS 212 Projects
 * 
 * @author stewartpowell
 *
 */
public class ThreadedIndexHandler extends IndexHandler {

    /** The WorkQueue used for this class */
    private WorkQueue queue;
    /** The default stemmer algorithm used by this class. */
    public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

    /**
     * Constructor for FileHandler Class
     * 
     * @param index      the InvertedIndex associated with the FileHandler
     * @param numThreads the number of threads to use
     */
    public ThreadedIndexHandler(ThreadedInvertedIndex index, int numThreads) {
        super(index);
        this.queue = new WorkQueue(numThreads);
    }

    /**
     * Searches through files starting at the given Path
     * 
     * @param path       the path of the file to handle
     * @param numThreads the number of threads to use
     * @throws IOException throws if there is an issue opening the file
     */
    public void handleFiles(Path path, int numThreads) throws IOException {
        List<Path> listPaths = TextFileFinder.list(path);
        for (Path filePath : listPaths) {
            queue.execute(new IndexBuilder(filePath, this.index));
        }
        queue.finish();

        queue.shutdown();

    }

    /**
     * 
     * Adds the contents of a file to the Index
     * 
     * @param path  the path to collect into the Index
     * @param index the invertedIndex to be created
     * 
     * @return boolean
     * @throws IOException Throws if there is an issue opening the file
     */
    public static boolean handleIndex(Path path, InvertedIndex index) throws IOException {
        return IndexHandler.handleIndex(path, index);
    }

    /**
     * Adds the contents of a file to an InvertedIndex
     * 
     * @param path the path to collect into the Index
     * @return boolean
     * @throws IOException throws an IOException
     */
    @Override
    public boolean handleIndex(Path path) throws IOException {
        return handleIndex(path, this.index);
    }

    /**
     * Runnable Object used for building an index
     * 
     * @author stewartpowell
     *
     */
    private class IndexBuilder implements Runnable {
        /** The path used for building */
        private final Path path;

        /** The Thread safe InvertedIndex */
        private final InvertedIndex index;

        /**
         * Constructer for IndexBuilder
         * 
         * @param path  the path used for building
         * @param index the ThreadedInvertedIndex
         */
        public IndexBuilder(Path path, InvertedIndex index) {
            this.path = path;
            this.index = index;
        }

        @Override
        public void run() {
            InvertedIndex local = new InvertedIndex();
            try {
                handleIndex(path, local);
            } catch (IOException e) {
                System.out.println("Unable to Handle this Index");
            }
            synchronized (index) {
                index.addAll(local);
            }
        }

    }
}