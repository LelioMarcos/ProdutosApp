package com.example.produtos.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Cache para diminuir o tempo de carregamento das imagens
public class ImageCache {
    public static void loadToImageView(Activity activity, String pid, ImageView imageView) {
        //Checar se a imagem já existe no dispositivo.
        String imageLocation = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + pid;
        File f = new File(imageLocation);
        if (f.exists() && !f.isDirectory()) {
            imageView.setImageBitmap(Util.getBitmap(imageLocation));
        }
        else { // Se não, pegar a imagem no banco de dados utilizando o crud.
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    HttpRequest httpRequest = new HttpRequest(Config.PRODUCTS_APP_URl + "get_product_image.php", "GET", "UTF-8");
                    httpRequest.addParam("pid", pid);

                    try {
                        InputStream is = httpRequest.execute();
                        String imgBase64 = Util.inputStream2String(is, "UTF-8");
                        httpRequest.finish();

                        String pureBase64Encoded = imgBase64.substring(imgBase64.indexOf(",") + 1);
                        Bitmap img = Util.base642Bitmap(pureBase64Encoded);

                        Util.saveImage(img, imageLocation);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(img);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
