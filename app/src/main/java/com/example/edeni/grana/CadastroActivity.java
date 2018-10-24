package com.example.edeni.grana;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.content.ClipData;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.opengl.EGLExt;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edeni.grana.dao.EnderecoDao;
import com.example.edeni.grana.model.Categoria;
import com.example.edeni.grana.model.Endereco;
import com.example.edeni.grana.model.Operacao;
import com.example.edeni.grana.room.AppDatabase;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;

public class CadastroActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static String TITULO_ORIGINAL = "Cadastro de Operação";
    private static String TITULO_ALTERAR = "Editar Operação";
    Toolbar toolbarCadastro;

    // DATABASE
    private AppDatabase db;

    // utilizada para passar o CONTEXT da activity
    View parentLayout;

    // CAMPOS DA TELA
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
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    RelativeLayout relativeLayoutMapa;

    private static final int PERMISSION_REQUEST_CODE = 7001;
    private static final int PLAY_SERVICE_REQUEST = 7002;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private PlaceAutocompleteFragment placeAutocompleteFragment;

    Marker marker;

    Place place;

    LatLng latLong;

    EditText txtEndereco;
    // FECHA MAPA


    //  MODELS
    List<String> tipoOperacoes;
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
        txtEndereco.setTextSize(14);

        placeAutocompleteFragment.setFilter(new AutocompleteFilter.Builder().setCountry("BR").build());

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                latLong = place.getLatLng(); // pega Objeto LatLong
                String rua = (String) place.getName();
                String latitude = String.valueOf(latLong.latitude);
                String longitude = String.valueOf(latLong.longitude);
                String enderecoCompleto = (String) place.getAddress(); // pega o endereço completo com cep

                if(operacao != null && operacao.getEndereco_Id() > 0){
                    endereco = db.enderecoDao().procurarPorId(operacao.getEndereco_Id());

                    endereco.setEnderecoCompleto(enderecoCompleto);
                    endereco.setRua(rua);
                    endereco.setLatitude(latitude);
                    endereco.setLongitude(longitude);

                    db.enderecoDao().alterar(endereco);

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

        //usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        // aqui carrega o objeto OPERAÇÃO
        operacao = (Operacao) getIntent().getSerializableExtra("operacao");
        if (operacao != null) {

            toolbarCadastro.setTitle(TITULO_ALTERAR);

            // Mostro a Label de Categoria e a Label Operação
            lblCategoria.setVisibility(View.VISIBLE);
            lblOperacao.setVisibility(View.VISIBLE);

            // Procura no banco se encontra um usuario com este ID
            // usuario = db.loginDao().procurarPorId(operacao.getUsuario_Id());

            txtDescricao.setText(operacao.getDescricao());
            txtValor.setText(Double.toString(operacao.getValor()));
            displayData.setText(operacao.getData());

            enderecoSelecionado = db.enderecoDao().procurarPorId(operacao.getEndereco_Id());

            if(enderecoSelecionado != null)
                txtEndereco.setText(enderecoSelecionado.getRua());

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
        tipoOperacoes.add("Selecione uma Operação");
        tipoOperacoes.add("Crédito");
        tipoOperacoes.add("Despesa");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipoOperacoes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperacao.setAdapter(dataAdapter);
        spinnerOperacao.requestFocus();

    }

    public List<Categoria> initComboCategoria() {

        // Carrega a lista do BANCO
        List<Categoria> categorias = new ArrayList<>();

        categorias.add(new Categoria("Selecione uma Categoria"));
        categorias.add(new Categoria("Alimentação"));
        categorias.add(new Categoria("Educação"));
        categorias.add(new Categoria("Esporte"));
        categorias.add(new Categoria("Financeiro"));
        categorias.add(new Categoria("Lazer"));
        categorias.add(new Categoria("Manutenção"));
        categorias.add(new Categoria("Saúde"));
        categorias.add(new Categoria("Transporte"));

        return categorias;
    }

    public void CriaComboCategoria() {

        //List<Categoria>  categorias = db.categoriaDao().listar();
        List<Categoria> categorias = initComboCategoria();
        List<String> nomeCategorias = new ArrayList<String>();

        if (categorias.size() <= 1) {
            categorias = initComboCategoria();
        }

        for (Categoria cat : categorias) {
            nomeCategorias.add(cat.getNomeCategoria());
            db.categoriaDao().inserir(cat);
        }

        Spinner spinnerCategoria = (Spinner) findViewById(R.id.spinner_categoria);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (position > 0) {
                    //((TextView) parentView.getChildAt(position)).setTextColor(Color.BLUE);
                    lblCategoria.setVisibility(View.VISIBLE);
                } else {
                    // ((TextView) parentView.getChildAt(position)).setTextColor(Color.GRAY);
                    lblCategoria.setVisibility(View.GONE);
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

        if (idcategoria == 0) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
        }

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

        MenuItem itemExcluir = menu.getItem(2);
        if (operacao == null) itemExcluir.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.btn_salvar:
                Snackbar.make(
                        parentLayout,
                        "Entrou no Salvar",
                        Snackbar.LENGTH_LONG
                ).show();
                break;

            case R.id.btn_excluir:
                Snackbar.make(
                        parentLayout,
                        "Entrou no Excluir",
                        Snackbar.LENGTH_LONG
                ).show();
                break;
            case R.id.btn_sair:
                //finish();
                System.exit(0);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // EVENTOS DO MAPA


    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(operacao != null){

           Endereco enderecoOperacao = db.enderecoDao().procurarPorId(operacao.getEndereco_Id());
           String lat = enderecoOperacao.getLatitude();
           String lng = enderecoOperacao.getLongitude();

            LatLng latLngOperacao = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

            Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLngOperacao).title(enderecoOperacao.getEnderecoCompleto()).visible(true));
            newMarker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngOperacao.latitude, latLngOperacao.longitude), 16.0f));
        } else{

            if(mLocation == null){
                relativeLayoutMapa.setVisibility(View.GONE);
            }

            if (mLocation != null) {
                final double latitude = mLocation.getLatitude();
                final double longitude = mLocation.getLongitude();

                Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Meu Endereço").visible(true));
                newMarker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.0f));
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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

    public void excluiOperacao(MenuItem item) {

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

    public void salvarOperacao(MenuItem item) {
        boolean valido = ValidaCampos();
        long idEndereco = 0;

        List<Operacao> operacoes = db.operacaoDao().listar();

        if(valido){
            String descricao = txtDescricao.getText().toString();
            Double valor = Double.parseDouble(txtValor.getText().toString());
            String data = displayData.getText().toString();
            String tipo = spinnerOperacao.getSelectedItem().toString();
            long idcategoria = (spinCategoria.getSelectedItemId() + 1);

            if(latLong == null){
                idEndereco = 0;
            }else{
                idEndereco = db.enderecoDao().procurarPorLatLong(String.valueOf(latLong.latitude),String.valueOf(latLong.longitude)).getID();
            }

            if(operacao == null){
                //  INSERIR REGISTRO
                if(idEndereco <= 0)
                    operacao = new Operacao(descricao, tipo, data, valor, idcategoria);
                else
                    operacao = new Operacao(descricao, tipo, data, valor, idcategoria, idEndereco);

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

            }else{ // EDITAR REGISTRO
                try{
                    AlterarCash(operacao);
                    Toast.makeText(this, "Registro alterado com sucesso!", Toast.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Toast.makeText(this, "Não foi possível alterar o registro, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            finish();
        }else{
            // Toast.makeText(this, "Não foi possível salvar o registro.", Toast.LENGTH_SHORT).show();
        }
    }
}
