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
