package com.github.derrop.cloudnettransformer.cloud.variable;

import com.github.derrop.cloudnettransformer.VariableLoader;
import com.github.derrop.cloudnettransformer.util.HttpHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class CloudNet3VariableLoader implements VariableLoader {
    @Override
    public void loadVariables(Map<String, String> variables) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            HttpHelper.download("https://cloudnetservice.eu/cloudnet/updates/repository", stream);
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

        Properties properties = new Properties();
        try {
            properties.load(new ByteArrayInputStream(stream.toByteArray()));
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

        System.out.println(properties.getProperty("app-version"));
        variables.put("${VERSION}", properties.getProperty("app-version"));
    }
}
