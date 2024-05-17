package org.shoplify.common.util;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Method;

public class ServiceClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    public static final String USERSERVICE_URL = "http://userservice:8082/";
    public static final String PRODUCTSERVICE_URL = "http://productservice:8083/";

    public static <T extends Message> T callService(String url, String requestBody, Class<T> responseType) throws IOException {
        RequestBody body = RequestBody.create(requestBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return extractPayload(response, responseType);

        }
    }

    private static <T extends Message> T extractPayload(Response response, Class<T> payloadClass) {
        try {
            Method newBuilderMethod = payloadClass.getMethod("newBuilder");
            Message.Builder responseBuilder;
            responseBuilder = (Message.Builder) newBuilderMethod.invoke(null);
            JsonFormat.parser().merge(response.body().string(), responseBuilder);
            return payloadClass.cast(responseBuilder.build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            response.body().close();
        }
    }
}
