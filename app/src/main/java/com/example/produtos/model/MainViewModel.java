package com.example.produtos.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MainViewModel extends ViewModel {
    // MutableLiveData -> Sempre será observado por alterações.
    MutableLiveData<List<Product>> products;

    // Pegar a lista de produtos como uma live data
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


    void loadProducts() {}
}
