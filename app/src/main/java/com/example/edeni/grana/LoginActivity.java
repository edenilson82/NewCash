package com.example.edeni.grana;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.edeni.grana.model.Usuario;
import com.example.edeni.grana.room.AppDatabase;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // DATABASE
    private AppDatabase db;

    //FIREBASE
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth    mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btn_login_google;

    //FACEBOOK
    CallbackManager callbackManager;
    LoginButton btn_login_facebook;

    private static String TITULO = "Login";
    EditText txtEmail;
    EditText txtPass;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        App.setContext(this);

        // BANCO DE DADOS
        db = AppDatabase.getInstance(this);
/*
        txtEmail = (EditText) findViewById(R.id.txt_email_usuario);
        txtPass = (EditText) findViewById(R.id.txt_senha_usuario);
        btn_login = (Button) findViewById(R.id.btn_login);
*/
        // FIREBASE
        mFirebaseAuth = FirebaseAuth.getInstance();

        btn_login_google = (SignInButton) findViewById(R.id.btnGooleLogin);
        btn_login_google.setColorScheme(2);
        btn_login_google.setBackgroundColor(Color.GREEN);

        btn_login_google.setOnClickListener(btnLoginGoogleOnClickListener);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        // INICIALIZA Facebook Login

        callbackManager = CallbackManager.Factory.create();
        btn_login_facebook = findViewById(R.id.btn_facebook_login);
        btn_login_facebook.setReadPermissions("email", "public_profile");
        btn_login_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Não foi possivel conectar a conta do Facebook",
                        Toast.LENGTH_SHORT).show();
                //FirebaseAuth.getInstance().signOut();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Não foi possivel conectar a conta do Facebook",
                        Toast.LENGTH_SHORT).show();
                //FirebaseAuth.getInstance().signOut();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean sucesso = task.isSuccessful();
                        if (sucesso) {

                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            UpdateUI(user);

                        } else {

                            Toast.makeText(LoginActivity.this, "Falha de autenticação.",
                                    Toast.LENGTH_SHORT).show();
                            UpdateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        UpdateUI(currentUser);
    }

    public void UpdateUI(FirebaseUser currentUser){

        if(currentUser != null){

            String email = currentUser.getEmail();
            String senha = currentUser.getUid();
            String token_google = currentUser.getUid();
            String nome = currentUser.getDisplayName();
            Usuario usuario = db.usuarioDao().procurarPorEmail(email);

            if(usuario != null){
                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                mainActivity.putExtra("usuario", usuario);
                startActivity(mainActivity);
                finish();
            }else{
                usuario = new Usuario(nome, email, senha, null, token_google, false);
                db.usuarioDao().inserir(usuario);

                usuario = db.usuarioDao().procurarPorEmail(email);
                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                mainActivity.putExtra("usuario", usuario);
                startActivity(mainActivity);
            }
        }

    }

    private View.OnClickListener btnLoginGoogleOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(intent, 1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.i("GoogleSignInResult", result.getStatus().toString());
            if( result.isSuccess() ){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseLogin( account );
            }else{
                Toast.makeText(LoginActivity.this, "Conta Google não localizada.", Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseLogin(GoogleSignInAccount account){

        String token = account.getIdToken();
        String email = account.getEmail();

        AuthCredential credential = GoogleAuthProvider.getCredential(token,email);

        this.mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( task.isSuccessful() ){

                            String email = task.getResult().getUser().getEmail();
                            Usuario usuario = db.usuarioDao().procurarPorEmail(email);

                            if(usuario != null){
                                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                mainActivity.putExtra("usuario", usuario);
                                startActivity(mainActivity);
                            }
                            else{

                                usuario = new Usuario("", "", email,"");
                                db.usuarioDao().inserir(usuario);
                                usuario = db.usuarioDao().procurarPorEmail(email);

                                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                mainActivity.putExtra("usuario", usuario);
                                startActivity(mainActivity);

                                //Toast.makeText(LoginActivity.this, "Usuário não localizado, efetue o cadastro.", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(LoginActivity.this,task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
/*
    public void abrirCadastroUsuario(View view) {
        Intent intent = new Intent(LoginActivity.this, CadastroDeUsuarioActivity.class);
        startActivity(intent);
    }

    public void efetuaLogin(View view) {

        boolean valido = validaLogin();

        if(valido){

            String email = txtEmail.getText().toString();
            String senha = txtPass.getText().toString();

            // pode ser que o email ja exista , evite gravar novamente
            Usuario usuario = db.usuarioDao().procurarPorEmailSenha(email, senha);

            if(usuario != null){
                limparCampos();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("login", usuario);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Usuário não localizado, efetue o cadastro.", Toast.LENGTH_SHORT).show();
                limparCampos();
            }
        }
    }

    private void limparCampos(){
        txtEmail.setText(null);
        txtPass.setText(null);
    }

    private boolean validaLogin(){

        String email = txtEmail.getText().toString();
        String senha = txtPass.getText().toString();

        if (email.equals(null) || email.equals("")) {
            Toast.makeText(this, "Campo e-mail é obrigatório.", Toast.LENGTH_SHORT).show();
            txtEmail.requestFocus();
            return false;
        }

        if (!validaEmail(email)) {
            Toast.makeText(this, "Campo E-mail incorreto.", Toast.LENGTH_SHORT).show();
            txtEmail.requestFocus();
            return false;
        }

        if (senha.equals(null) || senha.equals("")) {
            Toast.makeText(this, "Campo senha é obrigatório.", Toast.LENGTH_SHORT).show();
            txtPass.requestFocus();
            return false;
        }

        if (senha.length() < 3) {
            Toast.makeText(this, "Campo senha não pode ter menos que 3 letras/números.", Toast.LENGTH_SHORT).show();
            txtPass.requestFocus();
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
*/
    /*
    private void autenticaUsuarioGoogle(FirebaseUser currentUser) {

        if (currentUser != null) {

            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();
            String phoneNumber = currentUser.getPhoneNumber();
            String providerId = currentUser.getProviderId();
            String tokenUiID = currentUser.getUid();

            Usuario user = db.usuarioDao().procurarPorEmailSenha(email, tokenUiID);

            if (user == null) {

                user = new Usuario(email, tokenUiID);
                db.usuarioDao().inserir(user);

                user = db.usuarioDao().procurarPorEmailSenha(email, tokenUiID);
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);

        }
    }

    public void registraUsuarioGoogle(String email, String senha){

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(App.getContext(),  "Usuário criado com sucesso.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            autenticaUsuarioGoogle(user);
                        } else {

                            Toast.makeText(App.getContext(),  task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    public void efetuaLoginGoogle(View view) {

        String user_name = user.getText().toString();
        String senha = pass.getText().toString();

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(user_name, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            autenticaUsuarioGoogle(currentUser);
                            Toast.makeText(App.getContext(), "Sucesso", Toast.LENGTH_SHORT).show();

                        }else{

                            // SE O USUARIO NAO FOI ENCONTRADO, ELE CRIA UM NOVO REGISTRO.
                            String user_name = user.getText().toString();
                            String senha = pass.getText().toString();
                            registraUsuarioGoogle(user_name, senha);

                        }
                    }
                });
    }

*/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if( connectionResult != null ){
            Toast.makeText(LoginActivity.this,"Falha na autenticação: "+connectionResult.getErrorMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }
}
