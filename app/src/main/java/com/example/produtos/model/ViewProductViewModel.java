package com.example.produtos.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.produtos.activity.ViewProductActivity;
import com.example.produtos.util.Config;
import com.example.produtos.util.HttpRequest;
import com.example.produtos.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewProductViewModel extends ViewModel {
    String pid;
    MutableLiveData<Product> product;

    public ViewProductViewModel(String pid) {
        this.pid = pid;
    }

    public LiveData<Product> getProduct() {
        if (this.product == null) {
            product = new MutableLiveData<Product>();
            loadProduct();
        }
        return product;
    }

    void loadProduct() {
        // Dessa vez, fazer uma request http para pegar as informações — o id, o nome, o preço e a descrição — de 1 produto de acordo com o seu id.
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpRequest httpRequest = new HttpRequest(Config.PRODUCTS_APP_URl + "get_product_details2.php", "GET", "UTF-8");
                httpRequest.addParam("pid", pid);

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is, "UTF-8");
                    httpRequest.finish();

                    Log.d("HTTP_REQUEST_RESULT", result);

                    JSONObject jsonObject = new JSONObject(result);
                    int success = jsonObject.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("product");
                        JSONObject jProduct = jsonArray.getJSONObject(0);

                        String pid = jProduct.getString("pid");
                        String name = jProduct.getString("name");
                        String price = jProduct.getString("price");
                        String description = jProduct.getString("description");

                        Product p = new Product(pid, name, price, description);

                        product.postValue(p);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Factory para que o ViewModelProvider aceite parâmetros (o id do produto, nesse caso).
    static public class ViewProductViewModelFactory implements ViewModelProvider.Factory {
        String pid;

        public ViewProductViewModelFactory(String pid) {
            this.pid = pid;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ViewProductViewModel(pid);
        }
    }

}
