import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
        var input = elements.iterator();
        writer.write("[");
        if (input.hasNext()) {
            writer.write("\n");
            indent(input.next(), writer, level + 1);
        }
        while (input.hasNext()) {
            writer.write(",\n");
            indent(input.next(), writer, level + 1);
        }
        writer.write("\n");
        indent("]", writer, level);
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
        var input = elements.entrySet().iterator();
        writer.write("{");
        if (input.hasNext()) {
            var entry = input.next();
            writer.write("\n");
            quote(entry.getKey(), writer, level + 1);
            writer.write(": " + entry.getValue());
        }
        while (input.hasNext()) {
            var entry = input.next();
            writer.write(",\n");
            quote(entry.getKey(), writer, level + 1);
            writer.write(": " + entry.getValue());
        }
        writer.write("\n}");
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
     * Writes a nested entry into pretty json format
     * 
     * @param entry  the Map entry
     * @param writer the writer to do the writign
     * @param level  the level of Indentation
     * @throws IOException throws an IOException
     */
    public static void writeAsNestedEntry(Map.Entry<String, ? extends Collection<Integer>> entry, Writer writer,
            int level) throws IOException {
        quote(entry.getKey(), writer, level);
        writer.write(": ");
        asArray(entry.getValue(), writer, level);
    }

    /**
     * Writes the index into pretty Json format
     * 
     * @param entry  the index to be written
     * @param writer the writer
     * @param level  the level of indentation
     * @throws IOException throws IOException
     */
    public static void writeIndexToJson(Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry, Writer writer,
            int level) throws IOException {
        quote(entry.getKey(), writer, level);
        writer.write(": {\n");
        asNestedArray(entry.getValue(), writer, level);
        writer.write("}");
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
        var locationIter = elements.entrySet().iterator();
        level++;
        if (locationIter.hasNext()) {
            writeAsNestedEntry(locationIter.next(), writer, level);
        }
        while (locationIter.hasNext()) {
            writer.write(",\n");
            writeAsNestedEntry(locationIter.next(), writer, level);
        }
        writer.write("\n");
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
    public static String indexToJson(Map<String, TreeMap<String, TreeSet<Integer>>> index, Writer writer, int level)
            throws IOException {
        writer.write("{");
        var locationIter = index.entrySet().iterator();
        level++;
        if (locationIter.hasNext()) {
            writer.write("\n");
            writeIndexToJson(locationIter.next(), writer, level);
        }
        while (locationIter.hasNext()) {
            writer.write(",\n");
            writeIndexToJson(locationIter.next(), writer, level);
        }
        writer.write("\n");
        indent("}", writer, level - 1);
        return writer.toString();
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

    /**
     * Outputs the given index in Json form to the given Path
     * 
     * @param index the index to output
     * @param path  the Path to output the Json to
     * @throws IOException throws IOException
     */
    public static void indexToJsonFile(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Path path)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            indexToJson(index, writer, 0);
        }
    }

}
