package com.example.edeni.grana;


import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.edeni.grana.fragments.AlertasFragment;
import com.example.edeni.grana.fragments.ConfiguracaoFragment;
import com.example.edeni.grana.fragments.ExtratoFragment;
import com.example.edeni.grana.fragments.HomeFragment;
import com.example.edeni.grana.fragments.MoedasFragment;
import com.example.edeni.grana.fragments.SaldoFragment;
import com.example.edeni.grana.model.Categoria;
import com.example.edeni.grana.receiver.ReceiverInternet;
import com.example.edeni.grana.room.AppDatabase;

public class MainActivity extends AppCompatActivity{

    // DATABASE
    private AppDatabase db;

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private SaldoFragment saldoFragment;
    private ExtratoFragment extratoFragment;
    private MoedasFragment moedasFragment;
    private ConfiguracaoFragment configuracaoFragment;
    private AlertasFragment alertasFragment;

    ReceiverInternet receiverInternet;
    View parentLayout;

    // CADASTRO DE ALERTAS

    boolean isRadioCondicaoAlertaChecked;
    boolean isRadioTipoDeAlertChecked;
    String txtDescricaoAlerta;
    EditText valorAlerta;


    // FECHA


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.setContext(this);

        // BANCO DE DADOS
        db = AppDatabase.getInstance(this);

        //App.getContext().startService(new Intent(App.getContext(), ServiceAccessInternet.class));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // criado somente para poder utilizar o Snackbar
        parentLayout = findViewById(android.R.id.content);

        frameLayout = (FrameLayout) findViewById(R.id.main_frame);

        // tabs dos fragments
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        homeFragment = new HomeFragment();
        saldoFragment = new SaldoFragment();
        extratoFragment = new ExtratoFragment();
        configuracaoFragment = new ConfiguracaoFragment();
        moedasFragment = new MoedasFragment();
        alertasFragment = new AlertasFragment();

        setFragment(homeFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(homeFragment);
                        return true;
                    case R.id.nav_saldo:
                        bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(saldoFragment);
                        return true;
                        /*
                    case R.id.nav_extrato:
                        bottomNavigationView.setItemBackgroundResource(R.color.colorPrimaryDark);
                        setFragment(extratoFragment);
                        return true;
                        */
                    case R.id.nav_moeda:
                        bottomNavigationView.setItemBackgroundResource(R.color.colorPrimaryDark);
                        setFragment(moedasFragment);
                        return true;
                        /*
                    case R.id.nav_config:
                        bottomNavigationView.setItemBackgroundResource(R.color.colorPrimaryDark);
                        setFragment(configuracaoFragment);
                        return true;
                        */
                    default:
                        return false;
                }
            }
        });


        // CADASTRO DE ALERTAS

    }


    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    // MENU PRINCIPARL COM ALERTAS,  CADASTRO USUARIO, CADASTRO CATEGORIA E SAIR
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.alertas:
                setFragment(alertasFragment);
                break;
            case R.id.cadastro_usuario:
                Snackbar.make(
                        parentLayout,
                        "Entrou no usuario",
                        Snackbar.LENGTH_LONG
                ).show();

                break;

            case R.id.cadastro_categoria:
                cadastroDeCategoriaDialog();
                break;
            case R.id.sair:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void cadastroDeCategoriaDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.cadastro_categoria);

        final Button confirmar = (Button) dialog.findViewById(R.id.btn_Confirmar);
        final Button cancelar = (Button) dialog.findViewById(R.id.btn_Cancelar);

        confirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final EditText nomeCategoria = (EditText) dialog.findViewById(R.id.txtNomeNovaCategoria);
                boolean valido = salvaCategoria(nomeCategoria.getText().toString());

                if(valido)
                    dialog.dismiss();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //finaliza o dialog
                dialog.dismiss();
            }
        });
        //exibe na tela o dialog
        dialog.show();
    }

    private boolean salvaCategoria(String nomeCategoria){

       if(nomeCategoria.equals(null) || nomeCategoria.equals("")){

           Toast.makeText(this, "Preencha o campo Nova Categoria.", Toast.LENGTH_SHORT).show();

           return false;
       }else{

           nomeCategoria = nomeCategoria.toLowerCase();
           Categoria categoria = db.categoriaDao().procurarPorNome(nomeCategoria);

           if(categoria == null){
               categoria = new Categoria(nomeCategoria);
               db.categoriaDao().inserir(categoria);
               String mensagem = "Categoria " + nomeCategoria + " criada com sucesso.";
               Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
           }else{
               db.categoriaDao().alterar(categoria);
               String mensagem = "Nome da Categoria " + categoria.getNomeCategoria() + " foi alterado para " + nomeCategoria;
               Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
           }
           return true;
       }
    }

    // chamado no botao salvar da janela de alertas
    public void salvaAlerta(View view) {

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_saldo:
                if (checked){
                    txtDescricaoAlerta = ((RadioButton) view).getText().toString();
                    isRadioTipoDeAlertChecked = checked;
                }
                    break;
            case R.id.radio_tipo_operacao_credito:
                if (checked){
                    txtDescricaoAlerta = ((RadioButton) view).getText().toString();
                    isRadioTipoDeAlertChecked = checked;
                }
                    break;
            case R.id.radio_tipo_operacao_despesa:
                if (checked){
                    txtDescricaoAlerta = ((RadioButton) view).getText().toString();
                    isRadioTipoDeAlertChecked = checked;
                }
                    break;
            case R.id.radio_condicao_maior:
                if (checked){
                    txtDescricaoAlerta = ((RadioButton) view).getText().toString();
                    isRadioCondicaoAlertaChecked = checked;
                }
                    break;
            case R.id.radio_condicao_menor:
                if (checked){
                    txtDescricaoAlerta = ((RadioButton) view).getText().toString();
                    isRadioCondicaoAlertaChecked = checked;
                }
                    break;
        }
    }

}
