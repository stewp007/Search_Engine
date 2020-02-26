import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;




/**
 * @author stewartpowell
 *
 *This is the InvertedIndex data structure used for USF CS212 Project 1
 */
public class InvertedIndex{
	
	/**
	 * the data structure to be used
	 */
	
	private TreeMap<String, WordIndex> invertedIndex;
	
	/**
	 * Instantiates the InvertedIndex object
	 */
	public InvertedIndex() {
		this.invertedIndex = new TreeMap<>();
	}
	
	/**
	 * Adds the word, path, and position to the index
	 * 
	 * @param word the word to be added
	 * @param path the path the word was found in
	 * @param position the position in the file the word was found in
	 * 
	 * @return boolean if the Index was changed
	 */
	public boolean add(String word, String path, int position) {
		if(invertedIndex.putIfAbsent(word, new WordIndex()) == null) {
			//invertedIndex.put(word, new WordIndex());
			return invertedIndex.get(word).add(path, position) != false? true: false;
		}else {
			return invertedIndex.get(word).add(path, position) != false? true: false;
		}
		
	}
	
	/**
	 * Adds the contents of a file to the Index
	 * 
	 * @param path the path to collect into the Index
	 * 
	 * @return boolean 
	 * 
	 * @see add(String word, String path, int position)
	 */
	
	public boolean addFromFile(Path path) {
		
		//System.out.println("Add From file path: " + path.toString()); used for testing
		int filePosition = 0;
		int linePosition = 0;
		
		try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)){
			
			String line;
			System.out.println("Inside try/catch.");
			
			while((line = reader.readLine()) != null) {
				
				System.out.println("Inside the while loop.");
				
				TreeSet<String> uniqueStems = TextFileStemmer.uniqueStems(line);
				ArrayList<String> allStems = TextFileStemmer.listStems(line);
				
				System.out.println("unique stems: "+ uniqueStems);
				System.out.println("All stems: " + allStems); 
				 
				for(String uniqueWord: uniqueStems) {
					linePosition = 0;
					for(String allWord: allStems) {
						linePosition++;
						
						if(uniqueWord.equals(allWord)) {
							this.add(uniqueWord, path.toString(), filePosition + linePosition);
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
	
	/**
	 * returns an unmodifiable collection of the keys of the Index
	 * @return Collection<String>
	 */
	public Collection<String> getElements() {
		return Collections.unmodifiableSet(invertedIndex.keySet());
	}
	
	/**
	 * returns the WordIndex associated with the InvertedIndex
	 * @param word the word to get value from
	 * @return WordIndex
	 */
	public WordIndex getValue(String word){
		
		return invertedIndex.get(word);
		
	}
	
	/**
	 * returns the size of the InvertedIndex
	 * @return int size of the index
	 */
	
	public int size() {
		return invertedIndex.size();
	}
	
	@Override
	public String toString() {
		return invertedIndex.toString();
		
	}

	
	
	
}
