package androidpro.com.wert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidpro.com.general.AcaoAtiva;
import androidpro.com.general.AcaoInativa;

public class CriarAcaoActivity extends AppCompatActivity {

    private final String TAG = "CriarAcaoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_acao_ativa);
        configureMenu();

        SwitchMaterial switchAtividade = findViewById(R.id.switch_atividade);
        switchAtividade.setChecked(true);

        configureCriarAcao();

        switchAtividade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    setContentView(R.layout.activity_criar_acao_inativa);
                } else {
                    setContentView(R.layout.activity_criar_acao_ativa);
                }
                configureMenu();
                configureCriarAcao();
            }
        });
    }

    private void configureCriarAcao(){
        Button btnCadastrar = findViewById(R.id.btn_cadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText inputTicker = findViewById(R.id.input_ticker);
                TextInputEditText inputNomeAcao = findViewById(R.id.nome_acao);
                SwitchMaterial switchPrazo = findViewById(R.id.switch_prazo);
                SwitchMaterial switchAtividade = findViewById(R.id.switch_atividade);

                String ticker = inputTicker.getText().toString();
                String nomeAcao = inputNomeAcao.getText().toString();
                boolean prazo = switchPrazo.isChecked();

                if(switchAtividade.isChecked()){
                    TextInputEditText inputQtdComprada = findViewById(R.id.qtd_comprada);
                    TextInputEditText inputAlertaVenda = findViewById(R.id.alerta_venda);
                    TextInputEditText inputPrecoCompra = findViewById(R.id.preco_compra);

                    String qtdComprada = inputQtdComprada.getText().toString();
                    String alertaVenda = inputAlertaVenda.getText().toString();
                    String precoCompra = inputPrecoCompra.getText().toString();

                    if(isEmpty(inputNomeAcao) || isEmpty(inputTicker) || isEmpty(inputAlertaVenda)
                    || isEmpty(inputPrecoCompra) || isEmpty(inputQtdComprada)){
                        Toast toast = Toast.makeText(CriarAcaoActivity.this, "Há campos vazios!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, 70);
                        toast.show();

                        return;
                    }

                    try {
                        AcaoAtiva novaAcao = new AcaoAtiva(ticker, nomeAcao, precoCompra, prazo, qtdComprada, alertaVenda);

                        DatabaseReference acaoAtivaRef = FirebaseDatabase
                                .getInstance()
                                .getReference("AcaoAtiva");
                        acaoAtivaRef.push().setValue(novaAcao);

                    } catch (RuntimeException e){
                        Toast toast = Toast.makeText(CriarAcaoActivity.this, "Ticker inválido", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, 70);
                        toast.show();

                        return;
                    }

                } else {
                    TextInputEditText inputAlertaCompra = findViewById(R.id.alerta_compra);

                    String alertaCompra = inputAlertaCompra.getText().toString();

                    if(isEmpty(inputNomeAcao) || isEmpty(inputTicker) || isEmpty(inputAlertaCompra)){
                        Toast toast = Toast.makeText(CriarAcaoActivity.this, "Há campos vazios!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, 70);
                        toast.show();

                        return;
                    }

                    try {
                        AcaoInativa novaAcao = new AcaoInativa(ticker, nomeAcao, alertaCompra, prazo);

                        DatabaseReference acaoInativaRef = FirebaseDatabase
                                .getInstance()
                                .getReference("AcaoInativa");
                        acaoInativaRef.push().setValue(novaAcao);

                    } catch (RuntimeException e){
                        Toast toast = Toast.makeText(CriarAcaoActivity.this, "Ticker inválido", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, 70);
                        toast.show();

                        return;
                    }
                }

                Intent criandoPost = new Intent(CriarAcaoActivity.this, AcoesActivity.class);
                startActivity(criandoPost);
                overridePendingTransition(0, 0);
            }
        });
    }

    private boolean isEmpty(EditText getText) {
        return getText.getText().toString().trim().length() <= 0;
    }

    private void configureMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.acoes);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intentHome = new Intent(CriarAcaoActivity.this, LucroActivity.class);
                        startActivity(intentHome);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.acoes:
                        Intent intentAcoes = new Intent(CriarAcaoActivity.this, AcoesActivity.class);
                        startActivity(intentAcoes);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.alertas:
                        Intent intentAlertas = new Intent(CriarAcaoActivity.this, AlertasActivity.class);
                        startActivity(intentAlertas);
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });
    }
}