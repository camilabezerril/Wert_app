package androidpro.com.general;

import org.json.JSONObject;

public class AcaoData {
    public static JSONObject getOverview(String ticker) {
        String url = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=" + ticker;

        return ConnectionAlpha.getJSON(url);
    }

    public static JSONObject getTimeSeriesDaily(String ticker) {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="
                      + ticker;

        return ConnectionAlpha.getJSON(url);
    }

    public static JSONObject getTimeSeriesWeekly(String ticker){
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol="
                + ticker;

        return ConnectionAlpha.getJSON(url);
    }

    public static JSONObject getTimeSeriesMonthly(String ticker){
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol="
                + ticker;

        return ConnectionAlpha.getJSON(url);
    }
}
