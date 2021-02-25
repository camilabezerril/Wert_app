package androidpro.com.wert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidpro.com.general.AcaoAtiva;
import androidpro.com.general.AcaoInativa;
import androidpro.com.general.UpdateAcao;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseReference acoesRef =  FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        acoesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot acoesAtiv = dataSnapshot.child("AcaoAtiva");
                DataSnapshot acoesInativ = dataSnapshot.child("AcaoInativa");

                for (DataSnapshot acao : acoesAtiv.getChildren()) { //Iterando em acao ativa
                    AcaoAtiva acaoAtiva = acao.getValue(AcaoAtiva.class);

                    UpdateAcao.updateDynamicData(acaoAtiva);
                }

                for (DataSnapshot acao : acoesInativ.getChildren()) { //Iterando em acao inativa
                    AcaoInativa acaoInativa = acao.getValue(AcaoInativa.class);

                    UpdateAcao.updateDynamicData(acaoInativa);
                }

                mostrarLucroActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void mostrarLucroActivity() {
        Intent intent = new Intent(MainActivity.this, LucroActivity.class);
        startActivity(intent);
        finish();
    }
}