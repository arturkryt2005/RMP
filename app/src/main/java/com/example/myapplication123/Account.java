package com.example.myapplication123;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Account extends AppCompatActivity {
    private static final int PICK_IMAGE = 1; // Код запроса для выбора изображения
    private TextView loginTextView;
    private TextView passwordTextView;
    private ImageView profileImageView; // ImageView для отображения выбранного изображения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        loginTextView = findViewById(R.id.loginTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        profileImageView = findViewById(R.id.profileImageView); // Инициализация ImageView

        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");

        loginTextView.setText("Логин: " + login);
        passwordTextView.setText("Пароль: " + password);

        Button uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем галерею для выбора изображения
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        ImageButton btnPost = findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, PostActivity.class);
                intent.putExtra("login", loginTextView.getText().toString());
                intent.putExtra("password", passwordTextView.getText().toString());
                startActivity(intent);
            }
        });

        // Кнопка "Лента"
        ImageButton btnFeed = findViewById(R.id.btnFeed);
        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, FeedActivity.class);
                intent.putExtra("login", loginTextView.getText().toString());
                intent.putExtra("password", passwordTextView.getText().toString());
                startActivity(intent);
            }
        });

        // Кнопка "Друзья"
        ImageButton btnFriends = findViewById(R.id.btnFriends);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, FriendsActivity.class);
                intent.putExtra("login", loginTextView.getText().toString());
                intent.putExtra("password", passwordTextView.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                profileImageView.setImageBitmap(selectedBitmap); // Установка выбранного изображения в ImageView
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
