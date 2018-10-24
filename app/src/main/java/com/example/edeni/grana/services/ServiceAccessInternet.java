package com.example.edeni.grana.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edeni.grana.App;
import com.example.edeni.grana.R;
import com.example.edeni.grana.fragments.HomeFragment;
import com.example.edeni.grana.receiver.ReceiverInternet;

import java.util.zip.Inflater;

public class ServiceAccessInternet extends Service implements Runnable{

    ReceiverInternet receiverInternet;
    View view;
    private Handler handler = new Handler();
    long TIMER = 1500;
    String MENSAGEM = "Sem conexão, verifique o sinal de internet.";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        App.setContext(this);

        LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(App.getContext().LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_tost_message, null);

        receiverInternet = new ReceiverInternet();
        handler.postDelayed(this, TIMER);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("ServiceInternet","Inicio o serviço do Sinal de Internet");
        handler.postDelayed(this, TIMER);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void isConectado(){
        if(!receiverInternet.verificaInternet(App.getContext())){

            TextView texto = (TextView) view.findViewById(R.id.msg_erro);
            Toast toast = Toast.makeText(this, MENSAGEM, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP , 0, 0);
            texto.setText(MENSAGEM);
            toast.setView(view);
            toast.show();
        }
    }

    @Override
    public void run() {
        isConectado();
    }
}
