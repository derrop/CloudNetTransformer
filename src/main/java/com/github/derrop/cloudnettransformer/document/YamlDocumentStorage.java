package com.github.derrop.cloudnettransformer.document;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.Reader;
import java.io.Writer;
import java.util.*;

public class YamlDocumentStorage implements DocumentStorage {

    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
        @Override
        protected Yaml initialValue() {
            Representer representer = new Representer() {
                {
                    /*representers.put(DefaultDocument.class, data -> represent(((DefaultDocument) data).jsonObject.entrySet()));
                    representers.put(JsonPrimitive.class, data -> represent(((JsonPrimitive) data).getAsString()));
                    representers.put(JsonObject.class, data -> represent(((JsonObject) data).entrySet()));*/
                }
            };

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            return new Yaml(new Constructor(), representer, options);
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    public Document read(Reader reader) {
        Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
        JsonElement element = this.asJson(map);
        return new DefaultDocument(element);
    }

    @Override
    public void write(Document document, Writer writer) {
        yaml.get().dump(this.asObject(((DefaultDocument) document).jsonObject), writer);
    }

    @SuppressWarnings("unchecked")
    private JsonElement asJson(Object object) {
        return DefaultDocument.GSON.toJsonTree(object);
    }

    private Object asObject(JsonElement element) {
        if (element.isJsonArray()) {
            Collection<Object> array = new ArrayList<>(element.getAsJsonArray().size());
            for (JsonElement jsonElement : element.getAsJsonArray()) {
                array.add(this.asObject(jsonElement));
            }
            return array;
        } else if (element.isJsonObject()) {
            Map<String, Object> map = new HashMap<>(element.getAsJsonObject().size());
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                Object value = this.asObject(entry.getValue());
                if (value != null) {
                    map.put(entry.getKey(), value);
                }
            }
            return map;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();

            if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

}
