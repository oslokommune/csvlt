package no.ok.origo.csvlt;

import org.junit.jupiter.api.Test;

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
}