import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/*
 * TODO Break this up into two different handlers...
 * 
 * IndexHandler
 * QueryHandler
 */

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
        List<String> cleanedQueries = new ArrayList<String>();
        TreeMap<String, List<SearchResult>> allResults = new TreeMap<String, List<SearchResult>>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
              // TODO call the other handleQueries method
                TreeSet<String> cleaned = TextFileStemmer.uniqueStems(line);
                Collections.sort(cleanedQueries);
                if (!cleaned.isEmpty()) {
                    if (exact) {
                        allResults.put(String.join(" ", cleaned), this.index.exactSearch(cleaned));
                    } else {
                        allResults.put(String.join(" ", cleaned), this.index.partialSearch(cleaned));
                    }
                    cleanedQueries.clear();
                }
            }
        }
        if (result) {
            SimpleJsonWriter.writeSearchResultsToFile(allResults, output);
        }

    }
    
    /* TODO 
    public void handleQueries(String line, boolean exact) throws IOException {
      TreeSet<String> cleaned = TextFileStemmer.uniqueStems(line);
      String joined = String.join(" ", cleaned);
      
      if (!cleaned.isEmpty() && allResults.containsKey(joined)) {
          allResults.put(joined, this.index.search(cleaned, exact));
      }
    }
    */

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
     * @param path the path to collect into the Index
     * @return boolean
     * @throws IOException throws an IOException
     */
    public boolean handleIndex(Path path) throws IOException {
        return handleIndex(path, this.index);
    }
}