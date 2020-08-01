package eu.cloudnetservice.cloudnet.v2.lib.database;

import com.github.derrop.cloudnettransformer.cloudnet2.database.CloudNet2NitriteDatabase;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DatabaseDocument implements Mappable {

    @Id
    private String _database_id_unique;

    private Document backingDocument;

    public DatabaseDocument(Document document, String id) {
        this._database_id_unique = id;
        this.backingDocument = document;
    }

    public DatabaseDocument(String id) {
        this._database_id_unique = id;
    }

    public DatabaseDocument() {
    }

    public Document getBackingDocument() {
        return this.backingDocument;
    }

    public String getId() {
        return this._database_id_unique;
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

    @Override
    public org.dizitart.no2.Document write(NitriteMapper mapper) {
        return mapper.asDocument(this.asObject(this.backingDocument.toInstanceOf(JsonObject.class)));
    }

    @Override
    public void read(NitriteMapper mapper, org.dizitart.no2.Document document) {
        this.backingDocument = Documents.newDocument();
        org.dizitart.no2.Document parsed = mapper.asDocument(document);
        if (parsed != null) {
            parsed.forEach((key, value) -> this.backingDocument.append(key, value));
            this._database_id_unique = this.backingDocument.getString(CloudNet2NitriteDatabase.UNIQUE_NAME_KEY);
        }
    }
}
