package com.example.produtos.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.produtos.util.HttpRequest;
import com.example.produtos.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel {
    // MutableLiveData -> Sempre será observado por alterações.
    MutableLiveData<List<Product>> products;

    // Pegar a lista de produtos como uma LiveData
    public LiveData<List<Product>> getProducts() {
        if (products==null) {
            products = new MutableLiveData<List<Product>>();
            loadProducts();
        }

        return products;
    }

    public void refreshProducts() {
        loadProducts();
    }

    // Carregar todos os produtos do servidos.
    void loadProducts() {
        // Criar uma nova thread para executar a request HTTP (pois o android proibe fazer isso na thread principal)
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Criar a lista de produtos
                List<Product> productsList = new ArrayList<>();

                // Criar o construtor de GET http request
                HttpRequest httpRequest = new HttpRequest("https://productifes.herokuapp.com/get_all_products.php", "GET", "UTF-8");

                try {
                    // Executar a request HTTP e pegar o resultado json para uma string
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is, "UTF-8"); // ler o fluxo de bits para uma string
                    httpRequest.finish();

                    Log.d("HTTP_REQUEST_RESULT", result);

                    // Criar um objeto com o json retornado
                    JSONObject jsonObject = new JSONObject(result);

                    // Se a requisição funcionou, criar os objetos de produto com os resultados e colocá-los na lista de produtos.
                    int success = jsonObject.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("products");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jProduct = jsonArray.getJSONObject(i);

                            String pid = jProduct.getString("pid");
                            String name = jProduct.getString("name");

                            Product product = new Product(pid, name);
                            productsList.add(product);
                        }
                        // Adicionar a lista de produtos para o LiveData.
                        products.postValue(productsList);
                    }

                }
                catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
