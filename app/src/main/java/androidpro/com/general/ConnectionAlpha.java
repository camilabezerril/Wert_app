package androidpro.com.general;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import androidpro.com.wert.BuildConfig;

public class ConnectionAlpha {
    private static final String apiKey = BuildConfig.ApiKey;
    private static final String TAG = "AcoesActivity";

    public static JSONObject getJSON(String url){
        String urlAll = url + "&apikey=" + apiKey;
        final JSONObject[] json = {new JSONObject()};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection urlConn;
                    BufferedReader bufferedReader;

                            URL url = new URL(urlAll);
                            urlConn = url.openConnection();
                            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                            StringBuffer stringBuffer = new StringBuffer();
                            String line;
                            while ((line = bufferedReader.readLine()) != null)
                                stringBuffer.append(line);

                            json[0] = new JSONObject(stringBuffer.toString());

                        } catch (Exception ex) {
                            ex.printStackTrace();
                }

            }
        });
        thread.start();

        try{
            thread.join();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return json[0];
    }
}
