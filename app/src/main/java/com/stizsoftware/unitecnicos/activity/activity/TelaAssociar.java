package com.stizsoftware.unitecnicos.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterEpi;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterFerramenta;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterTecnicoEpi;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterTecnicoFerramenta;
import com.stizsoftware.unitecnicos.activity.auxiliar.PhotoProvider;
import com.stizsoftware.unitecnicos.activity.auxiliar.RecyclerItemClickListener;
import com.stizsoftware.unitecnicos.activity.model.Epi;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Movimentacao;
import com.stizsoftware.unitecnicos.activity.model.TecnicoEpi;
import com.stizsoftware.unitecnicos.activity.model.TecnicoFerramenta;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TelaAssociar extends AppCompatActivity {

    DatabaseReference firebaseBanco = FirebaseDatabase.getInstance().getReference();
    DatabaseReference episDB, ferramentasDB,tecnicoEpiDB, tecnicoFerramentaDB, movimentacaoDB;

    FirebaseStorage storage;
    StorageReference storageReference;

    public String filialLogada;

    ImageView comOuSemImagem, comOuSemImagem1;
    RadioButton rbEpi, rbFerramenta;
    EditText pesquisar;
    RecyclerView recyclerView;
    ImageView semDados;
    ProgressBar progressBar;

    TextView textoItemSelecionado, textoItemSelecionadoOutros, textoComOuSemImagem, textoComOuSemImagem1;
    EditText quantidade, motivo;
    Button botaoCamera, botaoSalvar, botaoCancelar;
    int parametro = 0; // 0 - Ferramenta | 1 - EPI
    int parametroItemFoiSelecionado = 0; //serve para sabermos se o usuario clicou em um item ou não no momento de salvar
    private int codigo;
    private String nome;
    private Ferramenta ferramentaSalvar;
    private Epi epiSalvar;

    private List<Movimentacao> listaMovimentacoes = new ArrayList<>();
    private List<TecnicoFerramenta> listaTecnicoFerramentas = new ArrayList<>();
    private List<TecnicoEpi> listaTecnicoEpis = new ArrayList<>();
    private List<Ferramenta> listaFerramentas = new ArrayList<>();
    private List<Epi> listaEpis = new ArrayList<>();
    private ArrayList<Ferramenta> listaFerramentaFiltrada = new ArrayList<>();
    private ArrayList<Epi> listaEpiFiltrada = new ArrayList<>();
    private AdapterFerramenta adapterFerramenta = new AdapterFerramenta(listaFerramentas);
    private AdapterEpi adapterEpi = new AdapterEpi(listaEpis);

    private Uri fotoSalvar;

    public int idTecFerramenta = 0;
    public int idTecEpi = 0;
    public int idMovimentacao = 0;

    private static final String TEMP_IMAGE_NAME = "tempImage";

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;


    public Movimentacao novaMovimentacao = new Movimentacao();
    public TecnicoEpi novoTecnicoEpi = new TecnicoEpi();
    public TecnicoFerramenta novoTecnicoFerramenta = new TecnicoFerramenta();
    public int qtd_adicionar_final;
    public String motivo_adicionar_final;
    public String diretorio_imagem_salva;
    public int qtdAtualUltimaLiberacao;
    public int qtdUltimaLiberacao;
    public String dataUltimaLiberacao; //data em que a ferramenta foi liberada por ultimo
    public int idExistenteTecFerEpi;

    public String url_imagem_salva = "aguardando-upload";

    //serão utilizadas para fazer o update do campo "imagem" quando o link de donwload da imagem for gerado
    int idTecFerramentaAuxiliarDownload = 0;
    int idTecEpiAuxiliarDownload = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_associar);
        getSupportActionBar().hide();

        SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date currentTime = new Date();
        String dataAtual = dataFormatada.format(currentTime);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle dados = getIntent().getExtras();
        codigo = dados.getInt("codigo");
        nome = dados.getString("nome");

        //aqui recuperamos a filial que o usuário utilizou para se logar
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailCompleto = user.getEmail();
        filialLogada = emailCompleto.substring(0, 4);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        episDB = firebaseBanco.child(filialLogada).child("epis");
        ferramentasDB = firebaseBanco.child(filialLogada).child("ferramentas");
        tecnicoEpiDB = firebaseBanco.child(filialLogada).child("tecnico-epis");
        tecnicoFerramentaDB = firebaseBanco.child(filialLogada).child("tecnico-ferramentas");
        movimentacaoDB = firebaseBanco.child(filialLogada).child("movimentacoes");

        rbEpi = findViewById(R.id.rbEpi);
        rbFerramenta = findViewById(R.id.rbFerramenta);
        pesquisar = findViewById(R.id.editTextPesquisaItemLiberar);
        textoItemSelecionado = findViewById(R.id.textViewNomeItemSelecionadoLiberar);
        textoItemSelecionadoOutros = findViewById(R.id.textViewOutrosItemSelecionadoLiberar);
        quantidade = findViewById(R.id.editTextQtdItemLiberar);
        motivo = findViewById(R.id.editTextMotivoItemLiberar);
        botaoCamera = findViewById(R.id.btCamera);
        botaoSalvar = findViewById(R.id.btSalvarLiberarItem);
        botaoCancelar = findViewById(R.id.btCancelarLiberarItem);
        comOuSemImagem = findViewById(R.id.imageViewComOuSemFoto);
        comOuSemImagem1 = findViewById(R.id.imageViewComOuSemFoto1);
        textoComOuSemImagem = findViewById(R.id.textViewFotoSelecionadaOuNao);
        textoComOuSemImagem1 = findViewById(R.id.textViewFotoSelecionadaOuNao1);
        semDados = findViewById(R.id.imageViewSemDadosTelaAssociar);
        progressBar = findViewById(R.id.progressBarTelaAssociar);

        motivo.setMovementMethod(new ScrollingMovementMethod());

        recyclerView = findViewById(R.id.recyclerViewLiberarItem);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapterFerramenta);
        adapterEpi.notifyDataSetChanged();
        recuperaEpis();
        recuperaFerramentas();
        recuperaTecFerramenta();
        recuperaTecEpi();
        recuperaMovimentacoes();
        rbFerramenta.setChecked(true);
        parametro = 0;
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String barraPesquisa = "";
                                barraPesquisa = pesquisar.getText().toString();
                                if (parametro == 0) {
                                    Ferramenta ferramenta = new Ferramenta();
                                    if(barraPesquisa.isEmpty())
                                    {
                                        ferramenta = listaFerramentas.get(position);
                                    }
                                    else
                                    {
                                        ferramenta = listaFerramentaFiltrada.get(position);
                                    }
                                    textoItemSelecionado.setText(ferramenta.getDescricao());
                                    textoItemSelecionadoOutros.setText(ferramenta.getOutros());
                                    ferramentaSalvar = ferramenta;
                                    parametroItemFoiSelecionado = 1;
                                    limpaCampos();
                                    quantidade.setText("1");

                                } else {
                                    Epi epi = new Epi();
                                    if(barraPesquisa.isEmpty())
                                    {
                                        epi = listaEpis.get(position);
                                    }
                                    else
                                    {
                                        epi = listaEpiFiltrada.get(position);
                                    }
                                    textoItemSelecionado.setText(epi.getDescricao());
                                    textoItemSelecionadoOutros.setText("(" + epi.getOutros() + ")");
                                    epiSalvar = epi;
                                    parametroItemFoiSelecionado = 1;
                                    limpaCampos();
                                    quantidade.setText("1");
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

        motivo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    motivo.setSelection(0);
                }
                else
                {
                    motivo.setSelection(motivo.getText().length());
                }
            }
        });

        rbEpi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbEpi.isChecked()) {
                    adapterEpi = new AdapterEpi(listaEpis);
                    recyclerView.setAdapter(adapterEpi);
                    pesquisar.setText("");
                    limpaCampos();
                    parametro = 1;
                    semDados.setVisibility(View.GONE);
                    if(listaEpis.size() == 0)
                    {
                        semDados.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        rbFerramenta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbFerramenta.isChecked()) {
                    adapterFerramenta = new AdapterFerramenta(listaFerramentas);
                    recyclerView.setAdapter(adapterFerramenta);
                    pesquisar.setText("");
                    limpaCampos();
                    parametro = 0;
                    semDados.setVisibility(View.GONE);
                    if(listaFerramentas.size() == 0)
                    {
                        semDados.setVisibility(View.VISIBLE);
                    }
                }
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
        botaoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarDialogoSelecionarOuTirarFoto();
            }
        });
        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarMovimento();
            }
        });
        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retornarTelaDetalhesTecnico();
            }
        });
        criaListenerParaIdMovimentacao();
        criaListenerParaIdTecFerramenta();
        criaListenerParaIdTecEpi();

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        semDados.setVisibility(View.GONE);
    }

    private void limpaCampos()
    {
        quantidade.setText("");
        motivo.setText("");
        textoComOuSemImagem.setText("(Sem imagem)");
        textoComOuSemImagem1.setText("(Sem imagem)");
        comOuSemImagem.setImageResource(R.drawable.ic_baseline_cancel_24);
        comOuSemImagem1.setImageResource(R.drawable.ic_baseline_cancel_24);
        bmp = null;
    }

    private void filter(String text) {
        listaFerramentaFiltrada.clear();
        listaEpiFiltrada.clear();
        if (parametro == 0) {
            for (Ferramenta item : listaFerramentas) {
                if (item.getDescricao().toLowerCase().contains(text.toLowerCase())) {
                    listaFerramentaFiltrada.add(item);
                }
            }
            adapterFerramenta.listaComFiltro(listaFerramentaFiltrada);
            if(!text.isEmpty() && listaFerramentaFiltrada.size() == 0)
            {
                semDados.setVisibility(View.VISIBLE);
            }
            else
            {
                semDados.setVisibility(View.GONE);
            }
        } else {
            for (Epi item : listaEpis) {
                if (item.getDescricao().toLowerCase().contains(text.toLowerCase())) {
                    listaEpiFiltrada.add(item);
                }
            }
            adapterEpi.listaComFiltro(listaEpiFiltrada);
            if(!text.isEmpty() && listaEpiFiltrada.size() == 0)
            {
                semDados.setVisibility(View.VISIBLE);
            }
            else
            {
                semDados.setVisibility(View.GONE);
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
                adapterEpi.notifyDataSetChanged();
                recyclerView.setAdapter(adapterEpi);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                semDados.setVisibility(View.GONE);
                if(listaEpis.size() == 0)
                {
                    semDados.setVisibility(View.VISIBLE);
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
                adapterFerramenta.notifyDataSetChanged();
                recyclerView.setAdapter(adapterFerramenta);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                semDados.setVisibility(View.GONE);
                if(listaFerramentas.size() == 0)
                {
                    semDados.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    public void gerarDialogoSelecionarOuTirarFoto() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_tirar_ou_selecionar_foto);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloDialogoTirarFoto);
        Button botaoTirarFoto = (Button) dialog.findViewById(R.id.btDialogoTirarFoto);
        Button botaoSelecionarFoto = (Button) dialog.findViewById(R.id.btDialogoSelecionarFoto);


        // if button is clicked, close the custom dialog
        botaoTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrirCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(abrirCamera.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(abrirCamera, SELECAO_CAMERA);
                }
                dialog.dismiss();
            }
        });

        botaoSelecionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , SELECAO_GALERIA);//one can be replaced with any action code
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private Bitmap bmp;
    private int parametroCameraOuGaleria = 0;

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
                            textoComOuSemImagem.setText("(Imagem ok!)");
                            textoComOuSemImagem1.setText("(Imagem ok!)");
                            comOuSemImagem.setImageResource(R.drawable.ic_foto_selecionada);
                            comOuSemImagem1.setImageResource(R.drawable.ic_foto_selecionada);
                            Toast.makeText(this, "Imagem registrada com sucesso!", Toast.LENGTH_SHORT).show();// do whatever you want to do with this Uri
                        }
                        else
                        {
                            textoComOuSemImagem.setText("(Sem imagem)");
                            textoComOuSemImagem1.setText("(Sem imagem)");
                            comOuSemImagem.setImageResource(R.drawable.ic_baseline_cancel_24);
                            comOuSemImagem1.setImageResource(R.drawable.ic_baseline_cancel_24);
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
                    }
                    parametroCameraOuGaleria = 2; //significa que foi selecionada imagem da galeria
                    if(fotoSalvar != null)
                    {
                        textoComOuSemImagem.setText("(Imagem ok!)");
                        textoComOuSemImagem1.setText("(Imagem ok!)");
                        comOuSemImagem.setImageResource(R.drawable.ic_foto_selecionada);
                        comOuSemImagem1.setImageResource(R.drawable.ic_foto_selecionada);
                        Toast.makeText(this, "Imagem registrada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        textoComOuSemImagem.setText("(Sem imagem)");
                        textoComOuSemImagem1.setText("(Sem imagem)");
                        comOuSemImagem.setImageResource(R.drawable.ic_baseline_cancel_24);
                        comOuSemImagem1.setImageResource(R.drawable.ic_baseline_cancel_24);
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

    public void uploadImage() {

        int idMovimentacaoSalvarLinkDownload = novaMovimentacao.getId();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Salvando...");
        progressDialog.setCanceledOnTouchOutside(false);
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
                    Toast.makeText(TelaAssociar.this, "Salvo com sucesso!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    criaListenerParaIdMovimentacao();
                    criaListenerParaIdTecFerramenta();
                    criaListenerParaIdTecEpi();

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            Uri downloadUri = uri;
                            url_imagem_salva = downloadUri.toString(); /// The string(file link) that you need
                            movimentacaoDB.child(String.valueOf(idMovimentacaoSalvarLinkDownload)).child("imagem").setValue(url_imagem_salva);

                            if(parametro == 0) //ferramenta
                            {
                                tecnicoFerramentaDB.child(String.valueOf(idTecFerramentaAuxiliarDownload)).child("imagem").setValue(url_imagem_salva);
                            }
                            else // epi
                            {
                                tecnicoEpiDB.child(String.valueOf(idTecEpiAuxiliarDownload)).child("imagem").setValue(url_imagem_salva);
                            }
                            retornarTelaDetalhesTecnico();
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
                    Toast.makeText(TelaAssociar.this, "Erro ao salvar: " + e, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Erro: " + e, Toast.LENGTH_SHORT).show();
        }
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
    public void criaListenerParaIdTecEpi()
    {
        tecnicoEpiDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                idTecEpi = (int) snapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void criaListenerParaIdTecFerramenta()
    {
        tecnicoFerramentaDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                idTecFerramenta = (int) snapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void salvarMovimento()
    {
        String motivo_texto, qtd_texto;
        int qtd_valor;

        motivo_texto = motivo.getText().toString().toUpperCase();
        qtd_texto = quantidade.getText().toString();
        qtd_valor = 0;

        if(bmp!=null){
            if(!motivo_texto.isEmpty()) {
                motivo_adicionar_final = motivo_texto;
                if(!qtd_texto.isEmpty()){
                    try{
                        Integer.parseInt(qtd_texto);
                    }catch (Exception e)
                    {
                        Toast.makeText(TelaAssociar.this, "Quantidade informada é inválida!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    qtd_valor = Integer.parseInt(qtd_texto);
                    if(qtd_valor>0){
                        qtd_adicionar_final = qtd_valor;
                        if(parametroItemFoiSelecionado != 0){
                            preencherDadosMovimentacao();
                            int salvar = verificaDadosMovimentacao();
                            if(salvar == 1) //foi verificado que o técnico já possui essa ferramenta
                            {
                                gerarDialogoAlertaLiberacaoDuplicada();
                            }else
                            {
                                salvarMovimentoFirebase();
                            }
                        }else{Toast.makeText(TelaAssociar.this, "Nenhum item foi selecionado", Toast.LENGTH_LONG).show(); }
                    }else{Toast.makeText(TelaAssociar.this, "Quantidade da movimentação inválida!", Toast.LENGTH_LONG).show(); }
                }else{Toast.makeText(TelaAssociar.this, "Quantidade da movimentação não foi informada!", Toast.LENGTH_LONG).show(); }
            }else{Toast.makeText(TelaAssociar.this, "Motivo da movimentação não foi informado!", Toast.LENGTH_LONG).show(); }
        }else {Toast.makeText(TelaAssociar.this, "Não foi inserida imagem no registro!", Toast.LENGTH_LONG).show(); }
    }



    public void retornarTelaDetalhesTecnico()
    {
        Intent intent = new Intent(TelaAssociar.this, TelaDetalhesTecnico.class);
        intent.putExtra("codigo", codigo);
        intent.putExtra("nome", nome);
        startActivity(intent);
        Animatoo.animateSlideUp(TelaAssociar.this);
    }
    public void preencherDadosTecnicoFerramenta()
    {
        novoTecnicoFerramenta.setId(idTecFerramenta);
        idTecFerramentaAuxiliarDownload = idTecFerramenta;
        novoTecnicoFerramenta.setId_ferramenta(ferramentaSalvar.getId());
        novoTecnicoFerramenta.setDesc_ferramenta(ferramentaSalvar.getDescricao());
        novoTecnicoFerramenta.setOutros(ferramentaSalvar.getOutros());
        novoTecnicoFerramenta.setId_tecnico(codigo);
        novoTecnicoFerramenta.setNome_tecnico(nome);
        novoTecnicoFerramenta.setQtd(novaMovimentacao.getQtd_nova());
        novoTecnicoFerramenta.setImagem(url_imagem_salva);
        novoTecnicoFerramenta.setId_movimentacao(idMovimentacao);
        novoTecnicoFerramenta.setStatus(0);
    }

    public void preencherDadosTecnicoEpi()
    {
        novoTecnicoEpi.setId(idTecEpi);
        idTecEpiAuxiliarDownload = idTecEpi;
        novoTecnicoEpi.setId_epi(epiSalvar.getId());
        novoTecnicoEpi.setDesc_epi(epiSalvar.getDescricao());
        novoTecnicoEpi.setOutros(epiSalvar.getOutros());
        novoTecnicoEpi.setId_tecnico(codigo);
        novoTecnicoEpi.setNome_tecnico(nome);
        novoTecnicoEpi.setQtd(novaMovimentacao.getQtd_nova());
        novoTecnicoEpi.setImagem(url_imagem_salva);
        novoTecnicoEpi.setId_movimentacao(idMovimentacao);
        novoTecnicoFerramenta.setStatus(0);
    }

    public void salvarMovimentoFirebase()
    {

        if(parametro == 0) //salvamos registro na tabela Tecnico Ferramenta
        {
            uploadImage();
            preencherDadosTecnicoFerramenta();
            verificaDadosTecFerramenta();
            /*if(idExistenteTecFerEpi != 0)
            {
                novoTecnicoFerramenta.setId(idExistenteTecFerEpi);
                idTecFerramentaAuxiliarDownload = idExistenteTecFerEpi;
                tecnicoFerramentaDB.child(String.valueOf(idExistenteTecFerEpi)).setValue(novoTecnicoFerramenta);
                criaListenerParaIdTecFerramenta();
            }
            else {
                tecnicoFerramentaDB.child(String.valueOf(idTecFerramenta)).setValue(novoTecnicoFerramenta);
                criaListenerParaIdTecFerramenta();
            }*/
            tecnicoFerramentaDB.child(String.valueOf(idTecFerramenta)).setValue(novoTecnicoFerramenta);
            criaListenerParaIdTecFerramenta();
        }
        else //salvamos registro na tabela Tecnico EPI
        {
            uploadImage();
            preencherDadosTecnicoEpi();
            verificaDadosTecEpi();
            /*if(idExistenteTecFerEpi != 0)
            {
                novoTecnicoEpi.setId(idExistenteTecFerEpi);
                idTecEpiAuxiliarDownload = idExistenteTecFerEpi;
                tecnicoEpiDB.child(String.valueOf(idExistenteTecFerEpi)).setValue(novoTecnicoEpi);
                criaListenerParaIdTecEpi();
            }
            else
            {
                tecnicoEpiDB.child(String.valueOf(idTecEpi)).setValue(novoTecnicoEpi);
                criaListenerParaIdTecEpi();
            }*/
            tecnicoEpiDB.child(String.valueOf(idTecEpi)).setValue(novoTecnicoEpi);
            criaListenerParaIdTecEpi();
        }
        novaMovimentacao.setImagem(url_imagem_salva);
        movimentacaoDB.child(String.valueOf(idMovimentacao)).setValue(novaMovimentacao);
        criaListenerParaIdMovimentacao();
        criaListenerParaIdTecFerramenta();
        criaListenerParaIdTecEpi();
    }

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
        novaMovimentacao.setTipo("+");
        if(parametro == 0) // 0 - Ferramenta | 1 - EPI
        {
            novaMovimentacao.setDescricao_item(ferramentaSalvar.getDescricao());
            novaMovimentacao.setOutros_item(ferramentaSalvar.getOutros());
            qtd_atual_ferramenta = contaFerramenta(ferramentaSalvar.getId());
            novaMovimentacao.setQtd_antiga(qtd_atual_ferramenta);
            novaMovimentacao.setQtd_nova(qtd_adicionar_final);
            //novaMovimentacao.setQtd_nova(qtd_atual_ferramenta + qtd_adicionar_final);
            novaMovimentacao.setId_grupo(0); // é ferramenta
            novaMovimentacao.setId_item(ferramentaSalvar.getId());
            novaMovimentacao.setId_tec_item(idTecFerramenta);
        }
        else //EPI
        {
            novaMovimentacao.setDescricao_item(epiSalvar.getDescricao());
            novaMovimentacao.setOutros_item(epiSalvar.getOutros());
            qtd_atual_epi = contaEpi(epiSalvar.getId());
            novaMovimentacao.setQtd_antiga(qtd_atual_epi);
            novaMovimentacao.setQtd_nova(qtd_adicionar_final);
            //novaMovimentacao.setQtd_nova(qtd_atual_epi + qtd_adicionar_final);
            novaMovimentacao.setId_grupo(1); // É epi
            novaMovimentacao.setId_item(epiSalvar.getId());
            novaMovimentacao.setId_tec_item(idTecEpi);
        }
        novaMovimentacao.setMotivo(motivo_adicionar_final);
        String diretorioFilial = filialLogada + "/";
        diretorio_imagem_salva = diretorioFilial+ String.valueOf(idMovimentacao);
        novaMovimentacao.setImagem(diretorio_imagem_salva);
    }

    public int verificaDadosMovimentacao()
    {
        int salvar = 0;
        for (Movimentacao item : listaMovimentacoes) {
            if (item.getId_item() == novaMovimentacao.getId_item()) {
                if(novaMovimentacao.getQtd_antiga() > 0)
                {
                    int qtd_atual = item.getQtd_nova();
                    qtdAtualUltimaLiberacao = qtd_atual;
                    qtdUltimaLiberacao = item.getQtd_nova() - item.getQtd_antiga();
                    dataUltimaLiberacao = item.getData();
                    salvar = 1;
                }
            }
        }
        return salvar;
    }

    public void verificaDadosTecFerramenta()
    {
        for (TecnicoFerramenta item : listaTecnicoFerramentas) {
            if (item.getId_ferramenta() == novoTecnicoFerramenta.getId_ferramenta() && item.getId_tecnico() == novoTecnicoFerramenta.getId_tecnico()) {
                idExistenteTecFerEpi = item.getId();
            }
        }
    }
    public void verificaDadosTecEpi()
    {
        for (TecnicoEpi item : listaTecnicoEpis) {
            if (item.getId_epi() == novoTecnicoEpi.getId_epi() && item.getId_tecnico() == novoTecnicoEpi.getId_tecnico()) {
                idExistenteTecFerEpi = item.getId();
            }
        }
    }

    public void recuperaTecFerramenta() {
        tecnicoFerramentaDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaTecnicoFerramentas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listaTecnicoFerramentas.add(ds.getValue(TecnicoFerramenta.class));
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
                listaTecnicoEpis.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listaTecnicoEpis.add(ds.getValue(TecnicoEpi.class));
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
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    public int contaFerramenta(int id)
    {
        int qtd_verificada = 0;
        for (TecnicoFerramenta item : listaTecnicoFerramentas) {
            if (item.getId_ferramenta() == id && item.getId_tecnico() == codigo) {
                qtd_verificada = item.getQtd();
            }
        }
        return qtd_verificada;
    }
    public int contaEpi(int id)
    {
        int qtd_verificada = 0;
        for (TecnicoEpi item : listaTecnicoEpis) {
            if (item.getId_epi() == id && item.getId_tecnico() == codigo) {
                qtd_verificada = item.getQtd();
            }
        }
        return qtd_verificada;
    }

    public int opcao = -1;


    public int gerarDialogoAlertaLiberacaoDuplicada()
    {
        new AlertDialog.Builder(this)
                .setTitle("Alerta:")
                .setMessage("O técnico já possui este item.\nData da liberação: " + dataUltimaLiberacao + "\nQtd ultima liberação: " + qtdUltimaLiberacao + "\nQtd atual: " + qtdAtualUltimaLiberacao + "\n\nDeseja liberar o item mesmo assim?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        salvarMovimentoFirebase();
                    }
                })//"O técnico já possui este item.\nData da liberação: " + data + "\nQtd ultima liberação: " + qtd_ultima_liberacao + "\nQtd atual: " + qtd_atual
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(TelaAssociar.this, "Operação cancelada pelo usuário", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_alerta)
                .show();
        return opcao;
    }
}