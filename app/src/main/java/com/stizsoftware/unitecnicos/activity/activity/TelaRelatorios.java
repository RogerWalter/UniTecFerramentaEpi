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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterEpi;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterFerramenta;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterTecnico;
import com.stizsoftware.unitecnicos.activity.auxiliar.Mask;
import com.stizsoftware.unitecnicos.activity.auxiliar.RecyclerItemClickListener;
import com.stizsoftware.unitecnicos.activity.model.Epi;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Tecnico;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TelaRelatorios extends AppCompatActivity {

    DatabaseReference firebaseBanco = FirebaseDatabase.getInstance().getReference();
    DatabaseReference episDB, ferramentasDB,tecnicsDB;
    public String filialLogada;

    private Button btTotalFerramentas, btTotalEpis, btFerXEpi, btEpixFer, btSair;
    private RecyclerView recyclerViewTecnicos, recyclerViewItens;
    private RadioButton rbFerramenta, rbEpi;
    private EditText pesquisar;
    private ProgressBar progressBarTecnicos, progressBarItens;
    private ImageView semDadosTecnicos, semDadosItens;

    private List<Tecnico> listaTecnicos = new ArrayList<>();
    private List<Ferramenta> listaFerramentas = new ArrayList<>();
    private List<Epi> listaEpis = new ArrayList<>();

    private ArrayList<Ferramenta> listaFerramentaFiltrada = new ArrayList<>();
    private ArrayList<Epi> listaEpiFiltrada = new ArrayList<>();
    private ArrayList<Tecnico> listaTecnicosFiltrada = new ArrayList<>();

    private AdapterTecnico adapterTecnicos = new AdapterTecnico(listaTecnicos);
    private AdapterFerramenta adapterFerramentas = new AdapterFerramenta(listaFerramentas);
    private AdapterEpi adapterEpis = new AdapterEpi(listaEpis);

    private Ferramenta ferramentaSelecionada = new Ferramenta();
    private Epi epiSelecionado = new Epi();
    private Tecnico tecnicoSelecionado = new Tecnico();

    private int parametro = 0; //0 - Ferramenta | 1 - Epi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_relatorios);
        getSupportActionBar().hide();

        btTotalFerramentas = findViewById(R.id.btContagemGeralDeFerramentas);
        btTotalEpis = findViewById(R.id.btContagemGeralDeEpis);
        btFerXEpi = findViewById(R.id.btQtdFerramentaQtdTecnico);
        btEpixFer = findViewById(R.id.btQtdEpiQtdTecnico);
        btSair = findViewById(R.id.btSairTelaRelatorios);
        recyclerViewTecnicos = findViewById(R.id.recyclerViewTecnicosRelatorio);
        recyclerViewItens = findViewById(R.id.recyclerViewItensRelatorio);
        rbFerramenta = findViewById(R.id.rbFerramentaRelatorio);
        rbEpi = findViewById(R.id.rbEpiRelatorio);
        pesquisar = findViewById(R.id.editTextPesquisaRelatorio);
        progressBarTecnicos = findViewById(R.id.progressBarRelatoriosTecnicos);
        progressBarItens = findViewById(R.id.progressBarRelatoriosItens);
        semDadosTecnicos = findViewById(R.id.imageViewSemDadosTelaRelatoriosTecnicos);
        semDadosItens = findViewById(R.id.imageViewSemDadosTelaRelatoriosItens);

        rbFerramenta.setChecked(true);

        //aqui recuperamos a filial que o usuário utilizou para se logar
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailCompleto = user.getEmail();
        filialLogada = emailCompleto.substring(0, 4);

        episDB = firebaseBanco.child(filialLogada).child("epis");
        ferramentasDB = firebaseBanco.child(filialLogada).child("ferramentas");
        tecnicsDB = firebaseBanco.child(filialLogada).child("tecnicos");

        recuperaTecnicos();
        recuperaFerramentas();
        recuperaEpis();

        //RecyclerView Tecnicos
        RecyclerView.LayoutManager layoutManagerTecnicos = new LinearLayoutManager(getApplicationContext());
        recyclerViewTecnicos.setLayoutManager(layoutManagerTecnicos);
        recyclerViewTecnicos.setHasFixedSize(true);
        recyclerViewTecnicos.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerViewTecnicos.setAdapter(adapterTecnicos);
        recyclerViewTecnicos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewTecnicos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Tecnico tecnico = listaTecnicos.get(position);
                                tecnicoSelecionado = tecnico;
                                gerarDialogoCustomizadoGerarRelatorioTecnico();
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        })
        );
        //RecyclerView Ferramentas/Epis
        RecyclerView.LayoutManager layoutManagerItens = new LinearLayoutManager(getApplicationContext());
        recyclerViewItens.setLayoutManager(layoutManagerItens);
        recyclerViewItens.setHasFixedSize(true);
        recyclerViewItens.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerViewItens.setAdapter(adapterFerramentas);
        recyclerViewItens.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewItens,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (parametro == 0) {
                                    Ferramenta ferramenta = listaFerramentas.get(position);
                                    ferramentaSelecionada = ferramenta;

                                } else {
                                    Epi epi = listaEpis.get(position);
                                    epiSelecionado = epi;
                                }
                                gerarDialogoCustomizadoGerarRelatorioItem();
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        })
        );

        btSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaRelatorios.this, TelaTecnico.class);
                startActivity(intent);
                Animatoo.animateSlideUp(TelaRelatorios.this);
            }
        });

        rbEpi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbEpi.isChecked()) {
                    adapterTecnicos = new AdapterTecnico(listaTecnicos);
                    recyclerViewTecnicos.setAdapter(adapterTecnicos);
                    adapterEpis = new AdapterEpi(listaEpis);
                    recyclerViewItens.setAdapter(adapterEpis);
                    pesquisar.setText("");
                    parametro = 1;
                    semDadosItens.setVisibility(View.GONE);
                    if(listaEpis.size() == 0 )
                        semDadosItens.setVisibility(View.VISIBLE);
                }
            }
        });
        rbFerramenta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbFerramenta.isChecked()) {
                    adapterTecnicos = new AdapterTecnico(listaTecnicos);
                    recyclerViewTecnicos.setAdapter(adapterTecnicos);
                    adapterFerramentas = new AdapterFerramenta(listaFerramentas);
                    recyclerViewItens.setAdapter(adapterFerramentas);
                    pesquisar.setText("");
                    parametro = 0;
                    semDadosItens.setVisibility(View.GONE);
                    if(listaFerramentas.size() == 0 )
                        semDadosItens.setVisibility(View.VISIBLE);
                }
            }
        });
        btTotalFerramentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                intent.putExtra("parametro", 1);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaRelatorios.this);
            }
        });
        btTotalEpis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                intent.putExtra("parametro", 2);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaRelatorios.this);
            }
        });
        btFerXEpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                intent.putExtra("parametro", 3);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaRelatorios.this);
            }
        });
        btEpixFer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                intent.putExtra("parametro", 4);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaRelatorios.this);
            }
        });
        pesquisar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textoBarraPesquisa = pesquisar.getText().toString();
                //if(!textoBarraPesquisa.isEmpty())
                    filter(s.toString());
            }
        });
        recyclerViewTecnicos.setVisibility(View.GONE);
        progressBarTecnicos.setVisibility(View.VISIBLE);
        recyclerViewItens.setVisibility(View.GONE);
        progressBarItens.setVisibility(View.VISIBLE);
        semDadosTecnicos.setVisibility(View.GONE);
        semDadosItens.setVisibility(View.GONE);
    }
    private void filter(String text) {
        listaFerramentaFiltrada.clear();
        listaEpiFiltrada.clear();
        listaTecnicosFiltrada.clear();
        for(Tecnico item : listaTecnicos)
        {
            if(item.getNome().toLowerCase().contains(text.toLowerCase()))
            {
                listaTecnicosFiltrada.add(item);
            }
        }
        adapterTecnicos.listaComFiltro(listaTecnicosFiltrada);
        if(!text.isEmpty() && listaTecnicosFiltrada.size() == 0)
        {
            semDadosTecnicos.setVisibility(View.VISIBLE);
        }
        else
        {
            semDadosTecnicos.setVisibility(View.GONE);
        }

        if (parametro == 0) {
            for (Ferramenta item : listaFerramentas) {
                if (item.getDescricao().toLowerCase().contains(text.toLowerCase())) {
                    listaFerramentaFiltrada.add(item);
                }
            }
            adapterFerramentas.listaComFiltro(listaFerramentaFiltrada);
            if(!text.isEmpty() && listaFerramentaFiltrada.size() == 0)
            {
                semDadosItens.setVisibility(View.VISIBLE);
            }
            else
            {
                semDadosItens.setVisibility(View.GONE);
            }
        } else {
            for (Epi item : listaEpis) {
                if (item.getDescricao().toLowerCase().contains(text.toLowerCase())) {
                    listaEpiFiltrada.add(item);
                }
            }
            adapterEpis.listaComFiltro(listaEpiFiltrada);
            if(!text.isEmpty() && listaEpiFiltrada.size() == 0)
            {
                semDadosItens.setVisibility(View.VISIBLE);
            }
            else
            {
                semDadosItens.setVisibility(View.GONE);
            }
        }
    }

    public void recuperaEpis() {
        DatabaseReference epiTabela = firebaseBanco.child(filialLogada).child("epis");
        epiTabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaEpis.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getValue(Epi.class).getStatus() == 0)//verificamos se o item não está inativo
                    {
                        listaEpis.add(ds.getValue(Epi.class));
                    }
                }
                adapterEpis.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void recuperaFerramentas() {
        DatabaseReference ferramentaTabela = firebaseBanco.child(filialLogada).child("ferramentas");
        ferramentaTabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaFerramentas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getValue(Ferramenta.class).getStatus() == 0)//verificamos se o item não está inativo
                    {
                        listaFerramentas.add(ds.getValue(Ferramenta.class));
                    }
                }
                adapterFerramentas.notifyDataSetChanged();
                recyclerViewItens.setVisibility(View.VISIBLE);
                progressBarItens.setVisibility(View.GONE);
                semDadosItens.setVisibility(View.GONE);
                if(listaFerramentas.size() == 0 )
                    semDadosItens.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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
                adapterTecnicos.notifyDataSetChanged();
                recyclerViewTecnicos.setVisibility(View.VISIBLE);
                progressBarTecnicos.setVisibility(View.GONE);
                semDadosTecnicos.setVisibility(View.GONE);
                if(listaTecnicos.size() == 0)
                {
                    semDadosTecnicos.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void gerarDialogoCustomizadoGerarRelatorioTecnico()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_gerar_relatorio_tecnico);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTituloItensAssociados = (TextView) dialog.findViewById(R.id.tvTituloADDetalhesTecnico);
        Button botaoGerarFerramentasAssociadas = (Button) dialog.findViewById(R.id.btGerarRelatorioTecnicoFerramentasAssociadas);
        Button botaoGerarEpisAssociados = (Button) dialog.findViewById(R.id.btGerarRelatorioTecnicoEpisAssociadas);
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloEditarOuRemoverEpi);
        TextView textoDescricao = (TextView) dialog.findViewById(R.id.textViewDescricaoOutrosDialogoEditarOuRemoverEpi);
        TextView textodtIn = (TextView) dialog.findViewById(R.id.textdtInRelTec);
        TextView textodtFi = (TextView) dialog.findViewById(R.id.textdtFinRelTec);
        EditText dataInicial = (EditText) dialog.findViewById(R.id.editTextDataInicialRelatorioTecnico);
        EditText dataFinal = (EditText) dialog.findViewById(R.id.editTextDataFinalRelatorioTecnico);
        Button botaoGerarFerramenta = (Button) dialog.findViewById(R.id.btRelatorioTecnicoFerramenta);
        Button botaoGerarEpi = (Button) dialog.findViewById(R.id.btRelatorioTecnicoEpi);
        Button botaoGerarTodos = (Button) dialog.findViewById(R.id.btRelatorioTecnicoMovimentacaoEpiFer);
        Button botaoSair = (Button) dialog.findViewById(R.id.btCancelarDialogoRelatorioTecnico);

        String nome = tecnicoSelecionado.getNome();
        int codigo = tecnicoSelecionado.getId();

        dataInicial.addTextChangedListener(Mask.insert("##/##/####", dataInicial));
        dataFinal.addTextChangedListener(Mask.insert("##/##/####", dataFinal));

        textoTitulo.setText("Movimentações de " + nome);

        // if button is clicked, close the custom dialog
        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botaoGerarFerramenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoDataInicial = dataInicial.getText().toString();
                String textoDataFinal = dataFinal.getText().toString();
                Date dataInicialConvertida, dataFinalConvertida;
                if(textoDataInicial.isEmpty() || textoDataFinal.isEmpty())
                {
                    Toast.makeText(TelaRelatorios.this, "Existem campos obrigatórios sem preenchimento.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        dataInicialConvertida = formato.parse(textoDataInicial);
                    }catch(ParseException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(TelaRelatorios.this, "Data inicial informada inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try{
                        dataFinalConvertida = formato.parse(textoDataFinal);
                    }catch(ParseException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(TelaRelatorios.this, "Data final informada inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                    intent.putExtra("parametro", 5);
                    intent.putExtra("codigo", codigo);
                    intent.putExtra("item", 0);
                    intent.putExtra("data-inicial", textoDataInicial);
                    intent.putExtra("data-final", textoDataFinal);
                    intent.putExtra("nome", nome);
                    startActivity(intent);
                    Animatoo.animateSlideLeft(TelaRelatorios.this);
                    dialog.dismiss();
                }
            }
        });
        botaoGerarEpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoDataInicial = dataInicial.getText().toString();
                String textoDataFinal = dataFinal.getText().toString();
                Date dataInicialConvertida, dataFinalConvertida;
                if(textoDataInicial.isEmpty() || textoDataFinal.isEmpty())
                {
                    Toast.makeText(TelaRelatorios.this, "Existem campos obrigatórios sem preenchimento.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        dataInicialConvertida = formato.parse(textoDataInicial);
                    }catch(ParseException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(TelaRelatorios.this, "Data inicial informada inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try{
                        dataFinalConvertida = formato.parse(textoDataFinal);
                    }catch(ParseException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(TelaRelatorios.this, "Data final informada inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                    intent.putExtra("parametro", 5);
                    intent.putExtra("codigo", codigo);
                    intent.putExtra("item", 1);
                    intent.putExtra("data-inicial", textoDataInicial);
                    intent.putExtra("data-final", textoDataFinal);
                    startActivity(intent);
                    Animatoo.animateSlideLeft(TelaRelatorios.this);
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });

        botaoGerarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoDataInicial = dataInicial.getText().toString();
                String textoDataFinal = dataFinal.getText().toString();
                Date dataInicialConvertida, dataFinalConvertida;
                if(textoDataInicial.isEmpty() || textoDataFinal.isEmpty())
                {
                    Toast.makeText(TelaRelatorios.this, "Existem campos obrigatórios sem preenchimento.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        dataInicialConvertida = formato.parse(textoDataInicial);
                    }catch(ParseException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(TelaRelatorios.this, "Data inicial informada inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try{
                        dataFinalConvertida = formato.parse(textoDataFinal);
                    }catch(ParseException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(TelaRelatorios.this, "Data final informada inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                    intent.putExtra("parametro", 5);
                    intent.putExtra("codigo", codigo);
                    intent.putExtra("item", 2);
                    intent.putExtra("nome", nome);
                    intent.putExtra("data-inicial", textoDataInicial);
                    intent.putExtra("data-final", textoDataFinal);
                    startActivity(intent);
                    Animatoo.animateSlideLeft(TelaRelatorios.this);
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        botaoGerarFerramentasAssociadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                intent.putExtra("parametro", 7);
                intent.putExtra("codigo", codigo);
                intent.putExtra("item", 0);
                intent.putExtra("nome", nome);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaRelatorios.this);
                dialog.dismiss();
            }
        });
        botaoGerarEpisAssociados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                intent.putExtra("parametro", 7);
                intent.putExtra("codigo", codigo);
                intent.putExtra("item", 1);
                intent.putExtra("nome", nome);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaRelatorios.this);
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public int idItem = 0;
    public String descricaoItem = "";

    public void gerarDialogoCustomizadoGerarRelatorioItem()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_gerar_relatorio_ferramenta_epi);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewRelatorioItem);
        TextView textoDescricao = (TextView) dialog.findViewById(R.id.textViewDescricaoRelatorioItem);
        TextView textodtIn = (TextView) dialog.findViewById(R.id.textdtInRelItem);
        TextView textodtFi = (TextView) dialog.findViewById(R.id.textdtFinRelItem);
        EditText dataInicial = (EditText) dialog.findViewById(R.id.editTextDataInicialRelatorioItem);
        EditText dataFinal = (EditText) dialog.findViewById(R.id.editTextDataFinalRelatorioItem);
        Button botaoGerar = (Button) dialog.findViewById(R.id.btRelatorioGerarItem);
        Button botaoSair = (Button) dialog.findViewById(R.id.btCancelarDialogoRelatorioItem);

        if(parametro == 0)
        {
            textoTitulo.setText("Movimentações da Ferramenta");
            descricaoItem = ferramentaSelecionada.getDescricao();
            idItem = ferramentaSelecionada.getId();
        }
        else
        {
            textoTitulo.setText("Movimentações do EPI");
            descricaoItem = epiSelecionado.getDescricao();
            idItem = epiSelecionado.getId();
        }

        dataInicial.addTextChangedListener(Mask.insert("##/##/####", dataInicial));
        dataFinal.addTextChangedListener(Mask.insert("##/##/####", dataFinal));

        // if button is clicked, close the custom dialog
        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botaoGerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoDataInicial = dataInicial.getText().toString();
                String textoDataFinal = dataFinal.getText().toString();
                Date dataInicialConvertida, dataFinalConvertida;
                if(textoDataInicial.isEmpty() || textoDataFinal.isEmpty())
                {
                    Toast.makeText(TelaRelatorios.this, "Existem campos obrigatórios sem preenchimento.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        dataInicialConvertida = formato.parse(textoDataInicial);
                    }catch(ParseException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(TelaRelatorios.this, "Data inicial informada inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try{
                        dataFinalConvertida = formato.parse(textoDataFinal);
                    }catch(ParseException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(TelaRelatorios.this, "Data final informada inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(TelaRelatorios.this, TelaRelatorioGerado.class);
                    intent.putExtra("parametro", 6);
                    intent.putExtra("codigo", idItem);
                    intent.putExtra("item", descricaoItem);
                    intent.putExtra("data-inicial", textoDataInicial);
                    intent.putExtra("data-final", textoDataFinal);
                    startActivity(intent);
                    Animatoo.animateSlideLeft(TelaRelatorios.this);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}