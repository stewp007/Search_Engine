import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
    private final Map<String, Integer> counter;

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
        if (invertedIndex.get(word).get(path).add(position)) {
            int increment = counter.getOrDefault(path, 0);
            counter.put(path, increment + 1);
            return true;
        }
        return false;
    }

    /**
     * Adds the word, path, and position to the index
     * 
     * @param otherIndex the index we are adding
     */
    public void addAll(InvertedIndex otherIndex) {
        for (String key : otherIndex.invertedIndex.keySet()) {
            TreeMap<String, TreeSet<Integer>> existing = invertedIndex.putIfAbsent(key,
                    otherIndex.invertedIndex.get(key));
            if (existing != null) {
                // add to existing inner map
                for (String path : otherIndex.invertedIndex.get(key).keySet()) {
                    TreeSet<Integer> positions = invertedIndex.get(key).putIfAbsent(path,
                            otherIndex.invertedIndex.get(key).get(path));
                    if (positions != null) {
                        invertedIndex.get(key).get(path).addAll(otherIndex.invertedIndex.get(key).get(path));
                    }
                }
            }
        }

        for (String location : otherIndex.counter.keySet()) {
            int count = otherIndex.counter.getOrDefault(location, 0) + counter.getOrDefault(location, 0);
            counter.put(location, count);
        }
    }

    /**
     * returns an unmodifiable collection of the keys of the Index
     * 
     * @return Collection<String>
     */
    public Collection<String> getWords() {
        return Collections.unmodifiableSet(invertedIndex.keySet());
    }

    /**
     * returns an Unmodifiable Set of the locations associated with a given word
     * associated with the InvertedIndex
     * 
     * @param word the word to get locations from
     * @return Set<String> the set of locations associated with the given word
     */
    public Set<String> getLocations(String word) {
        return invertedIndex.get(word) != null ? Collections.unmodifiableSet(invertedIndex.get(word).keySet())
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
        return containsLocation(word, location) ? Collections.unmodifiableSet(invertedIndex.get(word).get(location))
                : Collections.emptySet();
    }

    /**
     * Checks if the inverted index contains the specified word
     * 
     * @param word the word to check if its in the index
     * @return boolean
     */
    public boolean containsWord(String word) {
        return invertedIndex.containsKey(word);
    }

    /**
     * Checks if the inverted index contains the specified location
     * 
     * @param word     the word associated with the location
     * @param location the location we are looking for
     * @return boolean
     */
    public boolean containsLocation(String word, String location) {
        return invertedIndex.containsKey(word) & invertedIndex.get(word).get(location) != null;
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
        return containsLocation(word, location) & invertedIndex.get(word).get(location).contains(position) != false;
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
        return invertedIndex.get(word) != null ? invertedIndex.get(word).size() : 0;
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
     * Searches the index with the given queries
     * 
     * @param queries the words to search for
     * @param exact   whether to do an exact or partial search
     * @return the list of search results generated from the search
     */
    public List<SearchResult> search(Collection<String> queries, boolean exact) {
        return exact ? exactSearch(queries) : partialSearch(queries);
    }

    /**
     * helper function for exact and partial search
     * 
     * @param key     the key in the inverted index
     * @param lookup  the lookup map
     * @param results the list of results
     */
    private void searchHelper(String key, Map<String, SearchResult> lookup, List<SearchResult> results) {
        for (String location : invertedIndex.get(key).keySet()) {
            if (!lookup.containsKey(location)) {
                SearchResult result = new SearchResult(location);
                lookup.put(location, result);
                results.add(result);
            }
            lookup.get(location).update(key);
        }
    }

    /**
     * Searches for exact matches in the Index
     * 
     * @param queries the queries to search for
     * @return A list of the Search Result Objects in sorted order
     */
    public List<SearchResult> exactSearch(Collection<String> queries) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        HashMap<String, SearchResult> lookup = new HashMap<String, SearchResult>();
        for (String query : queries) { // traverse through every query
            if (invertedIndex.containsKey(query)) {// check if key starts with the query
                searchHelper(query, lookup, results);
            }
        }

        results.sort(null);
        return results;
    }

    /**
     * Searches for partial matches in the Index
     * 
     * @param queries the queries to search for
     * @return the list of partial SearchResults
     */
    public List<SearchResult> partialSearch(Collection<String> queries) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        HashMap<String, SearchResult> lookup = new HashMap<String, SearchResult>();
        for (String query : queries) { // traverse through every query
            for (String key : invertedIndex.tailMap(query).keySet()) {
                if (!key.startsWith(query)) {
                    break;
                }
                searchHelper(key, lookup, results);
            }
        }
        results.sort(null);
        return results;
    }

    @Override
    public String toString() {
        return invertedIndex.toString();
    }

    /**
     * returns the counter
     * 
     * @return the counter
     */
    public Map<String, Integer> getCounter() {
        return Collections.unmodifiableMap(counter);
    }

    /**
     * Inner SearchResult class
     * 
     * @author stewartpowell
     *
     */
    public class SearchResult implements Comparable<SearchResult> {
        /**
         * the location of one or more of the matches
         */
        private final String where;
        /**
         * the total number of matches within the text file
         */
        private int count;
        /**
         * the total number of matches divide by the total number of words
         */
        private double score;

        /**
         * Constructor for the Search Results
         * 
         * @param where the location of one or more of the matches
         */
        public SearchResult(String where) {
            this.where = where;
            this.count = 0;
            this.score = 0;
        }

        /**
         * gets where
         * 
         * @return the location of one or more of the matches
         */
        public String getWhere() {
            return where;
        }

        /**
         * gets the count of the search result
         * 
         * @return the count
         */
        public int getCount() {
            return count;
        }

        /**
         * gets the score of the search result
         * 
         * @return the score
         */
        public double getScore() {
            return score;
        }

        /**
         * updates the SearchResult of the given query
         * 
         * @param word the query word
         */
        private void update(String word) {
            count += invertedIndex.get(word).get(where).size();
            score = (double) count / counter.get(where);
        }

        @Override
        public int compareTo(SearchResult other) {
            if (Double.compare(this.score, other.score) == 0) {
                if (Integer.compare(this.count, other.count) == 0) {
                    return this.where.compareToIgnoreCase(other.where);
                } else {
                    return Integer.compare(other.count, this.count);
                }
            } else {
                return Double.compare(other.score, this.score);
            }
        }

        @Override
        public String toString() {
            return "\nwhere: " + this.where + "\ncount: " + this.count + "\nscore: " + this.score + "\n";
        }

    }

}
