CSV Transformer
===============

Library to transform CSV files, based on [JSLT](https://github.com/schibsted/jslt).

Uses the [Apache Commons CSV](http://commons.apache.org/proper/commons-csv/)
library for parsing and writing CSV content. (Another alternative is
[OpenCSV](http://opencsv.sourceforge.net/))

## Usage

1. Create a CSV transformer, providing a JSLT transformation specification and an
   optional CSV format.
2. Apply the transformer on the input CSV document (as a String).
   The transformer returns the resulting CSV output as a String.

```$java
String jslt = "{ * : number(.) + 1 }";
String input = "a;b;c\n1;2;3\n";
CSVTransformer transformer = new CSVTransformer(jslt);
String result = transformer.transform(input);

// result == "a;b;c\n2;3;4\n"
```
