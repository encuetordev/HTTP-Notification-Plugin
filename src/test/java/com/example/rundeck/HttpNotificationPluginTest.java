package com.example.rundeck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the HttpNotificationPlugin class.
 */
class HttpNotificationPluginTest {

    private HttpNotificationPlugin plugin;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private StatusLine statusLine;

    /**
     * Sets up the test environment before each test.
     * Initializes mocks and configures basic parameters for HttpNotificationPlugin.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes mocks
        plugin = new HttpNotificationPlugin();
        plugin.setUrl("http://example.com");
        plugin.setHttpMethod("POST");
        plugin.setHttpClient(httpClient);
    }

    /**
     * Tests the HttpNotificationPlugin postNotification(String, HashMap, HashMap)
     * method when the server responds with an HTTP 200 status (success).
     *
     * @throws IOException if an error occurs during the simulated HTTP communication
     */
    @Test
    void testPostNotificationSuccess() throws IOException {
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);

        boolean result = plugin.postNotification("trigger", new HashMap<>(), new HashMap<>());

        assertTrue(result);
    }

    /**
     * Tests the HttpNotificationPlugin postNotification(String, HashMap, HashMap)
     * method when the server responds with an HTTP 500 status (failure).
     *
     * @throws IOException if an error occurs during the simulated HTTP communication
     */
    @Test
    void testPostNotificationFailure() throws IOException {
        when(statusLine.getStatusCode()).thenReturn(500);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);

        // Inject the mock HttpClient into the plugin || Reinjects the mock to ensure it is used in this test
        plugin.setHttpClient(httpClient);

        boolean result = plugin.postNotification("trigger", new HashMap<>(), new HashMap<>());

        assertFalse(result);
    }

    /**
     * Tests the HttpNotificationPlugin postNotification(String, HashMap, HashMap)
     * method when an invalid URL is set.
     *
     * This test ensures that an IllegalArgumentException is thrown when the URL is null.
     */

    @Test
    void testPostNotificationInvalidUrl() {
        plugin.setUrl(null);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            plugin.postNotification("trigger", new HashMap<>(), new HashMap<>());
        });
        assertEquals("Invalid URL: null", thrown.getMessage());
    }

    /**
     * Tests the HttpNotificationPlugin postNotification(String, HashMap, HashMap)
     * method when an unsupported HTTP method is set.
     *
     * This test ensures that an IllegalArgumentException is thrown when the HTTP method is invalid.
     */

    @Test
    void testPostNotificationUnsupportedHttpMethod() {
        plugin.setHttpMethod("INVALID_METHOD");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            plugin.postNotification("trigger", new HashMap<>(), new HashMap<>());
        });
        assertEquals("Unsupported HTTP method: INVALID_METHOD", thrown.getMessage());
    }
}
