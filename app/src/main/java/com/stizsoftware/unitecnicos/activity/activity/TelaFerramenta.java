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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.stizsoftware.unitecnicos.activity.adapter.AdapterFerramenta;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterTecnico;
import com.stizsoftware.unitecnicos.activity.auxiliar.RecyclerItemClickListener;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Tecnico;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TelaFerramenta extends AppCompatActivity {

    DatabaseReference firebaseBanco = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ferramentasDB;
    public String filialLogada;

    private ProgressBar progressBar;
    private ImageView semDados;
    private EditText pesquisarFerramenta;
    private Button botaoTec, botaoEpi;
    private FloatingActionButton botaoAddFerramenta;
    private RecyclerView recyclerView;
    private List<Ferramenta> listaFerramentas = new ArrayList<>();
    private ArrayList<Ferramenta> listaFiltrada = new ArrayList<>();
    public int idFerramenta = 0;

    private AdapterFerramenta adapter = new AdapterFerramenta(listaFerramentas);

    private int posicaoLista = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_ferramenta);
        getSupportActionBar().hide();

        //aqui recuperamos a filial que o usuário utilizou para se logar
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailCompleto = user.getEmail();
        filialLogada = emailCompleto.substring(0,4);

        ferramentasDB = firebaseBanco.child(filialLogada).child("ferramentas");

        botaoTec = findViewById(R.id.btTecFerramenta);
        botaoEpi = findViewById(R.id.btEpiFerramenta);
        botaoAddFerramenta = findViewById(R.id.btAddFerramenta);
        pesquisarFerramenta = findViewById(R.id.editTextPesquisaFerramenta);
        recyclerView = findViewById(R.id.recyclerViewFerramenta);
        semDados = findViewById(R.id.imageViewSemDadosTelaFerramenta);
        progressBar = findViewById(R.id.progressBarTelaFerramenta);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);
        recuperaFerramentas();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String barraPesquisa = "";
                                barraPesquisa = pesquisarFerramenta.getText().toString();
                                Ferramenta ferramenta = new Ferramenta();
                                if(barraPesquisa.isEmpty())
                                {
                                    ferramenta = listaFerramentas.get(position);
                                    posicaoLista = position;
                                }
                                else
                                {
                                    ferramenta = listaFiltrada.get(position);
                                    posicaoLista = position;
                                }
                                gerarDialogoCustomizadoEditarOuRemoverFerramenta(ferramenta.getId(), ferramenta.getDescricao(), ferramenta.getOutros());
                            }
                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        })
        );
        botaoAddFerramenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarDialogoCustomizadoAddFerramenta();
            }
        });
        botaoTec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaFerramenta.this, TelaTecnico.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaFerramenta.this);
            }
        });
        botaoEpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaFerramenta.this, TelaEpi.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(TelaFerramenta.this);
            }
        });
        criaListenerParaIdFerramenta();
        pesquisarFerramenta.addTextChangedListener(new TextWatcher() {
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(TelaFerramenta.this);
    }


    String nomeInformado;
    String outrosInformado;
    String marcaInformada;

    public void gerarDialogoCustomizadoAddFerramenta()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_add_ferramenta);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoAddFerramenta);
        TextView textoNome = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoAddFerramenta);
        TextView textoOutros = (TextView) dialog.findViewById(R.id.textViewOutrosDialogoAddFerramenta);
        EditText entradaNome = (EditText) dialog.findViewById(R.id.editTextDescFerramentaAddDialogo);
        EditText entradaOutros = (EditText) dialog.findViewById(R.id.editTextOutrosFerramentaAddDialogo);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoAddFerramenta);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btSalvarDialogoAddFerramenta);


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
                    Toast.makeText(TelaFerramenta.this, "Não foi informada uma descrição válida", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    if(outrosInformado.isEmpty())
                    {
                        outrosInformado="VAZIO";
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("ferramentas");
                    Ferramenta novaFerramenta = new Ferramenta(idFerramenta, nomeInformado, 0,outrosInformado);
                    tecnicosDB.child(String.valueOf(idFerramenta)).setValue(novaFerramenta);
                    recuperaFerramentas();
                    criaListenerParaIdFerramenta();
                    adapter = new AdapterFerramenta(listaFerramentas);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        dialog.show();
    }

    private void filter(String text)
    {
        listaFiltrada.clear();
        for(Ferramenta item : listaFerramentas)
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

    public void gerarDialogoCustomizadoEditarOuRemoverFerramenta(int id, String nomeAntigo, String outrosAntigo)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_editar_ou_remover_ferramenta);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloEditarOuRemoverFerramenta);
        TextView textoNomeAntigo = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoEditarOuRemoverFerramenta);
        TextView textoOutrosAntigo = (TextView) dialog.findViewById(R.id.textViewDescricaoOutrosDialogoEditarOuRemoverFerramenta);
        TextView textoNovoNome = (TextView) dialog.findViewById(R.id.textNovoNomeEditarOuRemoverFerramenta);
        TextView textoNovoOutros = (TextView) dialog.findViewById(R.id.textNovoOutrosEditarOuRemoverFerramenta);
        EditText entradaNovoNome = (EditText) dialog.findViewById(R.id.editTextNovoNomeEditarOuRemoverFerramenta);
        EditText entradaNovoOutros = (EditText) dialog.findViewById(R.id.editTextNovoOutrosEditarOuRemoverFerramenta);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoEditarOuRemoverFerramenta);
        Button botaoRemoverDialogo = (Button) dialog.findViewById(R.id.btRemoverDialogoEditarOuRemoverFerramenta);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btConfirmarEditarOuRemoverFerramenta);


        textoNomeAntigo.setText(nomeAntigo);
        textoOutrosAntigo.setText(outrosAntigo);

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

                DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("ferramentas");
                String textoNovoNome = entradaNovoNome.getText().toString().toUpperCase();
                String textoNovoOutros = entradaNovoOutros.getText().toString().toUpperCase();

                if(textoNovoNome.isEmpty() && textoNovoOutros.isEmpty())
                { //0-0
                    Toast.makeText(TelaFerramenta.this, "Não foram informados novos dados para serem salvos", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!textoNovoNome.isEmpty() && textoNovoOutros.isEmpty())
                {//1-0
                    //atualizamos o nome e utilizamos o mesmo "Outros"
                    tecnicosDB.child(String.valueOf(id)).child("descricao").setValue(entradaNovoNome.getText().toString());

                }
                if(textoNovoNome.isEmpty() && !textoNovoOutros.isEmpty())
                {//0-1
                    //atualizamos o "Outros" e mantemos o mesmo nome
                    tecnicosDB.child(String.valueOf(id)).child("outros").setValue(entradaNovoOutros.getText().toString());
                }
                if(!textoNovoNome.isEmpty() && !textoNovoOutros.isEmpty())
                {//1-1-0
                    //atualizamos o nome e o outros
                    tecnicosDB.child(String.valueOf(id)).child("descricao").setValue(entradaNovoNome.getText().toString());
                    tecnicosDB.child(String.valueOf(id)).child("outros").setValue(entradaNovoOutros.getText().toString());
                }
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
                pesquisarFerramenta.setText("");
                DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("ferramentas");
                tecnicosDB.child(String.valueOf(id)).child("status").setValue(1);
                recuperaFerramentas();
                criaListenerParaIdFerramenta();
                adapter = new AdapterFerramenta(listaFerramentas);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
                progressBar.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }
    public void recuperaFerramentas()
    {
        DatabaseReference ferramentaTabela = firebaseBanco.child(filialLogada).child("ferramentas");
        ferramentaTabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaFerramentas.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.getValue(Ferramenta.class).getStatus() == 0)//verificamos se o item não está inativo
                    {
                        listaFerramentas.add(ds.getValue(Ferramenta.class));
                    }
                }
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                semDados.setVisibility(View.GONE);
                if(listaFerramentas.size() == 0)
                    semDados.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void criaListenerParaIdFerramenta()
    {
        ferramentasDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                idFerramenta = (int) snapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void criarListagemPadraoFerramentas()
    {
        Ferramenta novo = new Ferramenta(1, "ALICATE CORTE DIAG. POLEGADAS", 0, ""); ferramentasDB.child("1").setValue(novo);
        novo = new Ferramenta(2, "ALICATE CRIMPADOR P/ TERMINAL", 0, ""); ferramentasDB.child("2").setValue(novo);
        novo = new Ferramenta(3, "ALICATE DE CORTE DIAG. 4 POLEGADAS", 0, ""); ferramentasDB.child("3").setValue(novo);
        novo = new Ferramenta(4, "ALICATE DE CRIMPAR RJ45", 0, ""); ferramentasDB.child("4").setValue(novo);
        novo = new Ferramenta(5, "ALICATE DE CRIMPAR RJ9", 0, ""); ferramentasDB.child("5").setValue(novo);
        novo = new Ferramenta(6, "ALICATE DECAPADOR DE FIBRA ACRILATO/JAQUETA", 0, ""); ferramentasDB.child("6").setValue(novo);
        novo = new Ferramenta(7, "ALICATE UNIVERSAL ISOLADO 1000V", 0, ""); ferramentasDB.child("7").setValue(novo);
        novo = new Ferramenta(8, "CHAVE BOCA 10MM", 0, ""); ferramentasDB.child("8").setValue(novo);
        novo = new Ferramenta(9, "CHAVE BOCA 11MM", 0, ""); ferramentasDB.child("9").setValue(novo);
        novo = new Ferramenta(10, "CHAVE BOCA 13MM", 0, ""); ferramentasDB.child("10").setValue(novo);
        novo = new Ferramenta(11, "CHAVE BOCA 17MM", 0, ""); ferramentasDB.child("11").setValue(novo);
        novo = new Ferramenta(12, "CHAVE COMBINADA 10MM", 0, ""); ferramentasDB.child("12").setValue(novo);
        novo = new Ferramenta(13, "CHAVE COMBINADA 11MM", 0, ""); ferramentasDB.child("13").setValue(novo);
        novo = new Ferramenta(14, "CHAVE COMBINADA 12MM", 0, ""); ferramentasDB.child("14").setValue(novo);
        novo = new Ferramenta(15, "CHAVE COMBINADA 13MM", 0, ""); ferramentasDB.child("15").setValue(novo);
        novo = new Ferramenta(16, "CHAVE COMBINADA 14MM", 0, ""); ferramentasDB.child("16").setValue(novo);
        novo = new Ferramenta(17, "CHAVE COMBINADA 16MM", 0, ""); ferramentasDB.child("17").setValue(novo);
        novo = new Ferramenta(18, "CHAVE COMBINADA 17MM", 0, ""); ferramentasDB.child("18").setValue(novo);
        novo = new Ferramenta(19, "CHAVE COMBINADA 18MM", 0, ""); ferramentasDB.child("19").setValue(novo);
        novo = new Ferramenta(20, "CHAVE COMBINADA 19MM", 0, ""); ferramentasDB.child("20").setValue(novo);
        novo = new Ferramenta(21, "CHAVE COMBINADA 20MM", 0, ""); ferramentasDB.child("21").setValue(novo);
        novo = new Ferramenta(22, "CHAVE COMBINADA 8MM", 0, ""); ferramentasDB.child("22").setValue(novo);
        novo = new Ferramenta(23, "CHAVE DE FENDA", 0, ""); ferramentasDB.child("23").setValue(novo);
        novo = new Ferramenta(24, "CHAVE PARA BAP", 0, ""); ferramentasDB.child("24").setValue(novo);
        novo = new Ferramenta(25, "CHAVE PHILLIPS", 0, ""); ferramentasDB.child("25").setValue(novo);
        novo = new Ferramenta(26, "ESTILETE 18MM", 0, ""); ferramentasDB.child("26").setValue(novo);
        novo = new Ferramenta(27, "FACAO 14 POLEGADAS", 0, ""); ferramentasDB.child("27").setValue(novo);
        novo = new Ferramenta(28, "MARTELO BORRACHA", 0, ""); ferramentasDB.child("28").setValue(novo);
        novo = new Ferramenta(29, "MARTELO UNHA", 0, ""); ferramentasDB.child("29").setValue(novo);
        novo = new Ferramenta(30, "MARTELO UNHA 25MM", 0, ""); ferramentasDB.child("30").setValue(novo);
        novo = new Ferramenta(31, "PA AJUNTAR RETA", 0, ""); ferramentasDB.child("31").setValue(novo);
        novo = new Ferramenta(32, "PA CAVADEIRA ARTICULADA", 0, ""); ferramentasDB.child("32").setValue(novo);
        novo = new Ferramenta(33, "PONTEIRA PHILIPS", 0, ""); ferramentasDB.child("33").setValue(novo);
        novo = new Ferramenta(34, "BORNAL", 0, ""); ferramentasDB.child("34").setValue(novo);
        novo = new Ferramenta(35, "CLIVADOR", 0, ""); ferramentasDB.child("35").setValue(novo);
        novo = new Ferramenta(36, "PARAFUSADEIRA", 0, ""); ferramentasDB.child("36").setValue(novo);
        novo = new Ferramenta(37, "LASER FIBRA", 0, ""); ferramentasDB.child("37").setValue(novo);
        novo = new Ferramenta(38, "POWER MEETER", 0, ""); ferramentasDB.child("38").setValue(novo);
    }
}
