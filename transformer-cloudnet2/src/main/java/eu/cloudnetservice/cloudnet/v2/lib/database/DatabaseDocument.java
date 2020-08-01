package eu.cloudnetservice.cloudnet.v2.lib.database;

import com.github.derrop.cloudnettransformer.cloudnet2.database.CloudNet2NitriteDatabase;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;

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

    @Override
    public org.dizitart.no2.Document write(NitriteMapper mapper) {
        return mapper.asDocument(this.backingDocument.toPlainObjects());
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
