import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This is the Thread Safe InvertedIndex data structure used for USF CS212
 * Project 3
 *
 * @author stewartpowell
 */
public class ThreadedInvertedIndex extends InvertedIndex {

    /**
     * the data structure to be used
     */
    @SuppressWarnings("unused")
    private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

    /**
     * the counter for files
     */
    private final TreeMap<String, Integer> counter;

    /** The lock used to protect concurrent access to the underlying set. */
    private SimpleReadWriteLock lock;

    /**
     * Instantiates the Thread safe InvertedIndex object
     */
    public ThreadedInvertedIndex() {
        this.invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
        this.counter = new TreeMap<String, Integer>();
        this.lock = new SimpleReadWriteLock();
    }

    /**
     * Adds the word, path, and position to the index
     * 
     * @param word     the word to be added
     * @param path     the path being added
     * @param position the position in the file the word was found in
     * 
     * @return boolean if the Index was changed
     */
    @Override
    public boolean add(String word, String path, int position) {
        synchronized (lock) {
            return super.add(word, path, position);
        }

    }

    /**
     * returns an unmodifiable collection of the keys of the Index
     * 
     * @return Collection<String>
     */
    @Override
    public Collection<String> getWords() {
        synchronized (lock) {
            return super.getWords();
        }

    }

    /**
     * returns an Unmodifiable Set of the locations associated with a given word
     * associated with the InvertedIndex
     * 
     * @param word the word to get locations from
     * @return Set<String> the set of locations associated with the given word
     */
    @Override
    public Set<String> getLocations(String word) {
        synchronized (lock) {
            return super.getLocations(word);
        }
    }

    /**
     * returns an Unmodifiable Map associated with the InvertedIndex
     * 
     * @param word     the word to get value from
     * @param location the location to find the positions from
     * @return Set<Integer> the set of positions associated with the given word and
     *         location
     */
    @Override
    public Set<Integer> getPositions(String word, String location) {
        synchronized (lock) {
            return super.getPositions(word, location);
        }
    }

    /**
     * Checks if the inverted index contains the specified word
     * 
     * @param word the word to check if its in the index
     * @return boolean
     */
    @Override
    public boolean containsWord(String word) {
        synchronized (lock) {
            return super.containsWord(word);
        }
    }

    /**
     * Checks if the inverted index contains the specified location
     * 
     * @param word     the word associated with the location
     * @param location the location we are looking for
     * @return boolean
     */
    @Override
    public boolean containsLocation(String word, String location) {
        synchronized (lock) {
            return super.containsLocation(word, location);
        }
    }

    /**
     * Checks if the inverted index contains the specified position
     * 
     * @param word     the word associated with the location
     * @param location the location association the position
     * @param position the position we are checking for
     * @return boolean
     */
    @Override
    public boolean containsPosition(String word, String location, Integer position) {
        synchronized (lock) {
            return super.containsPosition(word, location, position);
        }
    }

    /**
     * Return the number of words in the InvertedIndex
     * 
     * @return int
     */
    @Override
    public int numWords() {
        synchronized (lock) {
            return super.numWords();
        }
    }

    /**
     * Return the number of locations associated with the word
     * 
     * @param word the word to get the number of locations from
     * @return int
     */
    @Override
    public int numLocations(String word) {
        synchronized (lock) {
            return super.numLocations(word);
        }
    }

    /**
     * Return the number of words in the InvertedIndex
     * 
     * @param word     the word associated with the location
     * @param location the location to get the number of positions from
     * @return int
     */
    @Override
    public int numPositions(String word, String location) {
        synchronized (lock) {
            return super.numPositions(word, location);
        }
    }

    /**
     * Gets the pretty Json format of the Index
     * 
     * @param path the path to output the Json Index
     * @throws IOException throws IOException
     */
    public void getIndex(Path path) throws IOException {
        synchronized (lock) {
            super.getIndex(path);
        }
    }

    /**
     * returns the counter
     * 
     * @return the counter
     */
    public TreeMap<String, Integer> getCounter() {
        synchronized (lock) {
            return counter;
        }
    }

    @Override
    public List<SearchResult> search(Collection<String> queries, boolean exact) {
        synchronized (lock) {
            return super.search(queries, exact);
        }
    }

    @Override
    public List<SearchResult> exactSearch(Collection<String> queries) {
        synchronized (lock) {
            return super.exactSearch(queries);
        }
    }

    @Override
    public List<SearchResult> partialSearch(Collection<String> queries) {
        synchronized (lock) {
            return super.partialSearch(queries);
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return super.toString();
        }
    }
}
