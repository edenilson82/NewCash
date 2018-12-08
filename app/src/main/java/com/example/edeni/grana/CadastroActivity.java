package com.example.edeni.grana;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.arch.lifecycle.LiveData;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.EGLExt;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edeni.grana.dao.EnderecoDao;
import com.example.edeni.grana.fragments.SaldoFragment;
import com.example.edeni.grana.model.Alerta;
import com.example.edeni.grana.model.Categoria;
import com.example.edeni.grana.model.Endereco;
import com.example.edeni.grana.model.Operacao;
import com.example.edeni.grana.model.Usuario;
import com.example.edeni.grana.room.AppDatabase;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import me.drakeet.materialdialog.MaterialDialog;

public class CadastroActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static String TITULO_ORIGINAL = "Cadastro de Operação";
    private static String TITULO_ALTERAR = "Editar Operação";
    private static int POSICAO_MENU_EXCLUIR = 1;
    private static int POSICAO_MENU_SANDUICH = 2;
    private static int POSICAO_MENU_SAIR = 1;

    Toolbar toolbarCadastro;

    // DATABASE
    private AppDatabase db;

    // utilizada para passar o CONTEXT da activity
    View parentLayout;

    // CAMPOS DA TELA
    Usuario usuario;
    private TextView txt_id_usuario_logado;

    private TextView lblCategoria;
    private TextView lblOperacao; // é o TIPO
    private EditText txtDescricao;
    private EditText txtValor;
    private TextView displayData;
    private TextView displayDataHidden;
    private Spinner spinnerOperacao;
    private Spinner spinCategoria;

    private MenuItem btnExcluir;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Endereco enderecoSelecionado;

    // MAPA
    // MAPA
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    RelativeLayout relativeLayoutMapa;

    // DIALOG de permissoes
    private MaterialDialog mMaterialDialog;
    public static final int REQUEST_PERMISSIONS_CODE = 128;

    private static final int PERMISSION_REQUEST_CODE = 7001;
    private static final int PLAY_SERVICE_REQUEST = 7002;

    private static final int UPDATE_INTERVAL = 7000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private PlaceAutocompleteFragment placeAutocompleteFragment;

    Marker marker;

    Place place;

    LatLng latLong;
    LatLng meuEnderecoLatLong;

    EditText txtEndereco;
    // FECHA MAPA


    //  MODELS
    List<String> tipoOperacoes;
    List<Categoria> categorias;

    Operacao operacao;
    Endereco endereco;
    //  Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);

        App.setContext(this);

        toolbarCadastro = findViewById(R.id.toolbarSalvar);
        toolbarCadastro.setTitle(TITULO_ORIGINAL);
        setSupportActionBar(toolbarCadastro);

        // utilizado somente no SNACK BAR
        parentLayout = findViewById(android.R.id.content);
        relativeLayoutMapa = this.findViewById(R.id.relativelayout_map);

        // BANCO DE DADOS
        db = AppDatabase.getInstance(this);

        // MAPA

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


       if (checkPlayServices()) {
            buildGoogleApiClient(); // Pega a posição do meu celular
            createLocationRequest();
       }

        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        txtEndereco = (EditText) placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
        txtEndereco.setHint("Informe o endereço.");
        txtEndereco.setTextSize(12);

        placeAutocompleteFragment.setFilter(new AutocompleteFilter.Builder().setCountry("BR").build());

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // ENTRA AQUI QUANDO PESQUISA O ENDEREÇO
                latLong = place.getLatLng(); // pega Objeto LatLong
                String rua = (String) place.getName();
                String latitude = String.valueOf(latLong.latitude);
                String longitude = String.valueOf(latLong.longitude);
                String enderecoCompleto = (String) place.getAddress(); // pega o endereço completo com cep

                if(operacao != null){

                    Integer endereco_id = operacao.getEndereco_Id();
                    if(endereco_id != null){
                        endereco = db.enderecoDao().procurarPorId(endereco_id);

                        endereco.setEnderecoCompleto(enderecoCompleto);
                        endereco.setRua(rua);
                        endereco.setLatitude(latitude);
                        endereco.setLongitude(longitude);

                        db.enderecoDao().alterar(endereco);
                    }else{
                        endereco = new Endereco(enderecoCompleto, rua, latitude, longitude);
                        db.enderecoDao().inserir(endereco);
                    }
                }
                else{
                    endereco = new Endereco(enderecoCompleto, rua, latitude, longitude);
                    db.enderecoDao().inserir(endereco);
                }

                if (marker != null) {
                    marker.remove();
                }
                if (mMap != null) {

                    relativeLayoutMapa.setVisibility(View.VISIBLE);

                    CriaCirculoMapa(latLong.latitude, latLong.longitude);

                    Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLong).title(place.getName().toString()).visible(true));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLong.latitude, latLong.longitude), 16.0f));
                    newMarker.showInfoWindow();
                }

            }

            @Override
            public void onError(Status status) {
                Toast.makeText(CadastroActivity.this, "" + status.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        // CAMPOS DA TELA
        txt_id_usuario_logado = (TextView) findViewById(R.id.txt_usuario_logado);

        lblCategoria = (TextView) findViewById(R.id.lbl_categoria);
        lblOperacao = (TextView) findViewById(R.id.lbl_operacao);

        txtDescricao = (EditText) findViewById(R.id.txtDescricao);
        txtValor = (EditText) findViewById(R.id.txtValor);
        displayData = (TextView) findViewById(R.id.txtData);
        displayDataHidden = (TextView) findViewById(R.id.txtDataHidden);
        btnExcluir = (MenuItem) findViewById(R.id.btn_excluir);
        spinCategoria = (Spinner) findViewById(R.id.spinner_categoria);
        spinnerOperacao = (Spinner) findViewById(R.id.spinnertipo);

        CriaComboTipo();

        // Aqui tenta forçar o Focus no Spinner de Categoria
        spinCategoria.setFocusable(true);
        spinCategoria.setFocusableInTouchMode(true);
        spinCategoria.requestFocus();

        CriaComboCategoria();

        // aqui carrega o objeto OPERAÇÃO que vem da HOME ACTIVITY
        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        // aqui carrega o objeto OPERAÇÃO que vem da Tela MOVIMENTOS
        operacao = (Operacao) getIntent().getSerializableExtra("operacao");
        if (operacao != null) {

            // Procura no banco se encontra um usuario com este ID
            usuario = db.loginDao().procurarPorId(operacao.getUsuario_id());

            toolbarCadastro.setTitle(TITULO_ALTERAR);

            // Mostro a Label de Categoria e a Label Operação
            lblCategoria.setVisibility(View.VISIBLE);
            lblOperacao.setVisibility(View.VISIBLE);

            txtDescricao.setText(operacao.getDescricao());
            txtValor.setText(Double.toString(operacao.getValor()));
            displayData.setText(operacao.getData());

            Integer endereco_id = operacao.getEndereco_Id();
            if(endereco_id != null){
                enderecoSelecionado = db.enderecoDao().procurarPorId(endereco_id);
            }

            if(enderecoSelecionado != null)
                txtEndereco.setText(enderecoSelecionado.getEnderecoCompleto());

            // exibe a label acima da data selecionada
            displayDataHidden.setVisibility(View.VISIBLE);

            if ("Selecione".equals(operacao.getTipo())) {
                spinnerOperacao.setSelection(0);
            } else if (operacao.getTipo().equals("Crédito")) {
                spinnerOperacao.setSelection(1);
            } else {
                spinnerOperacao.setSelection(2);
            }

            int categoriaSelecionada = (int) (operacao.getCategoria_Id() - 1);
            spinCategoria.setSelection(categoriaSelecionada);

            //btnGravar.setText("Salvar");
        } else {
            //btnExcluir.setVisibility(View.GONE); // remove o botao excluir
            if (btnExcluir != null)
                btnExcluir.setVisible(false);
        }

        displayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int year = 0;
                int month = 0;
                int day = 0;
                if (operacao != null) {

                    String[] data = operacao.getData().split("/");
                    year = Integer.parseInt(data[2]);
                    month = Integer.parseInt(data[1]) - 1;
                    day = Integer.parseInt(data[0]);

                } else {
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }

                DatePickerDialog dialog = new DatePickerDialog(
                        CadastroActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        // aqui eu pego o valor atribuo o retorno no campo displayData
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                displayData.setText(date);
                // exibe a label acima da data selecionada
                displayDataHidden.setVisibility(View.VISIBLE);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void CriaComboTipo() {

        spinnerOperacao.setMinimumHeight(10);
        spinnerOperacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (position > 0) {
                    lblOperacao.setVisibility(View.VISIBLE);
                } else {
                    lblOperacao.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        // Adiciona os items no Combo
        tipoOperacoes = new ArrayList<String>();
        tipoOperacoes.add(0,"Selecione uma Operação");
        tipoOperacoes.add(1,"Crédito");
        tipoOperacoes.add(2,"Despesa");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipoOperacoes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperacao.setAdapter(dataAdapter);
        spinnerOperacao.requestFocus();

    }

    public void CriaComboCategoria() {

        categorias = db.categoriaDao().listar();

        List<String> nomeCategorias = new ArrayList<String>();

        if (categorias.size() <= 0) {
            categorias.add(0,new Categoria("Selecione uma Categoria"));
            db.categoriaDao().inserir(new Categoria("Selecione uma Categoria"));
        }

        for (Categoria cat : categorias) {
            String categoriaFormat = (cat.getNomeCategoria().substring(0, 1).toUpperCase() + (cat.getNomeCategoria().substring(1)));
            nomeCategorias.add(categoriaFormat);
        }

        //Collections.sort(nomeCategorias);
        Spinner spinnerCategoria = (Spinner) findViewById(R.id.spinner_categoria);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (position > 0) {
                    //((TextView) parentView.getChildAt(position)).setTextColor(Color.BLUE);
                    lblCategoria.setVisibility(View.VISIBLE);
                } else {
                    lblCategoria.setVisibility(View.GONE);

                    if(categorias.size() <= 1){
                        Toast.makeText(App.getContext(), "Não há registro de Categoria, adicione uma Categoria.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomeCategorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(dataAdapter);
        spinnerCategoria.requestFocus();
    }

    private boolean ValidaCampos() {
        String valor = txtValor.getText().toString();
        String data = displayData.getText().toString();
        long idcategoria = spinCategoria.getSelectedItemId();
        long tipo = spinnerOperacao.getSelectedItemId();

        List<Categoria> categorias = db.categoriaDao().listar();

        if(idcategoria == 0 && categorias.size() == 0){
            Toast.makeText(this, "Não há registro de Categorias, cadastre uma Categoria.", Toast.LENGTH_SHORT).show();
            spinCategoria.requestFocus();
            return false;
        }

        if (idcategoria == 0 && categorias.size() > 0) {
            Toast.makeText(this, "Selecione uma categoria.", Toast.LENGTH_SHORT).show();
            spinCategoria.requestFocus();
            return false;
        }

        if (valor.equals("")) {
            Toast.makeText(this, "Campo Valor não pode ser negativo.", Toast.LENGTH_SHORT).show();
            txtValor.requestFocus();
            return false;
        }

        if (valor.equals(null) || valor.equals("")) {
            Toast.makeText(this, "Campo Valor é obrigatório.", Toast.LENGTH_SHORT).show();
            txtValor.requestFocus();
            return false;
        }

        if (data == null) {
            Toast.makeText(this, "Campo Data é obrigatório.", Toast.LENGTH_SHORT).show();
            displayData.requestFocus();
            return false;
        }

        if (tipo == 0) {
            Toast.makeText(this, "Selecione um tipo de Operação.", Toast.LENGTH_SHORT).show();
            spinnerOperacao.requestFocus();
            return false;
        }
        return true;
    }

    public void AlterarCash(Operacao operacao) {

        // não estava carregando corretamente o valor selecionado
        Long idCategoria = (spinCategoria.getSelectedItemId() + 1);

        operacao.setDescricao(txtDescricao.getText().toString());
        operacao.setData(displayData.getText().toString());
        operacao.setValor(Double.parseDouble(txtValor.getText().toString()));
        operacao.setTipo(spinnerOperacao.getSelectedItem().toString());
        operacao.setCategoria_Id(idCategoria);

        db.operacaoDao().alterar(operacao);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }

                } else {

                    readMyCurrentCoordinates();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void readMyCurrentCoordinates() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Location location = null;
        double latitude = 0;
        double longitude = 0;

        if (!isGPSEnabled) {
            Log.i("LOG", "No geo resource able to be used.");
        }
        else {

            if (isGPSEnabled) {
                if (location == null) {
                    //locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 2000, 0, this );
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200,0,this);
                    Log.d("LOG", "GPS Enabled");
                    //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        }
        Log.i( "LOG", "Lat: "+latitude+" | Long: "+longitude );
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, PLAY_SERVICE_REQUEST).show();
            } else {
                Toast.makeText(this, "O dispositivo não tem permissão para acesso ao maps.", Toast.LENGTH_SHORT).show();
                finish();
            }

            return false;
        }

        return true;
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            final double latitude = mLocation.getLatitude();
            final double longitude = mLocation.getLongitude();

            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("your position").visible(true);
            mMap.addMarker(marker);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.0f));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (Build.VERSION.SDK_INT > 15) {
            this.getMenuInflater().inflate(R.menu.menu_cadastro_operacao, menu);
        } else
            new MenuInflater(this);

        MenuItem itemSanduich = menu.getItem(POSICAO_MENU_SANDUICH);
        MenuItem itemSair = itemSanduich.getSubMenu().getItem(POSICAO_MENU_SAIR);
        itemSair.setIcon(R.drawable.ic_close_circle_outline);

        // PEGA O MENU EXCLUIR
        MenuItem itemExcluir = menu.getItem(POSICAO_MENU_EXCLUIR);
        if (operacao == null) itemExcluir.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.btn_cadastro_categoria:
                cadastroDeCategoriaDialog();
                break;
            case R.id.btn_sair:

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                startActivity(intent);
                //finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
//  cadastro de categoria
    public void cadastroDeCategoriaDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.cadastro_categoria);

        final Button confirmar = (Button) dialog.findViewById(R.id.btn_Confirmar);
        final Button cancelar = (Button) dialog.findViewById(R.id.btn_Cancelar);

        confirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final EditText nomeCategoria = (EditText) dialog.findViewById(R.id.txtNomeNovaCategoria);
                boolean valido = salvaCategoria(nomeCategoria.getText().toString());

                if(valido){
                    dialog.dismiss();
                    CriaComboCategoria();
                }

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

    // EVENTOS DO MAPA  onMapReady  INICIA MAPA

    public void CriaCirculoMapa(Double latitude, Double longitude){

        Circle mCircle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude,  longitude))
                .radius(150)
                .strokeWidth(1)
                .strokeColor(Color.parseColor("#87CEFA"))
                .fillColor(Color.parseColor("#2271cce7")));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION ) ){
               // callDialog( null, new String[]{Manifest.permission.ACCESS_FINE_LOCATION} );
                ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
            //return;
        }

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(operacao != null){

            Integer endereco_id = operacao.getEndereco_Id();
            if(endereco_id != null) {
                Endereco enderecoOperacao = db.enderecoDao().procurarPorId(endereco_id);
                String lat = enderecoOperacao.getLatitude();
                String lng = enderecoOperacao.getLongitude();

                LatLng latLngOperacao = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                List<android.location.Address> addresses = null;

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latLngOperacao.latitude, latLngOperacao.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String enderecoCompleto = null;
                if(addresses == null){
                    enderecoCompleto = db.enderecoDao().procurarPorId(endereco_id).getEnderecoCompleto();
                }else{

                    String numero = addresses.get(0).getFeatureName();
                    String rua = addresses.get(0).getThoroughfare();
                    String bairro = addresses.get(0).getSubLocality();
                    String cidade = addresses.get(0).getLocality() == null ? addresses.get(0).getSubAdminArea() : addresses.get(0).getLocality();
                    String estado = getSiglaEstado(addresses.get(0).getAdminArea());
                    String pais = addresses.get(0).getCountryName();
                    enderecoCompleto = rua +  ", " + numero + " - " + bairro + ", " + cidade + " - " + estado + ", " + pais;
                }

                enderecoCompleto = enderecoCompleto.replace("null,","");
                CriaCirculoMapa(latLngOperacao.latitude,  latLngOperacao.longitude);

                String displayEndereco = enderecoCompleto == null || enderecoCompleto == "" ? enderecoOperacao.getRua() : enderecoCompleto;
                Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLngOperacao).title(displayEndereco).visible(true));
                newMarker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngOperacao.latitude, latLngOperacao.longitude), 16.0f));
            }
        } else{

            if(mLocation == null){
                relativeLayoutMapa.setVisibility(View.GONE);
            }

            if (mLocation != null) {
                final double latitude = mLocation.getLatitude();
                final double longitude = mLocation.getLongitude();

                CriaCirculoMapa(latitude, longitude);

                Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Meu Endereço").visible(true));
                newMarker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.0f));

            }
        }
    }

    /*
    private void callDialog( String message, final String[] permissions ){

        mMaterialDialog = new MaterialDialog(this)
                .setTitle("Permissão")
                .setMessage( message )
                .setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityCompat.requestPermissions(CadastroActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }
    */

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // entra aqui a primeira vez para permissao do mapa.
            if( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION ) ){
                // callDialog( null, new String[]{Manifest.permission.ACCESS_FINE_LOCATION} );
                ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // aqui pego o endereço do usuario, caso nao tenha registrado nenhum endereço para a operação
        if(operacao != null){
            Integer endereco_id = operacao.getEndereco_Id();
            if(endereco_id == null && mLocation != null){

                // pego o endereço do aparelho.
                meuEnderecoLatLong = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

                LatLng latLngOperacao = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

                List<android.location.Address> addresses = null;

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                if(meuEnderecoLatLong != null){

                    try {
                        addresses = geocoder.getFromLocation(meuEnderecoLatLong.latitude, meuEnderecoLatLong.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String enderecoCompleto =
                        String.valueOf(addresses.get(0).getThoroughfare()) + "," +
                                String.valueOf(addresses.get(0).getFeatureName()) + " - " +
                                String.valueOf(addresses.get(0).getSubLocality()) + "," +
                                addresses.get(0).getLocality() + " - " +
                                getSiglaEstado(addresses.get(0).getAdminArea());

                CriaCirculoMapa(latLngOperacao.latitude, latLngOperacao.longitude);

                Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLngOperacao).title(enderecoCompleto).visible(true));
                newMarker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngOperacao.latitude, latLngOperacao.longitude), 16.0f));

            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    //btnExclui - EXCLUIR
    public void excluiOperacao(MenuItem item) {

        if(operacao != null){
            String tipo = operacao.getTipo().toString();
            String descricao = operacao.getDescricao().toString();
            ExcluirDialog(tipo, descricao);
        }
    }

    private void excluirRegistro(){
        try{
            if(operacao != null) {

                db.operacaoDao().delete(operacao);
                Toast.makeText(this, "Registro excluído com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception ex){
            Toast.makeText(this, "Erro ao excluir o registro, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private void ExcluirDialog(String tipo, String descricao) {

        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.layout_excluir_operacao);

        //define o título do Dialog
        dialog.setTitle("Excluir Operação");

        //instancia os objetos que estão no layout customdialog.xml
        final Button confirmar = (Button) dialog.findViewById(R.id.btn_Confirmar_excluir);
        final Button cancelar = (Button) dialog.findViewById(R.id.btn_Cancelar_excluir);
        final TextView lbl_excluir = (TextView) dialog.findViewById(R.id.lbl_Exclui_operacao);

        String msg = lbl_excluir.getText().toString();

        lbl_excluir.setText(msg + " \n "  + tipo + " - " + descricao + " ?");
        //lbl_excluir.setTextColor(R.color.mensagem);

        confirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                excluirRegistro();

                //finaliza o dialog
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

    private Double getTotalCreditos(List<Operacao> operacoes){

        Double totalCreditos = 0.0;
        for (Operacao op: operacoes) {
            if("Crédito".equals(op.getTipo())){
                totalCreditos += op.getValor();
            }
        }
        return totalCreditos;
    }
    private Double getTotalDespesas(List<Operacao> operacoes){
        Double totalDespesas = 0.0;
        for (Operacao op: operacoes) {
            if("Despesa".equals(op.getTipo())){
                totalDespesas += op.getValor();
            }
        }
        return totalDespesas;
    }
    private Double getTotalSaldo(List<Operacao> operacoes){

        double totalCreditos = 0.0;
        double totalDespesas = 0.0;
        double saldo = 0.0;

        for (Operacao op: operacoes) {
            if("Crédito".equals(op.getTipo())){
                totalCreditos += op.getValor();
            }else if("Despesa".equals(op.getTipo())){
                totalDespesas += op.getValor();
            }
            saldo += (totalCreditos - totalDespesas);
        }
        return saldo;
    }

    // btnSalvar - SALVAR
    public void salvarOperacao(MenuItem item) {

        boolean valido = ValidaCampos();
        Integer idEndereco = 0;

        if(valido){
            String descricao = txtDescricao.getText().toString();
            Double valor = Double.parseDouble(txtValor.getText().toString());
            String data = displayData.getText().toString();
            String tipo = spinnerOperacao.getSelectedItem().toString();
            long idcategoria = (spinCategoria.getSelectedItemId() + 1);

            Integer id_usuario = 0;
            if(usuario != null)
                id_usuario = usuario.getID().intValue();

            List<Operacao> operacoes;
            List<Alerta> alertas;
            if(id_usuario > 0){
                alertas = db.alertaDao().listar(id_usuario);
            }else{
                alertas = null;
            }
            double total = 0.0;

            if(latLong == null){
                idEndereco = null;
            }else{
                idEndereco = db.enderecoDao().procurarPorLatLong(String.valueOf(latLong.latitude),String.valueOf(latLong.longitude)).getID();
            }

            if(operacao == null){
                //  INSERIR REGISTRO
                operacao = new Operacao(descricao, tipo, data, valor, idcategoria, idEndereco, id_usuario);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            db.operacaoDao().inserir(operacao);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
                Toast.makeText(this, "Registro salvo com sucesso!", Toast.LENGTH_SHORT).show();

                operacoes = db.operacaoDao().listar(id_usuario);

                Double totalCreditos = getTotalCreditos(operacoes);
                Double totalDespesas = getTotalDespesas(operacoes);
                Double saldo = getTotalSaldo(operacoes);

                for (Alerta alert:alertas) {
                    verificaAlerta(totalCreditos,totalDespesas ,saldo ,alert);
                }

            }else{ // EDITAR REGISTRO
                try{

                    Integer endereco_id = operacao.getEndereco_Id();
                    Endereco meuEndereco = null;

                    if(meuEnderecoLatLong != null || enderecoSelecionado != null) {

                        if (meuEnderecoLatLong != null)
                            meuEndereco = getEnderecoCompleto(endereco_id, meuEnderecoLatLong.latitude, meuEnderecoLatLong.longitude);
                        else if (enderecoSelecionado != null)
                            meuEndereco = getEnderecoCompleto(endereco_id, Double.parseDouble(enderecoSelecionado.getLatitude()), Double.parseDouble(enderecoSelecionado.getLongitude()));

                    }else {
                        if(endereco_id == null){
                            meuEndereco = null;
                        }else{
                            meuEndereco = db.enderecoDao().procurarPorId(endereco_id);
                        }
                    }

                    if(meuEndereco != null){
                        operacao.setEndereco_Id(meuEndereco.getID());
                    }else if(endereco_id == null){
                        operacao.setEndereco_Id(idEndereco);
                    }

                    AlterarCash(operacao);
                    Toast.makeText(this, "Registro alterado com sucesso!", Toast.LENGTH_SHORT).show();

                    operacoes = db.operacaoDao().listar(id_usuario);

                    Double totalCreditos = getTotalCreditos(operacoes);
                    Double totalDespesas = getTotalDespesas(operacoes);
                    Double saldo = getTotalSaldo(operacoes);

                    for (Alerta alert:alertas) {
                        verificaAlerta(totalCreditos,totalDespesas ,saldo ,alert);
                    }

                }catch (Exception ex){
                    Toast.makeText(this, "Não foi possível alterar o registro, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            finish();
        }else{
            // Toast.makeText(this, "Não foi possível salvar o registro.", Toast.LENGTH_SHORT).show();
        }
    }

    public void verificaAlerta(Double credito, Double despesa, Double saldo, Alerta alerta)
    {
        double valorCondicao = alerta.getValor(); // 200
        int condicao = alerta.getCondicao();  // maior ou menor
        String tipoDeAlerta = alerta.getTipo();

        if (tipoDeAlerta.equals("despesa"))
        {
            if(condicao == 2) // MAIOR
            {
                if (despesa > valorCondicao) // se o saldo for maior que valorCondicao  510 > 200
                {
                    executaAlerta(alerta);
                }
            }
            else if(condicao == 1)// MENOR
            {
                String valor = despesa.toString().replace("-", "");
                despesa = Double.parseDouble(valor);

                if (despesa < valorCondicao) // se o saldo for menor que valorCondicao  510 < 200
                {
                    executaAlerta(alerta);;
                }
            }
        }

        if (tipoDeAlerta.equals("credito"))
        {
            if (condicao == 2)
            {
                if (credito > valorCondicao) // se o saldo for maior que valorCondicao  510 > 200
                {
                    executaAlerta(alerta);
                }
            }
            else if(condicao == 1)// menor
            {
                String valor = saldo.toString().replace("-", "");
                saldo = Double.parseDouble(valor);

                if (credito < valorCondicao) // se o saldo for menor que valorCondicao  510 < 200
                {
                    executaAlerta(alerta);
                }
            }
        }

        if (tipoDeAlerta.equals("saldo"))
        {
            if (condicao == 2)
            {
                if (saldo > valorCondicao) // se o saldo for maior que valorCondicao  510 > 200
                {
                    executaAlerta(alerta);
                }
            }
            else if(condicao == 1)// menor
            {
                String valor = saldo.toString().replace("-", "");
                saldo = Double.parseDouble(valor);

                if (saldo < valorCondicao) // se o saldo for menor que valorCondicao  510 < 200
                {
                    executaAlerta(alerta);
                }
            }
        }
    }

    private void executaAlerta(Alerta alerta){

        final String NOTIFICATION_CHANNEL_ID = "10001";
        String condicao;
        String tipo = (alerta.getTipo().substring(0, 1).toUpperCase() + (alerta.getTipo().substring(1)));
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        String valor = numberFormat.format(alerta.getValor().doubleValue());

        if(alerta.getCondicao() == 1) {
            condicao = "menor";
        }else{
            condicao = "maior";
        }

        int id = 1;
        String TITULO = "Alerta de Movimentação";
        String DESCRICAO = tipo + " é " + condicao + " que " + valor;
        int SMALL_ICONE = R.drawable.small_icon_notification;
        int LARGE_ICONE = R.drawable.large_icon_notification;

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("alerta",alerta);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

       // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this,getString(R.string.msg_notificacao));
        notificacao.setContentText(DESCRICAO);
        notificacao.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(DESCRICAO));
        notificacao.setContentTitle(TITULO);
        notificacao.setSmallIcon(SMALL_ICONE);
        notificacao.setLargeIcon(BitmapFactory.decodeResource(getResources(), LARGE_ICONE) );
        notificacao.setContentIntent(pendingIntent);
        notificacao.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificacao.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificacao.setChannelId(NOTIFICATION_CHANNEL_ID);

            nm.createNotificationChannel(notificationChannel);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP );


        nm.notify(id, notificacao.build());
    }

    public Endereco getEnderecoCompleto(Integer id_endereco, Double latitude, Double longitude){

        Endereco meuEndereco = null;
        if(id_endereco ==  null){
            List<android.location.Address> listaEnderecos = null;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            meuEndereco = db.enderecoDao().procurarPorLatLong(String.valueOf(latitude),String.valueOf(longitude));

            try {
                listaEnderecos = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(meuEndereco == null){
                meuEndereco = new Endereco();
                for (android.location.Address endereco: listaEnderecos) {
                    meuEndereco.setLatitude(String.valueOf(listaEnderecos.get(0).getLatitude()));
                    meuEndereco.setLongitude(String.valueOf(listaEnderecos.get(0).getLongitude()));
                    meuEndereco.setRua(String.valueOf(listaEnderecos.get(0).getThoroughfare()));
                    meuEndereco.setNumeroDaResidencia(String.valueOf(listaEnderecos.get(0).getFeatureName()));
                    meuEndereco.setBairro(String.valueOf(listaEnderecos.get(0).getSubLocality()));
                    meuEndereco.setCidade(listaEnderecos.get(0).getLocality());
                    meuEndereco.setEstado(listaEnderecos.get(0).getAdminArea());
                    meuEndereco.setCEP(String.valueOf(listaEnderecos.get(0).getPostalCode()));
                    String enderecoCompleto =
                            String.valueOf(listaEnderecos.get(0).getThoroughfare()) + "," +
                                    String.valueOf(listaEnderecos.get(0).getFeatureName()) + " - " +
                                    String.valueOf(listaEnderecos.get(0).getSubLocality()) + "," +
                                    listaEnderecos.get(0).getLocality() + " - " +
                                    listaEnderecos.get(0).getAdminArea();
                    meuEndereco.setEnderecoCompleto(enderecoCompleto);
                }
                db.enderecoDao().inserir(meuEndereco);
                meuEndereco = db.enderecoDao().procurarPorLatLong(meuEndereco.getLatitude(),meuEndereco.getLongitude());
            }
        }else{
            meuEndereco = db.enderecoDao().procurarPorId(id_endereco);
        }
        return meuEndereco;
    }

    private String getSiglaEstado(String estado){
        String sigla = "";
        switch (estado){
            case "Acre": sigla = "AC"; break;
            case "Alagoas": sigla = "AL"; break;
            case "Amapá": sigla = "AP"; break;
            case "Amazonas": sigla = "AM"; break;
            case "Bahia": sigla = "BA"; break;
            case "Ceará": sigla = "CE"; break;
            case "Distrito Federal": sigla = "DF"; break;
            case "Espírito Santo": sigla = "ES"; break;
            case "Goiás": sigla = "GO"; break;
            case "Maranhão": sigla = "MA"; break;
            case "Mato Grosso": sigla = "MT"; break;
            case "Mato Grosso do Sul": sigla = "MS"; break;
            case "Minas Gerais": sigla = "MG"; break;
            case "Pará": sigla = "PA"; break;
            case "Paraíba": sigla = "PB"; break;
            case "Paraná": sigla = "PR"; break;
            case "Pernambuco": sigla = "PE"; break;
            case "Piauí": sigla = "PI"; break;
            case "Rio de Janeiro": sigla = "RJ"; break;
            case "Rio Grande do Norte": sigla = "RN"; break;
            case "Rio Grande do Sul": sigla = "RS"; break;
            case "Rondônia": sigla = "RO"; break;
            case "Roraima": sigla = "RR"; break;
            case "Santa Catarina": sigla = "SC"; break;
            case "São Paulo": sigla = "SP"; break;
            case "Sergipe": sigla = "SE"; break;
            case "Tocantins": sigla = "TO"; break;
            default:
        }
        return sigla;
    }
}
