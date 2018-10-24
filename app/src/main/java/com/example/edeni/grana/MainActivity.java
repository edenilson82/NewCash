package com.example.edeni.grana;


import android.content.Intent;
import android.os.Handler;
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
import android.widget.FrameLayout;

import com.example.edeni.grana.fragments.ConfiguracaoFragment;
import com.example.edeni.grana.fragments.ExtratoFragment;
import com.example.edeni.grana.fragments.HomeFragment;
import com.example.edeni.grana.fragments.MoedasFragment;
import com.example.edeni.grana.fragments.SaldoFragment;
import com.example.edeni.grana.receiver.ReceiverInternet;
import com.example.edeni.grana.services.ServiceAccessInternet;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity{

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private SaldoFragment saldoFragment;
    private ExtratoFragment extratoFragment;
    private MoedasFragment moedasFragment;
    private ConfiguracaoFragment configuracaoFragment;

    ReceiverInternet receiverInternet;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.setContext(this);

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
        getMenuInflater().inflate(R.menu.menu_sandwich, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.cadastro_usuario:
                Snackbar.make(
                        parentLayout,
                        "Entrou no usuario",
                        Snackbar.LENGTH_LONG
                ).show();
                break;

            case R.id.cadastro_categoria:
                Snackbar.make(
                        parentLayout,
                        "Entrou na Categoria",
                        Snackbar.LENGTH_LONG
                ).show();
                break;
            case R.id.sair:
                finish();
                System.exit(0);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
