import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Helper Class for CS 212 Projects
 * 
 * @author stewartpowell
 *
 */
public class IndexHandler {

    /** Class Member to reference the index */
    private final ThreadedInvertedIndex index;
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
    public IndexHandler(ThreadedInvertedIndex index, int numThreads) {
        this.index = index;
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

        try {
            queue.finish();
        } catch (InterruptedException e) {
            System.out.println("Interupted Thread while Handling Files.");
            Thread.currentThread().interrupt();
        }
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
        int filePosition = 0;
        int linePosition = 0;
        SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String location = path.toString();
            String line;
            String stemmedWord;
            while ((line = reader.readLine()) != null) {
                String[] allStems = TextParser.parse(line);
                linePosition = 0;
                for (String word : allStems) {
                    linePosition++;
                    stemmedWord = stemmer.stem(word).toString();
                    index.add(stemmedWord, location, filePosition + linePosition);
                }
                filePosition += allStems.length;
            }
            if (filePosition > 0) {
                index.getCounter().putIfAbsent(location, filePosition);
            }
        }
        return true;
    }

    /**
     * Adds the contents of a file to an InvertedIndex
     * 
     * @param path       the path to collect into the Index
     * @param numThreads the number of threads to use
     * @return boolean
     * @throws IOException throws an IOException
     */
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
        private final ThreadedInvertedIndex index;

        /**
         * Constructer for IndexBuilder
         * 
         * @param path  the path used for building
         * @param index the ThreadedInvertedIndex
         */
        public IndexBuilder(Path path, ThreadedInvertedIndex index) {
            this.path = path;
            this.index = index;
        }

        @Override
        public void run() {
            synchronized (queue) {
                try {
                    handleIndex(path, this.index);
                } catch (IOException e) {
                    System.out.println("IOException occured in IndexBuilder @" + path);
                }
            }

        }

    }
}