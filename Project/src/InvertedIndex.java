import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
        return invertedIndex.get(word).get(location).contains(position) & containsLocation(word, location) != false;
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
     * helper function for exact and partial search
     * 
     * @param query    the query word
     * @param location the location
     * @param results  the list of results
     * @param queries  the queries used
     * @return the newly created SearchResult
     */
    public SearchResult createNewResult(String query, String location, List<SearchResult> results) {
        SearchResult currResult = new SearchResult(location, 0, 0);
        int newCount = numPositions(query, location);
        currResult.setCount(newCount);
        double newScore = (double) newCount / counter.get(location);
        currResult.setScore(newScore);
        results.add(currResult);
        return currResult;
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
     * Searches for exact matches in the Index
     * 
     * @param queries the queries to search for
     * @return A list of the Search Result Objects in sorted order
     */
    public List<SearchResult> exactSearch(Collection<String> queries) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        TreeMap<String, SearchResult> lookup = new TreeMap<String, SearchResult>();
        synchronized (lookup) {
            SearchResult newResult;
            for (String query : queries) { // traverse through every query
                if (invertedIndex.containsKey(query)) {// check if key starts with the query
                    for (String location : invertedIndex.get(query).keySet()) {
                        if (lookup.containsKey(location)) {
                            lookup.get(location).update(query);
                        } else {
                            newResult = createNewResult(query, location, results);
                            lookup.put(location, newResult);
                        }
                    }
                }
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
        TreeMap<String, SearchResult> lookup = new TreeMap<String, SearchResult>();
        SearchResult newResult;
        for (String query : queries) { // traverse through every query
            for (String key : invertedIndex.keySet()) {
                if (key.startsWith(query)) {// check if key starts with the query
                    for (String location : invertedIndex.get(key).keySet()) {
                        if (lookup.containsKey(location)) {
                            lookup.get(location).update(key);
                        } else {
                            newResult = createNewResult(key, location, results);
                            lookup.put(location, newResult);
                        }
                    }
                }
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
    public TreeMap<String, Integer> getCounter() {
        return counter;
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
        private Integer count;
        /**
         * the total number of matches divide by the total number of words
         */
        private double score;

        /**
         * Constructor for the Search Results
         * 
         * @param where the location of one or more of the matches
         * @param count total matches within the text file
         * @param score the total matches divided by the total words
         */
        public SearchResult(String where, Integer count, double score) {
            this.where = where;
            this.count = count;
            this.score = score;
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
        public Integer getCount() {
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
         * sets a new count
         * 
         * @param newCount the new count number
         */
        public void setCount(int newCount) {
            this.count = newCount;
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

        /**
         * sets a new score
         * 
         * @param newScore the new score
         */
        public void setScore(double newScore) {
            this.score = newScore;
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
