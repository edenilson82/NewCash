package com.example.edeni.grana.services;

import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.edeni.grana.App;
import com.example.edeni.grana.fragments.MoedasFragment;
import com.example.edeni.grana.model.Bitcoin;
import com.example.edeni.grana.model.Moedas;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class ServiceCotacao extends Service implements Runnable{

    MoedasFragment _moedasFragment;
    private Handler handler = new Handler();
    long TIMER = 1500;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        App.setContext(this);
        _moedasFragment = new MoedasFragment();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("COTACAO","Inicio o serviço de contação");
        handler.postDelayed(this, TIMER);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void run() {
        _moedasFragment.ConsultaRequestBitcoin();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
