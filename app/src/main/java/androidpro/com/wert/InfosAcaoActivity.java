package androidpro.com.wert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import androidpro.com.general.AcaoAtiva;

public class InfosAcaoActivity extends AppCompatActivity {

    private DatabaseReference acoesRef =  FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_acao);
        configureMenu();

        acoesRef.child("AcaoAtiva").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = getIntent();
                if (intent.hasExtra("nomeAcao")) {
                    String nomeAcao = intent.getStringExtra("nomeAcao");

                    for (DataSnapshot acao : dataSnapshot.getChildren()) { //Iterando em acao ativa
                        AcaoAtiva infoAcao = acao.getValue(AcaoAtiva.class);

                        if (infoAcao.getNomeAcao().equals(nomeAcao)) {
                            TextView textNome = findViewById(R.id.text_nomeAcao_val);
                            TextView textTicker = findViewById(R.id.text_ticker_val);
                            TextView textPerdaGanhoP = findViewById(R.id.perda_ganho_p_val);
                            TextView textPerdaGanhoRS = findViewById(R.id.perda_ganho_rs_val);
                            TextView textSaldoAtual = findViewById(R.id.saldo_atual_val);
                            TextView textQtdComprada = findViewById(R.id.qtd_comprada_val);
                            TextView textPrecoCompra = findViewById(R.id.preco_compra_val);
                            TextView textDataCompra = findViewById(R.id.data_compra_val);
                            TextView textAlertaVenda = findViewById(R.id.alerta_venda_val);
                            TextView textTotalInvestido = findViewById(R.id.total_investido_val);

                            textNome.setText(infoAcao.getNomeAcao());
                            textTicker.setText(infoAcao.getTicker());
                            textQtdComprada.setText(infoAcao.getQtdComprada());
                            textDataCompra.setText(infoAcao.getDataCompra());

                            NumberFormat formatter = new DecimalFormat("R$ #,##0.00");

                            String df = formatter.format(Double.parseDouble(infoAcao.getTotalInvestido()));
                            textTotalInvestido.setText(df);
                            df = formatter.format(Double.parseDouble(infoAcao.getAlertaVenda()));
                            textAlertaVenda.setText(df);
                            df = formatter.format(Double.parseDouble(infoAcao.getPrecoCompra()));
                            textPrecoCompra.setText(df);
                            df = formatter.format(Double.parseDouble(infoAcao.getSaldoAtual()));
                            textSaldoAtual.setText(df);

                            double pgP = Double.parseDouble(infoAcao.getPerdaGanhoP());
                            String setText;
                            if (pgP <= 0) {
                                formatter = new DecimalFormat("#,##0.00'%'");
                                setText = formatter.format(Double.parseDouble(infoAcao.getPerdaGanhoP()));
                                textPerdaGanhoP.setText(setText);
                                textPerdaGanhoP.setTextColor(Color.parseColor("#D41B1B"));

                                formatter = new DecimalFormat("R$ #,##0.00");
                                setText = formatter.format(Double.parseDouble(infoAcao.getPerdaGanhoR()));
                                textPerdaGanhoRS.setText(setText);
                                textPerdaGanhoRS.setTextColor(Color.parseColor("#D41B1B"));
                            } else {
                                formatter = new DecimalFormat("+#,##0.00'%'");
                                setText = formatter.format(Double.parseDouble(infoAcao.getPerdaGanhoP()));
                                textPerdaGanhoP.setText(setText);
                                textPerdaGanhoP.setTextColor(Color.parseColor("#318954"));

                                formatter = new DecimalFormat("+R$ #,##0.00");
                                setText = formatter.format(Double.parseDouble(infoAcao.getPerdaGanhoR()));
                                textPerdaGanhoRS.setText(setText);
                                textPerdaGanhoRS.setTextColor(Color.parseColor("#318954"));
                            }

                            SparkView sparkViewWeekly = (SparkView) findViewById(R.id.sparkWeek);
                            sparkViewWeekly.setAdapter(new AdapterSpark(infoAcao.getWeekly()));

                            SparkView sparkViewMonthly = (SparkView) findViewById(R.id.sparkMonth);
                            sparkViewMonthly.setAdapter(new AdapterSpark(infoAcao.getMonthly()));

                            SparkView sparkViewYear = (SparkView) findViewById(R.id.sparkMeses);
                            sparkViewYear.setAdapter(new AdapterSpark(infoAcao.getMeses()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void configureMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.acoes);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intentHome = new Intent(InfosAcaoActivity.this, LucroActivity.class);
                        startActivity(intentHome);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.acoes:
                        Intent intentAcoes = new Intent(InfosAcaoActivity.this, AcoesActivity.class);
                        startActivity(intentAcoes);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.alertas:
                        Intent intentAlertas = new Intent(InfosAcaoActivity.this, AlertasActivity.class);
                        startActivity(intentAlertas);
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });
    }

    public class AdapterSpark extends SparkAdapter {
        private float[] yData;

        public AdapterSpark(Map<String, String> dados) {
            this.yData = new float[dados.size()];

            int i = 0;
            for (Map.Entry<String, String> entry : dados.entrySet()) {
                this.yData[i++] = Float.parseFloat(entry.getValue());
            }
        }

        @Override
        public int getCount() {
            return yData.length;
        }

        @Override
        public Object getItem(int index) {
            return yData[index];
        }

        @Override
        public float getY(int index) {
            return yData[index];
        }
    }
}