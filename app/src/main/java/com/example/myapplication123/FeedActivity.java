package com.example.myapplication123;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FeedActivity extends AppCompatActivity {
    private LinearLayout postContainer;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        postContainer = findViewById(R.id.postContainer);

        // Инициализация Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        storage = FirebaseStorage.getInstance();

        // Загрузка данных постов из Firebase
        loadPosts();
    }

    private void loadPosts() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        // Создаем отображение для каждого поста
                        createPostView(post);
                    }
                }
            } else {
                Toast.makeText(FeedActivity.this, "Failed to load posts: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPostView(Post post) {
        // Создаем новое представление для поста
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16); // Пример отступов между постами

        LinearLayout postLayout = new LinearLayout(this);
        postLayout.setLayoutParams(layoutParams);
        postLayout.setOrientation(LinearLayout.VERTICAL);

        // Добавляем заголовок
        TextView textViewTitle = new TextView(this);
        textViewTitle.setText(post.getTitle());
        textViewTitle.setTextSize(18);
        textViewTitle.setPadding(16, 16, 16, 0); // Пример внутренних отступов
        postLayout.addView(textViewTitle);

        // Добавляем описание
        TextView textViewDescription = new TextView(this);
        textViewDescription.setText(post.getDescription());
        textViewDescription.setTextSize(16);
        textViewDescription.setPadding(16, 8, 16, 0); // Пример внутренних отступов
        postLayout.addView(textViewDescription);

        // Добавляем изображение
        ImageView imageViewPost = new ImageView(this);
        imageViewPost.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        imageViewPost.setAdjustViewBounds(true);
        imageViewPost.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Загрузка изображения с помощью FirebaseUI Storage
        loadFirebaseImage(this, post.getImageUrl(), imageViewPost);

        postLayout.addView(imageViewPost);

        // Добавляем созданное представление поста в контейнер
        postContainer.addView(postLayout);
    }

    private void loadFirebaseImage(Context context, String imageUrl, ImageView imageView) {
        StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

        Glide.with(context)
                .load(storageRef)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(context, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }

}
