package com.example.produtos.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.produtos.R;
import com.example.produtos.adapter.MyAdapter;
import com.example.produtos.model.MainViewModel;
import com.example.produtos.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static int ADD_PRODUCT_ACTIVITY_RESULT = 1;
    static int RESULT_REQUEST_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Verificar se todas as permissões foram concedidas.
        checkForPermissions(permissions);


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

        Button btnNewProduct = findViewById(R.id.btnNewProduct);
        btnNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddProductActivity.class);
                startActivityForResult(i, ADD_PRODUCT_ACTIVITY_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PRODUCT_ACTIVITY_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
                mainViewModel.refreshProducts();
            }
        }
    }

    // Função para verificar as permissões do app.
    private void checkForPermissions(List<String> permissions) {
        // Verificar por permissões não concedidas.
        List<String> permissionNotGranted = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                permissionNotGranted.add(permission);
            }
        }

        // Se a versão do android for maior ou igual a versão M e
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // caso tenha alguma permissão não concedida, requerer por essas permissões.
            if(permissionNotGranted.size() > 0) {
                requestPermissions(permissionNotGranted.toArray(new String[permissionNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            }
        }
    }

    // Verificar se certa permissão foi concedida (apenas para versões do android maiores ou iguas a versão M)
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }

        return false;
    }

    // Função para perguntar novamente ao usuário para conceder as permissões, já que o app não funciona sem as permissões concedidas.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> permissionsRejected = new ArrayList<>();

        // Verificar novamente por permissões não concedidas.
        if (requestCode == RESULT_REQUEST_PERMISSION) {
            for (String permission : permissions) {
                if (!hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }

        // Caso tenha alguma permissão não concedida (e tiver em uma versão acima ou igual a M), avisar ao usuário que
        // essas permissões devem ser concedidas e perguntar de novo sobre as permissões.
        if (permissionsRejected.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("É preciso conceder essas permissões")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                                }
                            }).create().show();
                }
            }
        }
    }

}