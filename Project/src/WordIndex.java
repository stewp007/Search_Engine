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
	
	
	/**
	 * WordIndex data type
	 */
	private TreeMap<String, TreeSet<Integer>> wordIndex;
	
	/**
	 * Initializes the WordIndex object
	 * 
	 */
	public WordIndex() {
		wordIndex = new TreeMap<>();
	}

	
	/**
	 * adds words and their positions to the WordIndex
	 * 
	 * @param element the element to be added
	 * @param position the position the element was found
	 * @return boolean if the WordIndex was changed
	 */
	@Override
	public boolean add(String element, int position) {
		 if(wordIndex.putIfAbsent(element, new TreeSet<>()) != null) {
			 return wordIndex.get(element).add(position) != false ? true : false;
		 }else {
			 wordIndex.get(element).add(position);
			 return true;
		 }
	}
	
	/**
	 * return the number of positions associated with the given element
	 * @param element the element to get positions from
	 * @return int the number of positions
	 */
	@Override
	public int numPositions(String element) {
		return wordIndex.size();
	}
	
	/**
	 * return the size of the WordIndex
	 * 
	 * @return int the number of positions
	 */
	@Override
	public int numElements() {
		return wordIndex.size() != 0 ? wordIndex.size(): 0;
	}

	/**
	 * sees if the element is in the WordIndex
	 * @param element
	 * @return boolean whether the element was in the WordIndex
	 */
	@Override
	public boolean contains(String element) {
		return wordIndex.containsKey(element);
	}
	
	/**
	 * sees if the element is in the WordIndex with the given position
	 * @param element
	 * @param position
	 * @return boolean whether the element and position was in the WordIndex
	 */
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
	
	/**
	 * returns the size of the wordIndex
	 * @return int size
	 */
	public int size() {
		return wordIndex.size();
	}
	
	/**
	 * returns the set of keys from the WordIndex
	 * @return Set<Entry<String, TreeSet<Integer>>>
	 * 
	 */
	public  Set<Entry<String, TreeSet<Integer>>> entrySet() {
		return wordIndex.entrySet();
	}

}
