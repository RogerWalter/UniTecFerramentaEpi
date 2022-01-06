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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.squareup.picasso.Picasso;
import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterEpi;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterFerramenta;
import com.stizsoftware.unitecnicos.activity.adapter.AdapterPedido;
import com.stizsoftware.unitecnicos.activity.auxiliar.RecyclerItemClickListener;
import com.stizsoftware.unitecnicos.activity.model.Epi;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Pedido;
import com.stizsoftware.unitecnicos.activity.model.RelatorioMovItem;
import com.stizsoftware.unitecnicos.activity.model.RelatorioTecItem;
import com.stizsoftware.unitecnicos.activity.model.TecnicoEpi;
import com.stizsoftware.unitecnicos.activity.model.TecnicoFerramenta;
import com.stizsoftware.unitecnicos.activity.model.Totalizador;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TelaListaPedido extends AppCompatActivity {

    DatabaseReference firebaseBanco = FirebaseDatabase.getInstance().getReference();
    DatabaseReference pedidoDB;
    public String filialLogada;
    private ArrayList<Pedido> listaItens = new ArrayList<>();
    private ArrayList<Pedido> listaItensBKP = new ArrayList<>();
    private ArrayList<Pedido> listaFiltradaItens = new ArrayList<>();
    private AdapterPedido adapter = new AdapterPedido(listaItens);

    private EditText pesquisar;
    private Button sair;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageView semdados;
    private FloatingActionButton limpar, exportar;

    private Pedido pedidoSelecionado = new Pedido();

    public int parametroBotao = 0; // 1-Excluir Item || 2-limpar lista
    public int idDeletar = 0;

    public int posicaoLista = 0;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_lista_pedido);
        getSupportActionBar().hide();

        listaItens.clear();

        //aqui recuperamos a filial que o usuário utilizou para se logar
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailCompleto = user.getEmail();
        filialLogada = emailCompleto.substring(0,4);

        pedidoDB = firebaseBanco.child(filialLogada).child("pedidos");

        pesquisar = findViewById(R.id.editTextPesquisaPedido);
        recyclerView = findViewById(R.id.recyclerViewPedido);
        progressBar = findViewById(R.id.progressBarListaPedido);
        semdados = findViewById(R.id.imageViewSemDadosTelaListaPedido);
        limpar = findViewById(R.id.floatingActionButtonLimparPedido);
        exportar = findViewById(R.id.floatingActionExportarPedido);
        sair = findViewById(R.id.btSairTelaPedidos);

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        pesquisar.setVisibility(View.GONE);
        semdados.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Pedido clicado = listaItens.get(position);
                                posicaoLista = position;
                                idDeletar = clicado.getId();
                                pedidoSelecionado = clicado;
                                parametroBotao = 1; //excluir item individual
                                gerarDialogoExcluirPedido();

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        })
        );
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaListaPedido.this, TelaTecnico.class);
                startActivity(intent);
                Animatoo.animateSlideUp(TelaListaPedido.this);
            }
        });

        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parametroBotao = 2; //limpar
                if(listaItens.size() == 0)
                {
                    Toast.makeText(TelaListaPedido.this, "Não há dados para excluir", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    gerarDialogoExcluirPedido();
                }
            }
        });
        exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listaItens.size() == 0)
                {
                    Toast.makeText(TelaListaPedido.this, "Não há dados para exportar", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    gerarDialogoCustomizadoExportarDados();
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
                filter(s.toString());
            }
        });
        recuperaPedido();
    }

    private ArrayList<Pedido> listaPedidoFiltrada = new ArrayList<>();

    private void filter(String text)
    {
        listaPedidoFiltrada.clear();
        for(Pedido item : listaItens)
        {
            if(item.getDescricao().toLowerCase().contains(text.toLowerCase())||item.getTecnico().toLowerCase().contains(text.toLowerCase()))
            {
                listaPedidoFiltrada.add(item);
            }
        }
        adapter.listaComFiltro(listaPedidoFiltrada);
        listaFiltradaItens = listaPedidoFiltrada;
        if(!text.isEmpty() && listaPedidoFiltrada.size() == 0)
        {
            semdados.setVisibility(View.VISIBLE);
        }
        else
        {
            semdados.setVisibility(View.GONE);
        }
    }

    public void recuperaPedido() {
        pedidoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaItens.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listaItens.add(ds.getValue(Pedido.class));
                }
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                pesquisar.setVisibility(View.VISIBLE);
                semdados.setVisibility(View.GONE);
                if(listaItens.size() == 0)
                    semdados.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    public void gerarDialogoExcluirPedido()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_alertdialog_excluir_item_pedido);
        dialog.setTitle("");

        // set the custom dialog components - text, image and button
        TextView textoTitulo = (TextView) dialog.findViewById(R.id.textViewTituloRemoverItemPedido);
        TextView textoItem = (TextView) dialog.findViewById(R.id.textViewDescricaoRemoverItemPedido);
        Button botaoCancelarDialogo = (Button) dialog.findViewById(R.id.btCancelarDialogoRemoverItemPedido);
        Button botaoSalvarDialogo = (Button) dialog.findViewById(R.id.btRemoverItemPedido);

        if(parametroBotao == 1)
        {
            textoItem.setText("Deseja realmente excluir este item?");
        }
        else
        {
            textoItem.setText("Deseja realmente limpar a lista do pedido?");
        }


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
                if(parametroBotao==1)
                {
                    pedidoDB.child(String.valueOf(idDeletar)).removeValue();
                    recuperaPedido();
                    adapter = new AdapterPedido(listaItens);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else
                {
                    pedidoDB.removeValue();
                    listaItens.clear();
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
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
                com.itextpdf.text.Document document = new Document();
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
                Chunk mTituloChunk = new Chunk("Listagem de Pedidos dos Técnicos", fonteTituloRelatorio);
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

                PdfPTable table = new PdfPTable(3);
                document.add(new Paragraph(""));

                Font fonteTituloColunaRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);

                Chunk tituloColuna1 = new Chunk("DESCRIÇÃO", fonteTituloColunaRelatorio);
                Chunk tituloColuna2 = new Chunk("QTD", fonteTituloColunaRelatorio);
                Chunk tituloColuna3 = new Chunk("TÉCNICO", fonteTituloColunaRelatorio);

                PdfPCell a1 = new PdfPCell();
                PdfPCell a2 = new PdfPCell();
                PdfPCell a3 = new PdfPCell();

                a1.addElement(tituloColuna1);
                a2.addElement(tituloColuna2);
                a3.addElement(tituloColuna3);

                table.addCell(a1);
                table.addCell(a2);
                table.addCell(a3);

                for(int i = 0; i < listaItens.size(); i++)
                {

                    Pedido inserir = listaItens.get(i);

                    Font fonteListaRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
                    Font fonteListaMeioRelatorio = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

                    Chunk mListaDescChunk = new Chunk(inserir.getDescricao(), fonteListaRelatorio);
                    Chunk mListaOutrosChunk = new Chunk(String.valueOf(inserir.getQtd()), fonteListaMeioRelatorio);
                    Chunk mListaQtdChunk = new Chunk(inserir.getTecnico(), fonteListaRelatorio);

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
                    table.setWidths(new float[] {47, 7, 46});
                }
                document.add(table);
                listaItens = listaItensBKP;
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

    public void testaLista()
    {
        String textoPesquisar = pesquisar.getText().toString();
        if(listaItens.size() != 0 && textoPesquisar.isEmpty())
        {
            listaItensBKP = listaItens;
            createPDF();
        }
        else
        {
            if(listaFiltradaItens.size() != 0 && !textoPesquisar.isEmpty())
            {
                listaItensBKP = listaItens;
                listaItens = listaFiltradaItens;
                createPDF();
            }
            else
            {
                Toast.makeText(this, "Não há dados para serem exportados", Toast.LENGTH_LONG).show();
                return;
            }
        }
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
                            Uri uri = FileProvider.getUriForFile(TelaListaPedido.this, TelaListaPedido.this.getPackageName() + ".provider", file_exportar);
                            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            startActivity(Intent.createChooser(emailIntent, "Send email using:"));
                        }catch(Exception e)
                        {
                            Toast.makeText(TelaListaPedido.this, "Erro ao abrir o Email.", Toast.LENGTH_SHORT).show();
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
                            Uri uri = FileProvider.getUriForFile(TelaListaPedido.this, TelaListaPedido.this.getPackageName() + ".provider", file_exportar);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(shareIntent, "Compartilhando no whatsapp"));
                        }catch(Exception e)
                        {
                            Toast.makeText(TelaListaPedido.this, "Erro ao abrir o WhatsApp.", Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(TelaListaPedido.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

}