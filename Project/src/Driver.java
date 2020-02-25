import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Class responsible for running this project based on the provided command-line arguments. See the
 * README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class Driver {

  /**
   * Initializes the classes necessary based on the provided command-line arguments. This includes
   * (but is not limited to) how to build or search an inverted index.
   *
   * @param args flag/value pairs used to start this program
   */
  public static void main(String[] args) {
    // store initial start time
    Instant start = Instant.now();
    
    if(args.length <= 1) {
    	System.out.println("Please provide necessary arguments.");
    	System.exit(1);;
    }

    // TODO Fill in and modify this method as necessary.
    System.out.println(Arrays.toString(args));
    
    ArgumentParser parser = new ArgumentParser(args);
    System.out.println("Argument Map: " + parser.toString());
    Path path = parser.getPath("-path");
    
    
    try {
		List<Path> listPaths = TextFileFinder.list(path);
		//System.out.println(listPaths.toString());
		
		TreeSet<String> stemmedWords = new TreeSet<>();
	    for(Path filePath: listPaths) {
	    	System.out.println("File Path: " + filePath);
	    	stemmedWords.addAll(TextFileStemmer.uniqueStems(filePath));
	    }
	    System.out.println(stemmedWords);
	    
	    WordIndex index = new WordIndex();
	 
	    
	} catch (IOException e) {
		System.out.println("Error opening files");
	}
  //WordIndex index = new WordIndex();
   
    	

    // calculate time elapsed and output
    Duration elapsed = Duration.between(start, Instant.now());
    double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
    System.out.printf("Elapsed: %f seconds%n", seconds);
  }

  
  
  /*
   * TODO: Delete this after reading...
   *
   * Generally, "driver" classes are responsible for setting up and calling other classes, usually
   * from a main() method that parses command-line parameters. If the driver were only responsible
   * for a single class, we use that class name. For example, "PizzaDriver" is what we would name a
   * driver class that just sets up and calls the "Pizza" class.
   */
}
