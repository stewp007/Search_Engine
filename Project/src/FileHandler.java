import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Helper Class for CS 212 Projects
 * 
 * @author stewartpowell
 *
 */
public class FileHandler {

    /**
     * Class Member to reference the index
     */

    private final InvertedIndex index;
    /**
     * Class Member to reference the counter
     */
    // private final TreeMap<String, Integer> counter;

    /** The default stemmer algorithm used by this class. */
    public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

    /**
     * Constructor for FileHandler Class
     * 
     * @param index   the InvertedIndex associated with the FileHandler
     * @param counter the word counter associated with the FileHandler
     */
    public FileHandler(InvertedIndex index) {
        this.index = index;
    }

    /**
     * Searches through files starting at the given Path
     * 
     * @param path the path of the file to handle
     * @throws IOException throws if there is an issue opening the file
     */
    public void handleFiles(Path path) throws IOException {
        List<Path> listPaths = TextFileFinder.list(path);
        for (Path filePath : listPaths) {
            handleIndex(filePath);
        }
    }

    /**
     * Cleans and parses queries from the given Path
     * 
     * @param path   the path of the Query file
     * @param exact  flag for partial or exact search
     * @param output the file to output the Json results
     * @param result whether to ouptut the search to a file or not
     * @throws IOException throws an IOException
     */
    public void handleQueries(Path path, boolean exact, Path output, Boolean result) throws IOException {
        List<String> allQueries = new ArrayList<String>();
        List<String> cleanedQueries = new ArrayList<String>();
        TreeMap<String, List<SearchResult>> allResults = new TreeMap<String, List<SearchResult>>();
        SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
        String stemmedWord;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parsedQueries = TextParser.parse(line);
                for (String word : parsedQueries) {
                    stemmedWord = stemmer.stem(word).toString();
                    if (!cleanedQueries.contains(stemmedWord) && !allQueries.contains(stemmedWord)) {
                        cleanedQueries.add(stemmedWord);
                        allQueries.add(stemmedWord);
                    }

                }
                Collections.sort(cleanedQueries);
                if (!cleanedQueries.isEmpty()) {
                    if (exact) {
                        allResults.put(String.join(" ", cleanedQueries), this.index.exactSearch(cleanedQueries));
                    } else {
                        allResults.put(String.join(" ", cleanedQueries), this.index.partialSearch(cleanedQueries));
                    }
                    cleanedQueries.clear();
                }
            }
        }
        if (result) {
            SimpleJsonWriter.writeSearchResultsToFile(allResults, output);
        }

    }

    /**
     * 
     * Adds the contents of a file to the Index
     * 
     * @param path the path to collect into the Index
     * 
     * @return boolean
     * @throws IOException Throws if there is an issue opening the file
     */
    public boolean handleIndex(Path path) throws IOException {
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
                    this.index.add(stemmedWord, location, filePosition + linePosition);
                }
                filePosition += allStems.length;
            }
            if (filePosition > 0) {
                this.index.getCounter().putIfAbsent(location, filePosition);
            }
        }
        return true;
    }

}
