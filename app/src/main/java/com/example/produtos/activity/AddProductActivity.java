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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.produtos.R;
import com.example.produtos.model.AddProductViewModel;
import com.example.produtos.util.Config;
import com.example.produtos.util.HttpRequest;
import com.example.produtos.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddProductActivity extends AppCompatActivity {
    static int RESULT_TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        AddProductViewModel addProductViewModel = new ViewModelProvider(this).get(AddProductViewModel.class);
        String currentPhotoPath = addProductViewModel.getCurrentPhotoPath();

        // Se o viewmodel já tiver uma imagem, utilizar esta no ImageView
        if (!currentPhotoPath.isEmpty()) {
            ImageView imvPhoto = findViewById(R.id.imvPhoto);
            Bitmap bitmap = Util.getBitmap(currentPhotoPath, imvPhoto.getWidth(), imvPhoto.getHeight());
            imvPhoto.setImageBitmap(bitmap);
        }

        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Desabilitar o botão de enviar
                view.setEnabled(false);

                //Verificar se os campos foram preenchidos
                EditText etName = findViewById(R.id.etName);
                String name = etName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo nome do produto não foi preenchido", Toast.LENGTH_LONG).show();
                    view.setEnabled(true);
                    return;
                }

                EditText etPrice = findViewById(R.id.etPrice);
                String price = etPrice.getText().toString();
                if (price.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo preço do produto não foi preenchido", Toast.LENGTH_LONG).show();
                    view.setEnabled(true);
                    return;
                }

                EditText etDescription = findViewById(R.id.etDescription);
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo descrição do produto não foi preenchido", Toast.LENGTH_LONG).show();
                    view.setEnabled(true);
                    return;
                }

                String currentPhotoPath = addProductViewModel.getCurrentPhotoPath();
                if (currentPhotoPath.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo foto do produto não foi preenchido", Toast.LENGTH_LONG).show();
                    view.setEnabled(true);
                    return;
                }

                // Escalar a imagem, para diminuir o tamanho no banco de dados.
                try {
                    Util.scaleImage(currentPhotoPath, 1000, 300);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }

                //Envio dos dados do novo produto. Aqui será feito uma POST http request para enviar esses dados.
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        HttpRequest httpRequest = new HttpRequest(Config.PRODUCTS_APP_URl + "create_product.php", "POST", "UTF-8");

                        // Argumentos da request.
                        httpRequest.addParam("name", name);
                        httpRequest.addParam("price", price);
                        httpRequest.addParam("description", description);
                        httpRequest.addFile("img", new File(currentPhotoPath));

                        try {
                            InputStream is = httpRequest.execute();
                            String result = Util.inputStream2String(is, "UTF-8");
                            httpRequest.finish();

                            Log.d("HTTP_REQUEST_RESULT", result);

                            JSONObject jsonObject = new JSONObject(result);
                            int success = jsonObject.getInt("success");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (success == 1) {
                                        Toast.makeText(AddProductActivity.this, "Produto adicionado com sucesso", Toast.LENGTH_LONG).show();
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(AddProductActivity.this, "Produto não foi adicionado com sucesso", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

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