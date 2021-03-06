package no.ok.origo.csvlt;

import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvTransformerTest {

    @Test
    public void transform_should_handle_empty_input() throws Exception {
        CsvTransformer transformer = new CsvTransformer("{ * : .}");
        String result = transformer.transform("");
        String expected = "";
        assertEquals(expected, result, "Transformer should handle empty input");
    }

    @Test
    public void dummy_transform_should_pass_content_through() throws Exception {
        CsvTransformer transformer = new CsvTransformer("{ * : .}");
        String result = transformer.transform("a;b;c\n1;2;3\n");
        String expected = "a;b;c\n1;2;3\n";
        assertEquals(expected, result, "Dummy transform should pass content through unchanged");
    }

    @Test
    public void add_one_to_value() throws Exception {
        CsvTransformer transformer = new CsvTransformer("{ * : number(.) + 1 }");
        String result = transformer.transform("a;b;c\n1;2;3\n");
        String expected = "a;b;c\n2;3;4\n";
        assertEquals(expected, result, "Add one to value");
    }

    @Test
    public void renaming_column() throws Exception {
        CsvTransformer transformer = new CsvTransformer("{ \"foo\": .a, * - a: .}");
        String result = transformer.transform("a;b;c\n1;2;3\n");
        String expected = "foo;b;c\n1;2;3\n";
        assertEquals(expected, result, "Renaming column");
    }

    @Test
    public void let_user_specify_different_format() throws Exception {
        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator('\n').withHeader();
        CsvTransformer transformer = new CsvTransformer("{ * : number(.) + 1 }", format);
        String result = transformer.transform("a,b,c\n1,2,3\n");
        String expected = "a,b,c\n2,3,4\n";
        assertEquals(expected, result, "Let user specify different CSV format");
    }

    @Test
    public void keep_column_order_even_with_empty_values_on_first_row() throws Exception {
        CsvTransformer transformer = new CsvTransformer(readResource("delbydel-id.jslt"));
        String result = transformer.transform(readResource("boligpriser.csv"));

        String expected = readResource("boligpriser-result.csv");

        assertEquals(expected, result, "Keep column order even with empty values on first row");
    }

    @Test
    public void transform_should_handle_empty_cells() throws Exception {
        CsvTransformer transformer = new CsvTransformer("{ * : . }");
        String result = transformer.transform("a;b;c\n1;;3\n");
        String expected = "a;b;c\n1;;3\n";
        assertEquals(expected, result, "Transform should handle empty cells");
    }

    @Test
    public void should_handle_streams() throws Exception {
        CsvTransformer transformer = new CsvTransformer(readResource("delbydel-id.jslt"));
        InputStream inputStream = Files.newInputStream(getResourcePath("boligpriser.csv"));
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        StringWriter writer = new StringWriter();

        transformer.transform(streamReader, writer);
        String result = writer.toString();

        streamReader.close();
        inputStream.close();
        writer.close();

        String expected = readResource("boligpriser-result.csv");

        assertEquals(expected, result, "Transform should handle streams");
    }

    @Test
    public void should_be_able_to_trim_strings() throws Exception {
        CsvTransformer transformer = new CsvTransformer("{ \"a\" : trim(.a) }");
        String result = transformer.transform("a\n foo \n bar\nbaz \n");
        String expected = "a\nfoo\nbar\nbaz\n";
        assertEquals(expected, result, "Transform should be able to trim cells");
    }

    private Path getResourcePath(String name) throws URISyntaxException {
        return Paths.get(getClass().getClassLoader().getResource(name).toURI());
    }

    private String readResource(String name) throws IOException, URISyntaxException {
        return Files.lines(getResourcePath(name)).collect(Collectors.joining("\n"));
    }
}