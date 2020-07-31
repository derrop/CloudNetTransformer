package com.github.derrop.cloudnettransformer.cloud.cloudnet2.signs;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.database.Database;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.PlacedSign;
import com.github.derrop.cloudnettransformer.cloud.executor.CloudReaderWriter;
import com.github.derrop.cloudnettransformer.cloud.executor.annotation.DescribedCloudExecutor;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@DescribedCloudExecutor(name = "Signs")
public class CloudNet2Signs implements CloudReaderWriter {

    private static final String DATABASE_NAME = "cloud_internal_cfg";
    private static final String DOCUMENT_NAME = "signs";

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);

        Document signs = Documents.newDocument();

        for (PlacedSign sign : cloudSystem.getSigns()) {
            String id = UUID.randomUUID().toString();

            signs.append(id, Documents.newDocument()
                    .append("uniqueId", id)
                    .append("targetGroup", sign.getTargetGroup())
                    .append("position", Documents.newDocument()
                            .append("group", sign.getPlacedGroup())
                            .append("world", sign.getWorld())
                            .append("x", sign.getX())
                            .append("y", sign.getY())
                            .append("z", sign.getZ())
                    )
            );
        }

        database.insert(DOCUMENT_NAME, Documents.newDocument().append("signs", signs));

        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) throws IOException {

        Database database = cloudSystem.getDatabaseProvider().getDatabase(DATABASE_NAME);
        Document document = database.get(DOCUMENT_NAME);
        if (document == null) {
            return true;
        }

        Document signs = document.getDocument("signs");
        if (signs == null) {
            return false;
        }

        int timeId = 0;

        for (String id : signs.keys()) {
            Document sign = signs.getDocument(id);
            if (sign == null) {
                continue;
            }
            Document position = sign.getDocument("position");
            if (position == null) {
                continue;
            }

            cloudSystem.getSigns().add(new PlacedSign(
                    System.currentTimeMillis() + (timeId++ * 10000),
                    sign.getString("targetGroup"),
                    position.getString("group"),
                    position.getString("world"),
                    position.getDouble("x"),
                    position.getDouble("y"),
                    position.getDouble("z")
            ));
        }

        return true;
    }
}
