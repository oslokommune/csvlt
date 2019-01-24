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
import java.util.Collection;
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
        Iterable<CSVRecord> records = CSVParser.parse(input, format);
        if (!records.iterator().hasNext()) { // empty
            return "";
        }

        List<JsonNode> rows = transformRecords(records);
        return writeCsv(rows);
    }

    private List<JsonNode> transformRecords(Iterable<CSVRecord> records) {
        return StreamSupport.stream(records.spliterator(), false)
                .map(this::toJsonValue)
                .map(jslt::apply)
                .collect(Collectors.toList());
    }

    private String writeCsv(Collection<JsonNode> rows) throws IOException {
        ArrayList<String> fieldNames = getFieldNames(rows);

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

    private ArrayList<String> getFieldNames(Collection<JsonNode> rows) {
        JsonNode firstRow = rows.iterator().next();

        ArrayList<String> fieldNames = new ArrayList<>();
        firstRow.fieldNames().forEachRemaining(fieldNames::add);
        return fieldNames;
    }

    private JsonNode toJsonValue(CSVRecord record) {
        ObjectNode json = om.createObjectNode();
        record.toMap().forEach(json::put);
        return json;
    }

    private Object toCsvValue(JsonNode node) {
        return node.asText();
    }
}
