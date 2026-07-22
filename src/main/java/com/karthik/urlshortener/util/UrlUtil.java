package com.karthik.urlshortener.util;

import java.net.MalformedURLException;
import java.net.URI;

public final class UrlUtil {

    private UrlUtil() {
    }

    /**
     * Returns protocol://host[:port]
     */
    public static String getBaseUrl(String url) throws MalformedURLException {

        try {
            URI uri = URI.create(url);

            String protocol = uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();

            if (port == -1) {
                return protocol + "://" + host;
            }

            return protocol + "://" + host + ":" + port;

        } catch (IllegalArgumentException e) {
            throw new MalformedURLException("Invalid URL: " + url);
        }
    }
}