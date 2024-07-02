package ob1lab.blocklimiter;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Requests {
    public static void post(String url, String data) {
        try {
            URL address = new URL(url);
            HttpURLConnection http = getHttpURLConnection(data, address);
            http.connect();
        } catch (IOException urlException) {
            urlException.printStackTrace();
        }
    }

    @NotNull
    private static HttpURLConnection getHttpURLConnection(String data, URL address) throws IOException {
        URLConnection con = address.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setDoInput(true);
        byte[] out = data.getBytes(StandardCharsets.UTF_8);
        http.setFixedLengthStreamingMode(out.length);
        http.setRequestProperty("user-agent", "Mozilla/5.0");
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
        return http;
    }
}
