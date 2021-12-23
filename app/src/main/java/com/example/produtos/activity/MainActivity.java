package com.example.produtos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.produtos.R;
import com.example.produtos.adapter.MyAdapter;
import com.example.produtos.model.MainViewModel;
import com.example.produtos.model.Product;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvProduct = findViewById(R.id.rvProducts);
        rvProduct.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvProduct.setLayoutManager(layoutManager);

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        LiveData<List<Product>> products = mainViewModel.getProducts();
        products.observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                MyAdapter myAdapter = new MyAdapter(MainActivity.this, products);
                rvProduct.setAdapter(myAdapter);
            }
        });
    }
}