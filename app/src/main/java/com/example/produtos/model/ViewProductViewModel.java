package com.example.produtos.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.produtos.activity.ViewProductActivity;

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

    void loadProduct() {}

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
