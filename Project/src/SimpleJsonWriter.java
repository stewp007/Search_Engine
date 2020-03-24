import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

// TODO Use the more efficient iterator approach and reuse methods where possible

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class SimpleJsonWriter {

    /**
     * Writes the elements as a pretty JSON array.
     *
     * @param elements the elements to write
     * @param writer   the writer to use
     * @param level    the initial indent level
     * @throws IOException if an IO error occurs
     */
    public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {

        Iterator<Integer> input = elements.iterator();
        int c = 0;
        int size = elements.size();
        writer.write("[\n");
        while (input.hasNext()) {
            if (size > 1 && c < size - 1) {
                indent(input.next() + ",\n", writer, 1);

            } else {
                indent(input.next() + "\n", writer, 1);
            }
            c++;
        }
        writer.write("]");
    }

    /**
     * Writes the elements as a pretty JSON array to file.
     *
     * @param elements the elements to write
     * @param path     the file path to use
     * @throws IOException if an IO error occurs
     *
     * @see #asArray(Collection, Writer, int)
     */
    public static void asArray(Collection<Integer> elements, Path path) throws IOException {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            asArray(elements, writer, 0);
        }
    }

    /**
     * Returns the elements as a pretty JSON array.
     *
     * @param elements the elements to use
     * @return a {@link String} containing the elements in pretty JSON format
     *
     * @see #asArray(Collection, Writer, int)
     */
    public static String asArray(Collection<Integer> elements) {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        try {
            StringWriter writer = new StringWriter();
            asArray(elements, writer, 0);
            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes the elements as a pretty JSON object.
     *
     * @param elements the elements to write
     * @param writer   the writer to use
     * @param level    the initial indent level
     * @throws IOException if an IO error occurs
     */
    public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {

        int c = 0;
        int size = elements.size();

        writer.write("{\n");

        for (var entry : elements.entrySet()) {
            if (size > 1 && c < size - 1) {
                quote(entry.getKey(), writer, 1);
                writer.write(":");
                indent(" " + entry.getValue() + ",\n", writer, 0);
            } else {
                quote(entry.getKey(), writer, 1);
                writer.write(":");
                indent(" " + entry.getValue() + "\n", writer, 0);
            }
            c++;
        }

        writer.write("}");

    }

    /**
     * Writes the elements as a pretty JSON object to file.
     *
     * @param elements the elements to write
     * @param path     the file path to use
     * @throws IOException if an IO error occurs
     *
     * @see #asObject(Map, Writer, int)
     */
    public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            asObject(elements, writer, 0);
        }
    }

    /**
     * Returns the elements as a pretty JSON object.
     *
     * @param elements the elements to use
     * @return a {@link String} containing the elements in pretty JSON format
     *
     * @see #asObject(Map, Writer, int)
     */
    public static String asObject(Map<String, Integer> elements) {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        try {
            StringWriter writer = new StringWriter();
            asObject(elements, writer, 0);
            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes the elements as a pretty JSON object with a nested array. The generic
     * notation used allows this method to be used for any type of map with any type
     * of nested collection of integer objects.
     *
     * @param elements the elements to write
     * @param writer   the writer to use
     * @param level    the initial indent level
     * @throws IOException if an IO error occurs
     */
    public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
            throws IOException {

        int locIterations = 0;
        int posIterations = 0;
        // int size = elements.size();

        Iterator<String> locationIter = elements.keySet().iterator();
        int numLocations = elements.keySet().size();
        // level++;
        while (locationIter.hasNext()) {
            // Print out String stuff
            level++;
            String location = locationIter.next();
            // indent(writer, level);
            quote(location, writer, level);
            writer.write(": [\n");
            Iterator<Integer> positionIter = elements.get(location).iterator();
            int numPositions = elements.get(location).size();
            while (positionIter.hasNext()) {
                // Print out Integer Stuff
                level++;
                Integer position = positionIter.next();
                indent(position.toString(), writer, level);
                if (posIterations < numPositions - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
                level--;
                posIterations++;
            }

            if (locIterations < numLocations - 1) {
                indent("],\n", writer, level);
            } else {
                indent("]\n", writer, level);
            }
            locIterations++;
            level--;
        }

        /*
         * The generic notation:
         *
         * Map<String, ? extends Collection<Integer>> elements
         *
         * ...may be confusing. You can mentally replace it with:
         *
         * HashMap<String, HashSet<Integer>> elements
         */
    }

    /**
     * Writes the elements as a pretty JSON object with a nested array. The generic
     * notation used allows this method to be used for any type of map with any type
     * of nested collection of integer objects.
     *
     * @param index  the index to convert to JSON
     * @param writer the writer to use
     * @param level  the initial indent level
     * 
     * @return String the index converted to JSON formatted string
     * @throws IOException if an IO error occurs
     */
    public static String indexToJson(InvertedIndex index, Writer writer, int level) throws IOException { // TODO Want
                                                                                                         // code that
                                                                                                         // works for
                                                                                                         // anything
                                                                                                         // that uses
                                                                                                         // the same
                                                                                                         // nested map
                                                                                                         // structure

        writer.write("{\n");

        int numWords = index.numWords();
        int wordIterations = 0;
        Iterator<String> words = index.getWords().iterator();
        while (words.hasNext()) {
            level++;
            String word = words.next();
            quote(word, writer, level);
            writer.write(": {\n");
            asNestedArray(index.getMap(word), writer, level++);
            level--;
            if (wordIterations < numWords - 1) {
                indent("},\n", writer, level);
            } else {
                indent("}\n", writer, level);
            }
            wordIterations++;
            level--;
        }
        writer.write("}");
        return writer.toString();
    }

    /**
     * Writes the the InvertedIndex as a pretty JSON object to file.
     *
     * @param elements the elements to write
     * @param path     the file path to use
     * @throws IOException if an IO error occurs
     *
     * @see #indexToJson(InvertedIndex index, Writer writer, int level)
     */
    public static void indexJsonToFile(InvertedIndex elements, Path path) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            indexToJson(elements, writer, 0);
        }
    }

    /**
     * Indents using 2 spaces by the number of times specified.
     *
     * @param writer the writer to use
     * @param times  the number of times to write a tab symbol
     * @throws IOException if an IO error occurs
     */
    public static void indent(Writer writer, int times) throws IOException {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        for (int i = 0; i < times; i++) {
            writer.write(' ');
            writer.write(' ');
        }
    }

    /**
     * Indents and then writes the element.
     *
     * @param element the element to write
     * @param writer  the writer to use
     * @param times   the number of times to indent
     * @throws IOException if an IO error occurs
     *
     * @see #indent(String, Writer, int)
     * @see #indent(Writer, int)
     */
    public static void indent(Integer element, Writer writer, int times) throws IOException {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        indent(element.toString(), writer, times);
    }

    /**
     * Indents and then writes the element.
     *
     * @param element the element to write
     * @param writer  the writer to use
     * @param times   the number of times to indent
     * @throws IOException if an IO error occurs
     *
     * @see #indent(Writer, int)
     */
    public static void indent(String element, Writer writer, int times) throws IOException {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        indent(writer, times);
        writer.write(element);
    }

    /**
     * Writes the element surrounded by {@code " "} quotation marks.
     *
     * @param element the element to write
     * @param writer  the writer to use
     * @throws IOException if an IO error occurs
     */
    public static void quote(String element, Writer writer) throws IOException {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        writer.write('"');
        writer.write(element);
        writer.write('"');
    }

    /**
     * Indents and then writes the element surrounded by {@code " "} quotation
     * marks.
     *
     * @param element the element to write
     * @param writer  the writer to use
     * @param times   the number of times to indent
     * @throws IOException if an IO error occurs
     *
     * @see #indent(Writer, int)
     * @see #quote(String, Writer)
     */
    public static void quote(String element, Writer writer, int times) throws IOException {
        // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
        indent(writer, times);
        quote(element, writer);
    }

}
