package com.stizsoftware.unitecnicos.activity.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterTecnico;
import com.stizsoftware.unitecnicos.activity.auxiliar.RecyclerItemClickListener;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Tecnico;
import com.stizsoftware.unitecnicos.activity.model.TecnicoEpi;
import com.stizsoftware.unitecnicos.activity.model.TecnicoFerramenta;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TelaTecnico extends AppCompatActivity {

    DatabaseReference firebaseBanco = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tecnicosDB;
    public String filialLogada;

    private ProgressBar progressBar;
    private ImageView semDados;
    private EditText pesquisarTecnico;
    private RecyclerView recyclerView;
    private List<Tecnico> listaTecnicos = new ArrayList<>();
    private ArrayList<Tecnico> listaFiltrada = new ArrayList<>();
    private List<TecnicoFerramenta> listaTecnicoFerramentas = new ArrayList<>();
    private List<TecnicoEpi> listaTecnicoEpis = new ArrayList<>();
    private Button botaoRelatorio, botaoFerramenta, botaoEpi, botaoPedido;
    private FloatingActionButton botaoAddTecnico;
    public int idTecnico = 0;

    private AdapterTecnico adapter = new AdapterTecnico(listaTecnicos);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //aqui recuperamos a filial que o usuário utilizou para se logar
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailCompleto = user.getEmail();
        filialLogada = emailCompleto.substring(0,4);

        tecnicosDB = firebaseBanco.child(filialLogada).child("tecnicos");

        botaoFerramenta = findViewById(R.id.btFerramenta);
        botaoAddTecnico = findViewById(R.id.btAddTecnico);
        botaoEpi = findViewById(R.id.btEpiFerramenta);
        botaoRelatorio = findViewById(R.id.buttonRelatorio);
        pesquisarTecnico = findViewById(R.id.editTextPesquisaTecnico);
        botaoPedido = findViewById(R.id.buttonPedido);
        semDados = findViewById(R.id.imageViewSemDadosTelaTecnico);
        progressBar = findViewById(R.id.progressBarTelaTecnico);


        recyclerView = findViewById(R.id.recyclerViewTecnico);
        AdapterTecnico adapter = new AdapterTecnico(listaTecnicos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);
        recuperaTecnicos();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String barraPesquisa = "";
                                barraPesquisa = pesquisarTecnico.getText().toString();
                                Tecnico tecnico = new Tecnico();
                                if(barraPesquisa.isEmpty())
                                {
                                    tecnico = listaTecnicos.get(position);
                                }
                                else
                                {
                                    tecnico = listaFiltrada.get(position);
                                }

                                Intent intent = new Intent(TelaTecnico.this, TelaDetalhesTecnico.class);
                                intent.putExtra("codigo", tecnico.getId());
                                intent.putExtra("nome", tecnico.getNome());
                                startActivity(intent);
                                Animatoo.animateSlideUp(TelaTecnico.this);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        })
        );

        botaoRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaTecnico.this, TelaRelatorios.class);
                startActivity(intent);
                Animatoo.animateSlideDown(TelaTecnico.this);
            }
        });

        botaoAddTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarDialogoCustomizadoAddTecnico();
            }
        });

        botaoFerramenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaTecnico.this, TelaFerramenta.class);
                startActivity(intent);
                Animatoo.animateSlideRight(TelaTecnico.this);
            }
        });
        botaoEpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaTecnico.this, TelaEpi.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaTecnico.this);
            }
        });
        botaoPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaTecnico.this, TelaListaPedido.class);
                startActivity(intent);
                Animatoo.animateSlideDown(TelaTecnico.this);
            }
        });
        criaListenerParaIdTecnico();
        pesquisarTecnico.addTextChangedListener(new TextWatcher() {
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

    public void gerarDialogoCustomizadoAddTecnico()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_add_tecnico);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoAddTecnico);
        TextView textoNome = (TextView) dialog.findViewById(R.id.textViewNomeDialogoAddTecnico);
        EditText entradaNome = (EditText) dialog.findViewById(R.id.editTextNomeTecnicoAddDialogo);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoAddTecnico);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btSalvarDialogoAddTecnico);

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

                String textoNome = entradaNome.getText().toString().toUpperCase();

                if(textoNome.isEmpty())
                {
                    Toast.makeText(TelaTecnico.this, "Não foi informado um nome válido", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("tecnicos");
                    Tecnico novoTecnico = new Tecnico(idTecnico, textoNome, filialLogada,0); //Status: 0 - Ativo | 1 - Inativo (Excluido)
                    tecnicosDB.child(String.valueOf(idTecnico)).setValue(novoTecnico);
                    criaListenerParaIdTecnico();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void recuperaTecnicos()
    {
        DatabaseReference tecnicoTabela = firebaseBanco.child(filialLogada).child("tecnicos");
        tecnicoTabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaTecnicos.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.getValue(Tecnico.class).getStatus() == 0) //verificamos se o item não está inativo
                    {
                        listaTecnicos.add(ds.getValue(Tecnico.class));
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                semDados.setVisibility(View.GONE);
                if(listaTecnicos.size() == 0)
                    semDados.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void filter(String text)
    {
        listaFiltrada.clear();
        for(Tecnico item : listaTecnicos)
        {
            if(item.getNome().toLowerCase().contains(text.toLowerCase()))
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

    public void criaListenerParaIdTecnico()
    {
        tecnicosDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                idTecnico = (int) snapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
