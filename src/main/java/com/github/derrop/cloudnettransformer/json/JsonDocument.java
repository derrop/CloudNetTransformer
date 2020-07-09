package com.github.derrop.cloudnettransformer.json;

import com.google.gson.*;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JsonDocument {

    public static final TypeAdapter<JsonDocument> TYPE_ADAPTER = new TypeAdapter<JsonDocument>() {
        @Override
        public void write(JsonWriter jsonWriter, JsonDocument document) throws IOException {
            TypeAdapters.JSON_ELEMENT.write(jsonWriter, document == null ? new JsonObject() : document.jsonObject);
        }

        @Override
        public JsonDocument read(JsonReader jsonReader) throws IOException {
            JsonElement jsonElement = TypeAdapters.JSON_ELEMENT.read(jsonReader);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                return new JsonDocument(jsonElement);
            } else {
                return null;
            }
        }
    };

    public static Gson GSON = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(TypeAdapters.newTypeHierarchyFactory(JsonDocument.class, TYPE_ADAPTER))
            .create();

    protected final JsonObject jsonObject;

    public JsonDocument(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonDocument() {
        this(new JsonObject());
    }

    public JsonDocument(Object toObjectMirror) {
        this(GSON.toJsonTree(toObjectMirror));
    }

    public JsonDocument(JsonElement jsonElement) {
        this(jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : new JsonObject());
    }

    public JsonDocument(Properties properties) {
        this();
        this.append(properties);
    }

    public JsonDocument(String key, String value) {
        this();
        this.append(key, value);
    }

    public JsonDocument(String key, Object value) {
        this();
        this.append(key, value);
    }

    public JsonDocument(String key, Boolean value) {
        this();
        this.append(key, value);
    }

    public JsonDocument(String key, Number value) {
        this();
        this.append(key, value);
    }

    public JsonDocument(String key, Character value) {
        this();
        this.append(key, value);
    }

    public JsonDocument(String key, JsonDocument value) {
        this();
        this.append(key, value);
    }

    public JsonDocument(String key, Properties value) {
        this();
        this.append(key, value);
    }


    public static JsonDocument newDocument() {
        return new JsonDocument();
    }

    public static JsonDocument newDocument(JsonObject jsonObject) {
        return new JsonDocument(jsonObject);
    }

    public static JsonDocument newDocument(String key, String value) {
        return new JsonDocument(key, value);
    }

    public static JsonDocument newDocument(String key, Number value) {
        return new JsonDocument(key, value);
    }

    public static JsonDocument newDocument(String key, Character value) {
        return new JsonDocument(key, value);
    }

    public static JsonDocument newDocument(String key, Boolean value) {
        return new JsonDocument(key, value);
    }

    public static JsonDocument newDocument(String key, Object value) {
        return new JsonDocument(key, value);
    }

    public static JsonDocument newDocument(byte[] bytes) {
        return newDocument(new String(bytes, StandardCharsets.UTF_8));
    }

    public static JsonDocument newDocument(Object object) {
        return new JsonDocument(GSON.toJsonTree(object));
    }

    public static JsonDocument newDocument(File file) {
        if (file == null) {
            return null;
        }

        return newDocument(file.toPath());
    }

    public static JsonDocument newDocument(Path path) {
        JsonDocument document = new JsonDocument();
        document.read(path.toFile());
        return document;
    }

    public static JsonDocument newDocument(String input) {
        return new JsonDocument().read(input);
    }

    public Collection<String> keys() {
        Collection<String> collection = new ArrayList<>(this.jsonObject.size());

        for (Map.Entry<String, JsonElement> entry : this.jsonObject.entrySet()) {
            collection.add(entry.getKey());
        }

        return collection;
    }
    
    public int size() {
        return this.jsonObject.size();
    }
    
    public JsonDocument clear() {
        for (Map.Entry<String, JsonElement> elementEntry : this.jsonObject.entrySet()) {
            this.jsonObject.remove(elementEntry.getKey());
        }

        return this;
    }
    
    public JsonDocument remove(String key) {
        this.jsonObject.remove(key);
        return this;
    }
    
    public boolean contains(String key) {
        return key != null && this.jsonObject.has(key);
    }
    
    public <T> T toInstanceOf(Class<T> clazz) {
        return GSON.fromJson(jsonObject, clazz);
    }
    
    public <T> T toInstanceOf(Type type) {
        return GSON.fromJson(jsonObject, type);
    }
    
    public JsonDocument append(String key, Object value) {
        if (key == null || value == null) {
            return this;
        }

        this.jsonObject.add(key, GSON.toJsonTree(value));
        return this;
    }
    
    public JsonDocument append(String key, Number value) {
        if (key == null || value == null) {
            return this;
        }

        this.jsonObject.addProperty(key, value);
        return this;
    }
    
    public JsonDocument append(String key, Boolean value) {
        if (key == null || value == null) {
            return this;
        }

        this.jsonObject.addProperty(key, value);
        return this;
    }
    
    public JsonDocument append(String key, String value) {
        if (key == null || value == null) {
            return this;
        }

        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonDocument append(String key, Character value) {
        if (key == null || value == null) {
            return this;
        }

        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonDocument append(String key, JsonDocument value) {
        if (key == null || value == null) {
            return this;
        }

        this.jsonObject.add(key, value.jsonObject);
        return this;
    }

    public JsonDocument append(JsonDocument document) {
        if (document == null) {
            return this;
        } else {
            return append(document.jsonObject);
        }
    }

    public JsonDocument append(JsonObject jsonObject) {
        if (jsonObject == null) {
            return this;
        }

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            this.jsonObject.add(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public JsonDocument append(Properties properties) {
        if (properties == null) {
            return this;
        }

        Object entry;
        Enumeration<?> enumeration = properties.keys();

        while (enumeration.hasMoreElements() && (entry = enumeration.nextElement()) != null) {
            append(entry.toString(), properties.getProperty(entry.toString()));
        }

        return this;
    }

    public JsonDocument append(String key, Properties properties) {
        return append(key, new JsonDocument(properties));
    }

    public JsonDocument append(String key, byte[] bytes) {
        if (key == null || bytes == null) {
            return this;
        }

        return this.append(key, Base64.getEncoder().encodeToString(bytes));
    }

    public JsonDocument append(Map<String, Object> map) {
        if (map == null) {
            return this;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            this.append(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public JsonDocument append(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return append(reader);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return this;
    }

    public JsonDocument append(Reader reader) {
        return append(JsonParser.parseReader(reader).getAsJsonObject());
    }

    public JsonDocument getDocument(String key) {
        if (!contains(key)) {
            return null;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonObject()) {
            return new JsonDocument(jsonElement);
        } else {
            return null;
        }
    }

    public int getInt(String key) {
        if (!contains(key)) {
            return 0;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsInt();
        } else {
            return 0;
        }
    }

    public double getDouble(String key) {
        if (!contains(key)) {
            return 0;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsDouble();
        } else {
            return 0;
        }
    }
    
    public float getFloat(String key) {
        if (!contains(key)) {
            return 0;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsFloat();
        } else {
            return 0;
        }
    }
    
    public byte getByte(String key) {
        if (!contains(key)) {
            return 0;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsByte();
        } else {
            return 0;
        }
    }
    
    public short getShort(String key) {
        if (!contains(key)) {
            return 0;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsShort();
        } else {
            return 0;
        }
    }

    public long getLong(String key) {
        if (!contains(key)) {
            return 0;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsLong();
        } else {
            return 0;
        }
    }

    public boolean getBoolean(String key) {
        if (!contains(key)) {
            return false;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsBoolean();
        } else {
            return false;
        }
    }

    public String getString(String key) {
        if (!contains(key)) {
            return null;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        } else {
            return null;
        }
    }

    public char getChar(String key) {
        if (!contains(key)) {
            return 0;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString().charAt(0);
        } else {
            return 0;
        }
    }

    public BigDecimal getBigDecimal(String key) {
        if (!contains(key)) {
            return null;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsBigDecimal();
        } else {
            return null;
        }
    }

    public BigInteger getBigInteger(String key) {
        if (!contains(key)) {
            return null;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsBigInteger();
        } else {
            return null;
        }
    }

    public JsonArray getJsonArray(String key) {
        if (!contains(key)) {
            return null;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        } else {
            return null;
        }
    }

    public JsonObject getJsonObject(String key) {
        if (!contains(key)) {
            return null;
        }

        JsonElement jsonElement = this.jsonObject.get(key);

        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else {
            return null;
        }
    }

    public Properties getProperties(String key) {
        Properties properties = new Properties();

        for (Map.Entry<String, JsonElement> entry : this.jsonObject.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue().toString());
        }

        return properties;
    }

    public JsonElement get(String key) {
        if (!contains(key)) {
            return null;
        }

        return this.jsonObject.get(key);
    }

    public byte[] getBinary(String key) {
        return Base64.getDecoder().decode(this.getString(key));
    }

    public <T> T get(String key, Class<T> clazz) {
        return this.get(key, GSON, clazz);
    }

    public <T> T get(String key, Type type) {
        return this.get(key, GSON, type);
    }

    public <T> T get(String key, Gson gson, Class<T> clazz) {
        if (key == null || gson == null || clazz == null) {
            return null;
        }

        JsonElement jsonElement = get(key);

        if (jsonElement == null) {
            return null;
        } else {
            return gson.fromJson(jsonElement, clazz);
        }
    }

    public <T> T get(String key, Gson gson, Type type) {
        if (key == null || gson == null || type == null) {
            return null;
        }

        if (!contains(key)) {
            return null;
        }

        JsonElement jsonElement = get(key);

        if (jsonElement == null) {
            return null;
        } else {
            return gson.fromJson(jsonElement, type);
        }
    }

    public Integer getInt(String key, Integer def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getInt(key);
    }

    public Short getShort(String key, Short def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getShort(key);
    }

    public Boolean getBoolean(String key, Boolean def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getBoolean(key);
    }

    public Long getLong(String key, Long def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getLong(key);
    }

    public Double getDouble(String key, Double def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getDouble(key);
    }


    public Float getFloat(String key, Float def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getFloat(key);
    }

    public String getString(String key, String def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getString(key);
    }

    public JsonDocument getDocument(String key, JsonDocument def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getDocument(key);
    }

    public JsonArray getJsonArray(String key, JsonArray def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getJsonArray(key);
    }

    public JsonObject getJsonObject(String key, JsonObject def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getJsonObject(key);
    }

    public byte[] getBinary(String key, byte[] def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getBinary(key);
    }


    public <T> T get(String key, Type type, T def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.get(key, type);
    }

    public <T> T get(String key, Class<T> clazz, T def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.get(key, clazz);
    }

    public Properties getProperties(String key, Properties def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getProperties(key);
    }

    public BigInteger getBigInteger(String key, BigInteger def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getBigInteger(key);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getBigDecimal(key);
    }

    public Character getChar(String key, Character def) {
        if (!this.contains(key)) {
            this.append(key, def);
        }

        return this.getChar(key);
    }

    public JsonDocument write(OutputStream outputStream) {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            this.write(outputStreamWriter);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return this;
    }

    public JsonDocument write(File file) {
        return write(file.toPath());
    }

    public JsonDocument write(Path path) {
        Path parent = path.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (OutputStream stream = Files.newOutputStream(path)) {
                return this.write(stream);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return this;
    }
    
    public JsonDocument write(Writer writer) {
        GSON.toJson(this.jsonObject, writer);
        return this;
    }

    public JsonDocument read(InputStream inputStream) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return this.read(inputStreamReader);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return this;
    }

    public JsonDocument read(Path path) {
        try (InputStream stream = Files.newInputStream(path)) {
            return read(stream);
        } catch (final IOException ex) {
            ex.printStackTrace();
            return this;
        }
    }

    public JsonDocument read(File file) {
        return read(file.toPath());
    }
    
    public JsonDocument read(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            return this.append(JsonParser.parseReader(bufferedReader).getAsJsonObject());
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return this;
    }

    
    public void read(byte[] bytes) {
        this.append(JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject());
    }

    public JsonDocument read(String input) {
        try {
            this.append(JsonParser.parseReader(new BufferedReader(new StringReader(input))).getAsJsonObject());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return this;
    }
    
    public JsonObject toJsonObject() {
        return jsonObject;
    }

    public String toPrettyJson() {
        return GSON.toJson(this.jsonObject);
    }

    public String toJson() {
        return this.jsonObject.toString();
    }

    public byte[] toByteArray() {
        return toJson().getBytes(StandardCharsets.UTF_8);
    }
    
    public String toString() {
        return toJson();
    }
    
    public Iterator<String> iterator() {
        return this.jsonObject.keySet().iterator();
    }
}
