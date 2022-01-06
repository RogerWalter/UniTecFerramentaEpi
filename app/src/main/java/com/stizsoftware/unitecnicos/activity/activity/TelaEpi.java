package com.stizsoftware.unitecnicos.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterEpi;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterFerramenta;
import com.stizsoftware.unitecnicos.activity.auxiliar.RecyclerItemClickListener;
import com.stizsoftware.unitecnicos.activity.model.Epi;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Tecnico;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TelaEpi extends AppCompatActivity {

    DatabaseReference firebaseBanco = FirebaseDatabase.getInstance().getReference();
    DatabaseReference episDB;
    public String filialLogada;

    private ImageView semDados;
    private ProgressBar progressBar;
    private EditText pesquisarEpi;
    private Button botaoTecnico, botaoFerramenta;
    private FloatingActionButton botaoAddEpi;
    private RecyclerView recyclerView;
    private List<Epi> listaEpis = new ArrayList<>();
    private ArrayList<Epi> listaFiltrada = new ArrayList<>();
    public int idEpi = 0;

    private AdapterEpi adapter = new AdapterEpi(listaEpis);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_epi);
        getSupportActionBar().hide();

        //aqui recuperamos a filial que o usuário utilizou para se logar
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailCompleto = user.getEmail();
        filialLogada = emailCompleto.substring(0,4);

        episDB = firebaseBanco.child(filialLogada).child("epis");

        botaoTecnico = findViewById(R.id.btTecEpi);
        botaoFerramenta = findViewById(R.id.btFerramentaEpi);
        botaoAddEpi = findViewById(R.id.btAddEpi);
        pesquisarEpi = findViewById(R.id.editTextPesquisaEpi);
        recyclerView = findViewById(R.id.recyclerViewEpi);
        semDados = findViewById(R.id.imageViewSemDadosTelaEpi);
        progressBar = findViewById(R.id.progressBarTelaEpi);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);
        recuperaEpis();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                String barraPesquisa = "";
                                barraPesquisa = pesquisarEpi.getText().toString();
                                Epi epi = new Epi();
                                if(barraPesquisa.isEmpty())
                                {
                                    epi = listaEpis.get(position);
                                }
                                else
                                {
                                    epi = listaFiltrada.get(position);
                                }
                                gerarDialogoCustomizadoEditarOuRemoverEpi(epi.getId(), epi.getDescricao(),epi.getOutros());
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        })
        );

        botaoAddEpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarDialogoCustomizadoAddEpi();
            }
        });

        botaoTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaEpi.this, TelaTecnico.class);
                startActivity(intent);
                Animatoo.animateSlideRight(TelaEpi.this);
            }
        });

        botaoFerramenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaEpi.this, TelaFerramenta.class);
                startActivity(intent);
                Animatoo.animateSlideRight(TelaEpi.this);
            }
        });
        criaListenerParaIdEpi();
        pesquisarEpi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
    }
    private void filter(String text)
    {
        listaFiltrada.clear();
        for(Epi item : listaEpis)
        {
            if(item.getDescricao().toLowerCase().contains(text.toLowerCase()))
            {
                listaFiltrada.add(item);
            }
        }
        adapter.listaComFiltro(listaFiltrada);
        if(!text.isEmpty() && listaFiltrada.size() == 0)
        {
            semDados.setVisibility(View.VISIBLE);
        }
        else
        {
            semDados.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(TelaEpi.this);
    }

    String nomeInformado;
    String outrosInformado;

    public void gerarDialogoCustomizadoAddEpi()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_add_epi);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoAddEpi);
        TextView textoNome = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoAddEpi);
        TextView textoOutros = (TextView) dialog.findViewById(R.id.textViewOutrosDialogoAddEpi);
        EditText entradaNome = (EditText) dialog.findViewById(R.id.editTextDescEpiAddDialogo);
        EditText entradaOutros = (EditText) dialog.findViewById(R.id.editTextOutrosEpiAddDialogo);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoAddEpi);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btSalvarDialogoAddEpi);

        // if button is clicked, close the custom dialog
        botaoCancelarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botaoSalvarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomeInformado = entradaNome.getText().toString().toUpperCase();
                outrosInformado = entradaOutros.getText().toString().toUpperCase();
                if(nomeInformado.isEmpty())
                {
                    Toast.makeText(TelaEpi.this, "Não foi informada uma descrição válida", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    if(outrosInformado.isEmpty())
                    {
                        outrosInformado="VAZIO";
                    }
                    DatabaseReference episDB = firebaseBanco.child(filialLogada).child("epis");
                    Epi novoEpi = new Epi(idEpi, nomeInformado, 0,outrosInformado);
                    episDB.child(String.valueOf(idEpi)).setValue(novoEpi);
                    criaListenerParaIdEpi();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void gerarDialogoCustomizadoEditarOuRemoverEpi(int id, String nomeAntigo, String outrosAntigo)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_editar_ou_remover_epi);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloEditarOuRemoverEpi);
        TextView textoNomeAntigo = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoEditarOuRemoverEpi);
        TextView textoOutrosAntigo = (TextView) dialog.findViewById(R.id.textViewDescricaoOutrosDialogoEditarOuRemoverEpi);
        TextView textoNovoNome = (TextView) dialog.findViewById(R.id.textNovoNomeEditarOuRemoverEpi);
        EditText entradaNovoNome = (EditText) dialog.findViewById(R.id.editTextNovoNomeEditarOuRemoverEpi);
        TextView textoNovoOutros = (TextView) dialog.findViewById(R.id.textNovoOutrosEditarOuRemoverEpi);
        EditText entradaNovoOutros = (EditText) dialog.findViewById(R.id.editTextNovoOutrosEditarOuRemoverEpi);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoEditarOuRemoverEpi);
        Button botaoRemoverDialogo = (Button) dialog.findViewById(R.id.btRemoverDialogoEditarOuRemoverEpi);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btConfirmarEditarOuRemoverEpi);


        textoNomeAntigo.setText(nomeAntigo.toString());
        textoOutrosAntigo.setText("(" + outrosAntigo.toString() + ")");

        // if button is clicked, close the custom dialog
        botaoCancelarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botaoSalvarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference episDB = firebaseBanco.child(filialLogada).child("epis");
                String textoNovoNome = entradaNovoNome.getText().toString().toUpperCase();
                String textoNovoOutros = entradaNovoOutros.getText().toString().toUpperCase();

                if(textoNovoNome.isEmpty() && textoNovoOutros.isEmpty())
                {
                    Toast.makeText(TelaEpi.this, "Não foram informados novos dados para serem salvos", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    if(!textoNovoNome.isEmpty() && textoNovoOutros.isEmpty())
                    {
                        //atualizamos o nome e utilizamos o mesmo "Outros"
                        episDB.child(String.valueOf(id)).child("descricao").setValue(entradaNovoNome.getText().toString());

                    }
                    else
                    {
                        if(textoNovoNome.isEmpty() && !textoNovoOutros.isEmpty())
                        {
                            //atualizamos o "Outros" e mantemos o mesmo nome
                            episDB.child(String.valueOf(id)).child("outros").setValue(entradaNovoOutros.getText().toString());
                        }
                        else
                        {
                            //atualizamos o nome e o outros
                            episDB.child(String.valueOf(id)).child("descricao").setValue(entradaNovoNome.getText().toString());
                            episDB.child(String.valueOf(id)).child("outros").setValue(entradaNovoOutros.getText().toString());
                        }
                    }
                }
                criaListenerParaIdEpi();
                adapter.notifyDataSetChanged();
                pesquisarEpi.setText("");
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }
        });

        botaoRemoverDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //exclusão do item
                //(na verdade não vamos excluir, mas sim alterar seu Status para 1, que significa que este item está excluido)
                //Status - 0=Ativo / 1=Excluido
                progressBar.setVisibility(View.VISIBLE);
                pesquisarEpi.setText("");
                DatabaseReference episDB = firebaseBanco.child(filialLogada).child("epis");
                episDB.child(String.valueOf(id)).child("status").setValue(1);
                recuperaEpis();
                criaListenerParaIdEpi();
                adapter = new AdapterEpi(listaEpis);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
                progressBar.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    public void recuperaEpis()
    {
        DatabaseReference epiTabela = firebaseBanco.child(filialLogada).child("epis");
        epiTabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaEpis.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.getValue(Epi.class).getStatus() == 0)//verificamos se o item não está inativo
                    {
                        listaEpis.add(ds.getValue(Epi.class));
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                semDados.setVisibility(View.GONE);
                if(listaEpis.size() == 0)
                    semDados.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void criaListenerParaIdEpi()
    {
        episDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                idEpi = (int) snapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
