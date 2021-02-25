package androidpro.com.wert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import androidpro.com.general.AcaoInativa;

public class InfosAcaoInativaActivity extends AppCompatActivity {

    private DatabaseReference acoesRef =  FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_acao_inativa);
        configureMenu();

        acoesRef.child("AcaoInativa").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = getIntent();
                if (intent.hasExtra("nomeAcao")) {
                    String nomeAcao = intent.getStringExtra("nomeAcao");

                    for (DataSnapshot acao: dataSnapshot.getChildren()) { //Iterando em acao ativa
                        AcaoInativa infoAcao = acao.getValue(AcaoInativa.class);

                        if (infoAcao.getNomeAcao().equals(nomeAcao)) {
                            TextView textNome = findViewById(R.id.text_minhasacoes);
                            TextView textTicker = findViewById(R.id.text_ticker);
                            TextView textSegmento = findViewById(R.id.segmento);
                            TextView textAlertaCompra = findViewById(R.id.alerta_compra);
                            TextView textValorMercado = findViewById(R.id.valor_mercado_val);
                            TextView textNAcoes = findViewById(R.id.n_acoes);

                            textNome.setText(infoAcao.getNomeAcao());
                            textTicker.setText(infoAcao.getTicker());
                            textSegmento.setText(infoAcao.getSegmento());

                            NumberFormat formatter = new DecimalFormat("R$ #,##0.00");
                            String setText = formatter.format(Double.parseDouble(infoAcao.getAlertaCompra()));

                            textAlertaCompra.setText(setText);
                            textValorMercado.setText(infoAcao.getValorMercado());
                            textNAcoes.setText(infoAcao.getnAcoes());

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

    public void configureMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.acoes);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intentHome = new Intent(InfosAcaoInativaActivity.this, LucroActivity.class);
                        startActivity(intentHome);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.acoes:
                        Intent intentAcoes = new Intent(InfosAcaoInativaActivity.this, AcoesActivity.class);
                        startActivity(intentAcoes);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.alertas:
                        Intent intentAlertas = new Intent(InfosAcaoInativaActivity.this, AlertasActivity.class);
                        startActivity(intentAlertas);
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });
    }
}