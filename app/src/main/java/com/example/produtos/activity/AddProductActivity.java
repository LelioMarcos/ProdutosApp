package com.example.produtos.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.produtos.R;
import com.example.produtos.model.AddProductViewModel;
import com.example.produtos.util.Util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProductActivity extends AppCompatActivity {
    static int RESULT_TAKE_PICTURE = 1;

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

        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageView imvPhoto = findViewById(R.id.imvPhoto);
        imvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePhotoIntent();
            }
        });
    }

    private void dispatchTakePhotoIntent() {
        // Iniciar um intent para abrir o app de câmera.
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Criar o arquivo em que a foto será salva.
        File f = null;
        try {
            f = createImageFile();
        } catch (IOException e) {
            Toast.makeText(AddProductActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }

        AddProductViewModel addProductViewModel = new ViewModelProvider(this).get(AddProductViewModel.class);
        addProductViewModel.setCurrentPhotoPath(f.getAbsolutePath());

        // Caso a foto for realmente tirada.
        if (f != null) {
            // Uri da foto tirada, para permitir outros apps usarem o arquivo, utilizando o fileprovider definido no AndroidManifest.
            Uri fUri = FileProvider.getUriForFile(AddProductActivity.this, "com.example.produtos.fileprovider", f);
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            startActivityForResult(i, RESULT_TAKE_PICTURE); // Iniciar a intent esperando o retorno da foto tirada.
        }

    }

    // Função para criar o arquivo da foto em um formato que evite conflitos de nomes, usando o tempo exato de quando a foto foi tirada.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imgFileName, ".jpg", storageDir);

        return f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Se a codigo da requisição for a de tirar a foto e
        if (requestCode == RESULT_TAKE_PICTURE) {
            AddProductViewModel addProductViewModel = new ViewModelProvider(this).get(AddProductViewModel.class);
            String currentPhotoPath = addProductViewModel.getCurrentPhotoPath();
            // Se o código de resultado for positivo,
            if (resultCode == Activity.RESULT_OK) {
                ImageView imvPhoto = findViewById(R.id.imvPhoto);
                Bitmap bitmap = Util.getBitmap(currentPhotoPath, imvPhoto.getWidth(), imvPhoto.getHeight());
                imvPhoto.setImageBitmap(bitmap);
            } else { // Senão, deletar o arquivo que receberia a foto.
                File f = new File(currentPhotoPath);
                f.delete();
                addProductViewModel.setCurrentPhotoPath("");
            }
        }
    }
}