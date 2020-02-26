import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * 
 */

/**
 * @author stewartpowell
 *
 */
public class InvertedIndex{
	
	private TreeMap<String, WordIndex> invertedIndex;
	
	
	public InvertedIndex() {
		this.invertedIndex = new TreeMap<>();
	}
	
	public boolean add(String word, String path, int position) {
		if(invertedIndex.putIfAbsent(word, new WordIndex()) != null) {
			invertedIndex.put(word, new WordIndex());
			return invertedIndex.get(word).add(path, position) != false? true: false;
		}else {
			return invertedIndex.get(word).add(path, position) != false? true: false;
		}
		
	}
	
	public boolean addFromFile(Path path) {
		int position = 1;
		try(BufferedReader reader = Files.newBufferedReader(path)){
			  String line = null;
			  while((line = reader.readLine()) != null) {
				  
				  TreeSet<String> uniqueStems = TextFileStemmer.uniqueStems(line);
				  ArrayList<String> allStems = TextFileStemmer.listStems(line);
				  System.out.println("unique stems: "+ uniqueStems);
				  System.out.println("All stems: " + allStems);
				  for(String uniqueWord: uniqueStems) {
					  for(String allWord: allStems) {
						  
						  if(uniqueWord.equals(allWord)) {
							  position++;
							  this.add(uniqueWord, path.toString(), position);
						  }
						 
					  }
					  
				  }
			  }
		  }catch(IOException e) {
			  System.out.println("Error adding from file");
			  return false;
		  }
		return true;
	}
	
	
	public Collection<String> getElements() {
		return Collections.unmodifiableSet(invertedIndex.keySet());
	}
	
	public WordIndex getValue(String word){
		
		return invertedIndex.get(word);
		
	}
	
	public int size() {
		return invertedIndex.size();
	}
	
	
	
	
	
	@Override
	public String toString() {
		return invertedIndex.toString();
		
	}

	
	
	
}
