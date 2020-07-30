package com.github.derrop.cloudnettransformer.cloud.cloudnet3;

import com.github.derrop.cloudnettransformer.cloud.deserialized.service.ServiceInclusion;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

public class CloudNet3Utils {

    public static Document inclusionToDocument(ServiceInclusion inclusion) {
        Document document = Documents.newDocument().append("destination", inclusion.getTarget()).append("url", inclusion.getUrl());
        if (!inclusion.getHeaders().isEmpty()) {
            document.append("properties", Documents.newDocument("httpHeaders", inclusion.getHeaders()));
        }
        return document;
    }

    public static ServiceInclusion documentToInclusion(Document document) {
        ServiceInclusion inclusion = new ServiceInclusion(document.getString("destination"), document.getString("url"));
        Document properties = document.getDocument("properties");
        if (properties != null) {
            Document headers = properties.getDocument("httpHeaders");
            if (headers != null) {
                for (String key : headers.keys()) {
                    String value = headers.getString(key);
                    if (value != null) {
                        inclusion.getHeaders().put(key, value);
                    }
                }
            }
        }
        return inclusion;
    }

}
