package com.example.myapplication123;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PostActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private ImageView imageView;
    private Button buttonPost;
    private ProgressBar progressBar;

    private Uri imageUri;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        imageView = findViewById(R.id.imageView);
        buttonPost = findViewById(R.id.buttonPost);
        progressBar = findViewById(R.id.progressBar);

        imageView.setOnClickListener(v -> openFileChooser());
        buttonPost.setOnClickListener(v -> uploadPost());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadPost() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTextTitle.setError("Введите заголовок");
            editTextTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            editTextDescription.setError("Введите описание");
            editTextDescription.requestFocus();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Выберите фотографию", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Загрузка изображения в Firebase Storage
        String imageName = System.currentTimeMillis() + ".jpg"; // Генерируем уникальное имя для изображения
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + imageName);

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);

            UploadTask uploadTask = imageRef.putBytes(imageBytes);

            uploadTask.addOnFailureListener(e -> {
                Toast.makeText(PostActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }).addOnSuccessListener(taskSnapshot -> {
                // Получение URL загруженного изображения
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    savePostToDatabase(title, description, imageUrl); // Сохраняем данные поста в базу данных
                }).addOnFailureListener(exception -> {
                    Toast.makeText(PostActivity.this, "Failed to get download URL: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(PostActivity.this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    // Получение массива байт из InputStream
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    // Сохранение данных поста в базу данных
    private void savePostToDatabase(String title, String description, String imageUrl) {
        String postId = databaseReference.push().getKey();
        if (postId == null) {
            Toast.makeText(this, "Ошибка при создании поста", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Post post = new Post(postId, title, description, imageUrl);

        databaseReference.child(postId).setValue(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PostActivity.this, "Пост опубликован", Toast.LENGTH_SHORT).show();
                    clearForm();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void clearForm() {
        editTextTitle.setText("");
        editTextDescription.setText("");
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed));
        imageUri = null;
    }

}
