package com.example.myapplication123;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendsActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        container = findViewById(R.id.container);

        // Получаем ссылку на узел "User" в базе данных Firebase
        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        // Загружаем пользователей из Firebase Database
        loadUsers();
    }

    private void loadUsers() {
        // Читаем данные из узла "User"
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Очищаем контейнер перед добавлением новых пользователей
                container.removeAllViews();

                // Перебираем все дочерние узлы (пользователей) в узле "User"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Получаем данные пользователя
                    User user = snapshot.getValue(User.class);

                    // Создаем новый LinearLayout для каждого пользователя
                    if (user != null) {
                        LinearLayout userLayout = new LinearLayout(FriendsActivity.this);
                        userLayout.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(0, 0, 0, 16); // Отступ снизу между пользователями
                        userLayout.setLayoutParams(layoutParams);

                        // TextView для отображения логина пользователя
                        TextView textViewLogin = new TextView(FriendsActivity.this);
                        textViewLogin.setText(user.getLogin());
                        textViewLogin.setTextSize(18);
                        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        textParams.setMargins(0, 0, 16, 0); // Отступ справа от текста логина
                        textViewLogin.setLayoutParams(textParams);

                        // Button для действия с пользователем
                        Button buttonAction = new Button(FriendsActivity.this);
                        buttonAction.setText("+");

                        // Добавляем TextView и Button в userLayout
                        userLayout.addView(textViewLogin);
                        userLayout.addView(buttonAction);

                        // Добавляем userLayout в контейнер
                        container.addView(userLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при загрузке данных
                Toast.makeText(FriendsActivity.this, "Failed to load users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
