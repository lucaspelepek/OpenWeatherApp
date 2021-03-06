package com.example.openweatherapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String BaseUrl = "http://api.openweathermap.org/";
    public static final String AppId = "291e9fad746cb0be6727189ed3420199";
    public static String cidadePais;
    String stringBuilder;
    private TextView weatherData;
    private EditText editTextPais, editTextCidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherData = findViewById(R.id.TextViewWeatherData);
        editTextPais = findViewById(R.id.editTextPais);
        editTextCidade = findViewById(R.id.editTextCidade);

        findViewById(R.id.button).setOnClickListener(v -> getCurrentData());
    }

    void getCurrentData() {
        if (TextUtils.isEmpty(editTextCidade.getText().toString()) || TextUtils.isEmpty(editTextPais.getText().toString())) {
            Toast.makeText(this, "Preencha o país e cidade por favor", Toast.LENGTH_SHORT).show();
            return;
        }

        cidadePais = (editTextCidade.getText().toString() + "," + editTextPais.getText().toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);

        Call<WeatherResponse> call = service.getCurrentWeatherData(cidadePais, "metric", AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    String stringBuilder = "Temperatura atual: " +
                            weatherResponse.getMain().getTemp() +
                            "°C\n" +
                            "Temperatura mínima: " +
                            weatherResponse.getMain().getTempMin() +
                            "°C\n" +
                            "Temperatura máxima: " +
                            weatherResponse.getMain().getTempMax() +
                            "°C\n" +
                            "Umidade: " +
                            weatherResponse.getMain().getHumidity() +
                            "%";

                    weatherData.setText(stringBuilder);

                    getPrevisaoData(weatherResponse.getCoord().getLat(), weatherResponse.getCoord().getLon(), service);

                } else {
                    Toast.makeText(MainActivity.this, "País ou cidade incorretos!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherData.setText(t.getMessage());
            }
        });

    }

    private void getPrevisaoData(Double lat, Double lon, WeatherService service) {
        TextView textViewPrevisao = findViewById(R.id.textViewPrevisao);

        Call<PrevisaoResponse> call = service.getPrevisaoWeatherData(lat, lon, "minutely,daily,alerts", AppId);

        call.enqueue(new Callback<PrevisaoResponse>() {
            @Override
            public void onResponse(Call<PrevisaoResponse> call, Response<PrevisaoResponse> response) {
                if (response.code() == 200) {
                    PrevisaoResponse previsaoResponse = response.body();
                    assert previsaoResponse != null;

                    if (previsaoResponse.getHourly().get(0).getRain() == null) {
                        stringBuilder = "Chance de chuva para a próxima hora: Dado não disponivel";
                    } else {
                        stringBuilder = "Chance de chuva para a próxima hora: " +
                                previsaoResponse.getHourly().get(0).getRain().get1h() +
                                "%";
                    }

                    textViewPrevisao.setText(stringBuilder);

                }
            }

            @Override
            public void onFailure(Call<PrevisaoResponse> call, Throwable t) {
                textViewPrevisao.setText(t.getMessage());
            }
        });
    }

}