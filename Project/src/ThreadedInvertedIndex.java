import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the Thread Safe InvertedIndex data structure used for USF CS212
 * Project 3
 *
 * @author stewartpowell
 */
public class ThreadedInvertedIndex extends InvertedIndex {
    /** The lock used to protect concurrent access to the underlying set. */
    private SimpleReadWriteLock lock; // TODO final

    /**
     * Instantiates the Thread safe InvertedIndex object
     */
    public ThreadedInvertedIndex() {
        super();
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
        boolean result = false;
        lock.writeLock().lock();
        try {
          // TODO return super.add(...)
          // TODO Fix this in the other methods too
            result = super.add(word, path, position);
        } finally {
            lock.writeLock().unlock();
        }
        return result;
    }

    /**
     * returns an unmodifiable collection of the keys of the Index
     * 
     * @return Collection<String>
     */
    @Override
    public Collection<String> getWords() {
        Collection<String> words;
        lock.readLock().lock();
        try {
            words = super.getWords();
        } finally {
            lock.readLock().unlock();
        }
        return words;
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
        Set<String> locations;
        lock.readLock().lock();
        try {
            locations = super.getLocations(word);
        } finally {
            lock.readLock().unlock();
        }
        return locations;
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
        Set<Integer> positions;
        lock.readLock().lock();
        try {
            positions = super.getPositions(word, location);
        } finally {
            lock.readLock().unlock();
        }
        return positions;
    }

    /**
     * Checks if the inverted index contains the specified word
     * 
     * @param word the word to check if its in the index
     * @return boolean
     */
    @Override
    public boolean containsWord(String word) {
        boolean result = false;
        lock.readLock().lock();
        try {
            result = super.containsWord(word);
        } finally {
            lock.readLock().unlock();
        }
        return result;
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
        boolean result = false;
        lock.readLock().lock();
        try {
            result = super.containsLocation(word, location);
        } finally {
            lock.readLock().unlock();
        }
        return result;
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
        boolean result = false;
        lock.readLock().lock();
        try {
            result = super.containsPosition(word, location, position);
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    /**
     * Return the number of words in the InvertedIndex
     * 
     * @return int
     */
    @Override
    public int numWords() {
        int numWords;
        lock.readLock().lock();
        try {
            numWords = super.numWords();
        } finally {
            lock.readLock().unlock();
        }
        return numWords;
    }

    /**
     * Return the number of locations associated with the word
     * 
     * @param word the word to get the number of locations from
     * @return int
     */
    @Override
    public int numLocations(String word) {
        int numLocations;
        lock.readLock().lock();
        try {
            numLocations = super.numLocations(word);
        } finally {
            lock.readLock().unlock();
        }
        return numLocations;
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
        int numPositions;
        lock.readLock().lock();
        try {
            numPositions = super.numPositions(word, location);
        } finally {
            lock.readLock().unlock();
        }
        return numPositions;
    }

    /**
     * Gets the pretty Json format of the Index
     * 
     * @param path the path to output the Json Index
     * @throws IOException throws IOException
     */
    @Override
    public void getIndex(Path path) throws IOException {
        lock.readLock().lock();
        try {
            super.getIndex(path);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * returns the counter
     * 
     * @return the counter
     */
    @Override
    public Map<String, Integer> getCounter() {
        Map<String, Integer> counter;
        lock.readLock().lock();
        try {
            counter = super.getCounter();
        } finally {
            lock.readLock().unlock();
        }
        return counter;
    }

    // TODO Remove
    @Override
    public List<SearchResult> search(Collection<String> queries, boolean exact) {
        List<SearchResult> results;
        lock.readLock().lock();
        try {
            results = super.search(queries, exact);
        } finally {
            lock.readLock().unlock();
        }
        return results;
    }

    @Override
    public List<SearchResult> exactSearch(Collection<String> queries) {
        List<SearchResult> results;
        lock.readLock().lock();
        try {
            results = super.exactSearch(queries);
        } finally {
            lock.readLock().unlock();
        }
        return results;
    }

    @Override
    public List<SearchResult> partialSearch(Collection<String> queries) {
        List<SearchResult> results;
        lock.readLock().lock();
        try {
            results = super.partialSearch(queries);
        } finally {
            lock.readLock().unlock();
        }
        return results;
    }

    @Override
    public String toString() {
        String toString;
        lock.readLock().lock();
        try {
            toString = super.toString();
        } finally {
            lock.readLock().unlock();
        }
        return toString;
    }
}
