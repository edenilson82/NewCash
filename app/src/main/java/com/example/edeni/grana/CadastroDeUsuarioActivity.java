package com.example.edeni.grana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.edeni.grana.model.Usuario;
import com.example.edeni.grana.room.AppDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CadastroDeUsuarioActivity extends AppCompatActivity {

    // DATABASE
    private AppDatabase db;

    private static String TITULO = "Cadastro de Usuário";
    Toolbar toolbarCadastroUsuario;

    private EditText txtNome;
    private EditText txtUserName;
    private EditText txtEmail;
    private EditText txtSenha;
    private Button btnSalvar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_de_usuario);

        // BANCO DE DADOS
        db = AppDatabase.getInstance(this);

        toolbarCadastroUsuario = findViewById(R.id.toolbarCadastroUsuario);
        toolbarCadastroUsuario.setTitle(TITULO);
        setSupportActionBar(toolbarCadastroUsuario);

        txtNome = findViewById(R.id.txt_nome_usuario);
        txtUserName = findViewById(R.id.txt_user_name);
        txtEmail = findViewById(R.id.txt_email);
        txtSenha = findViewById(R.id.txt_senha);

    }

    private boolean validaUsuario(){

        String nome = txtNome.getText().toString().toLowerCase();
        String username = txtUserName.getText().toString().toLowerCase();
        String email = txtEmail.getText().toString().toLowerCase();
        String pass = txtSenha.getText().toString();

        if (nome.equals(null) || nome.equals("")) {
            Toast.makeText(this, "Campo Nome é obrigatório.", Toast.LENGTH_SHORT).show();
            txtUserName.requestFocus();
            return false;
        }

        if (username.equals(null) || username.equals("")) {
            Toast.makeText(this, "Campo Login é obrigatório.", Toast.LENGTH_SHORT).show();
            txtUserName.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            Toast.makeText(this, "Campo Login não pode ter menos que 3 letras/números.", Toast.LENGTH_SHORT).show();
            txtUserName.requestFocus();
            return false;
        }

        if (email.equals(null) || email.equals("")) {
            Toast.makeText(this, "Campo E-mail é obrigatório.", Toast.LENGTH_SHORT).show();
            txtEmail.requestFocus();
            return false;
        }

        if (!validaEmail(email)) {
            Toast.makeText(this, "Campo E-mail incorreto.", Toast.LENGTH_SHORT).show();
            txtNome.requestFocus();
            return false;
        }

        if (pass.equals(null) || pass.equals("")) {
            Toast.makeText(this, "Campo Senha é obrigatório.", Toast.LENGTH_SHORT).show();
            txtSenha.requestFocus();
            return false;
        }

        if (pass.length() < 3) {
            Toast.makeText(this, "Campo Senha não pode ter menos que 3 letras/números.", Toast.LENGTH_SHORT).show();
            txtSenha.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validaEmail(String email){

        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }

    public void salvarUsuario(View view) {

        boolean valido = validaUsuario();
        String nome = txtNome.getText().toString().toLowerCase();
        String username = txtUserName.getText().toString().toLowerCase();
        String email = txtEmail.getText().toString().toLowerCase();
        String pass = txtSenha.getText().toString();

        if(valido){

            Usuario user = new Usuario(nome, username, email, pass);

            try{
                db.usuarioDao().inserir(user);
                Toast.makeText(this, "Usuário cadastrado com sucesso.", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Toast.makeText(this, "Ocorreu algum erro ao salvar o registro.", Toast.LENGTH_SHORT).show();
                return;
            }
            // envia para a MainACTIVITY
            Intent intent = new Intent(CadastroDeUsuarioActivity.this, MainActivity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);

        }else{
            Toast.makeText(this, "Ocorreu algum erro, verifique novamente os dados.", Toast.LENGTH_SHORT).show();
            txtNome.requestFocus();
            return;
        }

    }
}
