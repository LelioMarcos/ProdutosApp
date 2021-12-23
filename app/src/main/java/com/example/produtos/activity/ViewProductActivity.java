package com.example.produtos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.produtos.R;
import com.example.produtos.adapter.MyAdapter;
import com.example.produtos.model.MainViewModel;
import com.example.produtos.model.Product;
import com.example.produtos.model.ViewProductViewModel;
import com.example.produtos.util.ImageCache;

import java.util.List;

public class ViewProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        Intent i = getIntent();
        String pid = i.getStringExtra("pid");

        // Criar um viewmodel, passando como parâmetro o id do produto.
        ViewProductViewModel viewProductViewModel = new ViewModelProvider(this, new ViewProductViewModel.ViewProductViewModelFactory(pid)).get(ViewProductViewModel.class);
        LiveData<Product> product = viewProductViewModel.getProduct();
        product.observe(this, new Observer<Product>() {
            // Preencher os campos com os dados do produto
            @Override
            public void onChanged(Product product) {
                // Pegar a imagem no cache da aplicação.
                ImageView imvPhotoProduct = findViewById(R.id.imvPhotoProduct);
                ImageCache.loadToImageView(ViewProductActivity.this, product.getId(), imvPhotoProduct);

                TextView tvName = findViewById(R.id.tvName);
                tvName.setText(product.getName());

                TextView tvPrice = findViewById(R.id.tvPrice);
                tvPrice.setText(product.getPrice());

                TextView tvDescription = findViewById(R.id.tvDescription);
                tvDescription.setText(product.getDescription());
            }
        });
    }
}