package no.ok.origo.csvlt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.schibsted.spt.data.jslt.Function;
import com.schibsted.spt.data.jslt.impl.AbstractFunction;
import com.schibsted.spt.data.jslt.impl.NodeUtils;

import java.util.HashMap;
import java.util.Map;

public class JsltFunctions {

    public static final Map<String, Function> functions = new HashMap<>();

    static {
        functions.put("trim", new JsltFunctions.Trim());
    }

    public static class Trim extends AbstractFunction {
        public Trim() {
            super("trim", 1, 1);
        }

        @Override
        public JsonNode call(JsonNode input, JsonNode[] arguments) {
            String s = NodeUtils.toString(arguments[0], true);

            if (s == null) {
                return NullNode.instance;
            }

            return new TextNode(s.trim());
        }
    }
}
