package no.ok.origo.csvlt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CsvTransformer {

    private final CSVFormat format;
    private final Expression jslt;
    private final ObjectMapper om;

    public CsvTransformer(String transform) {
        this(transform, CSVFormat.DEFAULT
                .withDelimiter(';')
                .withRecordSeparator('\n')
                .withHeader()); // Read header from first row
    }

    public CsvTransformer(String transform, CSVFormat format) {
        this.format = format;
        jslt = Parser.compileString(transform);
        om = new ObjectMapper();
    }

    public String transform(String input) throws IOException {
        CSVParser csvParser = CSVParser.parse(input, format);
        List<JsonNode> rows = StreamSupport.stream(csvParser.spliterator(), false)
                .map(this::toJsonValue)
                .map(jslt::apply)
                .collect(Collectors.toList());

        if (rows.isEmpty()) {
            return "";
        }

        ArrayList<String> fieldNames = new ArrayList<>();
        rows.get(0).fieldNames().forEachRemaining(fieldNames::add);

        StringWriter sw = new StringWriter();
        CSVPrinter csvPrinter = format
                .withHeader(fieldNames.toArray(new String[0]))
                .print(sw);

        for (JsonNode row : rows) {
            List<Object> values = fieldNames.stream()
                    .map(row::findValue)
                    .map(this::toCsvValue)
                    .collect(Collectors.toList());
            csvPrinter.printRecord(values);
        }

        return sw.toString();
    }

    protected JsonNode toJsonValue(CSVRecord record) {
        ObjectNode json = om.createObjectNode();
        record.toMap().forEach(json::put);
        return json;
    }

    protected Object toCsvValue(JsonNode node) {
        return node.asText();
    }
}
