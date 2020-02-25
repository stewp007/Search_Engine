import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;


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

    System.out.println(Arrays.toString(args));
    
    ArgumentParser parser = new ArgumentParser(args);
    System.out.println("Argument Map: " + parser.toString());
    Path path = parser.getPath("-path");
    
    
    InvertedIndex index = new InvertedIndex();
    
    try {
		List<Path> listPaths = TextFileFinder.list(path);
		
	    for(Path filePath: listPaths) {
	    	System.out.println("File Path: " + filePath);
	    	index.addFromFile(path);
	    	
	    }
	    System.out.println(index);
	    StringWriter writer = new StringWriter();
	    
	    System.out.println(SimpleJsonWriter.indexToJson(index, writer, 0));
	    
	    if(parser.hasValue("-index")) {
	    	SimpleJsonWriter.indexJsonToFile(index, parser.getPath("-index"));
	    }
	    
	    
	 
	    
	} catch (IOException e) {
		System.out.println("Error opening files");
	}
 
   
    	

    // calculate time elapsed and output
    Duration elapsed = Duration.between(start, Instant.now());
    double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
    System.out.printf("Elapsed: %f seconds%n", seconds);
  }

 
}
