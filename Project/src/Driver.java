import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
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
    
    
    //Check if enough arguments are provided
    if(args.length < 1) {
    	System.out.println("Please provide necessary arguments.");
    	return;
    }
    
    //parse arguments into a Map
    ArgumentParser parser = new ArgumentParser(args);
    Path path = parser.getPath("-path");
    
    
    
    //Initialize the InvertedIndex
    InvertedIndex index = new InvertedIndex();
    
    
    
    //decide default index and path options
    if(path == null && !parser.hasFlag("-index")) {
    	return;
    }else if(path == null && parser.hasFlag("-index") && !parser.hasValue("-index")){
    	Path output = Path.of("index.json");
    	try {
			SimpleJsonWriter.indexJsonToFile(index, output);
			return;
		} catch (IOException e) {
			System.out.println("error writing to default file");
		}
    }
    
    
    
    
    
    //load files into the InvertedIndex and output to the necessary location
    try {
		List<Path> listPaths = TextFileFinder.list(path);
		//System.out.println(listPaths);   used for testing
	    for(Path filePath: listPaths) {
	    	System.out.println("File Path: " + filePath);
	    	index.addFromFile(filePath);
	    	
	    }
	    
	    
	    //Decide where to output Index if output file is provided
	    
	    if(parser.hasFlag("-index") && parser.hasValue("-index")) {            //output to provided -index file
	    	SimpleJsonWriter.indexJsonToFile(index, parser.getPath("-index"));
	    }else if(parser.hasFlag("-index") && !parser.hasValue("-index")) {     //default output to index.json
	    	SimpleJsonWriter.indexJsonToFile(index, Path.of("index.json"));
	    }else {
	    	//do nothing
	    	return;
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
