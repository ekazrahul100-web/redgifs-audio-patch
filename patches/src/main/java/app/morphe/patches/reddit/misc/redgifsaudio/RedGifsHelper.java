package app.morphe.patches.reddit.misc.redgifsaudio;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.os.StrictMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedGifsHelper {
    public static String fetchAudioUrl(String fallbackUrl) {
        if (fallbackUrl == null || !fallbackUrl.toLowerCase().contains("redgifs.com")) {
            return null;
        }

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Extract ID
            String[] parts = fallbackUrl.split("/");
            String lastPart = parts[parts.length - 1];
            String id = lastPart.split("\\.")[0].split("-")[0];

            if (id.isEmpty() || id.contains("redgifs.com")) return null;

            // 1. Fetch Temporary Token
            URL authUrl = new URL("https://api.redgifs.com/v2/auth/temporary");
            HttpURLConnection authConn = (HttpURLConnection) authUrl.openConnection();
            authConn.setRequestMethod("GET");
            authConn.setConnectTimeout(3000);
            authConn.setReadTimeout(3000);

            String token = null;
            if (authConn.getResponseCode() == 200) {
                InputStream is = authConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder responseStr = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseStr.append(line);
                }
                reader.close();

                // Parse token using regex
                Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(responseStr.toString());
                if (m.find()) {
                    token = m.group(1);
                }
            }

            if (token == null) return null;

            // 2. Fetch GIF Metadata
            URL gifUrl = new URL("https://api.redgifs.com/v2/gifs/" + id);
            HttpURLConnection gifConn = (HttpURLConnection) gifUrl.openConnection();
            gifConn.setRequestMethod("GET");
            gifConn.setRequestProperty("Authorization", "Bearer " + token);
            gifConn.setConnectTimeout(3000);
            gifConn.setReadTimeout(3000);

            if (gifConn.getResponseCode() == 200) {
                InputStream is = gifConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder responseStr = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseStr.append(line);
                }
                reader.close();

                Matcher m = Pattern.compile("\"hd\"\\s*:\\s*\"([^\"]+)\"").matcher(responseStr.toString());
                if (m.find()) {
                    String hdUrl = m.group(1);
                    if (hdUrl != null && !hdUrl.isEmpty()) {
                        return hdUrl;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
