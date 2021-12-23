package com.example.produtos.model;

import androidx.lifecycle.ViewModel;

public class AddProductViewModel extends ViewModel {
    String currentPhotoPath;
    
    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }
}
