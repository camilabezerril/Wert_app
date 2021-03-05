package androidpro.com.general;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UpdateAcao {
    private static DatabaseReference acoesFirebase;
    private static Map<String, String> weekly;
    private static Map<String, String> monthly;
    private static Map<String, String> meses;

    public static void updateDynamicData (AcaoAtiva acao){
        JSONObject timeSeriesDaily = AcaoData.getTimeSeriesDaily(acao.getTicker());

        try {
            JSONObject dias = timeSeriesDaily.getJSONObject("Time Series (Daily)");
            Iterator<String> diasKeys = dias.keys();

            String key = diasKeys.next();
            acao.setValorAtual(((JSONObject) dias.get(key)).getString("4. close"));

            key = diasKeys.next();
            acao.setValorAnterior(((JSONObject) dias.get(key)).getString("4. close"));

            int qtdComprada = Integer.parseInt(acao.getQtdComprada());
            double valorAtual = Double.parseDouble(acao.getValorAtual());
            double totalInvestido = Double.parseDouble(acao.getTotalInvestido());

            double perdaGanhoR = (qtdComprada * valorAtual - totalInvestido);
            acao.setPerdaGanhoR(String.valueOf(perdaGanhoR));

            double saldoAtual = totalInvestido + perdaGanhoR;
            acao.setSaldoAtual(String.valueOf(saldoAtual));

            double perdaGanhoP = ((saldoAtual * 100) / totalInvestido) - 100;
            acao.setPerdaGanhoP(String.valueOf(perdaGanhoP));

            updateSpark(acao);
            sendToFirebase(acao);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateDynamicData (AcaoInativa acao) {
        JSONObject overview = AcaoData.getOverview(acao.getTicker());
        JSONObject timeSeriesDaily = AcaoData.getTimeSeriesDaily(acao.getTicker());

        try {
            NumberFormat formatter = new DecimalFormat("R$ #,##0.00");

            BigDecimal bd = new BigDecimal(overview.getString("MarketCapitalization"));
            acao.setValorMercado(formatter.format(bd.longValue()));

            formatter = new DecimalFormat("#,##0");
            bd = new BigDecimal(overview.getString("SharesOutstanding"));
            acao.setnAcoes(formatter.format(bd.longValue()));

            JSONObject dias = timeSeriesDaily.getJSONObject("Time Series (Daily)");
            Iterator<String> diasKeys = dias.keys();

            String key = diasKeys.next();
            acao.setValorAtual(((JSONObject) dias.get(key)).getString("4. close"));

            key = diasKeys.next();
            acao.setValorAnterior(((JSONObject) dias.get(key)).getString("4. close"));

            updateSpark(acao);
            sendToFirebase(acao);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateSpark (AcaoAtiva acao) {
        JSONObject timeSeriesDaily = AcaoData.getTimeSeriesDaily(acao.getTicker());
        getInfoSpark(timeSeriesDaily);

        acao.setWeekly(weekly);
        acao.setMonthly(monthly);
        acao.setMeses(meses);
    }

    private static void updateSpark (AcaoInativa acao) {
        JSONObject timeSeriesDaily = AcaoData.getTimeSeriesDaily(acao.getTicker());
        getInfoSpark(timeSeriesDaily);

        acao.setWeekly(weekly);
        acao.setMonthly(monthly);
        acao.setMeses(meses);
    }

    private static void getInfoSpark(JSONObject timeSeriesDaily){
        try {
            JSONObject dias = timeSeriesDaily.getJSONObject("Time Series (Daily)");
            Iterator<String> diasKeys;

            // *********** WEEK ********** //
            String key;
            diasKeys = dias.keys(); //reiniciar iterator
            weekly = new HashMap<>();

            int i = 0;
            while (diasKeys.hasNext() && i != 6) {
                key = diasKeys.next();
                weekly.put(key, ((JSONObject) dias.get(key)).getString("4. close"));
                i++;
            }

            // *********** MONTH ************ //
            diasKeys = dias.keys();
            monthly = new HashMap<>();

            i = 0;
            while (diasKeys.hasNext() && i != 29) {
                key = diasKeys.next();
                monthly.put(key, ((JSONObject) dias.get(key)).getString("4. close"));
                i++;
            }

            // *********** 3 Meses ************ //
            diasKeys = dias.keys();
            meses = new HashMap<>();

            i = 0;
            while (diasKeys.hasNext() && i != 90) {
                key = diasKeys.next();
                meses.put(key, ((JSONObject) dias.get(key)).getString("4. close"));
                i++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void sendToFirebase(AcaoAtiva acaoUpdated){
        acoesFirebase = FirebaseDatabase.getInstance().getReference("AcaoAtiva");

        acoesFirebase.child(acaoUpdated.getUid()).setValue(acaoUpdated);
    }

    private static void sendToFirebase(AcaoInativa acaoUpdated){
        acoesFirebase = FirebaseDatabase.getInstance().getReference().child("AcaoInativa");

        acoesFirebase.child(acaoUpdated.getUid()).setValue(acaoUpdated);
    }
}

