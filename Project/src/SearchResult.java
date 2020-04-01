/**
 * Generates the Search results
 */

/**
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
     * the query or queries used to get the result
     */
    private String queries;

    /**
     * Constructor for the Search Results
     * 
     * @param queries the queries used
     * 
     * @param where   the location of one or more of the matches
     * @param count   total matches within the text file
     * @param score   the total matches divided by the total words
     */
    public SearchResult(String queries, String where, Integer count, double score) {
        this.queries = queries;
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
     * gets the queries
     * 
     * @return the queries used
     */
    public String getQueries() {
        return queries;
    }

    /**
     * sets the queries
     * 
     * @param queries the queries used
     */
    public void setQueries(String queries) {
        this.queries = queries;
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
                return Integer.compare(this.count, other.count);
            }
        } else {
            return Double.compare(this.score, other.score);
        }
    }

    @Override
    public String toString() {
        return "\"" + queries + "\": [\nwhere: " + this.where + "\ncount: " + this.count + "\nscore: " + this.score
                + "\n]\n";
    }

}
