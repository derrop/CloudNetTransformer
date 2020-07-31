package com.github.derrop.cloudnettransformer.cloudnet3.modules.signs;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.PlacedSign;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

@DescribedCloudExecutor(name = "Signs")
public class CloudNet3Signs implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloudnet_module_configuration";
    private static final String DOCUMENT_NAME = "signs_store";

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        Collection<Document> signs = new ArrayList<>();

        for (PlacedSign sign : cloudSystem.getSigns()) {
            signs.add(Documents.newDocument()
                    .append("signId", sign.getCreationTime())
                    .append("providedGroup", sign.getPlacedGroup())
                    .append("targetGroup", sign.getTargetGroup())
                    .append("worldPosition", Documents.newDocument()
                            .append("world", sign.getWorld())
                            .append("x", sign.getX())
                            .append("y", sign.getY())
                            .append("z", sign.getZ())
                            .append("yaw", 0F)
                            .append("pitch", 0F)
                    )
            );
        }

        database.insert(DOCUMENT_NAME, Documents.newDocument("signs", signs));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        Document document = database.get(DOCUMENT_NAME);

        if (document == null) {
            return true;
        }

        Collection<Document> signs = document.getDocuments("signs");
        if (signs == null) {
            return false;
        }

        for (Document sign : signs) {
            Document position = sign.getDocument("worldPosition");
            if (position == null) {
                continue;
            }

            cloudSystem.getSigns().add(new PlacedSign(
                    sign.getLong("signId"),
                    sign.getString("targetGroup"),
                    sign.getString("providedGroup"),
                    position.getString("world"),
                    position.getDouble("x"),
                    position.getDouble("y"),
                    position.getDouble("z")
            ));
        }

        return true;
    }
}
