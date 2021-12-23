package com.example.produtos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.produtos.R;
import com.example.produtos.model.AddProductViewModel;
import com.example.produtos.util.Util;

public class AddProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        AddProductViewModel addProductViewModel = new ViewModelProvider(this).get(AddProductViewModel.class);
        String currentPhotoPath = addProductViewModel.getCurrentPhotoPath();

        if (!currentPhotoPath.isEmpty()) {
            ImageView imvPhoto = findViewById(R.id.imvPhoto);
            Bitmap bitmap = Util.getBitmap(currentPhotoPath, imvPhoto.getWidth(), imvPhoto.getHeight());
            imvPhoto.setImageBitmap(bitmap);
        }
    }
}