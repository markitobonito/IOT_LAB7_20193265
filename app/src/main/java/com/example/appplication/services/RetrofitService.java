package com.example.appplication.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import okhttp3.ResponseBody;

/**
 * Servicio Retrofit para comunicarse con el microservicio de validación
 * En la configuración de tu microservicio, reemplaza 192.168.x.x con tu IP local
 * Donde se ejecuta el registro-service en puerto 8080
 */
public class RetrofitService {
    private static final String TAG = "RetrofitService";
    // Para emulador Android: 10.0.2.2 es el alias para localhost de la máquina host
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static RetrofitService instance;
    private RegistroAPI api;

    // Interface para el endpoint de registro
    public interface RegistroAPI {
        @POST("registro")
        Call<ResponseBody> registro(@Body JsonObject body);
    }

    private RetrofitService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(RegistroAPI.class);
    }

    public static synchronized RetrofitService getInstance() {
        if (instance == null) {
            instance = new RetrofitService();
        }
        return instance;
    }

    /**
     * Valida el DNI y correo con el microservicio
     * Realiza una llamada POST a /registro del microservicio
     * @param email Correo del usuario
     * @param dni DNI del usuario
     * @param callback Callback para manejar la respuesta
     */
    public void validateRegistration(String email, String dni, AuthService.AuthCallback callback) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("email", email);
        requestBody.addProperty("dni", dni);

        api.registro(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Microservicio validó correctamente el registro");
                    callback.onSuccess("Validación exitosa");
                } else {
                    // Intentar obtener el mensaje de error del microservicio
                    String errorMessage = "Validación fallida";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            // El error puede ser string o JSON
                            try {
                                JsonObject errorJson = new Gson().fromJson(errorBody, JsonObject.class);
                                if (errorJson.has("mensaje")) {
                                    errorMessage = errorJson.get("mensaje").getAsString();
                                } else {
                                    errorMessage = errorBody;
                                }
                            } catch (Exception e) {
                                // Si no es JSON, usar el string directamente
                                errorMessage = errorBody;
                            }
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    Log.e(TAG, "Microservicio rechazó el registro: " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error llamando al microservicio", t);
                callback.onError("Error de conexión: " + t.getMessage() + 
                        ". Verifique que el microservicio esté corriendo en " + BASE_URL);
            }
        });
    }

    /**
     * Actualiza la URL base del microservicio (útil para testing)
     * @param newBaseUrl Nueva URL del microservicio
     */
    public static void setBaseUrl(String newBaseUrl) {
        instance = null; // Reset singleton para forzar nueva creación
    }
}
