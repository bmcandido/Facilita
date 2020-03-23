package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
// import java.util.HashMap;
import java.util.Map;

public class Http {

    private URL url;

    public static class Response {
        private int code;
        private String message;
        private byte[] data;

        public Response(final HttpURLConnection httpURLConnection) throws IOException {
            this.code = httpURLConnection.getResponseCode();
            this.message = httpURLConnection.getResponseMessage();

            try {
                this.data = read(httpURLConnection.getInputStream());
            } catch(final Exception exception) {
                exception.printStackTrace();
                this.data = read(httpURLConnection.getErrorStream());
            }
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public byte[] getData() {
            return data;
        }

        private byte[] read(final InputStream inputStream) throws IOException {
            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                final byte[] buffer = new byte[1024];

                int read = 0;
                while ((read = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, read);
                }

                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    public Http(final String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public static void setProxy(final String host, final int port, final String user, final String pass) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", "" + port);
        System.setProperty("http.proxyUser", user);
        System.setProperty("http.proxyPassword", pass);
    }

    public String getUrl() {
        return url.toString();
    }

    public Response post(final Map<String, String> properties, final byte[] post) throws IOException {
        return post(properties, post, "POST");
    }

    public Response put(final Map<String, String> properties, final byte[] put) throws IOException {
        return post(properties, put, "PUT");
    }

    public Response get(final Map<String, String> properties) throws IOException {
        return get(properties, "GET");
    }

    private Response post(final Map<String, String> properties, final byte[] post, final String method) throws IOException {
    	boolean redirect = false;
    	HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    	
    	int status = httpURLConnection.getResponseCode();

        if (status != HttpURLConnection.HTTP_OK) {
    		if (status == HttpURLConnection.HTTP_MOVED_TEMP
    			|| status == HttpURLConnection.HTTP_MOVED_PERM
    				|| status == HttpURLConnection.HTTP_SEE_OTHER)
    		redirect = true;
    	}
        
        if (redirect) {
    		String newUrl = httpURLConnection.getHeaderField("Location");
    		System.out.println("Nova URL: " + newUrl);
    		httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
    	}           
    	
    	httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        httpURLConnection.setRequestMethod(method);
        for (final Map.Entry<String, String> entry : properties.entrySet()) {
            httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        httpURLConnection.setRequestProperty("Content-Length", Integer.toString(post.length));

        httpURLConnection.getOutputStream().write(post);

        return new Response(httpURLConnection);
    }

    private Response get(final Map<String, String> properties, final String method) throws IOException {
    	boolean redirect = false;
    	HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    	
    	int status = httpURLConnection.getResponseCode();
    	
    	System.out.println("Codigo http consulta webservice: " + status);
        
        if (status != HttpURLConnection.HTTP_OK) {
    		if (status == HttpURLConnection.HTTP_MOVED_TEMP
    			|| status == HttpURLConnection.HTTP_MOVED_PERM
    				|| status == HttpURLConnection.HTTP_SEE_OTHER)
    		redirect = true;
    	}
        
        if (redirect) {
    		String newUrl = httpURLConnection.getHeaderField("Location");
    		System.out.println("Nova URL: " + newUrl);
    		httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
    	}        

        System.out.println("*** PASSOU 30");
        httpURLConnection.setDoOutput(false);
        httpURLConnection.setDoInput(true);
        System.out.println("*** PASSOU 31");
        httpURLConnection.setRequestMethod(method);
        System.out.println("*** PASSOU 32");
        for (final Map.Entry<String, String> entry : properties.entrySet()) {
        	System.out.println("*** PASSOU 33");
            httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            System.out.println("*** PASSOU 34");
        }
        System.out.println("*** PASSOU 35");
        return new Response(httpURLConnection);
    }
}