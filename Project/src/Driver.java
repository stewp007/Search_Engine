import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

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
    
    //Check if enough arguments are provided
    if(args.length < 1) {
    	System.out.println("Please provide necessary arguments.");
    	return;
    }
    //parse arguments into a Map
    ArgumentParser parser = new ArgumentParser(args);
    //Initialize the InvertedIndex
    InvertedIndex index = new InvertedIndex();
    //Initialize FileHandler
    FileHandler handler = new FileHandler(index);
    
    if(parser.hasFlag("-path")) {
    	Path path = parser.getPath("-path");
    	if(path != null) {
    		handler.handleFiles(path);
    	}
    	
    }
    if(parser.hasFlag("-index")) {
    	Path output;
    	if(parser.hasValue("-index")) {
    		output = parser.getPath("-index");
    	}else {
    		output = Path.of("index.json");
    	}
    	try {
			SimpleJsonWriter.indexJsonToFile(index, output);
			return;
		} catch (IOException e) {
			System.out.println("Error writing to specified file: "+ output);
		}
    }
    // calculate time elapsed and output
    Duration elapsed = Duration.between(start, Instant.now());
    double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
    System.out.printf("Elapsed: %f seconds%n", seconds);
  }
}
