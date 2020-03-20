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
public class FileHandler{
	
	/**
	 * Class Member to reference the index
	 */
	
	private final InvertedIndex index;
	
	/**
	 * Constructor for FileHandler Class
	 * @param index
	 */
	public FileHandler(InvertedIndex index){
		this.index = index;
	}
	
	/**
	 * Searches through files starting at the given Path
	 * @param path
	 */
	public void handleFiles(Path path) {
	   try {
			List<Path> listPaths = TextFileFinder.list(path);
		    for(Path filePath: listPaths) {
		    	handleIndex(filePath);
		    }
		} catch (IOException e) {
			System.out.println("Error opening files from: "+ path);
		}
	}
	
	/**
	 * 
	 * Adds the contents of a file to the Index
	 * 
	 * @param path the path to collect into the Index
	 * 
	 * @return boolean
	 */
	public boolean handleIndex(Path path) {
		int filePosition = 0;
		int linePosition = 0;
		try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)){
			String line;
			while((line = reader.readLine()) != null) {
				TreeSet<String> uniqueStems = TextFileStemmer.uniqueStems(line);
				ArrayList<String> allStems = TextFileStemmer.listStems(line); 
				for(String uniqueWord: uniqueStems) {
					linePosition = 0;
					for(String allWord: allStems) {
						linePosition++;
						if(uniqueWord.equals(allWord)) {
							this.index.add(uniqueWord, path.toString(), filePosition + linePosition);
						} 
					}  
				}
				filePosition += allStems.size(); 
			}
		}catch(IOException e) {
			System.out.println("Error adding from file");
			return false;
		}
			return true;
	}
}
