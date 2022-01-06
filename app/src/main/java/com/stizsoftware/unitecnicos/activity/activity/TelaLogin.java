package com.stizsoftware.unitecnicos.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stizsoftware.unitecnicos.R;

public class TelaLogin extends AppCompatActivity {

    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference(); //Declaração do banco que será acessado no FB
    private FirebaseAuth usuario = FirebaseAuth.getInstance(); //declaração do autenticador do FB
    public String retorno; //retorno vindo da autentição do Firebase
    public int parametro = 0;
    public int parametroSalvarSenha = 0; //parametro para salvar o login na memoria do celular
    public String loginRecuperado, senhaRecuperado;

    private SQLiteDatabase bancoDados;

    //Declaração dos elementos da tela
    private EditText login, senha;
    private Button logar;
    private Switch salvarLogin;
    private ImageView botaoMostrarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);
        getSupportActionBar().hide();

        bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
        login = findViewById(R.id.editTextLogin);
        senha = findViewById(R.id.editTextSenha);
        logar = findViewById(R.id.logar);
        salvarLogin = findViewById(R.id.switch1);
        botaoMostrarSenha = findViewById(R.id.botaoMostrarSenha);

        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS acesso (login VARCHAR, senha VARCHAR)");

        if(recuperaLogin() != "Erro")
        {
            login.setText(recuperaLogin().toString());
            senha.setText(recuperaSenha().toString());
            salvarLogin.setChecked(true);
            parametroSalvarSenha = 1;
        }

        salvarLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    parametroSalvarSenha = 1; //salvar senha no celular
                }
                else{
                    parametroSalvarSenha = 0; //não salvar senha no celular
                }
            }
        });
        botaoMostrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parametro == 0)
                {
                    senha.setInputType(InputType.TYPE_CLASS_TEXT);
                    parametro = 1;
                }
                else{
                    senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    parametro = 0;
                }
            }
        });

        ProgressDialog progressoLogin = new ProgressDialog(this);
        progressoLogin.setTitle("Autenticando:");
        progressoLogin.setMessage("Aguarde...");
        progressoLogin.setCancelable(false);
        progressoLogin.setCanceledOnTouchOutside(false);
        progressoLogin.setIndeterminate(true);

        logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logarTexto;
                final String senhaTexto;
                final String loginSalvar;
                loginSalvar = login.getText().toString();
                logarTexto = login.getText().toString() + "@redeunifique.com.br";
                senhaTexto = senha.getText().toString();

                progressoLogin.show();

                usuario.signInWithEmailAndPassword(logarTexto, senhaTexto)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    progressoLogin.dismiss();
                                    Intent intent = new Intent(TelaLogin.this, TelaTecnico.class);
                                    startActivity(intent);
                                    Animatoo.animateFade(TelaLogin.this);
                                    Log.i("USER", "Sucesso ao logar");

                                    //salvar senha no celular
                                    if(parametroSalvarSenha == 1)
                                    {
                                        try{
                                            //Criar banco de dados
                                            //bancoDados.execSQL("CREATE TABLE IF NOT EXISTS acesso (login VARCHAR, senha VARCHAR)");
                                            bancoDados.execSQL("DELETE FROM acesso");
                                            bancoDados.execSQL("INSERT INTO acesso (login, senha) VALUES ('"+ loginSalvar +"','"+ senhaTexto +"')");

                                        }catch(Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                    {
                                        try{
                                            bancoDados.execSQL("DELETE FROM acesso");
                                        }catch(Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }else{
                                    Toast.makeText(TelaLogin.this, "Credenciais Incorretas!", Toast.LENGTH_LONG).show();
                                    Log.i("USER", "Erro ao logar");
                                    progressoLogin.dismiss();
                                }
                            }
                        });
            }
        });
    }
    public String recuperaLogin()
    {
        String login = "Erro";
        String senha = "Erro";

        //Recuperar pessoas
        Cursor cursor = bancoDados.rawQuery("SELECT login, senha FROM acesso LIMIT 1", null);

        //Recupera indices
        int indiceLogin = cursor.getColumnIndex("login");
        int indiceSenha = cursor.getColumnIndex("senha");

        if(cursor.moveToFirst()){
            login = cursor.getString(indiceLogin);
            senha = cursor.getString(indiceSenha);
        }
        return login;
    }
    public String recuperaSenha()
    {
        String login = "Erro";
        String senha = "Erro";

        //Recuperar pessoas
        Cursor cursor = bancoDados.rawQuery("SELECT login, senha FROM acesso LIMIT 1", null);

        //Recupera indices
        int indiceLogin = cursor.getColumnIndex("login");
        int indiceSenha = cursor.getColumnIndex("senha");

        if(cursor.moveToFirst()){
            login = cursor.getString(indiceLogin);
            senha = cursor.getString(indiceSenha);
        }
        return senha;
    }
}
