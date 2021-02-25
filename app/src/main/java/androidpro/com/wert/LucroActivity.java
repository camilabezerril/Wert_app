package androidpro.com.wert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidpro.com.general.AcaoAtiva;

public class LucroActivity extends AppCompatActivity {

    private static String TAG = "LucroActivity";
    PieChart pieInv, pieROI;

    private double saldoInvestido;
    private double saldoROI;
    private double emAlta;
    private double emQueda;

    private Map<String, Float> inv = new HashMap<>();
    private Map<String, Float> roi = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucro);

        pieInv = (PieChart) findViewById(R.id.piechart_i);
        pieROI = (PieChart) findViewById(R.id.piechart_s);

        createPies();

        DatabaseReference acoesAtivasRef =  FirebaseDatabase.getInstance().getReference();
        acoesAtivasRef.child("AcaoAtiva").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saldoInvestido = 0;

                for (DataSnapshot acao: dataSnapshot.getChildren()) { //Iterando em acao ativa
                    AcaoAtiva novaAcao = acao.getValue(AcaoAtiva.class);

                    if (inv.containsKey(novaAcao.getSegmento()))
                        inv.put(novaAcao.getSegmento(), inv.get(novaAcao.getSegmento()) + Float.parseFloat(novaAcao.getTotalInvestido()));
                    else
                        inv.put(novaAcao.getSegmento(), Float.parseFloat(novaAcao.getTotalInvestido()));

                    if  (roi.containsKey(novaAcao.getSegmento()))
                        roi.put(novaAcao.getSegmento(), roi.get(novaAcao.getSegmento()) + Float.parseFloat(novaAcao.getSaldoAtual()));
                    else
                        roi.put(novaAcao.getSegmento(), Float.parseFloat(novaAcao.getSaldoAtual()));

                    configurePie(pieInv, inv);
                    configurePie(pieROI, roi);

                    saldoInvestido = saldoInvestido + Double.parseDouble(novaAcao.getTotalInvestido());
                    saldoROI = saldoROI + Double.parseDouble(novaAcao.getSaldoAtual());

                    if (Double.parseDouble(novaAcao.getPerdaGanhoP()) > 0){
                        emAlta = emAlta + Double.parseDouble(novaAcao.getPerdaGanhoR());
                    } else {
                        emQueda = emQueda + Double.parseDouble(novaAcao.getPerdaGanhoR());
                    }
                }

                TextView textSaldoInvestido = findViewById(R.id.text_dinheiroinv);
                TextView textSaldoROI = findViewById(R.id.text_dinheiroroi);
                TextView textLucro = findViewById(R.id.textView3);
                TextView textDinheiroAlta = findViewById(R.id.text_dinheiroemalta);
                TextView textDinheiroEmQueda = findViewById(R.id.text_dinheiroemqueda);
                TextView textLucroP = findViewById(R.id.text_lucro_p);

                NumberFormat formatter = new DecimalFormat("R$ #,##0.00");

                String setText = formatter.format(saldoInvestido);
                textSaldoInvestido.setText(setText);

                setText = formatter.format(saldoROI);
                textSaldoROI.setText(setText);

                double lucro = saldoROI - saldoInvestido;

                if(lucro < 0)
                    textLucro.setTextColor(Color.parseColor("#D41B1B"));
                else
                    textLucro.setTextColor(Color.parseColor("#318954"));

                setText = formatter.format(saldoROI - saldoInvestido);
                textLucro.setText(setText);

                setText = formatter.format(emAlta);
                textDinheiroAlta.setText(setText);

                setText = formatter.format(emQueda);
                textDinheiroEmQueda.setText(setText);

                double lucroP = (saldoROI / saldoInvestido * 100) - 100;

                if (lucroP > 0)
                    formatter = new DecimalFormat("+#,##0.00'%'");
                else
                    formatter = new DecimalFormat("#,##0.00'%'");

                setText = formatter.format(lucroP);
                textLucroP.setText(setText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pieInv.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                pieInv.setAlpha(1);
                pieROI.setAlpha(0.3f);

                pieInv.setElevation(100);
                pieROI.setElevation(0);

                TextView text_title = findViewById(R.id.textView2);
                text_title.setText("Investimentos por Segmento");
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });

        pieROI.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                pieROI.setAlpha(1);
                pieInv.setAlpha(0.3f);

                pieInv.setElevation(0);
                pieROI.setElevation(100);

                TextView text_title = findViewById(R.id.textView2);
                text_title.setText("Saldo (ROI) por Segmento");
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });

        configureMenu();
    }

    public void configureMenu(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        break;
                    case R.id.acoes:
                        Intent intentAcoes = new Intent(LucroActivity.this, AcoesActivity.class);
                        startActivity(intentAcoes);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.alertas:
                        Intent intentAlertas = new Intent(LucroActivity.this, AlertasActivity.class);
                        startActivity(intentAlertas);
                        overridePendingTransition(0, 0);
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });
    }

    public void createPies(){
        pieInv.setRotationEnabled(true);
        pieInv.setHoleRadius(55f);
        pieInv.setHoleColor(Color.parseColor("#00000000"));
        pieInv.setTransparentCircleAlpha(0);
        pieInv.setDrawEntryLabels(true);
        pieInv.getDescription().setEnabled(false);
        pieInv.getLegend().setEnabled(false);

        pieROI.setRotationEnabled(true);
        pieROI.setHoleRadius(55f);
        pieROI.setHoleColor(Color.parseColor("#00000000"));
        pieROI.setTransparentCircleAlpha(0);
        pieROI.setDrawEntryLabels(true);
        pieROI.getDescription().setEnabled(false);
        pieROI.getLegend().setEnabled(false);
    }

    public void configurePie(PieChart pie, Map<String, Float> map){
        //CONDIGURANDO DADOS PARA PIE
        ArrayList<PieEntry> yEntry = new ArrayList<>();
        ArrayList<String> xEntry = new ArrayList<>();

        for (Map.Entry<String, Float> entry : map.entrySet()){
            yEntry.add(new PieEntry(entry.getValue(), entry.getKey()));
            xEntry.add(entry.getKey());
        }

        PieDataSet pieDataSet = new PieDataSet(yEntry, "por Segmento");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(0);

        //CORES DO PIECHART
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#173d1e"));
        colors.add(Color.parseColor("#1e5228"));
        colors.add(Color.parseColor("#256631"));
        colors.add(Color.parseColor("#2d7d3c"));
        colors.add(Color.parseColor("#349146"));
        colors.add(Color.parseColor("#3ba850"));
        colors.add(Color.parseColor("#41ba58"));

        pieDataSet.setColors(colors);

        //ADICIONANDO DADOS NO PIECHART
        PieData pieData = new PieData(pieDataSet);
        pie.setData(pieData);
        pie.invalidate();

        // INFOS NA TELA
        pie.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value selected from chart");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("y: ");
                String qtd = e.toString().substring(pos1 + 3);

                String setor = "";
                for (Map.Entry<String, Float> entry : map.entrySet()){
                    if(entry.getValue() == Float.parseFloat(qtd)){
                        setor = entry.getKey();
                    }
                }

                NumberFormat formatter = new DecimalFormat("R$ #,##0.00");
                qtd = formatter.format(Double.parseDouble(qtd));

                Toast toast = Toast.makeText(LucroActivity.this, "Setor: " + setor + "\n" + qtd, Toast.LENGTH_LONG);
                //posicao para o meu celular (modificar depois metódo de pegar xoffser e yoffset dependendndo da resolução)
                toast.setGravity(Gravity.CENTER, -270, 200);
                toast.show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
}