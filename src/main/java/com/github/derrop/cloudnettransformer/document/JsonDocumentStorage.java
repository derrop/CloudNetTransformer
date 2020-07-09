package com.github.derrop.cloudnettransformer.document;

import com.google.gson.JsonParser;

import java.io.*;

public class JsonDocumentStorage implements DocumentStorage {

    public void write(Document document, Writer writer) {
        DefaultDocument.GSON.toJson(((DefaultDocument) document).jsonObject, writer);
    }

    public DefaultDocument read(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            return new DefaultDocument().append(JsonParser.parseReader(bufferedReader).getAsJsonObject());
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return null;
    }

}
