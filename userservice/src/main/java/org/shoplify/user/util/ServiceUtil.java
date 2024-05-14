package org.shoplify.user.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import okhttp3.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceUtil {

    public static final String FAILURE_REASON_HTTP_HEADER = "Failure-Reason";
    public static final String PAYLOAD_HTTP_ATTRIBUTE = "payload";
    public static final String EMPTY_PROTO_JSON = "{}";

    public static String getRequestBody(HttpServletRequest request) {
        Object requestBody = request.getAttribute(PAYLOAD_HTTP_ATTRIBUTE);
        if (requestBody == null) {
            requestBody = EMPTY_PROTO_JSON;
        }
        return (String) requestBody;
    }


    public static <T extends Message> T getRequestBody(HttpServletRequest request, Class<T> requestClass) {
        Object requestBody = request.getAttribute(PAYLOAD_HTTP_ATTRIBUTE);
        if (requestBody == null) {
            requestBody = EMPTY_PROTO_JSON;
        }
        Method newBuilderMethod = null;
        try {
            newBuilderMethod = requestClass.getMethod("newBuilder");
            Message.Builder requestBuilder = (Message.Builder) newBuilderMethod.invoke(null);
            JsonFormat.parser().merge((String) requestBody, requestBuilder);
            return requestClass.cast(requestBuilder.build());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }


    public static String updateReturnResponse(HttpServletResponse httpServletResponse, int errorCode) {
        httpServletResponse.setStatus(errorCode);
        return "";
    }

}
