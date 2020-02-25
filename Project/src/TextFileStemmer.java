import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Collection;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections of stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 *
 * @see TextParser
 */
public class TextFileStemmer {

  /** The default stemmer algorithm used by this class. */
  public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

  private static void stemIntoContainer(String line, Stemmer stemmer, Collection<String> container) {
	  String [] splitWords = TextParser.parse(line);
	  for(String word: splitWords) {
	    container.add(stemmer.stem(word).toString());	
	  }
  }

  /**
   * Returns a list of cleaned and stemmed words parsed from the provided line.
   *
   * @param line the line of words to clean, split, and stem
   * @param stemmer the stemmer to use
   * @return a list of cleaned and stemmed words
   *
   * @see Stemmer#stem(CharSequence)
   * @see TextParser#parse(String)
   */
  public static ArrayList<String> listStems(String line, Stemmer stemmer) {
    ArrayList<String> stemmedWords = new ArrayList<>();
    stemIntoContainer(line, stemmer, stemmedWords);
	return stemmedWords;
    
  }

  /**
   * Returns a list of cleaned and stemmed words parsed from the provided line.
   *
   * @param line the line of words to clean, split, and stem
   * @return a list of cleaned and stemmed words
   *
   * @see SnowballStemmer
   * @see #DEFAULT
   * @see #listStems(String, Stemmer)
   */
  public static ArrayList<String> listStems(String line) {
    // THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
    return listStems(line, new SnowballStemmer(DEFAULT));
  }

  /**
   * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from the provided
   * line.
   *
   * @param line the line of words to clean, split, and stem
   * @param stemmer the stemmer to use
   * @return a sorted set of unique cleaned and stemmed words
   *
   * @see Stemmer#stem(CharSequence)
   * @see TextParser#parse(String)
   */
  public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
	  TreeSet<String> uniqueSet = new TreeSet<>();
	  stemIntoContainer(line, stemmer, uniqueSet);
	  return uniqueSet;
  }

  /**
   * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from the provided
   * line.
   *
   * @param line the line of words to clean, split, and stem
   * @return a sorted set of unique cleaned and stemmed words
   *
   * @see SnowballStemmer
   * @see #DEFAULT
   * @see #uniqueStems(String, Stemmer)
   */
  public static TreeSet<String> uniqueStems(String line) {
    // THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
    return uniqueStems(line, new SnowballStemmer(DEFAULT));
  }

  /**
   * Reads a file line by line, parses each line into cleaned and stemmed words, and then adds those
   * words to a set.
   *
   * @param inputFile the input file to parse
   * @return a sorted set of stems from file
   * @throws IOException if unable to read or parse file
   *
   * @see #uniqueStems(String)
   * @see TextParser#parse(String)
   */
  public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
	  TreeSet<String> fileSet = new TreeSet<>();
	  
	  try(BufferedReader reader = Files.newBufferedReader(inputFile)){
		  String line = null;
		  while((line = reader.readLine()) != null) {
			  
			  fileSet.addAll(uniqueStems(line));
			  
		  }
	  }catch(IOException e) {
		  System.out.println("Error reading file");
		  return null;
	  }
	  return fileSet;
  }

  /**
   * Reads a file line by line, parses each line into cleaned and stemmed words, and then adds those
   * words to a set.
   *
   * @param inputFile the input file to parse
   * @return a sorted set of stems from file
   * @throws IOException if unable to read or parse file
   *
   * @see #uniqueStems(String)
   * @see TextParser#parse(String)
   */
  public static ArrayList<String> listStems(Path inputFile) throws IOException {
	  ArrayList<String> fileArray = new ArrayList<>();
	  try(BufferedReader reader = Files.newBufferedReader(inputFile)){
		  String line = null;
		  while((line = reader.readLine()) != null) {
			  fileArray.addAll(listStems(line)); 
		  }
	  }catch(IOException e) {
		  System.out.println("Error reading file");
		  return null;
	  }
	  return fileArray;
  }

}