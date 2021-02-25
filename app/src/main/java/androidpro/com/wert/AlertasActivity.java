package androidpro.com.wert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidpro.com.general.AcaoAtiva;
import androidpro.com.general.AcaoInativa;

public class AlertasActivity extends AppCompatActivity {

    private DatabaseReference acoesRef =  FirebaseDatabase.getInstance().getReference();
    private AlertaAdapter alertaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas);
        configureMenu();

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        alertaAdapter = new AlertaAdapter();
        rv.setAdapter(alertaAdapter);
        List<Alerta> alertas = new ArrayList<>();

        acoesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot acoesAtiv = dataSnapshot.child("AcaoAtiva");
                DataSnapshot acoesInativ = dataSnapshot.child("AcaoInativa");

                for (DataSnapshot acao : acoesAtiv.getChildren()) { //Iterando em acao ativa
                    AcaoAtiva infoAcao = acao.getValue(AcaoAtiva.class);

                    if (Double.parseDouble(infoAcao.getValorAtual()) >= Double.parseDouble(infoAcao.getAlertaVenda()))
                        criarAlerta(infoAcao.getNomeAcao(), "AcaoAtiva", alertas);
                }

                for (DataSnapshot acao : acoesInativ.getChildren()) { //Iterando em acao inativa
                    AcaoInativa infoAcao = acao.getValue(AcaoInativa.class);

                    if (Double.parseDouble(infoAcao.getValorAtual()) <= Double.parseDouble(infoAcao.getAlertaCompra()))
                        criarAlerta(infoAcao.getNomeAcao(), "AcaoInativa", alertas);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void criarAlerta(String nomeAcao, String tipo, List<Alerta> alertas){
        Alerta alerta = new Alerta();

        alerta.setTextViewAlertaNome(nomeAcao);

        if(tipo.equals("AcaoAtiva"))
            alerta.setTextViewAlerta(nomeAcao + " está em constante queda, venda suas ações!");
        else
            alerta.setTextViewAlerta(nomeAcao + " está em baixa, compre ações!");

        alertas.add(alerta);
        alertaAdapter.setAlertas(alertas);
        alertaAdapter.notifyDataSetChanged();
    }

    private void configureMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.alertas);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intentHome = new Intent(AlertasActivity.this, LucroActivity.class);
                        startActivity(intentHome);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.acoes:
                        Intent intentAcoes = new Intent(AlertasActivity.this, AcoesActivity.class);
                        startActivity(intentAcoes);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.alertas:
                        break;
                }
                return false;
            }
        });
    }

    private class AlertaAdapter extends RecyclerView.Adapter<AlertaViewHolder>{

        private List<Alerta> alertas = new ArrayList<>();

        @NonNull
        @Override
        public AlertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.cell_alertas, parent, false);
            return new AlertaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlertaViewHolder holder, int position) {
           Alerta alerta = alertas.get(position);
            holder.bind(alerta);

        }

        @Override
        public int getItemCount() {
            return alertas.size();
        }

        public void setAlertas(List<Alerta> alertas) {
            this.alertas = alertas;
        }
    }

    private static class AlertaViewHolder extends RecyclerView.ViewHolder{

        private final TextView textAlertaNome;
        private final TextView textAlerta;

        public AlertaViewHolder(@NonNull View itemView) {
            super(itemView);

            textAlertaNome = itemView.findViewById(R.id.nome_alerta_cell);
            textAlerta = itemView.findViewById(R.id.text_alerta);
        }

        void bind (Alerta alerta){
            textAlertaNome.setText(alerta.getTextViewAlertaNome());
            textAlerta.setText(alerta.getTextViewAlerta());
        }
    }

    private class Alerta{
        private String textViewAlertaNome;
        private String TextViewAlerta;

        public String getTextViewAlertaNome() {
            return textViewAlertaNome;
        }

        public void setTextViewAlertaNome(String textViewAlertaNome) {
            this.textViewAlertaNome = textViewAlertaNome;
        }

        public String getTextViewAlerta() {
            return TextViewAlerta;
        }

        public void setTextViewAlerta(String textViewAlerta) {
            TextViewAlerta = textViewAlerta;
        }
    }
}