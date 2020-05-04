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
    private final SimpleReadWriteLock lock;

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
        lock.writeLock().lock();
        try {
            return super.add(word, path, position);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addAll(InvertedIndex otherIndex) {
        lock.writeLock().lock();
        try {
            super.addAll(otherIndex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * returns an unmodifiable collection of the keys of the Index
     * 
     * @return Collection<String>
     */
    @Override
    public Collection<String> getWords() {
        lock.readLock().lock();
        try {
            return super.getWords();
        } finally {
            lock.readLock().unlock();
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
        lock.readLock().lock();
        try {
            return super.getLocations(word);
        } finally {
            lock.readLock().unlock();
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
        lock.readLock().lock();
        try {
            return super.getPositions(word, location);
        } finally {
            lock.readLock().unlock();
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
        lock.readLock().lock();
        try {
            return super.containsWord(word);
        } finally {
            lock.readLock().unlock();
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
        lock.readLock().lock();
        try {
            return super.containsLocation(word, location);
        } finally {
            lock.readLock().unlock();
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
        lock.readLock().lock();
        try {
            return super.containsPosition(word, location, position);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Return the number of words in the InvertedIndex
     * 
     * @return int
     */
    @Override
    public int numWords() {
        lock.readLock().lock();
        try {
            return super.numWords();
        } finally {
            lock.readLock().unlock();
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
        lock.readLock().lock();
        try {
            return super.numLocations(word);
        } finally {
            lock.readLock().unlock();
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
        lock.readLock().lock();
        try {
            return super.numPositions(word, location);
        } finally {
            lock.readLock().unlock();
        }
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
        lock.readLock().lock();
        try {
            return super.getCounter();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<SearchResult> exactSearch(Collection<String> queries) {
        lock.readLock().lock();
        try {
            return super.exactSearch(queries);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<SearchResult> partialSearch(Collection<String> queries) {
        lock.readLock().lock();
        try {
            return super.partialSearch(queries);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return super.toString();
        } finally {
            lock.readLock().unlock();
        }
    }
}
