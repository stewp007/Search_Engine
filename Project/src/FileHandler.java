import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

    /** The default stemmer algorithm used by this class. */
    public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

    /**
     * Constructor for FileHandler Class
     * 
     * @param index the InvertedIndex associated with the FileHandler
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
        }
        return true;
    }
    
    /*
     * TODO Will eventually help to have a static and non-static way to build the index (for project 3). I recommend:
     * 
     * public static boolean handleIndex(Path path, InvertedIndex index) throws IOException {
     *     your original handleIndex implementation as-is except in a static method
     * }
     * 
     * public boolean handleIndex(Path path) throws IOException {
     *     handleIndex(path, this.index)
     * }
     */

}
