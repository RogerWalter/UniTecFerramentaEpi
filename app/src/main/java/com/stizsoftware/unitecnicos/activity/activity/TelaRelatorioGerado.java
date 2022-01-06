package com.stizsoftware.unitecnicos.activity.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.UFormat;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.TabStop;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.opencsv.CSVWriter;
import com.squareup.picasso.Picasso;
import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterEpi;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterRelatorioItemAusente;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterRelatorioMovItem;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterRelatorioTotalizacao;
import com.stizsoftware.unitecnicos.activity.auxiliar.Mask;
import com.stizsoftware.unitecnicos.activity.auxiliar.RecyclerItemClickListener;
import com.stizsoftware.unitecnicos.activity.model.Epi;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Movimentacao;
import com.stizsoftware.unitecnicos.activity.model.RelatorioMovItem;
import com.stizsoftware.unitecnicos.activity.model.RelatorioTecItem;
import com.stizsoftware.unitecnicos.activity.model.Tecnico;
import com.stizsoftware.unitecnicos.activity.model.TecnicoEpi;
import com.stizsoftware.unitecnicos.activity.model.TecnicoFerramenta;
import com.stizsoftware.unitecnicos.activity.model.Totalizador;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TelaRelatorioGerado extends AppCompatActivity {
    DatabaseReference firebaseBanco = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tecnicoEpiDB, tecnicoFerramentaDB, tecnicosDB, episDB, ferramentasDB, movimentacaoDB;

    private List<TecnicoFerramenta> listaTecnicoFerramenta = new ArrayList<>();
    private List<TecnicoEpi> listaTecnicoEpi = new ArrayList<>();
    private List<Totalizador> listaTotalizador = new ArrayList<>();
    private List<Tecnico> listaTecnicos = new ArrayList<>();
    private List<Ferramenta> listaFerramentas = new ArrayList<>();
    private List<Epi> listaEpis = new ArrayList<>();
    private List<RelatorioTecItem> listaRelatorioTecItem= new ArrayList<>();
    private List<Movimentacao> listaMovimentacoes = new ArrayList<>();
    private List<RelatorioMovItem> listaRelatorioMovItem = new ArrayList<>();

    public int qualRelatorio = 0; //1 - Total Ferramentas | 2 - Total EPIs | 3 - Ferramenta x Tecnico
    public String filialLogada;

    private RecyclerView recyclerView;
    private TextView titulo, filial, data, itemLabel, itemDesc;
    private ProgressBar progressBar;
    private EditText pesquisar;
    private Button sair;
    private FloatingActionButton btExportar;
    private ImageView semDados;

    private AdapterRelatorioTotalizacao adapterTotalizacao = new AdapterRelatorioTotalizacao(listaTotalizador);
    private AdapterRelatorioItemAusente adapterRelatorioItemAusente = new AdapterRelatorioItemAusente(listaRelatorioTecItem);
    private AdapterRelatorioMovItem adapterRelatorioMovItem = new AdapterRelatorioMovItem(listaRelatorioMovItem);

    public String titulo_do_relatorio = "";
    public String nome_tecnico = "";

    public int codigoTecnico = 0;
    public int parametroItem = -1;
    public String dataInicial = "";
    public String dataFinal = "";

    public int codigoItem = 0;
    public String descItem = "";

    public RelatorioMovItem itemMovSelecionado = new RelatorioMovItem();

    private static final int PERMISSION_REQUEST_CODE = 200;

    private ArrayList<RelatorioTecItem> listaFiltradaRelatorioTecItem = new ArrayList<>();
    private ArrayList<Totalizador> listaFiltradaTotalizador = new ArrayList<>();
    private ArrayList<RelatorioMovItem> listaFiltradaRelatorioMovItem = new ArrayList<>();

    private List<RelatorioTecItem> listaRelatorioTecItemBKP = new ArrayList<>();
    private List<Totalizador> listaTotalizadorBKP = new ArrayList<>();
    private List<RelatorioMovItem> listaRelatorioMovItemBKP = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_relatorio_gerado);
        getSupportActionBar().hide();

        //aqui recuperamos a filial que o usuário utilizou para se logar
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailCompleto = user.getEmail();
        filialLogada = emailCompleto.substring(0,4);

        tecnicoEpiDB = firebaseBanco.child(filialLogada).child("tecnico-epis");
        tecnicoFerramentaDB = firebaseBanco.child(filialLogada).child("tecnico-ferramentas");
        tecnicosDB = firebaseBanco.child(filialLogada).child("tecnicos");
        episDB = firebaseBanco.child(filialLogada).child("epis");
        ferramentasDB = firebaseBanco.child(filialLogada).child("ferramentas");
        movimentacaoDB = firebaseBanco.child(filialLogada).child("movimentacoes");

        Bundle dados = getIntent().getExtras();
        qualRelatorio = dados.getInt("parametro");

        semDados = findViewById(R.id.imageViewSemDadosTelaRelatorioGerado);
        btExportar = findViewById(R.id.botaoExportarRelatorioGerado);
        sair = findViewById(R.id.btSairTelaRelatorioGerado);
        pesquisar = findViewById(R.id.editTextPesquisaRelatorioGerado);
        progressBar = findViewById(R.id.progressBarRelatorioGerado);
        titulo = findViewById(R.id.textView90);
        filial = findViewById(R.id.textViewFilial);
        data = findViewById(R.id.textViewData);
        itemLabel = findViewById(R.id.textViewC);
        itemDesc = findViewById(R.id.textViewItem);
        recyclerView = findViewById(R.id.recyclerViewRelatorioGerado);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapterTotalizacao);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if(qualRelatorio == 5 || qualRelatorio == 6)
                                {
                                    itemMovSelecionado = listaRelatorioMovItem.get(position);
                                    gerarDialogoCustomizadoMostrarImagemMovimentacao();
                                }
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        })
        );

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.GONE);
        itemLabel.setVisibility(View.GONE);
        itemDesc.setVisibility(View.GONE);
        semDados.setVisibility(View.GONE);

        SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy");
        Date currentTime = new Date();
        String dataAtual = dataFormatada.format(currentTime);

        filial.setText(filialLogada);
        data.setText(dataAtual);

        if(qualRelatorio == 0)//Erro na Bagaça
        {

        }
        if(qualRelatorio == 1) //Total Ferramentas
        {
            titulo_do_relatorio = "Totalização de Ferramentas";
            recyclerView.setAdapter(adapterTotalizacao);
            titulo.setText("Total de Ferramentas");
            recuperaTecFerramenta();
        }
        if(qualRelatorio == 2) //Total EPIs
        {
            titulo_do_relatorio = "Totalização de EPI's";
            recyclerView.setAdapter(adapterTotalizacao);
            titulo.setText("Total de EPI's");
            recuperaTecEpi();
        }
        if(qualRelatorio == 3) //Ferramenta x Tecnico
        {
            titulo_do_relatorio = "Ferramenta x Técnico";
            recyclerView.setAdapter(adapterRelatorioItemAusente);
            titulo.setText("Ferramentas x Técnicos");
            recuperaFerramentas();
            recuperaTecnicos();
            recuperaTecFerramenta();
        }
        if(qualRelatorio == 4) //EPI's x Tecnico
        {
            titulo_do_relatorio = "EPI's x Técnico";
            recyclerView.setAdapter(adapterRelatorioItemAusente);
            titulo.setText("Epi's x Técnicos");
            recuperaEpis();
            recuperaTecnicos();
            recuperaTecEpi();
        }
        if(qualRelatorio == 5)
        {
            Bundle dadosRecebido = getIntent().getExtras();
            codigoTecnico = dadosRecebido.getInt("codigo");
            parametroItem = dadosRecebido.getInt("item");
            dataInicial = dadosRecebido.getString("data-inicial");
            dataFinal = dadosRecebido.getString("data-final");
            nome_tecnico = dadosRecebido.getString("nome");
            itemLabel.setVisibility(View.VISIBLE);
            itemDesc.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapterRelatorioMovItem);
            if(parametroItem == 0)//ferramenta
            {
                titulo_do_relatorio = "Movimentações de " + nome_tecnico + ": FERRAMENTAS";
                titulo.setText("Movimentações de " + nome_tecnico);
                itemDesc.setText("FERRAMENTAS");
                recuperaMovimentacoes();
            }
            if(parametroItem == 1) //epi
            {
                titulo_do_relatorio = "Movimentações de " + nome_tecnico + ": EPI's";
                titulo.setText("Movimentações de " + nome_tecnico);
                itemDesc.setText("EPI'S");
                recuperaMovimentacoes();
            }
            if (parametroItem == 2)
            {
                titulo_do_relatorio = "Movimentações de " + nome_tecnico + ": FERRAMENTAS e EPI's";
                titulo.setText("Movimentações de " + nome_tecnico);
                itemDesc.setText("FERRAMENTAS e EPI'S");
                recuperaMovimentacoes();
            }
        }
        if(qualRelatorio == 6)
        {
            Bundle dadosRecebidos = getIntent().getExtras();
            codigoItem = dadosRecebidos.getInt("codigo");
            descItem = dadosRecebidos.getString("item");
            dataInicial = dadosRecebidos.getString("data-inicial");
            dataFinal = dadosRecebidos.getString("data-final");
            itemLabel.setVisibility(View.VISIBLE);
            itemDesc.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapterRelatorioMovItem);
            titulo.setText("Movimentações do Item:");
            itemDesc.setText(descItem);
            titulo_do_relatorio = "Movimentações do Item: " + descItem;
            recuperaMovimentacoes();
        }
        if(qualRelatorio == 7)//ferramentas ou epis associados
        {
            Bundle dadosRecebido = getIntent().getExtras();
            codigoTecnico = dadosRecebido.getInt("codigo");
            parametroItem = dadosRecebido.getInt("item");
            nome_tecnico = dadosRecebido.getString("nome");
            itemLabel.setVisibility(View.VISIBLE);
            itemDesc.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapterTotalizacao);
            if(parametroItem == 0)//ferramenta
            {
                titulo_do_relatorio = "Ferramentas Associadas a " + nome_tecnico;
                titulo.setText("Ferramentas de " + nome_tecnico);
                itemDesc.setText("FERRAMENTAS");
                recuperaTecFerramenta();
            }
            if(parametroItem == 1) //epi
            {
                titulo_do_relatorio = "EPI's Associados a " + nome_tecnico;
                titulo.setText("EPI's de " + nome_tecnico);
                itemDesc.setText("EPI'S");
                recuperaTecEpi();
            }
        }
        pesquisar.addTextChangedListener(new TextWatcher() {
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
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaRelatorioGerado.this, TelaRelatorios.class);
                startActivity(intent);
                Animatoo.animateSlideRight(TelaRelatorioGerado.this);
            }
        });
        btExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarDialogoCustomizadoExportarDados();
            }
        });
    }

    private void filter(String text)
    {
        ArrayList<RelatorioTecItem> listaFiltrada = new ArrayList<>();
        ArrayList<Totalizador> listaFiltrada1 = new ArrayList<>();
        ArrayList<RelatorioMovItem> listaFiltrada2 = new ArrayList<>();

        if(qualRelatorio == 1 || qualRelatorio == 2) //Total Ferramentas
        {
            for(Totalizador item1 : listaTotalizador)
            {
                if(item1.getDescricao().toLowerCase().contains(text.toLowerCase()) || item1.getOutros().toLowerCase().contains(text.toLowerCase()) || item1.getDescricao().toLowerCase().contains(text.toLowerCase()))
                {
                    listaFiltrada1.add(item1);
                }
            }
            adapterTotalizacao.listaComFiltro(listaFiltrada1);
            listaFiltradaTotalizador = listaFiltrada1;
            if(!text.isEmpty() && listaFiltrada1.size() == 0)
            {
                semDados.setVisibility(View.VISIBLE);
            }
            else
            {
                semDados.setVisibility(View.GONE);
            }
        }
        if(qualRelatorio == 3 || qualRelatorio == 4) //Ferramenta x Tecnico
        {
            for(RelatorioTecItem item : listaRelatorioTecItem)
            {
                if(item.getNomeTecnico().toLowerCase().contains(text.toLowerCase()) || item.getNomeFerramenta().toLowerCase().contains(text.toLowerCase()))
                {
                    listaFiltrada.add(item);
                }
            }
            adapterRelatorioItemAusente.listaComFiltro(listaFiltrada);
            listaFiltradaRelatorioTecItem = listaFiltrada;
            if(!text.isEmpty() && listaFiltrada.size() == 0)
            {
                semDados.setVisibility(View.VISIBLE);
            }
            else
            {
                semDados.setVisibility(View.GONE);
            }
        }
        if(qualRelatorio == 5 || qualRelatorio == 6) //Movimentaçoes
        {
            for(RelatorioMovItem item2 : listaRelatorioMovItem)
            {
                if(item2.getDescricao().toLowerCase().contains(text.toLowerCase()) || item2.getOutros().toLowerCase().contains(text.toLowerCase()) || item2.getData().toLowerCase().contains(text.toLowerCase()) || item2.getSentido().toLowerCase().contains(text.toLowerCase()))
                {
                    listaFiltrada2.add(item2);
                }
            }
            adapterRelatorioMovItem.listaComFiltro(listaFiltrada2);
            listaFiltradaRelatorioMovItem = listaFiltrada2;
            if(!text.isEmpty() && listaFiltrada2.size() == 0)
            {
                semDados.setVisibility(View.VISIBLE);
            }
            else
            {
                semDados.setVisibility(View.GONE);
            }
        }
    }

    public void recuperaTecFerramenta() {
        tecnicoFerramentaDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaTecnicoFerramenta.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.getValue(TecnicoFerramenta.class).getStatus() == 0)
                    {
                        listaTecnicoFerramenta.add(ds.getValue(TecnicoFerramenta.class));
                    }
                }
                if(qualRelatorio == 1)
                    montarListaTotalFerramentas();
                if(qualRelatorio == 3)
                    montarListaFerramentaTecnico();
                if(qualRelatorio == 5 && parametroItem == 0)
                    montarListaMovimentacoesFerramentas();
                if(qualRelatorio == 5 && parametroItem == 2)
                    recuperaTecEpi();
                if(qualRelatorio == 7)
                    montarFerramentaTecnico();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
    public void montarListaTotalFerramentas()
    {
        //tamanho da lista
        int totalTecFerramenta = listaTecnicoFerramenta.size();
        //comparamos item a item da lista para gerar a lista final
        for(int i = 0; i<totalTecFerramenta; i++)
        {
            TecnicoFerramenta analisado = listaTecnicoFerramenta.get(i); //pegamos o item da lista
            int idProcurado = analisado.getId_ferramenta(); //pegamos o id da ferramenta deste ittem
            int tamanhoListaTotal = listaTotalizador.size(); //verificamos o tamanho da lista que mostraremos no recyclerview
            if (tamanhoListaTotal == 0)//lista está vazia, vamos inserir direto este primeiro item
            {
                Totalizador itemDaListaAdicionar = new Totalizador();//criamos um novo item;
                itemDaListaAdicionar.setIdItem(analisado.getId_ferramenta());
                itemDaListaAdicionar.setDescricao(analisado.getDesc_ferramenta());
                itemDaListaAdicionar.setOutros(analisado.getOutros());
                itemDaListaAdicionar.setQtd(analisado.getQtd());
                listaTotalizador.add(itemDaListaAdicionar);
            }
            int contador = 0;
            for(int j = 0; j<tamanhoListaTotal; j++)
            {
                contador++;
                Totalizador itemDaLista = listaTotalizador.get(j); //pegamos o item da lista final
                int idExistenteNaLista = itemDaLista.getIdItem(); //pegamos o id da ferramenta deste item
                if(idProcurado == idExistenteNaLista)//comparamos para ver se o item da lista tecFer já está na lista final
                {//se já estiver, adicionamos a sua quantidade ao item já existente
                    int qtdItemProcurado = analisado.getQtd(); //qtd item que estamos procurando
                    int qtdAtualListaFinal = itemDaLista.getQtd(); //qtd do item que já tem na lista final
                    int qtdFinal = qtdItemProcurado + qtdAtualListaFinal; //soma das duas quantidades para gerar a qtd final
                    Totalizador ItemDaListaAtualizado = itemDaLista;//pegamos o item antigo da lista
                    ItemDaListaAtualizado.setQtd(qtdFinal);//atualizamos ele com sua nova quantidade
                    listaTotalizador.set(j,ItemDaListaAtualizado);
                    break;
                }
                else
                {//se não estiver, adicionamos ele à lista final
                    if(tamanhoListaTotal == contador)
                    {
                        Totalizador itemDaListaAdicionar = new Totalizador();//criamos um novo item;
                        itemDaListaAdicionar.setIdItem(analisado.getId_ferramenta());
                        itemDaListaAdicionar.setDescricao(analisado.getDesc_ferramenta());
                        itemDaListaAdicionar.setOutros(analisado.getOutros());
                        itemDaListaAdicionar.setQtd(analisado.getQtd());
                        listaTotalizador.add(itemDaListaAdicionar);
                    }
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaTotalizador.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterTotalizacao.notifyDataSetChanged();
    }

    public void recuperaTecEpi() {
        tecnicoEpiDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaTecnicoEpi.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.getValue(TecnicoEpi.class).getStatus() == 0)
                    {
                        listaTecnicoEpi.add(ds.getValue(TecnicoEpi.class));
                    }
                }
                if(qualRelatorio == 2)
                    montarListaTotalEpis();
                if(qualRelatorio == 4)
                    montaListaEpiTecnico();
                if(qualRelatorio == 5 && parametroItem == 1)
                    montarListaMovimentacoesEpis();
                if(qualRelatorio == 5 && parametroItem == 2)
                    montarListaMovimentacoesFerramentasComEpis();
                if(qualRelatorio == 7)
                    montarEpiTecnico();

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    public void montarListaTotalEpis()
    {
        //tamanho da lista
        int totalTecEpi = listaTecnicoEpi.size();
        //comparamos item a item da lista para gerar a lista final
        for(int i = 0; i<totalTecEpi; i++)
        {
            TecnicoEpi analisado = listaTecnicoEpi.get(i); //pegamos o item da lista
            int idProcurado = analisado.getId_epi(); //pegamos o id da Epi deste ittem
            int tamanhoListaTotal = listaTotalizador.size(); //verificamos o tamanho da lista que mostraremos no recyclerview
            if (tamanhoListaTotal == 0)//lista está vazia, vamos inserir direto este primeiro item
            {
                Totalizador itemDaListaAdicionar = new Totalizador();//criamos um novo item;
                itemDaListaAdicionar.setIdItem(analisado.getId_epi());
                itemDaListaAdicionar.setDescricao(analisado.getDesc_epi());
                itemDaListaAdicionar.setOutros(analisado.getOutros());
                itemDaListaAdicionar.setQtd(analisado.getQtd());
                listaTotalizador.add(itemDaListaAdicionar);
            }
            int contador = 0;
            for(int j = 0; j<tamanhoListaTotal; j++)
            {
                contador++;
                Totalizador itemDaLista = listaTotalizador.get(j); //pegamos o item da lista final
                int idExistenteNaLista = itemDaLista.getIdItem(); //pegamos o id do Epi deste item
                if(idProcurado == idExistenteNaLista)//comparamos para ver se o item da lista tecFer já está na lista final
                {//se já estiver, adicionamos a sua quantidade ao item já existente
                    int qtdItemProcurado = analisado.getQtd(); //qtd item que estamos procurando
                    int qtdAtualListaFinal = itemDaLista.getQtd(); //qtd do item que já tem na lista final
                    int qtdFinal = qtdItemProcurado + qtdAtualListaFinal; //soma das duas quantidades para gerar a qtd final
                    Totalizador ItemDaListaAtualizado = itemDaLista;//pegamos o item antigo da lista
                    ItemDaListaAtualizado.setQtd(qtdFinal);//atualizamos ele com sua nova quantidade
                    listaTotalizador.set(j,ItemDaListaAtualizado);
                    break;
                }
                else
                {//se não estiver, adicionamos ele à lista final
                    if(tamanhoListaTotal == contador)
                    {
                        Totalizador itemDaListaAdicionar = new Totalizador();//criamos um novo item;
                        itemDaListaAdicionar.setIdItem(analisado.getId_epi());
                        itemDaListaAdicionar.setDescricao(analisado.getDesc_epi());
                        itemDaListaAdicionar.setOutros(analisado.getOutros());
                        itemDaListaAdicionar.setQtd(analisado.getQtd());
                        listaTotalizador.add(itemDaListaAdicionar);
                    }
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaTotalizador.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterTotalizacao.notifyDataSetChanged();
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
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void recuperaMovimentacoes() {
        movimentacaoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaMovimentacoes.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listaMovimentacoes.add(ds.getValue(Movimentacao.class));
                }
                if(parametroItem == 0)
                {
                    montarListaMovimentacoesFerramentas();
                }
                if(parametroItem == 1)
                {
                    montarListaMovimentacoesEpis();
                }
                if(parametroItem == 2)
                {
                    montarListaMovimentacoesFerramentasComEpis();
                }
                if(qualRelatorio == 6)
                {
                    montarListaMovimentacoesItem();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
    public void montarListaFerramentaTecnico()
    {
        listaRelatorioTecItem.clear();
        //quantidade de tecnicos
        int totalTecnico = listaTecnicos.size();
        //qtd de ferramentas
        int totalFerramenta = listaFerramentas.size();
        //qtd tecFer
        int totalTecFer = listaTecnicoFerramenta.size();

        for(int i = 0; i < totalTecnico; i++) //verificamos cada tecnico
        {
            Tecnico tecnicoVerificando = listaTecnicos.get(i);
            int idTecnicoVerificando = tecnicoVerificando.getId();

            if(listaTecnicoFerramenta.size() == 0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Aviso")
                        .setMessage("Não há ferramentas associadas a técnicos.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(TelaRelatorioGerado.this, TelaRelatorios.class);
                                startActivity(intent);
                                Animatoo.animateSlideRight(TelaRelatorioGerado.this);
                            }
                        });
                builder.show();
            }

            for(int j = 0; j < totalFerramenta; j++)
            {
                Ferramenta ferramentaVerificando = listaFerramentas.get(j);
                int idFerramentaVerificando = ferramentaVerificando.getId();
                String descFerramentaVerificando = ferramentaVerificando.getDescricao();

                int contador = 0;
                for (int k = 0; k < totalTecFer; k++)
                {
                    contador++;
                    TecnicoFerramenta tecFerVerificando = listaTecnicoFerramenta.get(k);
                    if((tecFerVerificando.getId_tecnico() == idTecnicoVerificando && tecFerVerificando.getId_ferramenta() == idFerramentaVerificando))
                    {//este técnico possui esta ferramenta
                        //(tecFerVerificando.getId_tecnico() == idTecnicoVerificando && tecFerVerificando.getDesc_ferramenta() == descFerramentaVerificando)
                        break;
                    }
                    else
                    {
                        if(totalTecFer == contador)//percorrimos a lista toda
                        {//tecnico nao tem a ferramenta

                            RelatorioTecItem ferramentaAusente = new RelatorioTecItem();
                            ferramentaAusente.setNomeTecnico(tecnicoVerificando.getNome());
                            ferramentaAusente.setNomeItem(ferramentaVerificando.getDescricao());
                            int parametroBusca = 0; // 0 - não achou | 1 - achou
                            for(int x = 0; x < listaRelatorioTecItem.size(); x++)
                            {
                                RelatorioTecItem item = listaRelatorioTecItem.get(x);
                                String itemNomeTec = item.getNomeTecnico();
                                String itemNomeFer = item.getNomeFerramenta();
                                String ferAusNomeTec = ferramentaAusente.getNomeTecnico();
                                String ferAusNomeFer = ferramentaAusente.getNomeFerramenta();

                                if(ferAusNomeTec.equals(itemNomeTec) && ferAusNomeFer.equals(itemNomeFer))
                                {
                                    parametroBusca = 1;
                                }
                            }
                            if(parametroBusca == 1)
                            {
                                break;
                            }
                            else
                            {
                                listaRelatorioTecItem.add(ferramentaAusente);
                            }
                        }
                    }
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaRelatorioTecItem.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterRelatorioItemAusente.notifyDataSetChanged();
    }

    public void montaListaEpiTecnico()
    {
        listaRelatorioTecItem.clear();
        //quantidade de tecnicos
        int totalTecnico = listaTecnicos.size();
        //qtd de Epis
        int totalEpi = listaEpis.size();
        //qtd tecFer
        int totalTecEpi = listaTecnicoEpi.size();

        for(int i = 0; i < totalTecnico; i++) //verificamos cada tecnico
        {
            Tecnico tecnicoVerificando = listaTecnicos.get(i);
            int idTecnicoVerificando = tecnicoVerificando.getId();

            if(listaTecnicoEpi.size() == 0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Aviso")
                        .setMessage("Não há EPI's associados a técnicos.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(TelaRelatorioGerado.this, TelaRelatorios.class);
                                startActivity(intent);
                                Animatoo.animateSlideRight(TelaRelatorioGerado.this);
                            }
                        });
                builder.show();
            }

            for(int j = 0; j < totalEpi; j++)
            {
                Epi EpiVerificando = listaEpis.get(j);
                int idEpiVerificando = EpiVerificando.getId();

                int contador = 0;
                for (int k = 0; k < totalTecEpi; k++)
                {
                    contador++;
                    TecnicoEpi tecFerVerificando = listaTecnicoEpi.get(k);
                    if(tecFerVerificando.getId_tecnico() == idTecnicoVerificando && tecFerVerificando.getId_epi() == idEpiVerificando)
                    {//este técnico possui esta Epi
                        break;
                    }
                    else
                    {
                        if(totalTecEpi == contador)//percorrimos a lista toda
                        {//tecnico nao tem a Epi
                            RelatorioTecItem EpiAusente = new RelatorioTecItem();
                            EpiAusente.setNomeTecnico(tecnicoVerificando.getNome());
                            EpiAusente.setNomeItem(EpiVerificando.getDescricao());
                            listaRelatorioTecItem.add(EpiAusente);
                        }
                    }
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaRelatorioTecItem.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterRelatorioItemAusente.notifyDataSetChanged();
    }

    public void montarListaMovimentacoesFerramentas()
    {
        listaRelatorioMovItem.clear();
        //quantidade de movimentações
        int totalMov = listaMovimentacoes.size();
        Date dataInicialConvertida = new Date();
        Date dataFinalConvertida = new Date();
        Date dataDoRegistro = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try{
            dataInicialConvertida = formato.parse(dataInicial);
        }catch(ParseException e)
        {
            e.printStackTrace();
        }
        try{
            dataFinalConvertida = formato.parse(dataFinal);
        }catch(ParseException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < totalMov; i++)
        {
            Movimentacao existente = listaMovimentacoes.get(i);
            String dataOriginal = existente.getData().substring(0,10);
            try{
                dataDoRegistro = formato.parse(dataOriginal);
            }catch(ParseException e)
            {
                e.printStackTrace();
            }
            RelatorioMovItem novo = new RelatorioMovItem();
            if(existente.getId_tecnico() == codigoTecnico && existente.getId_grupo() == 0)
            {
                if((dataDoRegistro.after(dataInicialConvertida)||dataDoRegistro.equals(dataInicialConvertida) ) && (dataDoRegistro.before(dataFinalConvertida) || dataDoRegistro.equals(dataFinalConvertida)))
                {
                    if (existente.getOutros_item().toLowerCase().equals("vazio") ||existente.getOutros_item().toLowerCase().equals("") || TextUtils.isEmpty(existente.getOutros_item().toLowerCase()))
                    {
                        novo.setDescricao(existente.getDescricao_item());
                    }
                    else
                    {
                        novo.setDescricao(existente.getDescricao_item() + " (" + existente.getOutros_item() + ")");
                    }
                    novo.setOutros(existente.getMotivo());
                    novo.setData(existente.getData().substring(0,10));
                    if(existente.getTipo().equals("+"))
                    {
                        novo.setSentido("ENTREGUE");
                    }
                    if(existente.getTipo().equals("-"))
                    {
                        novo.setSentido("DEVOLVIDO");
                    }
                    if(existente.getTipo().equals("="))
                    {
                        novo.setSentido("ATUALIZADO");
                    }
                    novo.setImagem_firebase(existente.getImagem());
                    listaRelatorioMovItem.add(novo);
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaRelatorioMovItem.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterRelatorioMovItem.notifyDataSetChanged();
    }
    public void montarListaMovimentacoesEpis()
    {
        listaRelatorioMovItem.clear();
        //quantidade de movimentações
        int totalMov = listaMovimentacoes.size();
        Date dataInicialConvertida = new Date();
        Date dataFinalConvertida = new Date();
        Date dataDoRegistro = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try{
            dataInicialConvertida = formato.parse(dataInicial);
        }catch(ParseException e)
        {
            e.printStackTrace();
        }
        try{
            dataFinalConvertida = formato.parse(dataFinal);
        }catch(ParseException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < totalMov; i++)
        {
            Movimentacao existente = listaMovimentacoes.get(i);
            String dataOriginal = existente.getData().substring(0,10);
            try{
                dataDoRegistro = formato.parse(dataOriginal);
            }catch(ParseException e)
            {
                e.printStackTrace();
            }
            RelatorioMovItem novo = new RelatorioMovItem();
            if(existente.getId_tecnico() == codigoTecnico && existente.getId_grupo() == 1)
            {
                if((dataDoRegistro.after(dataInicialConvertida)||dataDoRegistro.equals(dataInicialConvertida) ) && (dataDoRegistro.before(dataFinalConvertida) || dataDoRegistro.equals(dataFinalConvertida)))
                {
                    if (existente.getOutros_item().toLowerCase().equals("vazio") ||existente.getOutros_item().toLowerCase().equals("") || TextUtils.isEmpty(existente.getOutros_item().toLowerCase()))
                    {
                        novo.setDescricao(existente.getDescricao_item());
                    }
                    else
                    {
                        novo.setDescricao(existente.getDescricao_item() + " (" + existente.getOutros_item() + ")");
                    }
                    novo.setOutros(existente.getMotivo());
                    novo.setData(existente.getData().substring(0,10));
                    if(existente.getTipo().equals("+"))
                    {
                        novo.setSentido("ENTREGUE");
                    }
                    else
                    {
                        novo.setSentido("DEVOLVIDO");
                    }
                    novo.setImagem_firebase(existente.getImagem());
                    listaRelatorioMovItem.add(novo);
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaRelatorioMovItem.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterRelatorioMovItem.notifyDataSetChanged();
    }
    public void montarListaMovimentacoesFerramentasComEpis()
    {
        listaRelatorioMovItem.clear();
        //quantidade de movimentações
        int totalMov = listaMovimentacoes.size();
        Date dataInicialConvertida = new Date();
        Date dataFinalConvertida = new Date();
        Date dataDoRegistro = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try{
            dataInicialConvertida = formato.parse(dataInicial);
        }catch(ParseException e)
        {
            e.printStackTrace();
        }
        try{
            dataFinalConvertida = formato.parse(dataFinal);
        }catch(ParseException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < totalMov; i++)
        {
            Movimentacao existente = listaMovimentacoes.get(i);
            String dataOriginal = existente.getData().substring(0,10);
            try{
                dataDoRegistro = formato.parse(dataOriginal);
            }catch(ParseException e)
            {
                e.printStackTrace();
            }
            RelatorioMovItem novo = new RelatorioMovItem();
            if(existente.getId_tecnico() == codigoTecnico)
            {
                if((dataDoRegistro.after(dataInicialConvertida)||dataDoRegistro.equals(dataInicialConvertida) ) && (dataDoRegistro.before(dataFinalConvertida) || dataDoRegistro.equals(dataFinalConvertida)))
                {
                    if (existente.getOutros_item().toLowerCase().equals("vazio") ||existente.getOutros_item().toLowerCase().equals("") || TextUtils.isEmpty(existente.getOutros_item().toLowerCase()))
                    {
                        novo.setDescricao(existente.getDescricao_item());
                    }
                    else
                    {
                        novo.setDescricao(existente.getDescricao_item() + " (" + existente.getOutros_item() + ")");
                    }
                    novo.setOutros(existente.getMotivo());
                    novo.setData(existente.getData().substring(0,10));
                    if(existente.getTipo().equals("+"))
                    {
                        novo.setSentido("ENTREGUE");
                    }
                    if(existente.getTipo().equals("-"))
                    {
                        novo.setSentido("DEVOLVIDO");
                    }
                    if(existente.getTipo().equals("="))
                    {
                        novo.setSentido("ATUALIZADO");
                    }
                    novo.setImagem_firebase(existente.getImagem());
                    listaRelatorioMovItem.add(novo);
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaRelatorioMovItem.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterRelatorioMovItem.notifyDataSetChanged();
    }
    public void montarListaMovimentacoesItem()
    {
        listaRelatorioMovItem.clear();
        //quantidade de movimentações
        int totalMov = listaMovimentacoes.size();
        Date dataInicialConvertida = new Date();
        Date dataFinalConvertida = new Date();
        Date dataDoRegistro = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try{
            dataInicialConvertida = formato.parse(dataInicial);
        }catch(ParseException e)
        {
            e.printStackTrace();
        }
        try{
            dataFinalConvertida = formato.parse(dataFinal);
        }catch(ParseException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < totalMov; i++)
        {
            Movimentacao existente = listaMovimentacoes.get(i);
            String dataOriginal = existente.getData().substring(0,10);
            try{
                dataDoRegistro = formato.parse(dataOriginal);
            }catch(ParseException e)
            {
                e.printStackTrace();
            }
            RelatorioMovItem novo = new RelatorioMovItem();
            if(existente.getId_item() == codigoItem)
            {
                if((dataDoRegistro.after(dataInicialConvertida)||dataDoRegistro.equals(dataInicialConvertida) ) && (dataDoRegistro.before(dataFinalConvertida) || dataDoRegistro.equals(dataFinalConvertida)))
                {
                    novo.setDescricao(existente.getNome_tecnico());
                    novo.setOutros(existente.getMotivo());
                    novo.setData(existente.getData().substring(0,10));
                    if(existente.getTipo().equals("+"))
                    {
                        novo.setSentido("ENTREGUE");
                    }
                    if(existente.getTipo().equals("-"))
                    {
                        novo.setSentido("DEVOLVIDO");
                    }
                    if(existente.getTipo().equals("="))
                    {
                        novo.setSentido("ATUALIZADO");
                    }
                    novo.setImagem_firebase(existente.getImagem());
                    listaRelatorioMovItem.add(novo);
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaRelatorioMovItem.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterRelatorioMovItem.notifyDataSetChanged();
    }

    public void montarFerramentaTecnico()
    {
        List<TecnicoFerramenta> listaTecnicoFerramentaIntermediaria = new ArrayList<>();
        List<TecnicoFerramenta> listaTecnicoFerramentaIntermediariaComparacao = new ArrayList<>();
        List<TecnicoFerramenta> listaTecnicoFerramentaFinal = new ArrayList<>();
        listaTecnicoFerramentaIntermediaria.clear();
        listaTecnicoFerramentaIntermediariaComparacao.clear();
        listaTecnicoFerramentaFinal.clear();
        listaTotalizador.clear();

        //recupera somente associações do técnico selecionado
        for(TecnicoFerramenta item : listaTecnicoFerramenta)
        {
            if(item.getId_tecnico() == codigoTecnico)
            {
                listaTecnicoFerramentaIntermediaria.add(item);
            }
        }

        Collections.sort(listaTecnicoFerramentaIntermediaria, new Comparator<TecnicoFerramenta>() {
            @Override
            public int compare(TecnicoFerramenta lhs, TecnicoFerramenta rhs) {
                return lhs.getDesc_ferramenta().compareTo(rhs.getDesc_ferramenta());
            }
        });

        for(int i = 0; i < listaTecnicoFerramentaIntermediaria.size(); i++)
        {
            TecnicoFerramenta buscado = listaTecnicoFerramentaIntermediaria.get(i);
            for(int j = 0; j < listaTecnicoFerramentaIntermediaria.size(); j++)
            {
                TecnicoFerramenta varrendo = listaTecnicoFerramentaIntermediaria.get(j);
                if(varrendo.getId_ferramenta() == buscado.getId_ferramenta())
                {
                    if(i != j)
                    {
                        int qtdNova = buscado.getQtd() + varrendo.getQtd();
                        buscado.setQtd(qtdNova);
                        listaTecnicoFerramentaIntermediaria.set(i, buscado);
                        listaTecnicoFerramentaIntermediaria.remove(j);
                    }
                }
            }
        }

        listaTecnicoFerramentaFinal = listaTecnicoFerramentaIntermediaria;

        for(TecnicoFerramenta item : listaTecnicoFerramentaFinal)
        {
            Totalizador adicionar = new Totalizador();
            adicionar.setIdItem(item.getId_ferramenta());
            adicionar.setDescricao(item.getDesc_ferramenta());
            adicionar.setOutros(item.getOutros());
            adicionar.setQtd(item.getQtd());
            listaTotalizador.add(adicionar);
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaTotalizador.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterRelatorioItemAusente.notifyDataSetChanged();
    }

    public void montarEpiTecnico()
    {
        List<TecnicoEpi> listaTecnicoEpiIntermediaria = new ArrayList<>();
        List<TecnicoEpi> listaTecnicoEpiFinal = new ArrayList<>();
        listaTecnicoEpiIntermediaria.clear();
        listaTecnicoEpiFinal.clear();
        listaTotalizador.clear();

        //recupera somente associações do técnico selecionado
        for(TecnicoEpi item : listaTecnicoEpi)
        {
            if(item.getId_tecnico() == codigoTecnico)
            {
                listaTecnicoEpiIntermediaria.add(item);
            }
        }

        Collections.sort(listaTecnicoEpiIntermediaria, new Comparator<TecnicoEpi>() {
            @Override
            public int compare(TecnicoEpi lhs, TecnicoEpi rhs) {
                return lhs.getDesc_epi().compareTo(rhs.getDesc_epi());
            }
        });

        for(int i = 0; i < listaTecnicoEpiIntermediaria.size(); i++)
        {
            TecnicoEpi buscado = listaTecnicoEpiIntermediaria.get(i);
            for(int j = 0; j < listaTecnicoEpiIntermediaria.size(); j++)
            {
                TecnicoEpi varrendo = listaTecnicoEpiIntermediaria.get(j);
                if(varrendo.getId_epi() == buscado.getId_epi())
                {
                    if(i != j)
                    {
                        int qtdNova = buscado.getQtd() + varrendo.getQtd();
                        buscado.setQtd(qtdNova);
                        listaTecnicoEpiIntermediaria.set(i, buscado);
                        listaTecnicoEpiIntermediaria.remove(j);
                    }
                }
            }
        }

        listaTecnicoEpiFinal = listaTecnicoEpiIntermediaria;

        for(TecnicoEpi item : listaTecnicoEpiFinal)
        {
            Totalizador adicionar = new Totalizador();
            adicionar.setIdItem(item.getId_epi());
            adicionar.setDescricao(item.getDesc_epi());
            adicionar.setOutros(item.getOutros());
            adicionar.setQtd(item.getQtd());
            listaTotalizador.add(adicionar);
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaTotalizador.size() == 0)
            semDados.setVisibility(View.VISIBLE);
        adapterRelatorioItemAusente.notifyDataSetChanged();
    }

    public void gerarDialogoCustomizadoMostrarImagemMovimentacao()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_relatoriogerado_mostrar_imagem_movimentacao);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewDialogoRelatorioItem);
        TextView textoDescricao = (TextView) dialog.findViewById(R.id.textViewDialogoDescricaoRelatorioItem);
        ImageView imagemMostrar = (ImageView) dialog.findViewById(R.id.imagemRelatorio);
        Button botaoSair = (Button) dialog.findViewById(R.id.btCancelarDialogoRelatorioItem);

        Picasso.get().load(itemMovSelecionado.getImagem_firebase())
                .resize(480, 640)
                .centerCrop()
                .error(R.drawable.ic_erro_load_imagem)
                .placeholder(R.drawable.carregando)
                .into(imagemMostrar);


        // if button is clicked, close the custom dialog
        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void testaLista()
    {
        String textoPesquisar = pesquisar.getText().toString();
        if(qualRelatorio == 1 || qualRelatorio == 2 || qualRelatorio == 7)
        {
            if(listaTotalizador.size() != 0 && textoPesquisar.isEmpty())
            {
                createPDF();
            }
            else
            {
                if(listaFiltradaTotalizador.size() != 0 && !textoPesquisar.isEmpty())
                {
                    listaTotalizadorBKP = listaTotalizador;
                    listaTotalizador = listaFiltradaTotalizador;
                    createPDF();
                }
                else
                {
                    Toast.makeText(this, "Não há dados para serem exportados", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        if(qualRelatorio == 3 || qualRelatorio == 4)
        {
            if(listaRelatorioTecItem.size() != 0 && textoPesquisar.isEmpty())
            {
                createPDF();
            }
            else
            {
                if(listaFiltradaRelatorioTecItem.size() != 0 && !textoPesquisar.isEmpty())
                {
                    listaRelatorioTecItemBKP = listaRelatorioTecItem;
                    listaRelatorioTecItem = listaFiltradaRelatorioTecItem;
                    createPDF();
                }
                else
                {
                    Toast.makeText(this, "Não há dados para serem exportados", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        if(qualRelatorio == 5 || qualRelatorio == 6)
        {
            if(listaRelatorioMovItem.size() != 0 && textoPesquisar.isEmpty())
            {
                createPDF();
            }
            else
            {
                if(listaFiltradaRelatorioMovItem.size() != 0 && !textoPesquisar.isEmpty())
                {
                    listaRelatorioMovItemBKP = listaFiltradaRelatorioMovItem;
                    listaRelatorioMovItem = listaFiltradaRelatorioMovItem;
                    createPDF();
                }
                else
                {
                    Toast.makeText(this, "Não há dados para serem exportados", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    public File file_exportar;

    public void createPDF()
    {
        ProgressDialog progressoLogin = new ProgressDialog(this);
        progressoLogin.setTitle("Gerando a exportação dos dados:");
        progressoLogin.setMessage("Aguarde...");
        progressoLogin.setCancelable(false);
        progressoLogin.setCanceledOnTouchOutside(false);
        progressoLogin.setIndeterminate(true);

        if (checkPermission()) {

            progressoLogin.show();

            String filialMostrar = "Filial: " + filialLogada;

            SimpleDateFormat dataRelatorio = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date currentTimeRelatorio = new Date();
            String dataRelatorioMostrar = dataRelatorio.format(currentTimeRelatorio);

            SimpleDateFormat dataTitulo = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
            Date currentTime = new Date();
            String dataAtual = dataTitulo.format(currentTime);
            String nome_arquivo = "relatorio_" + dataAtual + ".pdf";

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, nome_arquivo);

            file_exportar = file;

            try{
                Document document = new Document();
                // Location to save
                PdfWriter.getInstance(document, new FileOutputStream(file));

                // Open to write
                document.open();

                // Document Settings
                document.setPageSize(PageSize.A4);
                document.addCreationDate();
                document.addAuthor("UniTecnicos");
                document.addCreator("UniTecnicos");

                /***
                 * Variables for further use....
                 */
                BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
                BaseColor azulEscuro = new BaseColor(51, 60, 101, 255);
                float mHeadingFontSize = 20.0f;
                float mValueFontSize = 26.0f;

                // LINE SEPARATOR
                LineSeparator lineSeparator = new LineSeparator();
                lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

                // Title Order Details...
                // Adding Title....
                Font mOrderDetailsTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 36.0f, Font.NORMAL, BaseColor.BLACK);
                // Creating Chunk
                Chunk mOrderDetailsTitleChunk = new Chunk("Exportação de Dados", mOrderDetailsTitleFont);
                // Creating Paragraph to add...
                Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
                // Setting Alignment for Heading
                mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
                // Finally Adding that Chunk
                document.add(mOrderDetailsTitleParagraph);

                // Fields of Order Details...
                // Adding Chunks for Title and value
                Font fonteTituloRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, mHeadingFontSize, Font.BOLD, azulEscuro);
                Chunk mTituloChunk = new Chunk(titulo_do_relatorio, fonteTituloRelatorio);
                Paragraph mTituloParagraph = new Paragraph(mTituloChunk);
                mTituloParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mTituloParagraph);

                document.add(new Paragraph(""));

                Font fonteDataRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, mHeadingFontSize, Font.NORMAL, azulEscuro);
                Chunk mDataChunk = new Chunk(dataRelatorioMostrar, fonteDataRelatorio);
                Paragraph mDataParagraph = new Paragraph(mDataChunk);
                mDataParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mDataParagraph);

                document.add(new Paragraph(""));

                Font fonteFilialRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, mHeadingFontSize, Font.BOLD, azulEscuro);
                Chunk mFilialChunk = new Chunk(filialMostrar, fonteFilialRelatorio);
                Paragraph mFilialParagraph = new Paragraph(mFilialChunk);
                mFilialParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mFilialParagraph);

                document.add(new Paragraph(""));
                document.add(new Chunk(lineSeparator));
                document.add(new Paragraph(""));
                document.add(new Paragraph(""));
                document.add(new Paragraph(""));

                if(qualRelatorio == 1 || qualRelatorio == 2 || qualRelatorio == 7) //Total Ferramentas
                {
                    PdfPTable table = new PdfPTable(3);
                    document.add(new Paragraph(""));

                    Font fonteTituloColunaRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);

                    Chunk tituloColuna1 = new Chunk("DESCRIÇÃO", fonteTituloColunaRelatorio);
                    Chunk tituloColuna2 = new Chunk("INFORMAÇÕES", fonteTituloColunaRelatorio);
                    Chunk tituloColuna3 = new Chunk("QTD", fonteTituloColunaRelatorio);

                    PdfPCell a1 = new PdfPCell();
                    PdfPCell a2 = new PdfPCell();
                    PdfPCell a3 = new PdfPCell();

                    a1.addElement(tituloColuna1);
                    a2.addElement(tituloColuna2);
                    a3.addElement(tituloColuna3);

                    table.addCell(a1);
                    table.addCell(a2);
                    table.addCell(a3);

                    for(int i = 0; i < listaTotalizador.size(); i++)
                    {

                        Totalizador inserir = listaTotalizador.get(i);

                        Font fonteListaRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
                        Font fonteListaMeioRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

                        Chunk mListaDescChunk = new Chunk(inserir.getDescricao(), fonteListaRelatorio);
                        Chunk mListaOutrosChunk = new Chunk(inserir.getOutros(), fonteListaMeioRelatorio);
                        Chunk mListaQtdChunk = new Chunk(String.valueOf(inserir.getQtd()), fonteListaRelatorio);

                        PdfPCell c1 = new PdfPCell();
                        PdfPCell c2 = new PdfPCell();
                        PdfPCell c3 = new PdfPCell();

                        c1.addElement(mListaDescChunk);
                        c2.addElement(mListaOutrosChunk);
                        c3.addElement(mListaQtdChunk);

                        table.addCell(c1);
                        table.addCell(c2);
                        table.addCell(c3);

                        table.setWidthPercentage(100);
                        table.setWidths(new float[] {47,46,7});
                    }
                    document.add(table);
                    listaTotalizador = listaTotalizadorBKP;
                }
                if(qualRelatorio == 3 || qualRelatorio == 4) //Ferramenta x Tecnico
                {
                    PdfPTable table = new PdfPTable(2);
                    document.add(new Paragraph(""));

                    Font fonteTituloColunaRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);

                    Chunk tituloColuna1 = new Chunk("TÉCNICO", fonteTituloColunaRelatorio);
                    Chunk tituloColuna2 = new Chunk("ITEM AUSENTE", fonteTituloColunaRelatorio);

                    PdfPCell a1 = new PdfPCell();
                    PdfPCell a2 = new PdfPCell();

                    a1.addElement(tituloColuna1);
                    a2.addElement(tituloColuna2);

                    table.addCell(a1);
                    table.addCell(a2);

                    for(int i = 0; i < listaRelatorioTecItem.size(); i++)
                    {

                        RelatorioTecItem inserir = listaRelatorioTecItem.get(i);

                        Font fonteListaRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
                        Font fonteListaMeioRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

                        Chunk mListaDescChunk = new Chunk(inserir.getNomeTecnico(), fonteListaRelatorio);
                        Chunk mListaOutrosChunk = new Chunk(inserir.getNomeFerramenta(), fonteListaRelatorio);

                        PdfPCell c1 = new PdfPCell();
                        PdfPCell c2 = new PdfPCell();

                        c1.addElement(mListaDescChunk);
                        c2.addElement(mListaOutrosChunk);

                        table.addCell(c1);
                        table.addCell(c2);

                        table.setWidthPercentage(100);
                        table.setWidths(new float[] {50,50});
                    }
                    document.add(table);
                    listaRelatorioTecItem = listaRelatorioTecItemBKP;
                }

                if(qualRelatorio == 5 || qualRelatorio == 6)//Movimentações do Técnico
                {
                    PdfPTable table = new PdfPTable(4);
                    document.add(new Paragraph(""));

                    Font fonteTituloColunaRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);

                    Chunk tituloColuna1 = new Chunk("DESCRICAO", fonteTituloColunaRelatorio);
                    Chunk tituloColuna2 = new Chunk("INFORMAÇÕES", fonteTituloColunaRelatorio);
                    Chunk tituloColuna3 = new Chunk("DATA", fonteTituloColunaRelatorio);
                    Chunk tituloColuna4 = new Chunk("SENTIDO", fonteTituloColunaRelatorio);

                    PdfPCell a1 = new PdfPCell();
                    PdfPCell a2 = new PdfPCell();
                    PdfPCell a3 = new PdfPCell();
                    PdfPCell a4 = new PdfPCell();

                    a1.addElement(tituloColuna1);
                    a2.addElement(tituloColuna2);
                    a3.addElement(tituloColuna3);
                    a4.addElement(tituloColuna4);

                    table.addCell(a1);
                    table.addCell(a2);
                    table.addCell(a3);
                    table.addCell(a4);

                    for(int i = 0; i < listaRelatorioMovItem.size(); i++)
                    {

                        RelatorioMovItem inserir = listaRelatorioMovItem.get(i);

                        Font fonteListaRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
                        Font fonteListaMeioRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

                        Chunk mListaDescChunk = new Chunk(inserir.getDescricao(), fonteListaRelatorio);
                        Chunk mListaOutrosChunk = new Chunk(inserir.getOutros(), fonteListaMeioRelatorio);
                        Chunk mListaDataChunk = new Chunk(inserir.getData(), fonteListaMeioRelatorio);
                        Chunk mListaSentidoChunk = new Chunk(inserir.getSentido(), fonteListaMeioRelatorio);

                        PdfPCell c1 = new PdfPCell();
                        PdfPCell c2 = new PdfPCell();
                        PdfPCell c3 = new PdfPCell();
                        PdfPCell c4 = new PdfPCell();

                        c1.addElement(mListaDescChunk);
                        c2.addElement(mListaOutrosChunk);
                        c3.addElement(mListaDataChunk);
                        c4.addElement(mListaSentidoChunk);

                        table.addCell(c1);
                        table.addCell(c2);
                        table.addCell(c3);
                        table.addCell(c4);

                        table.setWidthPercentage(100);
                        table.setWidths(new float[] { 35, 35, 15, 15});
                    }
                    document.add(table);
                    listaRelatorioMovItem = listaRelatorioMovItemBKP;
                }
                document.close();
                progressoLogin.dismiss();
            }catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            gerarDialogoCustomizadoEnviarRelatorio();
        } else {
            requestPermission();
        }
    }
    public void enviarPDFporEmail()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Abrir E-mail")
                .setMessage("O aplicativo deseja abrir o E-mail. Deseja permitir?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String[] mailto = {"@redeunifique.com.br"};

                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Exportação de Dados");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Em anexo o arquivo gerado na exportação de dados.");
                            emailIntent.setType("application/pdf");
                            Uri uri = FileProvider.getUriForFile(TelaRelatorioGerado.this, TelaRelatorioGerado.this.getPackageName() + ".provider", file_exportar);
                            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            startActivity(Intent.createChooser(emailIntent, "Send email using:"));
                        }catch(Exception e)
                        {
                            Toast.makeText(TelaRelatorioGerado.this, "Erro ao abrir o Email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        builder.show();
    }
    public void enviarPDFporWhatsApp()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Abrir WhatsApp")
                .setMessage("O aplicativo deseja abrir o WhatsApp. Deseja permitir?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("application/pdf");
                            shareIntent.setPackage("com.whatsapp");
                            Uri uri = FileProvider.getUriForFile(TelaRelatorioGerado.this, TelaRelatorioGerado.this.getPackageName() + ".provider", file_exportar);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(shareIntent, "Compartilhando no whatsapp"));
                        }catch(Exception e)
                        {
                            Toast.makeText(TelaRelatorioGerado.this, "Erro ao abrir o WhatsApp.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        builder.show();
    }
    /*
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(TelaRelatorioGerado.this, "Permissão Negada para acessar a memória do dispositivo.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }*/

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permissão Liberada", Toast.LENGTH_SHORT).show();
                    return;
                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permissão Negada", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("Você precisa liberar as permissões necessárias",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(TelaRelatorioGerado.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    public void gerarDialogoCustomizadoExportarDados()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_dialogo_exportar_relatorio);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoExportarDados);
        TextView textoDescricao = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoExportarDados);
        TextView textoDescricao1 = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoExportarDados1);
        Button botaoSair = (Button) dialog.findViewById(R.id.btCancelarDialogoExportarDados);
        Button botaoSim = (Button) dialog.findViewById(R.id.btExportarDialogoExportarDados);

        // if button is clicked, close the custom dialog
        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botaoSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testaLista();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void gerarDialogoCustomizadoEnviarRelatorio()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_dialogo_enviar_relatorio);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoEnviarRelatorio);
        TextView textoDescricao = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoEnviarRelatorio);
        Button botaoSair = (Button) dialog.findViewById(R.id.btCancelarDialogoEnviarRelatorio);
        Button botaoWhats = (Button) dialog.findViewById(R.id.btExportarDialogoEnviarRelatorioWhatsApp);
        Button botaoEmail = (Button) dialog.findViewById(R.id.btExportarDialogoEnviarRelatorioEmail);

        // if button is clicked, close the custom dialog
        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botaoWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarPDFporWhatsApp();
                dialog.dismiss();
            }
        });

        botaoEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarPDFporEmail();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}