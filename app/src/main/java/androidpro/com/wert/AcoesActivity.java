package androidpro.com.wert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import androidpro.com.general.AcaoAtiva;
import androidpro.com.general.AcaoInativa;

public class AcoesActivity extends AppCompatActivity {

    private static final String TAG = "AcoesActivity";
    private DatabaseReference acoesRef =  FirebaseDatabase.getInstance().getReference();
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acoes);

        ImageButton btnAdicionar = findViewById(R.id.adicionar);
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CriarAcaoActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        configureMenu();

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter();
        rv.setAdapter(postAdapter);
        List<PostAcao> posts = new ArrayList<>();

        acoesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot acoesAtiv = dataSnapshot.child("AcaoAtiva");
                DataSnapshot acoesInativ = dataSnapshot.child("AcaoInativa");

                for (DataSnapshot acao: acoesAtiv.getChildren()) { //Iterando em acao ativa
                    AcaoAtiva acaoAtiva = acao.getValue(AcaoAtiva.class);

                    criarPost(acaoAtiva.getNomeAcao(), true, acaoAtiva.isPrazo(),
                              acaoAtiva.getValorAnterior(), acaoAtiva.getValorAtual(),
                              acaoAtiva.getAlertaVenda(), posts);
                }

                for (DataSnapshot acao: acoesInativ.getChildren()) { //Iterando em acao inativa
                    AcaoInativa acaoInativa = acao.getValue(AcaoInativa.class);

                    criarPost(acaoInativa.getNomeAcao(), false, acaoInativa.isPrazo(),
                            acaoInativa.getValorAnterior(), acaoInativa.getValorAtual(),
                            acaoInativa.getAlertaCompra(), posts);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast toast = Toast.makeText(AcoesActivity.this, "Para excluir uma ação:\naperte e segure", Toast.LENGTH_SHORT);
        //posicao para o meu celular (modificar depois metódo de pegar xoffser e yoffset dependendndo da resolução)
        toast.setGravity(Gravity.BOTTOM, 0, 70);
        toast.show();
    }

    private void criarPost(String nomeAcao, boolean atividade, boolean prazo, String valorAnterior,
                           String valorAtual, String alerta, List<PostAcao> posts){

        PostAcao post = new PostAcao();

        String prazoStr;
        String atividadeStr;

        if (prazo)
            prazoStr = "Longo prazo";
        else
            prazoStr = "Curto prazo";

        if (atividade)
            atividadeStr = "Ativa";
        else
            atividadeStr = "Inativa";

        post.setTextViewAcao(nomeAcao);
        post.setTextViewAtividade(atividadeStr);
        post.setTextViewPrazo(prazoStr);
        post.setTextViewValor1(valorAtual);
        post.setTextViewValor2(valorAnterior);
        post.setTextViewValorAnterior("Valor Anterior: ");
        post.setTextViewValorAtual("Valor Atual: ");
        post.setImageViewWarning(0);

        if ((Double.parseDouble(valorAtual) >= Double.parseDouble(alerta)) && atividade)
            post.setImageViewWarning(R.drawable.warning);
        else if (Double.parseDouble(valorAtual) <= Double.parseDouble(alerta) && !atividade)
            post.setImageViewWarning(R.drawable.warning);


        posts.add(post);
        postAdapter.setPosts(posts);
        postAdapter.notifyDataSetChanged();
    }

    private void configureMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.acoes);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intentHome = new Intent(AcoesActivity.this, LucroActivity.class);
                        startActivity(intentHome);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.acoes:
                        break;

                    case R.id.alertas:
                        Intent intentAlertas = new Intent(AcoesActivity.this, AlertasActivity.class);
                        startActivity(intentAlertas);
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });
    }

    private static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewAcao;
        private final TextView textViewPrazo;
        private final TextView textViewAtividade;
        private final TextView textViewValorAtual;
        private final TextView textViewValorAnterior;
        private final TextView textViewValor1;
        private final TextView textViewValor2;
        private final ImageView imageViewWarning;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAcao = itemView.findViewById(R.id.nome_alerta);
            textViewPrazo = itemView.findViewById(R.id.prazo);
            textViewAtividade = itemView.findViewById(R.id.atividade);
            textViewValorAtual = itemView.findViewById(R.id.text_alerta);
            textViewValorAnterior = itemView.findViewById(R.id.valor_anterior);
            textViewValor1 = itemView.findViewById(R.id.valor1);
            textViewValor2 = itemView.findViewById(R.id.valor2);
            imageViewWarning= itemView.findViewById(R.id.warning);
        }

        void bind(PostAcao post){
            textViewAcao.setText(post.getTextViewAcao());
            textViewPrazo.setText(post.getTextViewPrazo());
            textViewAtividade.setText(post.getTextViewAtividade());
            textViewValorAtual.setText(post.getTextViewValorAtual());
            textViewValorAnterior.setText(post.getTextViewValorAnterior());

            if (Double.parseDouble(post.getTextViewValor1()) < Double.parseDouble(post.getTextViewValor2())) {
                textViewValor1.setTextColor(Color.parseColor("#D41B1B"));
            } else {
                textViewValor1.setTextColor(Color.parseColor("#318954"));
            }

            NumberFormat formatter = new DecimalFormat("R$ #,##0.00");

            String setText = formatter.format(Double.parseDouble(post.getTextViewValor1()));
            textViewValor1.setText(setText);

            setText = formatter.format(Double.parseDouble(post.getTextViewValor2()));
            textViewValor2.setText(setText);

            if (post.getTextViewAtividade().equals("Ativa"))
                textViewAtividade.setTextColor(Color.parseColor("#3BC96F"));


            imageViewWarning.setImageResource(post.getImageViewWarning());
        }
    }

    private class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

        private List<PostAcao> posts = new ArrayList<>();

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.cell_acoes, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            PostAcao post = posts.get(position);
            holder.bind(post);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String atividade = post.getTextViewAtividade();

                    if(atividade.equals("Inativa")){
                        Intent infoAcaoInativa = new Intent(AcoesActivity.this, InfosAcaoInativaActivity.class);
                        infoAcaoInativa.putExtra("nomeAcao", post.getTextViewAcao());
                        startActivity(infoAcaoInativa);
                    } else {
                        Intent infoAcaoAtiva = new Intent(AcoesActivity.this, InfosAcaoActivity.class);
                        infoAcaoAtiva.putExtra("nomeAcao", post.getTextViewAcao());
                        startActivity(infoAcaoAtiva);
                    }
                    overridePendingTransition(0, 0);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String atividade = post.getTextViewAtividade();

                    new AlertDialog.Builder(AcoesActivity.this)
                            .setTitle("Apagar ação")
                            .setMessage("Deseja apagar essa ação?")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (atividade.equals("Inativa")) {
                                        acoesRef.child("AcaoInativa").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot acao : dataSnapshot.getChildren()) { //Iterando em acao ativa
                                                    AcaoInativa infoAcao = acao.getValue(AcaoInativa.class);

                                                    if (infoAcao.getNomeAcao().equals(post.getTextViewAcao())) {
                                                        acoesRef.child("AcaoInativa").child(acao.getKey()).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        acoesRef.child("AcaoAtiva").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot acao : dataSnapshot.getChildren()) { //Iterando em acao ativa
                                                    AcaoAtiva infoAcao = acao.getValue(AcaoAtiva.class);

                                                    if (infoAcao.getNomeAcao().equals(post.getTextViewAcao())) {
                                                        acoesRef.child("AcaoAtiva").child(acao.getKey()).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    Intent refresh = new Intent(AcoesActivity.this, AcoesActivity.class);
                                    startActivity(refresh);
                                    overridePendingTransition(0, 0);

                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public void setPosts(List<PostAcao> posts) {
            this.posts = posts;
        }
    }

    private class PostAcao {
        private String textViewAcao;
        private String textViewPrazo;
        private String textViewAtividade;
        private String textViewValorAtual;
        private String textViewValorAnterior;
        private String textViewValor1;
        private String textViewValor2;
        private int imageViewWarning;

        public String getTextViewAcao() {
            return textViewAcao;
        }

        public void setTextViewAcao(String textViewAcao) {
            this.textViewAcao = textViewAcao;
        }

        public String getTextViewPrazo() {
            return textViewPrazo;
        }

        public void setTextViewPrazo(String textViewPrazo) {
            this.textViewPrazo = textViewPrazo;
        }

        public String getTextViewAtividade() {
            return textViewAtividade;
        }

        public void setTextViewAtividade(String textViewAtividade) {
            this.textViewAtividade = textViewAtividade;
        }

        public String getTextViewValorAtual() {
            return textViewValorAtual;
        }

        public void setTextViewValorAtual(String textViewValorAtual) {
            this.textViewValorAtual = textViewValorAtual;
        }

        public String getTextViewValorAnterior() {
            return textViewValorAnterior;
        }

        public void setTextViewValorAnterior(String textViewValorAnterior) {
            this.textViewValorAnterior = textViewValorAnterior;
        }

        public String getTextViewValor1() {
            return textViewValor1;
        }

        public void setTextViewValor1(String textViewValor1) {
            this.textViewValor1 = textViewValor1;
        }

        public String getTextViewValor2() {
            return textViewValor2;
        }

        public void setTextViewValor2(String textViewValor2) {
            this.textViewValor2 = textViewValor2;
        }

        public int getImageViewWarning() {
            return imageViewWarning;
        }

        public void setImageViewWarning(int imageViewWarning) {
            this.imageViewWarning = imageViewWarning;
        }
    }
}