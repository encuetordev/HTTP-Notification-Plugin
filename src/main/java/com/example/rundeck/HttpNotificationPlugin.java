package com.example.rundeck;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyScope;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP Notification Plugin for Rundeck.
 * This plugin sends HTTP notifications based on Rundeck events.
 */
@Plugin(service = "Notification", name = "http-notification")
@PluginDescription(title = "HTTP Notification Plugin", description = "A plugin for sending HTTP notifications")
public class HttpNotificationPlugin implements NotificationPlugin {

    /**
     * The URL to send the HTTP request to.
     */
    @PluginProperty(title = "URL", description = "The URL to send the HTTP request to", required = true)
    private String url;

    /**
     * The HTTP method to use (GET, POST, PUT, DELETE).
     */
    @PluginProperty(title = "HTTP Method", description = "The HTTP method to use", required = true, defaultValue = "POST")
    private String httpMethod;

    /**
     * The content type of the request body.
     */
    @PluginProperty(title = "Content Type", description = "The content type of the request body")
    private String contentType;

    /**
     * The body of the HTTP request.
     */
    @PluginProperty(title = "Request Body", description = "The body of the HTTP request", scope = PropertyScope.Instance)
    private String requestBody;

    /**
     * The HTTP client used to send requests.
     */
    private HttpClient httpClient;

    /**
     * Sends a notification based on the trigger and execution data.
     * It creates an HTTP request based on the configured URL and HTTP method,
     * sends the request using the HTTP client, and returns true if the response status
     * code indicates success (2xx), or false otherwise.
     *
     * @param trigger       The trigger that caused the notification.
     * @param executionData The data related to the execution.
     * @param config        The configuration properties.
     * @return true if the notification was successfully sent, false otherwise.
     * @throws IllegalArgumentException if the URL or HTTP method is invalid.
     */
    @Override
    public boolean postNotification(String trigger, Map executionData, Map config) {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }

        if (!isValidUrl(url)) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }

        if (!isValidHttpMethod(httpMethod)) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }

        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(createHttpRequest())) {
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode >= 200 && statusCode < 300;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates the appropriate HTTP request based on the configured HTTP method.
     * This method uses the configured URL and HTTP method to create and return
     * an HTTP request object.
     *
     * @return The HTTP request.
     * @throws IOException if there is an error creating the request.
     * @throws IllegalArgumentException if the HTTP method is unsupported.
     */
    private HttpUriRequest createHttpRequest() throws IOException {
        switch (httpMethod.toUpperCase()) {
            case "GET":
                return new HttpGet(url);
            case "POST":
                HttpPost postRequest = new HttpPost(url);
                setEntityAndContentType(postRequest);
                return postRequest;
            case "PUT":
                HttpPut putRequest = new HttpPut(url);
                setEntityAndContentType(putRequest);
                return putRequest;
            case "DELETE":
                return new HttpDelete(url);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }
    }

    /**
     * Sets the entity and content type for HTTP requests that can include a body (POST, PUT).
     * This method sets the request body and content type header if they are provided.
     *
     * @param request The HTTP request to set the entity and content type for.
     * @throws IOException if there is an error setting the entity or content type.
     */
    private void setEntityAndContentType(HttpEntityEnclosingRequestBase request) throws IOException {
        if (requestBody != null && !requestBody.isEmpty()) {
            request.setEntity(new StringEntity(requestBody));
            if (contentType != null && !contentType.isEmpty()) {
                request.setHeader("Content-Type", contentType);
            }
        }
    }

    /**
     * Validates the provided URL to ensure it is not null or empty.
     *
     * @param url The URL to validate.
     * @return true if the URL is valid, false otherwise.
     */
    private boolean isValidUrl(String url) {
        return url != null && !url.trim().isEmpty();
    }

    /**
     * Validates the provided HTTP method to ensure it is one of the supported methods
     * (GET, POST, PUT, DELETE).
     *
     * @param httpMethod The HTTP method to validate.
     * @return true if the HTTP method is supported, false otherwise.
     */
    private boolean isValidHttpMethod(String httpMethod) {
        return httpMethod != null && (httpMethod.equalsIgnoreCase("GET") ||
                httpMethod.equalsIgnoreCase("POST") ||
                httpMethod.equalsIgnoreCase("PUT") ||
                httpMethod.equalsIgnoreCase("DELETE"));
    }

    /**
     * Sets the URL to send the HTTP request to.
     *
     * @param url The URL to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the HTTP method to use (GET, POST, PUT, DELETE).
     *
     * @param httpMethod The HTTP method to set.
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * Sets the content type of the request body.
     *
     * @param contentType The content type to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the body of the HTTP request.
     *
     * @param requestBody The body of the request to set.
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * Sets the HTTP client used to send requests.
     *
     * @param httpClient The HTTP client to set.
     */
    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
