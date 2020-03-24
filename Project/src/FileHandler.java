import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                /*
                 * TODO Start with the easy and general solution... but then see if there is a
                 * way to optimize the efficiency.
                 * 
                 * Create 1 stemmer object per FILE Call TextParser.parse here Loop through and
                 * stem, then add immediately to the index
                 */
                TreeSet<String> uniqueStems = TextFileStemmer.uniqueStems(line);
                ArrayList<String> allStems = TextFileStemmer.listStems(line);
                for (String uniqueWord : uniqueStems) {
                    linePosition = 0;
                    for (String allWord : allStems) {
                        linePosition++;
                        if (uniqueWord.equals(allWord)) {
                            this.index.add(uniqueWord, path.toString(), filePosition + linePosition);
                        }
                    }
                }
                filePosition += allStems.size();
            }
        }
        return true;
    }
}
