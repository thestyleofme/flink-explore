package org.abigballofmud.flink.api.execeptions;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * <p>
 * description
 * </p>
 * 
 * @author isacc 2020/3/26 21:40
 * @since 1.0
 */
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return true;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // ignore do nothing
    }
}
