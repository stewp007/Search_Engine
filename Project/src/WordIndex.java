import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A special type of {@link Index} that indexes the locations words were found.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class WordIndex implements Index<String>{
	
	private TreeMap<String, TreeSet<Integer>> wordIndex;
	
	public WordIndex() {
		wordIndex = new TreeMap<>();
	}

	@Override
	public boolean add(String element, int position) {
		 if(wordIndex.putIfAbsent(element, new TreeSet<>()) != null) {
			 return wordIndex.get(element).add(position) != false ? true : false;
		 }else {
			 wordIndex.get(element).add(position);
			 return true;
		 }
	}

	@Override
	public int numPositions(String element) {
		return wordIndex.size();
	}

	@Override
	public int numElements() {
		return wordIndex.size() != 0 ? wordIndex.size(): 0;
	}

	@Override
	public boolean contains(String element) {
		return wordIndex.containsKey(element);
	}

	@Override
	public boolean contains(String element, int position) {
		
		return contains(element) ? wordIndex.get(element).contains(position) : false;
		
		
	}

	@Override
	public Collection<String> getElements() {
		
		return Collections.unmodifiableCollection(wordIndex.keySet());
	}

	@Override
	public Collection<Integer> getPositions(String element) {
		return wordIndex.containsKey(element)  == true ? Collections.unmodifiableCollection(wordIndex.get(element)) : Collections.emptySet();
	}

	@Override
	public String toString() {
		return wordIndex.toString();
	}

	public int size() {
		
		return wordIndex.size();
	}

	public  Set<Entry<String, TreeSet<Integer>>> entrySet() {
		
		return wordIndex.entrySet();
	}
	
	
	public static void main(String [] args) {
		WordIndex index =  new WordIndex();
		
		
		index.add("hello", 1);
		index.add("hello", 2);
		index.add("hello", 3);
		index.add("world", 7);
		index.add("world", 3);
		index.add("hello", 1);
		
		System.out.println(index);
	}
  

}
