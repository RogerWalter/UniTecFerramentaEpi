package com.stizsoftware.unitecnicos.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterEpi;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterFerramenta;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterTecnico;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterTecnicoEpi;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterTecnicoFerramenta;
import com.stizsoftware.unitecnicos.activity.auxiliar.RecyclerItemClickListener;
import com.stizsoftware.unitecnicos.activity.model.Epi;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Movimentacao;
import com.stizsoftware.unitecnicos.activity.model.Pedido;
import com.stizsoftware.unitecnicos.activity.model.Tecnico;
import com.stizsoftware.unitecnicos.activity.model.TecnicoEpi;
import com.stizsoftware.unitecnicos.activity.model.TecnicoFerramenta;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TelaDetalhesTecnico extends AppCompatActivity {

    DatabaseReference firebaseBanco = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tecnicoEpiDB, tecnicoFerramentaDB, movimentacaoDB, pedidoDB;

    FirebaseStorage storage;
    StorageReference storageReference;

    public String filialLogada;
    public int idMovimentacao = 0;
    public int idPedido = 0;

    private ProgressBar progressBar;
    private ImageView semDados;
    private EditText pesquisarFerramentaEpi;
    private TextView nomeTecnico;
    private RecyclerView recyclerView;
    private List<TecnicoFerramenta> listaTecnicoFerramenta = new ArrayList<>();
    private List<TecnicoEpi> listaTecnicoEpi = new ArrayList<>();
    private ArrayList<TecnicoFerramenta> listaTecnicoFerramentaFiltrada = new ArrayList<>();
    private ArrayList<TecnicoEpi> listaTecnicoEpiFiltrada = new ArrayList<>();
    private Button botaoMostrarFerramentas, botaoMostrarEpis, botaoVoltarTecnicos, botaoVoltarFerramentas, botaoVoltarEpis;
    private FloatingActionButton botaoLiberar, botaoAlterarNome, botaoAddItemPedido;
    private int parametro = 0; //0 - Ferramenta | 1 - EPI
    private int codigo;
    private String nome;

    private TecnicoFerramenta ferramentaSelecionada = new TecnicoFerramenta();
    private TecnicoEpi epiSelecionado = new TecnicoEpi();

    private AdapterTecnicoFerramenta adapterMostrarFerramentas = new AdapterTecnicoFerramenta(listaTecnicoFerramenta);
    private AdapterTecnicoEpi adapterMostrarEpis = new AdapterTecnicoEpi(listaTecnicoEpi);

    private ArrayList<Ferramenta> listaItens = new ArrayList<>();
    private AdapterFerramenta adapter = new AdapterFerramenta(listaItens);

    public Movimentacao novaMovimentacao = new Movimentacao();

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    public String url_imagem_salva = "aguardando-upload";
    public String diretorio_imagem_salva;

    public int posicaoNoRecyclerViewSelecionada = 0;
    public int parametroUpdateFoto = 0;

    public List<String> listaSugestoes = new ArrayList<String>();

    int idTecFerramentaAuxiliarDownload = 0;
    int idTecEpiAuxiliarDownload = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_detalhes_tecnico);
        getSupportActionBar().hide();

        listaTecnicoFerramenta.clear();
        listaTecnicoEpi.clear();

        //aqui recuperamos a filial que o usuário utilizou para se logar
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailCompleto = user.getEmail();
        filialLogada = emailCompleto.substring(0,4);

        tecnicoEpiDB = firebaseBanco.child(filialLogada).child("tecnico-epis");
        tecnicoFerramentaDB = firebaseBanco.child(filialLogada).child("tecnico-ferramentas");
        pedidoDB = firebaseBanco.child(filialLogada).child("pedidos");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        movimentacaoDB = firebaseBanco.child(filialLogada).child("movimentacoes");

        recuperaFerramentas();
        recuperaEpis();

        recuperaTecFerramenta();
        recuperaTecEpi();

        botaoMostrarFerramentas = findViewById(R.id.btVisualizarFerramentas);
        botaoMostrarEpis = findViewById(R.id.btVisualizarEpis);
        botaoLiberar = findViewById(R.id.btAdicionarNovoEquipamento);
        botaoAlterarNome = findViewById(R.id.btAlterarTecnico);
        pesquisarFerramentaEpi = findViewById(R.id.editTextPesquisaDetalhesTecnico);
        botaoVoltarTecnicos = findViewById(R.id.btTecDetalhesTec);
        botaoAddItemPedido = findViewById(R.id.btAdicionarPedidoTecnico);
        botaoVoltarFerramentas = findViewById(R.id.btFerramentaTelaDetalhesTec);
        botaoVoltarEpis = findViewById(R.id.btEpiTelaDetalhesTecnico);
        progressBar = findViewById(R.id.progressBarTelaDetalhesTecnico);
        semDados = findViewById(R.id.imageViewSemDadosTelaDetalhesTecnico);

        Bundle dados = getIntent().getExtras();
        codigo = dados.getInt("codigo");
        nome = dados.getString("nome");

        nomeTecnico = findViewById(R.id.textViewTituloDetalhesTecnico);

        nomeTecnico.setText(nome);

        recyclerView = findViewById(R.id.recyclerViewDetalhes);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapterMostrarFerramentas);

        pesquisarFerramentaEpi.setText("");
        parametro = 0;

        botaoAlterarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarDialogoCustomizadoAlterarTecnico(codigo, nome);
            }
        });


        botaoLiberar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaDetalhesTecnico.this, TelaAssociar.class);
                intent.putExtra("codigo", codigo);
                intent.putExtra("nome", nome);
                startActivity(intent);
                Animatoo.animateSlideUp(TelaDetalhesTecnico.this);
            }
        });

        botaoMostrarFerramentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterMostrarFerramentas = new AdapterTecnicoFerramenta(listaTecnicoFerramenta);
                recyclerView.setAdapter(adapterMostrarFerramentas);
                pesquisarFerramentaEpi.setText("");
                parametro = 0;
                semDados.setVisibility(View.GONE);
                if(listaTecnicoFerramenta.size() == 0)
                    semDados.setVisibility(View.VISIBLE);
            }
        });

        botaoMostrarEpis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterMostrarEpis = new AdapterTecnicoEpi(listaTecnicoEpi);
                recyclerView.setAdapter(adapterMostrarEpis);
                pesquisarFerramentaEpi.setText("");
                parametro = 1;
                semDados.setVisibility(View.GONE);
                if(listaTecnicoEpi.size() == 0)
                    semDados.setVisibility(View.VISIBLE);
            }
        });

        botaoVoltarTecnicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaDetalhesTecnico.this, TelaTecnico.class);
                startActivity(intent);
                Animatoo.animateSlideDown(TelaDetalhesTecnico.this);
            }
        });

        botaoVoltarFerramentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaDetalhesTecnico.this, TelaFerramenta.class);
                startActivity(intent);
                Animatoo.animateSlideDown(TelaDetalhesTecnico.this);
            }
        });

        botaoVoltarEpis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaDetalhesTecnico.this, TelaEpi.class);
                startActivity(intent);
                Animatoo.animateSlideDown(TelaDetalhesTecnico.this);
            }
        });

        botaoAddItemPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarDialogoAdicionarItemPedido();
            }
        });



        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String barraPesquisa = "";
                                barraPesquisa = pesquisarFerramentaEpi.getText().toString();
                                if(parametro == 0)
                                {
                                    TecnicoFerramenta tecnicoFerramenta = new TecnicoFerramenta();
                                    if(barraPesquisa.isEmpty())
                                    {
                                        tecnicoFerramenta = listaTecnicoFerramenta.get(position);
                                        posicaoNoRecyclerViewSelecionada = position;
                                        idTecFerramentaAuxiliarDownload = tecnicoFerramenta.getId();
                                    }
                                    else
                                    {
                                        tecnicoFerramenta = listaTecnicoFerramentaFiltrada.get(position);
                                        posicaoNoRecyclerViewSelecionada = position;
                                        idTecFerramentaAuxiliarDownload = tecnicoFerramenta.getId();
                                    }
                                    ferramentaSelecionada = tecnicoFerramenta;
                                    gerarDialogoCustomizadoFerramenta();

                                }
                                else
                                {
                                    TecnicoEpi tecnicoEpi = new TecnicoEpi();
                                    if(barraPesquisa.isEmpty())
                                    {
                                        tecnicoEpi = listaTecnicoEpi.get(position);
                                        posicaoNoRecyclerViewSelecionada = position;
                                        idTecEpiAuxiliarDownload= tecnicoEpi.getId();
                                    }
                                    else
                                    {
                                        tecnicoEpi = listaTecnicoEpi.get(position);
                                        posicaoNoRecyclerViewSelecionada = position;
                                        idTecEpiAuxiliarDownload= tecnicoEpi.getId();
                                    }
                                    epiSelecionado = tecnicoEpi;
                                    gerarDialogoCustomizadoEpi();
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

        pesquisarFerramentaEpi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textoBarraPesquisa = pesquisarFerramentaEpi.getText().toString();
                //if(!textoBarraPesquisa.isEmpty())
                    filter(s.toString());
            }
        });
        criaListenerParaIdMovimentacao();
        criaListenerParaIdPedido();

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
        if(listaTecnicoFerramenta.size() == 0)
        {
            if(parametro == 0)
            {
                semDados.setVisibility(View.VISIBLE);
            }
        }
    }

    private void filter(String text)
    {
        listaTecnicoEpiFiltrada.clear();
        listaTecnicoFerramentaFiltrada.clear();
        if(parametro == 0)
        {
            for(TecnicoFerramenta item : listaTecnicoFerramenta)
            {
                if(item.getDesc_ferramenta().toLowerCase().contains(text.toLowerCase()))
                {
                    listaTecnicoFerramentaFiltrada.add(item);
                }
            }
            adapterMostrarFerramentas.listaComFiltro(listaTecnicoFerramentaFiltrada);
            if(!text.isEmpty() && listaTecnicoFerramentaFiltrada.size() == 0)
            {
                semDados.setVisibility(View.VISIBLE);
            }
            else
            {
                semDados.setVisibility(View.GONE);
            }
        }
        else
        {
            for(TecnicoEpi item : listaTecnicoEpi)
            {
                if(item.getDesc_epi().toLowerCase().contains(text.toLowerCase()))
                {
                    listaTecnicoEpiFiltrada.add(item);
                }
            }
            adapterMostrarEpis.listaComFiltro(listaTecnicoEpiFiltrada);
            if(!text.isEmpty() && listaTecnicoEpiFiltrada.size() == 0)
            {
                semDados.setVisibility(View.VISIBLE);
            }
            else
            {
                semDados.setVisibility(View.GONE);
            }
        }

    }

    public void gerarDialogoCustomizadoFerramenta()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_edit_ferramenta);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoRemoverFerramenta);
        TextView textoItem = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoRemoverFerramenta);
        TextView textoOutros = (TextView) dialog.findViewById(R.id.textViewDescricaoOutrosDialogoRemoverFerramentaTecnico);
        ImageView imagemMostrar = (ImageView) dialog.findViewById(R.id.imagemFerramenta);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoRemoverFerramentasTecnicoDetalhes);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btConfirmarRemoverFerramentaTecnicoDetalhes);
        Button botaoAtualizarImagem = (Button) dialog.findViewById(R.id.btAtualizarImagemDialogoRemoverFerramentasTecnicoDetalhes);

        String diretorioFilial = filialLogada + "/";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child(ferramentaSelecionada + ".jpeg");
        String local_imagem = dateRef.toString();;


        Picasso.get().load(ferramentaSelecionada.getImagem())
                .resize(480, 640)
                .centerCrop()
                .error(R.drawable.ic_erro_load_imagem)
                .placeholder(R.drawable.carregando)
                .into(imagemMostrar);


        textoItem.setText(ferramentaSelecionada.getDesc_ferramenta());
        textoOutros.setText(ferramentaSelecionada.getOutros());


        // if button is clicked, close the custom dialog
        botaoCancelarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        botaoSalvarDialogo.setOnClickListener(new View.OnClickListener() {//botaoDesassociar
            @Override
            public void onClick(View v) {
                gerarDialogoDesassociarItem();
                dialog.dismiss();
            }
        });
        botaoAtualizarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parametroUpdateFoto = 1;
                final Dialog dialog1 = new Dialog(TelaDetalhesTecnico.this);
                dialog1.setContentView(R.layout.layout_alertdialog_tirar_ou_selecionar_foto);
                dialog1.setTitle("");

                // set the custom dialog components - text, image and button
                TextView textoTitulo = (TextView) dialog1.findViewById(R.id.textViewTituloDialogoTirarFoto);
                Button botaoTirarFoto = (Button) dialog1.findViewById(R.id.btDialogoTirarFoto);
                Button botaoSelecionarFoto = (Button) dialog1.findViewById(R.id.btDialogoSelecionarFoto);

                // if button is clicked, close the custom dialog
                botaoTirarFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent abrirCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(abrirCamera.resolveActivity(getPackageManager()) != null)
                        {
                            startActivityForResult(abrirCamera, SELECAO_CAMERA);
                        }
                        dialog1.dismiss();
                        dialog.dismiss();
                    }
                });

                botaoSelecionarFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , SELECAO_GALERIA);//one can be replaced with any action code
                        if (fotoSalvar != null) {
                            Toast.makeText(TelaDetalhesTecnico.this, "Imagem registrada com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                        }
                        dialog1.dismiss();
                        dialog.dismiss();
                    }
                });
                dialog1.show();
            }
        });
        dialog.show();
    }

    public void gerarDialogoCustomizadoEpi()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_edit_epi);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoRemoverEpi);
        TextView textoItem = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoRemoverEpi);
        TextView textoOutros = (TextView) dialog.findViewById(R.id.textViewDescricaoOutrosDialogoRemoverEpiTecnico);
        ImageView imagemMostrar = (ImageView) dialog.findViewById(R.id.imagemEpi);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoRemoverEpiTecnicoDetalhes);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btConfirmarRemoverEpiTecnicoDetalhes);

        String diretorioFilial = filialLogada + "/";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child(epiSelecionado + ".jpeg");
        String local_imagem = dateRef.toString();;

        Picasso.get().load(epiSelecionado.getImagem())
                .resize(480, 640)
                .centerCrop()
                .error(R.drawable.ic_erro_load_imagem)
                .placeholder(R.drawable.carregando)
                .into(imagemMostrar);

        textoItem.setText(epiSelecionado.getDesc_epi());
        textoOutros.setText("(" + epiSelecionado.getOutros() + ")");


        // if button is clicked, close the custom dialog
        botaoCancelarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botaoSalvarDialogo.setOnClickListener(new View.OnClickListener() { //botaoDesassociar
            @Override
            public void onClick(View v) {
                gerarDialogoDesassociarItem();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void gerarDialogoCustomizadoAlterarTecnico(int codigo, String nomeAntigo)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_alterar_nome_tecnico);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoAlterarNomeTecnico);
        TextView textoNomeAntigo = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoAlterarTecnico);
        TextView textoNovoNome = (TextView) dialog.findViewById(R.id.textNovoNomeTecnico);
        EditText entradaNovoNome = (EditText) dialog.findViewById(R.id.editTextNovoNomeTecnico);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoAlterarTecnico);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btConfirmarDialogoAlterarTecnico);
        Button botaoRemoverrDialogo = (Button) dialog.findViewById(R.id.btRemoverDialogoAlterarTecnico);


        textoNomeAntigo.setText(nomeAntigo);

        botaoRemoverrDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference tecFerDB = firebaseBanco.child(filialLogada).child("tecnico-ferramentas");
                DatabaseReference tecEpiDB = firebaseBanco.child(filialLogada).child("tecnico-epis");
                DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("tecnicos");
                tecnicosDB.child(String.valueOf(codigo)).child("status").setValue(1);
                //desassocia as ferramentas
                for(TecnicoFerramenta item : listaTecnicoFerramenta)
                {
                    String codigoExcluir = String.valueOf(item.getId());
                    tecFerDB.child(codigoExcluir).child("status").setValue(1);
                }
                //desassocia os epis
                for(TecnicoEpi item : listaTecnicoEpi)
                {
                    String codigoExcluir = String.valueOf(item.getId());
                    tecEpiDB.child(codigoExcluir).child("status").setValue(1);
                }
                dialog.dismiss();
                Intent intent = new Intent(TelaDetalhesTecnico.this, TelaTecnico.class);
                startActivity(intent);
                Animatoo.animateSlideDown(TelaDetalhesTecnico.this);
            }
        });

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
                DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("tecnicos");
                String nomeInformado = entradaNovoNome.getText().toString().toUpperCase();
                if(nomeInformado.isEmpty())
                {
                    Toast.makeText(TelaDetalhesTecnico.this, "Não foi informado um novo nome válido!", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    //Atualizar técnico
                    tecnicosDB.child(String.valueOf(codigo)).child("nome").setValue(entradaNovoNome.getText().toString());
                }
                nomeTecnico.setText(entradaNovoNome.getText().toString());
                nomeTecnico.setText(entradaNovoNome.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideDown(TelaDetalhesTecnico.this);
    }


    public void recuperaTecFerramenta() {
        tecnicoFerramentaDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaTecnicoFerramenta.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.getValue(TecnicoFerramenta.class).getId_tecnico() == codigo)
                    {
                        if(ds.getValue(TecnicoFerramenta.class).getStatus() == 0)
                        {
                            listaTecnicoFerramenta.add(ds.getValue(TecnicoFerramenta.class));
                        }
                    }
                }
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                semDados.setVisibility(View.GONE);
                if(listaTecnicoFerramenta.size() == 0)
                {
                    if(parametro == 0)
                    {
                        semDados.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
    public void recuperaTecEpi() {
        tecnicoEpiDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaTecnicoEpi.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.getValue(TecnicoEpi.class).getId_tecnico() == codigo)
                    {
                        if(ds.getValue(TecnicoEpi.class).getStatus() == 0)
                        {
                            listaTecnicoEpi.add(ds.getValue(TecnicoEpi.class));
                        }
                    }
                }
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if(parametro == 1)
                    semDados.setVisibility(View.GONE);
                if(listaTecnicoEpi.size() == 0)
                {
                    if(parametro == 1)
                    {
                        semDados.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    public void criaListenerParaIdMovimentacao()
    {
        movimentacaoDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                idMovimentacao = (int) snapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private String motivo_adicionar_final;

    public void preencherDadosMovimentacao()
    {
        SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date currentTime = new Date();
        String dataAtual = dataFormatada.format(currentTime);

        int qtd_atual_ferramenta = 0;
        int qtd_atual_epi = 0;

        novaMovimentacao.setId(idMovimentacao);
        novaMovimentacao.setId_tecnico(codigo);
        novaMovimentacao.setNome_tecnico(nome);
        novaMovimentacao.setData(dataAtual);
        novaMovimentacao.setFilial(filialLogada);
        novaMovimentacao.setTipo("-");
        if(parametroUpdateFoto == 1)
        {
            novaMovimentacao.setTipo("="); //significa um update de imagem;
        }
        if(parametro == 0) // 0 - Ferramenta | 1 - EPI
        {
            novaMovimentacao.setDescricao_item(ferramentaSelecionada.getDesc_ferramenta());
            novaMovimentacao.setOutros_item(ferramentaSelecionada.getOutros());
            qtd_atual_ferramenta = ferramentaSelecionada.getQtd();
            novaMovimentacao.setQtd_antiga(qtd_atual_ferramenta);
            novaMovimentacao.setQtd_nova(0);
            if(parametroUpdateFoto == 1)
            {
                novaMovimentacao.setQtd_nova(ferramentaSelecionada.getQtd());
            }
            novaMovimentacao.setId_grupo(0); // é ferramenta
            novaMovimentacao.setId_item(ferramentaSelecionada.getId_ferramenta());
            novaMovimentacao.setId_tec_item(ferramentaSelecionada.getId());
        }
        else //EPI
        {
            novaMovimentacao.setDescricao_item(epiSelecionado.getDesc_epi());
            novaMovimentacao.setOutros_item(epiSelecionado.getOutros());
            qtd_atual_epi = epiSelecionado.getQtd();
            novaMovimentacao.setQtd_antiga(qtd_atual_epi);
            novaMovimentacao.setQtd_nova(0);
            if(parametroUpdateFoto == 1)
            {
                novaMovimentacao.setQtd_nova(qtd_atual_epi);
            }
            novaMovimentacao.setId_grupo(1); // É epi
            novaMovimentacao.setId_item(epiSelecionado.getId_epi());
            novaMovimentacao.setId_tec_item(epiSelecionado.getId());
        }
        if(parametroUpdateFoto == 1)
        {
            novaMovimentacao.setMotivo("IMAGEM ATUALIZADA");
        }
        else
        {
            novaMovimentacao.setMotivo(motivo_adicionar_final.toUpperCase());
        }
        String diretorioFilial = filialLogada + "/";
        diretorio_imagem_salva = diretorioFilial+ String.valueOf(idMovimentacao);
        novaMovimentacao.setImagem(diretorio_imagem_salva);
    }

    public int contaFerramenta(int id)
    {
        int qtd_verificada = 0;
        for (TecnicoFerramenta item : listaTecnicoFerramenta) {
            if (item.getId_ferramenta() == id && item.getId_tecnico() == codigo) {
                qtd_verificada = item.getQtd();
            }
        }
        return qtd_verificada;
    }
    public int contaEpi(int id)
    {
        int qtd_verificada = 0;
        for (TecnicoEpi item : listaTecnicoEpi) {
            if (item.getId_epi() == id && item.getId_tecnico() == codigo) {
                qtd_verificada = item.getQtd();
            }
        }
        return qtd_verificada;
    }

    private Uri fotoSalvar;
    private Bitmap bmp;
    private int parametroCameraOuGaleria = 0;
    private int parametroComOuSemFoto = 0;

    public void gerarDialogoDesassociarItem()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_edit_desassociar_item);
        dialog.setTitle("");
        bmp = null;

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoDesassociarItem);
        TextView textoNomeItem = (TextView) dialog.findViewById(R.id.textViewDescricaoDialogoDesassociarItem);
        TextView textoInformacao = (TextView) dialog.findViewById(R.id.textViewDescricaoOutrosDialogoDesassociarItem);
        TextView textoMotivoInfo = (TextView) dialog.findViewById(R.id.textMotivoDesassociarItem);
        EditText entradaMotivo = (EditText) dialog.findViewById(R.id.editTextDesassociarItemMotivo);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoDesassociarItem);
        ImageButton botaoCameraDialogo = (ImageButton) dialog.findViewById(R.id.btCameraDesassociarItem);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btConfirmarDesassociarItem);

        if(parametro == 0)
            textoNomeItem.setText(ferramentaSelecionada.getDesc_ferramenta());
        else
            textoNomeItem.setText(epiSelecionado.getDesc_epi());

        // if button is clicked, close the custom dialog
        botaoCancelarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botaoCameraDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog1 = new Dialog(TelaDetalhesTecnico.this);
                dialog1.setContentView(R.layout.layout_alertdialog_tirar_ou_selecionar_foto);
                dialog1.setTitle("");

                // set the custom dialog components - text, image and button
                TextView textoTitulo = (TextView) dialog1.findViewById(R.id.textViewTituloDialogoTirarFoto);
                Button botaoTirarFoto = (Button) dialog1.findViewById(R.id.btDialogoTirarFoto);
                Button botaoSelecionarFoto = (Button) dialog1.findViewById(R.id.btDialogoSelecionarFoto);

                // if button is clicked, close the custom dialog
                botaoTirarFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent abrirCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(abrirCamera.resolveActivity(getPackageManager()) != null)
                        {
                            startActivityForResult(abrirCamera, SELECAO_CAMERA);
                        }
                        dialog1.dismiss();
                    }
                });

                botaoSelecionarFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , SELECAO_GALERIA);//one can be replaced with any action code
                        if (fotoSalvar != null) {
                            Toast.makeText(TelaDetalhesTecnico.this, "Imagem registrada com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                        }
                        dialog1.dismiss();
                    }
                });
                dialog1.show();
            }
        });

        botaoSalvarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String motivo_texto;
                motivo_texto = entradaMotivo.getText().toString();

                if(!motivo_texto.isEmpty()){
                    motivo_adicionar_final = motivo_texto.toUpperCase();
                    if(bmp!=null){
                        progressBar.setVisibility(View.VISIBLE);
                        preencherDadosMovimentacao();
                        salvarMovimentoFirebase();
                        pesquisarFerramentaEpi.setText("");
                        recuperaFerramentas();
                        recuperaEpis();
                        criaListenerParaIdMovimentacao();
                        adapterMostrarFerramentas = new AdapterTecnicoFerramenta(listaTecnicoFerramenta);
                        adapterMostrarEpis = new AdapterTecnicoEpi(listaTecnicoEpi);
                        if(parametro == 0)
                        {
                            recyclerView.setAdapter(adapterMostrarFerramentas);
                            if(listaTecnicoFerramenta.size() == 0)
                            {
                                semDados.setVisibility(View.VISIBLE);
                            }
                        }
                        if(parametro == 1)
                        {
                            recyclerView.setAdapter(adapterMostrarEpis);
                            if(listaTecnicoEpi.size() == 0)
                            {
                                semDados.setVisibility(View.VISIBLE);
                            }
                        }
                        dialog.dismiss();
                        progressBar.setVisibility(View.GONE);

                    }else {Toast.makeText(TelaDetalhesTecnico.this, "Não foi inserida imagem no registro!", Toast.LENGTH_LONG).show(); }
                }else{Toast.makeText(TelaDetalhesTecnico.this, "Motivo da movimentação não foi informado!", Toast.LENGTH_LONG).show(); }
            }
        });
        dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECAO_CAMERA:
                try {
                    if (resultCode == RESULT_OK) {
                        Bitmap imagemCapturada = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        bmp = imagemCapturada;
                        bmp = getResizedBitmap(bmp,920,1280);
                        parametroCameraOuGaleria = 1; //significa que foi capturada imagem na hora;
                        if (bmp != null) {             // use the same uri, that you initialized while calling camera intent
                            parametroComOuSemFoto = 1;
                            Toast.makeText(this, "Imagem registrada com sucesso!", Toast.LENGTH_SHORT).show();// do whatever you want to do with this Uri
                            if(parametroUpdateFoto == 1)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                preencherDadosMovimentacao();
                                salvarMovimentoFirebase();
                                pesquisarFerramentaEpi.setText("");
                                recuperaFerramentas();
                                recuperaEpis();
                                criaListenerParaIdMovimentacao();
                                adapterMostrarFerramentas = new AdapterTecnicoFerramenta(listaTecnicoFerramenta);
                                adapterMostrarEpis = new AdapterTecnicoEpi(listaTecnicoEpi);
                                if(parametro == 0)
                                {
                                    recyclerView.setAdapter(adapterMostrarFerramentas);
                                    if(listaTecnicoFerramenta.size() == 0)
                                    {
                                        semDados.setVisibility(View.VISIBLE);
                                    }
                                }
                                if(parametro == 1)
                                {
                                    recyclerView.setAdapter(adapterMostrarEpis);
                                    if(listaTecnicoEpi.size() == 0)
                                    {
                                        semDados.setVisibility(View.VISIBLE);
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            parametroComOuSemFoto = 0;
                            if(parametroUpdateFoto == 1)
                            {
                                return;
                            }
                        }
                    }
                } catch(Exception E) {
                    Toast.makeText(this, "Erro: " + E, Toast.LENGTH_SHORT).show();
                }
                break;
            case SELECAO_GALERIA:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    fotoSalvar = selectedImage;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), fotoSalvar);
                    }catch(Exception e)
                    {
                        Toast.makeText(this, "Erro: " + e, Toast.LENGTH_SHORT).show();
                        if(parametroUpdateFoto == 1)
                        {
                            return;
                        }
                    }
                    parametroCameraOuGaleria = 2; //significa que foi selecionada imagem da galeria
                    if(fotoSalvar != null)
                    {
                        parametroComOuSemFoto = 1;
                        Toast.makeText(this, "Imagem registrada com sucesso!", Toast.LENGTH_SHORT).show();
                        if(parametroUpdateFoto == 1)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            preencherDadosMovimentacao();
                            salvarMovimentoFirebase();
                            pesquisarFerramentaEpi.setText("");
                            recuperaFerramentas();
                            recuperaEpis();
                            criaListenerParaIdMovimentacao();
                            adapterMostrarFerramentas = new AdapterTecnicoFerramenta(listaTecnicoFerramenta);
                            adapterMostrarEpis = new AdapterTecnicoEpi(listaTecnicoEpi);
                            if(parametro == 0)
                            {
                                recyclerView.setAdapter(adapterMostrarFerramentas);
                                if(listaTecnicoFerramenta.size() == 0)
                                {
                                    semDados.setVisibility(View.VISIBLE);
                                }
                            }
                            if(parametro == 1)
                            {
                                recyclerView.setAdapter(adapterMostrarEpis);
                                if(listaTecnicoEpi.size() == 0)
                                {
                                    semDados.setVisibility(View.VISIBLE);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        parametroComOuSemFoto = 0;
                        if(parametroUpdateFoto == 1)
                        {
                            return;
                        }
                    }
                }
                break;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(resizedBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return resizedBitmap;
    }

    public void salvarMovimentoFirebase()
    {
        uploadImage();
        if(parametroUpdateFoto == 1)
        {
            if(novaMovimentacao.getId_grupo() == 0) //estamos revomendo uma ferramenta
            {
                DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("tecnico-ferramentas");
                tecnicosDB.child(String.valueOf(ferramentaSelecionada.getId())).child("imagem").setValue(url_imagem_salva);
            }
            else //estamos removendo um EPI
            {
                DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("tecnico-epis");
                tecnicosDB.child(String.valueOf(epiSelecionado.getId())).child("imagem").setValue(url_imagem_salva);
            }
            novaMovimentacao.setImagem(url_imagem_salva);
            movimentacaoDB.child(String.valueOf(idMovimentacao)).setValue(novaMovimentacao);
            criaListenerParaIdMovimentacao();
        }
        else
        {
            if(novaMovimentacao.getId_grupo() == 0) //estamos revomendo uma ferramenta
            {
                DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("tecnico-ferramentas");
                tecnicosDB.child(String.valueOf(ferramentaSelecionada.getId())).child("status").setValue(1);
                tecnicosDB.child(String.valueOf(ferramentaSelecionada.getId())).child("qtd").setValue(0);
                adapterMostrarFerramentas.notifyDataSetChanged();
                recyclerView.setAdapter(adapterMostrarFerramentas);
            }
            else //estamos removendo um EPI
            {
                DatabaseReference tecnicosDB = firebaseBanco.child(filialLogada).child("tecnico-epis");
                tecnicosDB.child(String.valueOf(epiSelecionado.getId())).child("status").setValue(1);
                tecnicosDB.child(String.valueOf(epiSelecionado.getId())).child("qtd").setValue(0);
                adapterMostrarEpis.notifyDataSetChanged();
                recyclerView.setAdapter(adapterMostrarEpis);
            }
            novaMovimentacao.setImagem(url_imagem_salva);
            movimentacaoDB.child(String.valueOf(idMovimentacao)).setValue(novaMovimentacao);
            criaListenerParaIdMovimentacao();
        }
    }
    public void uploadImage() {

        int idMovimentacaoSalvarLinkDownload = novaMovimentacao.getId();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Salvando...");
        progressDialog.show();
        String diretorioFilial = filialLogada + "/";

        //diretorio_imagem_salva = "";
        StorageReference ref = storageReference.child(diretorioFilial+ String.valueOf(idMovimentacao));
        //diretorio_imagem_salva = diretorioFilial+ String.valueOf(idMovimentacao);


        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(parametroCameraOuGaleria == 2)
            {
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            }
            else
            {
                bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            }
            byte[] dadosImagem = baos.toByteArray();
            //uploading the image
            UploadTask uploadTask2 = ref.putBytes(dadosImagem);
            uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(TelaDetalhesTecnico.this, "Salvo com sucesso!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    criaListenerParaIdMovimentacao();

                    if(parametro == 0)
                    {
                        adapterMostrarFerramentas.notifyDataSetChanged();
                        recyclerView.setAdapter(adapterMostrarFerramentas);
                    }
                    else
                    {
                        adapterMostrarEpis.notifyDataSetChanged();
                        recyclerView.setAdapter(adapterMostrarEpis);
                    }


                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            Uri downloadUri = uri;
                            url_imagem_salva = downloadUri.toString(); /// The string(file link) that you need
                            movimentacaoDB.child(String.valueOf(idMovimentacaoSalvarLinkDownload)).child("imagem").setValue(url_imagem_salva);
                            if(parametroUpdateFoto == 1)
                            {
                                if(parametro == 0) //ferramenta
                                {
                                    tecnicoFerramentaDB.child(String.valueOf(idTecFerramentaAuxiliarDownload)).child("imagem").setValue(url_imagem_salva);
                                }
                                else // epi
                                {
                                    tecnicoEpiDB.child(String.valueOf(idTecEpiAuxiliarDownload)).child("imagem").setValue(url_imagem_salva);
                                }
                                parametroUpdateFoto = 0;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TelaDetalhesTecnico.this, "Erro ao salvar: " + e, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Erro: " + e, Toast.LENGTH_SHORT).show();
        }
    }
    public Ferramenta itemAdicionarPedido = new Ferramenta();

    public void gerarDialogoAdicionarItemPedido()
    {
        String[] ITENS = new String[listaItens.size()];

        for(int i= 0; i < listaItens.size(); i++)
        {
            String desc = listaItens.get(i).getDescricao();
            ITENS[i] = desc;
        }

        final Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.layout_alertdialog_adicionar_item_pedido_tecnico);
        dialog1.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog1.findViewById(R.id.textViewTituloAdicionarItemPedido);
        TextView textoNomeItem = (TextView) dialog1.findViewById(R.id.textViewDescricaoAdicionarItemPedido);
        AutoCompleteTextView descItem = (AutoCompleteTextView) dialog1.findViewById(R.id.editTextItemAddPedido);
        EditText qtdItem = (EditText) dialog1.findViewById(R.id.editTextQtdItemAddPedido);
        Button botaoCancelarDialogo = (Button) dialog1.findViewById(R.id.btCancelarDialogoAdicionarItemPedido);
        Button botaoSalvarDialogo = (Button) dialog1.findViewById(R.id.btAdicionarItemPedido);

        ArrayAdapter<String> adapterAutoComplete = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, ITENS);
        descItem.setAdapter(adapterAutoComplete);

        qtdItem.setText("1");

        botaoCancelarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        botaoSalvarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = descItem.getText().toString();
                String qtd  = qtdItem.getText().toString();
                int qtd_valor = 0;

                if(desc.isEmpty() || qtd.isEmpty())
                {
                    Toast.makeText(TelaDetalhesTecnico.this, "Existem campos obrigatórios que não foram preenchidos!", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    try{
                        Integer.parseInt(qtd);
                    }catch (Exception e)
                    {
                        Toast.makeText(TelaDetalhesTecnico.this, "Quantidade informada é inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    qtd_valor = Integer.parseInt(qtd);
                    if(qtd_valor<=0)
                    {
                        Toast.makeText(TelaDetalhesTecnico.this, "Quantidade informada é inválida!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else
                    {
                        Pedido pedido = new Pedido();
                        pedido.setId(idPedido);
                        pedido.setDescricao(desc.toUpperCase());
                        pedido.setQtd(qtd);
                        pedido.setTecnico(nome.toUpperCase());
                        adicionarNovoItemPedido(pedido);
                        dialog1.dismiss();
                    }
                }
            }
        });
        dialog1.show();
    }

    public void adicionarNovoItemPedido(Pedido pedidoAdd)
    {
        pedidoDB.child(String.valueOf(idPedido)).setValue(pedidoAdd);
        Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
        criaListenerParaIdPedido();
    }

    public void recuperaFerramentas()
    {
        DatabaseReference ferramentaTabela = firebaseBanco.child(filialLogada).child("ferramentas");
        ferramentaTabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.getValue(Ferramenta.class).getStatus() == 0)//verificamos se o item não está inativo
                    {
                        listaItens.add(ds.getValue(Ferramenta.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void recuperaEpis()
    {
        DatabaseReference epiTabela = firebaseBanco.child(filialLogada).child("epis");
        epiTabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.getValue(Epi.class).getStatus() == 0)//verificamos se o item não está inativo
                    {
                        Ferramenta adicionar = new Ferramenta();
                        adicionar.setId(ds.getValue(Epi.class).getId());
                        adicionar.setDescricao(ds.getValue(Epi.class).getDescricao());
                        adicionar.setOutros(ds.getValue(Epi.class).getOutros());
                        adicionar.setStatus(0);
                        listaItens.add(adicionar);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void criaListenerParaIdPedido()
    {
        Query lastQuery = pedidoDB.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //idPedido = (int) snapshot.getChildrenCount() + 1;
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String ultimoId = ds.getKey();
                    idPedido = Integer.parseInt(ultimoId) + 1;
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
