package com.dingdongdeng.coinautotrading.common.config.filter;

import com.dingdongdeng.coinautotrading.common.logging.LoggingUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        LoggingUtils.track(); //logging trackingId
        ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);

        log.info(makeloggingString(wrappingRequest)); // logging request
        chain.doFilter(wrappingRequest, wrappingResponse);
        log.info(makeLoggingString(wrappingResponse)); // logging response

        wrappingResponse.copyBodyToResponse();
    }

    private String makeloggingString(ContentCachingRequestWrapper request) {
        return new StringBuilder()
            .append("\n")
            .append("request ::: ").append("\n")
            .append("url : ").append(request.getRequestURL()).append("\n")
            .append("method : ").append(request.getMethod()).append("\n")
            .append("headers : ").append(getHeaders(request)).append("\n")
            .append("body : ").append(getRequestBody(request))
            .toString();
    }

    private String makeLoggingString(ContentCachingResponseWrapper response) {
        return new StringBuilder()
            .append("\n")
            .append("response ::: ").append("\n")
            .append("status : ").append(response.getStatus()).append("\n")
            .append("body : ").append(getResponseBody(response))
            .toString();
    }


    private Map getHeaders(HttpServletRequest request) {
        Map headerMap = new HashMap<>();

        Enumeration headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = (String) headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return " - ";
                }
            }
        }
        return " - ";
    }

    private String getResponseBody(final HttpServletResponse response) {
        try {
            String payload = null;
            ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                    wrapper.copyBodyToResponse();
                }
            }
            return null == payload ? " - " : payload;
        } catch (Exception e) {
            log.error("fail response logging : " + e.getMessage(), e);
            return "";
        }

    }
}