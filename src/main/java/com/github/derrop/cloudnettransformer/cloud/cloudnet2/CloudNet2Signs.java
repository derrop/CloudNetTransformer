package com.github.derrop.cloudnettransformer.cloud.cloudnet2;

import com.github.derrop.cloudnettransformer.cloud.deserialized.CloudSystem;
import com.github.derrop.cloudnettransformer.cloud.deserialized.signs.*;
import com.github.derrop.cloudnettransformer.cloud.reader.CloudReader;
import com.github.derrop.cloudnettransformer.cloud.writer.CloudWriter;
import com.github.derrop.cloudnettransformer.json.JsonDocument;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CloudNet2Signs implements CloudReader, CloudWriter {
    @Override
    public String getName() {
        return "Signs";
    }

    private Path config(Path directory) {
        return directory.resolve("CloudNet-Master").resolve("local").resolve("signLayout.json");
    }

    @Override
    public boolean write(CloudSystem cloudSystem, Path directory) throws IOException {
        // TODO
        return true;
    }

    @Override
    public boolean read(CloudSystem cloudSystem, Path directory) {

        Path configPath = this.config(directory);
        if (!Files.exists(configPath)) {
            return false;
        }

        JsonDocument config = JsonDocument.newDocument(configPath).getDocument("layout_config");
        if (config == null) {
            return false;
        }

        boolean switchToSearchingWhenFull = config.getBoolean("fullServerHide");
        boolean knockbackEnabled = config.getBoolean("knockbackOnSmallDistance");
        double knockbackDistance = knockbackEnabled ? config.getDouble("distance") : 0;
        double knockbackStrength = knockbackEnabled ? config.getDouble("strength") : 0;

        TaskSignLayout globalLayout = null;
        Collection<TaskSignLayout> layouts = new ArrayList<>();

        for (JsonElement element : config.getJsonArray("groupLayouts")) {
            JsonDocument groupLayout = new JsonDocument(element);

            SignLayout empty = null;
            SignLayout online = null;
            SignLayout full = null;
            SignLayout maintenance = null;
            for (JsonElement layout : groupLayout.getJsonArray("layouts")) {
                JsonDocument layoutDoc = new JsonDocument(layout);
                switch (layoutDoc.getString("name")) {
                    case "empty":
                        empty = this.asSignLayout(layoutDoc);
                        break;

                    case "online":
                        online = this.asSignLayout(layoutDoc);
                        break;

                    case "full":
                        full = this.asSignLayout(layoutDoc);
                        break;

                    case "maintenance":
                        maintenance = this.asSignLayout(layoutDoc);
                        break;
                }
            }

            TaskSignLayout layout = new TaskSignLayout(groupLayout.getString("name"), online, empty, maintenance, full);

            if (layout.getTask().equalsIgnoreCase("default")) {
                globalLayout = layout;
            } else {
                layouts.add(layout);
            }
        }

        AnimatedSignLayout searchLayout = new AnimatedSignLayout(
                StreamSupport.stream(config.getDocument("searchingAnimation").getJsonArray("searchingLayouts").spliterator(), false)
                        .map(JsonDocument::new)
                        .map(this::asSignLayout)
                        .collect(Collectors.toList()),
                config.getDocument("searchingAnimation").getInt("animationsPerSecond")
        );

        Map<SignMessage, String> messages = new HashMap<>();
        for (SignMessage message : SignMessage.values()) {
            messages.put(message, message.getDefaultMessage());
        }
        messages.put(SignMessage.SERVER_CONNECTING, ""); // CloudNet 2 doesn't have the connecting message

        // TODO: Not for Lobby, but for every group with the groupMode LOBBY or STATIC_LOBBY
        cloudSystem.setSignConfiguration(new SignConfiguration(
                Collections.singletonList(
                        new GroupSignConfiguration(
                                "Lobby",
                                switchToSearchingWhenFull, knockbackDistance, knockbackStrength,
                                layouts, globalLayout,
                                searchLayout, searchLayout
                        )
                ),
                messages
        ));

        return true;
    }

    private SignLayout asSignLayout(JsonDocument document) {
        return new SignLayout(
                document.get("signLayout", String[].class),
                document.getString("blockType") != null ? document.getString("blockType") : String.valueOf(document.getInt("blockId")),
                document.getShort("subId")
        );
    }

}
