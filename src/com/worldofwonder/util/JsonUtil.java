package com.worldofwonder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lightweight, zero-dependency JSON parser and serializer in pure standard Java.
 */
public final class JsonUtil {

    private final String text;
    private int pos;

    private JsonUtil(String text) {
        this.text = text != null ? text : "";
    }

    public static Object parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        JsonUtil parser = new JsonUtil(text);
        Object value = parser.parseValue();
        parser.skipWs();
        if (parser.pos < parser.text.length()) {
            throw new IllegalArgumentException("Trailing content at offset " + parser.pos);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> parseList(String text) {
        Object val = parse(text);
        if (val instanceof List) {
            return (List<Map<String, Object>>) val;
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String text) {
        Object val = parse(text);
        if (val instanceof Map) {
            return (Map<String, Object>) val;
        }
        return new LinkedHashMap<>();
    }

    public static String readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            char[] buffer = new char[4096];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
        }
        return sb.toString();
    }

    public static synchronized void writeToFile(File file, String content) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
            writer.write(content);
            writer.flush();
        }
        if (file.exists()) {
            file.delete();
        }
        tempFile.renameTo(file);
    }

    public static String getAsString(Map<String, Object> map, String key, String def) {
        if (map == null || !map.containsKey(key)) return def;
        Object val = map.get(key);
        return val != null ? String.valueOf(val) : def;
    }

    public static int getAsInt(Map<String, Object> map, String key, int def) {
        if (map == null || !map.containsKey(key)) return def;
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        if (val instanceof String) {
            try {
                return Integer.parseInt((String) val);
            } catch (NumberFormatException ignored) {
            }
        }
        return def;
    }

    public static boolean getAsBoolean(Map<String, Object> map, String key, boolean def) {
        if (map == null || !map.containsKey(key)) return def;
        Object val = map.get(key);
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        if (val instanceof String) {
            return Boolean.parseBoolean((String) val);
        }
        return def;
    }

    private Object parseValue() {
        skipWs();
        if (pos >= text.length()) {
            throw new IllegalArgumentException("Unexpected end of input");
        }
        char c = text.charAt(pos);
        switch (c) {
            case '{':
                return parseObjectInternal();
            case '[':
                return parseArrayInternal();
            case '"':
                return parseStringInternal();
            case 't':
                expect("true");
                return Boolean.TRUE;
            case 'f':
                expect("false");
                return Boolean.FALSE;
            case 'n':
                expect("null");
                return null;
            default:
                if (c == '-' || (c >= '0' && c <= '9')) {
                    return parseNumberInternal();
                }
                throw new IllegalArgumentException("Unexpected character '" + c + "' at offset " + pos);
        }
    }

    private Map<String, Object> parseObjectInternal() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++;
        skipWs();
        if (peek('}')) {
            pos++;
            return map;
        }
        while (true) {
            skipWs();
            if (pos >= text.length() || text.charAt(pos) != '"') {
                throw new IllegalArgumentException("Expected string key at offset " + pos);
            }
            String key = parseStringInternal();
            skipWs();
            if (pos >= text.length() || text.charAt(pos) != ':') {
                throw new IllegalArgumentException("Expected ':' at offset " + pos);
            }
            pos++;
            map.put(key, parseValue());
            skipWs();
            if (peek('}')) {
                pos++;
                return map;
            }
            if (pos >= text.length() || text.charAt(pos) != ',') {
                throw new IllegalArgumentException("Expected ',' or '}' at offset " + pos);
            }
            pos++;
        }
    }

    private List<Object> parseArrayInternal() {
        List<Object> list = new ArrayList<>();
        pos++;
        skipWs();
        if (peek(']')) {
            pos++;
            return list;
        }
        while (true) {
            list.add(parseValue());
            skipWs();
            if (peek(']')) {
                pos++;
                return list;
            }
            if (pos >= text.length() || text.charAt(pos) != ',') {
                throw new IllegalArgumentException("Expected ',' or ']' at offset " + pos);
            }
            pos++;
        }
    }

    private String parseStringInternal() {
        pos++; // skip opening quote
        StringBuilder sb = new StringBuilder();
        while (pos < text.length()) {
            char c = text.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                if (pos >= text.length()) {
                    throw new IllegalArgumentException("Unterminated escape sequence");
                }
                char esc = text.charAt(pos++);
                switch (esc) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        if (pos + 4 > text.length()) {
                            throw new IllegalArgumentException("Unterminated unicode escape");
                        }
                        String hex = text.substring(pos, pos + 4);
                        pos += 4;
                        sb.append((char) Integer.parseInt(hex, 16));
                        break;
                    default:
                        sb.append(esc);
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("Unterminated string");
    }

    private Number parseNumberInternal() {
        int start = pos;
        if (text.charAt(pos) == '-') {
            pos++;
        }
        while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
            pos++;
        }
        boolean isDouble = false;
        if (pos < text.length() && text.charAt(pos) == '.') {
            isDouble = true;
            pos++;
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                pos++;
            }
        }
        if (pos < text.length() && (text.charAt(pos) == 'e' || text.charAt(pos) == 'E')) {
            isDouble = true;
            pos++;
            if (pos < text.length() && (text.charAt(pos) == '+' || text.charAt(pos) == '-')) {
                pos++;
            }
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                pos++;
            }
        }
        String numStr = text.substring(start, pos);
        if (isDouble) {
            return Double.parseDouble(numStr);
        }
        try {
            return Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            return Long.parseLong(numStr);
        }
    }

    private void skipWs() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
    }

    private boolean peek(char c) {
        skipWs();
        return pos < text.length() && text.charAt(pos) == c;
    }

    private void expect(String str) {
        if (!text.startsWith(str, pos)) {
            throw new IllegalArgumentException("Expected '" + str + "' at offset " + pos);
        }
        pos += str.length();
    }

    // --- Serializer with Pretty-Printing ---

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32 || c >= 127) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    public static String toPrettyJson(Object val) {
        StringBuilder sb = new StringBuilder();
        serializePretty(val, sb, 0);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void serializePretty(Object val, StringBuilder sb, int indent) {
        if (val == null) {
            sb.append("null");
        } else if (val instanceof Boolean || val instanceof Number) {
            sb.append(val);
        } else if (val instanceof String) {
            sb.append('"').append(escape((String) val)).append('"');
        } else if (val instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) val;
            if (map.isEmpty()) {
                sb.append("{}");
                return;
            }
            sb.append("{\n");
            int i = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (i++ > 0) sb.append(",\n");
                indent(sb, indent + 2);
                sb.append('"').append(escape(entry.getKey())).append("\": ");
                serializePretty(entry.getValue(), sb, indent + 2);
            }
            sb.append("\n");
            indent(sb, indent);
            sb.append("}");
        } else if (val instanceof List) {
            List<?> list = (List<?>) val;
            if (list.isEmpty()) {
                sb.append("[]");
                return;
            }
            sb.append("[\n");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",\n");
                indent(sb, indent + 2);
                serializePretty(list.get(i), sb, indent + 2);
            }
            sb.append("\n");
            indent(sb, indent);
            sb.append("]");
        } else {
            sb.append('"').append(escape(val.toString())).append('"');
        }
    }

    private static void indent(StringBuilder sb, int spaces) {
        for (int i = 0; i < spaces; i++) {
            sb.append(' ');
        }
    }
}

