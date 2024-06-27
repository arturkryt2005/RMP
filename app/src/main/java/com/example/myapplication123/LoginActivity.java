package com.example.myapplication123;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEditText;
    private EditText passwordEditText;
    private String USER_KEY = "User";

    DatabaseReference mDataBase;

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Устанавливаем макет для данной Activity

        loginButton = findViewById(R.id.loginButton); // Находим кнопку по ее идентификатору
        loginEditText = findViewById(R.id.loginEditText); // Находим поле для ввода логина по его идентификатору
        passwordEditText = findViewById(R.id.passwordEditText); // Находим поле для ввода пароля по его идентификатору

        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY); // Получаем ссылку на базу данных Firebase и устанавливаем путь к узлу с данными пользователей


        loginButton.setOnClickListener(new View.OnClickListener() { // Устанавливаем обработчик нажатия на кнопку входа
            @Override
            public void onClick(View v) {
                String userLogin = loginEditText.getText().toString(); // Получаем введенный пользователем логин
                String userPassword = passwordEditText.getText().toString(); // Получаем введенный пользователем пароль

                Query query = mDataBase.orderByChild("password").equalTo(userPassword).limitToFirst(1); // Создаем запрос к базе данных, чтобы найти пользователя с заданным паролем

                query.addListenerForSingleValueEvent(new ValueEventListener() { // Добавляем слушатель, который будет выполняться один раз для получения результата запроса
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Обрабатываем результаты запроса
                        if (dataSnapshot.exists()) { // Если в результате запроса были найдены данные
                            // Получаем объект пользователя
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // Проходим по всем полученным данным, чтобы получить объект пользователя
                                User user = snapshot.getValue(User.class); // Получаем объект пользователя из полученных данных

                                // Пользователь найден, обрабатываем его данные и проверяем на соответствие пароля
                                if(user.getLogin().equals(userLogin)){ // Если логин пользователя совпадает с введенным логином, то выводим сообщение об успешном входе в систему
                                    Toast.makeText(LoginActivity.this, "Добро пожаловать,  " + user.getLogin(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, Account.class);
                                    intent.putExtra("login", user.getLogin());
                                    intent.putExtra("password", userPassword);
                                    startActivity(intent);
                                }
                            }
                        } else { // Если в результате запроса не было найдено ни одного объекта
                            // Пользователь не найден, выводим сообщение об ошибке
                            Toast.makeText(LoginActivity.this, "Пользователь не найден!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Обрабатываем ошибку
                        Toast.makeText(LoginActivity.this, "Ошибка: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

