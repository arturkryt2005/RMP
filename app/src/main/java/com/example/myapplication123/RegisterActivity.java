package com.example.myapplication123;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private String USER_KEY = "User";
    private DatabaseReference mDataBase;

    private EditText loginEditText;
    private EditText passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY);
        registerButton = findViewById(R.id.registerButton);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String registerLogin = loginEditText.getText().toString();
                String registerPassword = passwordEditText.getText().toString();
                String id = mDataBase.getKey();
                User user = new User(registerLogin, registerPassword, id);
                mDataBase.push().setValue(user);
            }
        });

    }
}
