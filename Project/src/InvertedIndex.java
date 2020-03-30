import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

// TODO Clean up all the commented out code 

/**
 * This is the InvertedIndex data structure used for USF CS212 Project 1
 *
 * @author stewartpowell
 */
public class InvertedIndex {

    /**
     * the data structure to be used
     */
    private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;
    /**
     * the counter for files
     */
    private final TreeMap<String, Integer> counter;

    /**
     * Instantiates the InvertedIndex object
     */
    public InvertedIndex() {
        this.invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
        this.counter = new TreeMap<String, Integer>();
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
    public boolean add(String word, String path, int position) {
        invertedIndex.putIfAbsent(word, new TreeMap<>());
        invertedIndex.get(word).putIfAbsent(path, new TreeSet<>());
        return invertedIndex.get(word).get(path).add(position);
    }

    /**
     * returns an unmodifiable collection of the keys of the Index
     * 
     * @return Collection<String>
     */
    public Collection<String> getWords() {
        return Collections.unmodifiableSet(invertedIndex.keySet());
    }

    /*
     * TODO This is still breaking encapsulation---no safe way to return nested data
     * structures. Remove.
     */
    /**
     * returns an Unmodifiable Map associated with the InvertedIndex
     * 
     * @param word the word to get value from
     * @return Map<String, TreeSet<Integer>> the map associated with the
     *         InvertedIndex
     */
    private Map<String, TreeSet<Integer>> getMap(String word) {
        return Collections.unmodifiableMap(invertedIndex.get(word));
    }

    /**
     * returns an Unmodifiable Set of the locations associated with a given word
     * associated with the InvertedIndex
     * 
     * @param word the word to get locations from
     * @return Set<String> the set of locations associated with the given word
     */
    public Set<String> getLocations(String word) {
        return Collections.unmodifiableSet(invertedIndex.get(word).keySet()) != null
                ? Collections.unmodifiableSet(invertedIndex.get(word).keySet())
                : Collections.emptySet();
    }

    /**
     * returns an Unmodifiable Map associated with the InvertedIndex
     * 
     * @param word     the word to get value from
     * @param location the location to find the positions from
     * @return Set<Integer> the set of positions associated with the given word and
     *         location
     */
    public Set<Integer> getPositions(String word, String location) {
        return Collections.unmodifiableSet(invertedIndex.get(word).get(location)) != null
                ? Collections.unmodifiableSet(invertedIndex.get(word).get(location))
                : Collections.emptySet();
    }

    /**
     * Checks if the inverted index contains the specified location
     * 
     * @param word     the word associated with the location
     * @param location the location we are looking for
     * @return boolean
     */
    public boolean containsLocation(String word, String location) {
        return invertedIndex.containsKey(word) ? invertedIndex.get(word).get(location) != null : false;
    }

    /**
     * Checks if the inverted index contains the specified position
     * 
     * @param word     the word associated with the location
     * @param location the location association the position
     * @param position the position we are checking for
     * @return boolean
     */
    public boolean containsPosition(String word, String location, Integer position) {
        return this.containsLocation(word, location) ? this.getMap(word).get(location).contains(position) : false;
    }

    /**
     * Return the number of words in the InvertedIndex
     * 
     * @return int
     */
    public int numWords() {
        return invertedIndex.size();
    }

    /**
     * Return the number of locations associated with the word
     * 
     * @param word the word to get the number of locations from
     * @return int
     */
    public int numLocations(String word) {
        return invertedIndex.containsKey(word) ? invertedIndex.get(word).size() : 0;
    }

    /**
     * Return the number of words in the InvertedIndex
     * 
     * @param word     the word associated with the location
     * @param location the location to get the number of positions from
     * @return int
     */
    public int numPositions(String word, String location) {
        return this.containsLocation(word, location) ? invertedIndex.get(word).get(location).size() : 0;
    }

    /**
     * Gets the pretty Json format of the Index
     * 
     * @param path the path to output the Json Index
     * @throws IOException throws IOException
     */
    public void getIndex(Path path) throws IOException {
        SimpleJsonWriter.indexToJsonFile(this.invertedIndex, path);
    }

    /**
     * Searches for exact matches in the Index
     * 
     * @param queries the queries to search for
     * @return A list of the Search Result Objects in sorted order
     */
    public List<SearchResult> exactSearch(List<String> queries) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        String where;
        int count = 0;
        double score = 0;
        var query = queries.iterator();
        while (query.hasNext()) {
            // int count = 0;
            String key = query.next();
            if (this.invertedIndex.containsKey(key)) {
                var entry = invertedIndex.get(key).keySet().iterator();
                while (entry.hasNext()) {
                    String location = entry.next();
                    where = location;
                    var positions = invertedIndex.get(key).get(location).iterator();
                    while (positions.hasNext()) {
                        count++;
                        positions.next();
                    }
                    score = count / counter.get(location);
                    results.add(new SearchResult(where, count, score));
                    count = 0;
                }

            }
        }
        Collections.sort(results);
        return results;
    }

    /**
     * Searches for partial matches in the Index
     * 
     * @param queries the queries to search for
     * @return the list of partial SearchResults
     */
    public List<SearchResult> partialSearch(List<String> queries) {
        return null;
    }

    @Override
    public String toString() {
        return invertedIndex.toString();
    }

    /**
     * @return the counter
     */
    public TreeMap<String, Integer> getCounter() {
        return counter;
    }

}
