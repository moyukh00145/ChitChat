package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView textView, textView2;
    TextInputLayout userlay, emaillay, passlay;
    TextInputEditText username, email, pass;
    Button signup;
    boolean enter = false,f1=false,f2=false,f3=false;
    String user, password, mail;
    FirebaseAuth auth;
    String token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.contact_link);
        textView2 = findViewById(R.id.contact_link2);

        userlay = findViewById(R.id.username_lay);
        emaillay = findViewById(R.id.email_lay);
        passlay = findViewById(R.id.pass_lay);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);


        signup = findViewById(R.id.button_signup);

        auth=FirebaseAuth.getInstance();

        if (auth.getCurrentUser()!=null){
            Intent intent =new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }

        final ProgressDialog dialog=new ProgressDialog(MainActivity.this);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText(null);
                pass.setText(null);
                email.setText(null);
                userlay.setVisibility(View.GONE);
                signup.setText("Log in");
                textView.setVisibility(View.GONE);
                textView2.setVisibility(View.VISIBLE);
                enter = true;
                userlay.setError(null);
                emaillay.setError(null);
                passlay.setError(null);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText(null);
                pass.setText(null);
                email.setText(null);
                userlay.setVisibility(View.VISIBLE);
                signup.setText("Sign up");
                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.GONE);
                enter = false;
                userlay.setError(null);
                emaillay.setError(null);
                passlay.setError(null);
            }
        });

        if (enter == false) {
            username.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().matches(".*([a-zA-Z].*[a-zA-Z]).*")) {
                        userlay.setError(null);
                        user = username.getText().toString().trim();
                        f1=true;
                    } else {
                        userlay.setError("Userame must contains alphabet");
                        f1=false;
                    }
                }
            });

            email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isValidEmailId(s.toString())) {
                        emaillay.setError(null);
                        mail = email.getText().toString().trim();
                        f2=true;
                    } else {
                        emaillay.setError("Please provide a valid email");
                        f2=false;
                    }

                }
            });
            pass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() < 8) {
                        passlay.setError("Paassword should contain atlest 8 character");
                        f3=false;
                    } else {
                        passlay.setError(null);
                        password = pass.getText().toString().trim();
                        f3=true;
                    }
                }
            });
        }


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (enter==false){
                   dialog.setTitle("Resistering User");
                   dialog.setMessage("Loading....");
                   dialog.show();

                   if (f1==true&&f2==true&&f3==true){
                       auth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()){



                                   FirebaseMessaging.getInstance().getToken()
                                           .addOnCompleteListener(new OnCompleteListener<String>() {
                                               @Override
                                               public void onComplete(@NonNull Task<String> task) {
                                                   if (!task.isSuccessful()) {
                                                       Log.w("new error", "Fetching FCM registration token failed", task.getException());
                                                       return;
                                                   }

                                                   // Get new FCM registration token
                                                   token = task.getResult();

                                                   // Log and toast
                                                   Log.w("new Token", token);
                                                   if (!token.isEmpty()){
                                                       dialog.dismiss();
                                                       FirebaseDatabase.getInstance().getReference("user/"+auth.getCurrentUser().getUid()).setValue(new user(username.getText().toString().trim(),email.getText().toString().trim(),auth.getCurrentUser().getUid(),"","",token));
                                                       Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                                                       startActivity(intent);
                                                       MainActivity.this.finish();
                                                   }
                                                   else{
                                                       Toast.makeText(MainActivity.this, "User resistered but Firebase token generate failed!!...please try to login", Toast.LENGTH_LONG).show();
                                                   }


                                               }
                                           });

                               }
                               else {
                                   dialog.dismiss();
                                   Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               dialog.dismiss();
                               Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
                   else {
                       Toast.makeText(MainActivity.this, "Please enter all the field", Toast.LENGTH_SHORT).show();
                   }
               }
               else{



                   if (f2==true&&f3==true){
                       dialog.setTitle("Login user");
                       dialog.setMessage("Loading....");
                       dialog.show();

                       auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()){
                                   dialog.dismiss();
                                   Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                                   startActivity(intent);
                                   MainActivity.this.finish();
                               }
                               else {
                                   dialog.dismiss();
                                   Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });

                   }
                   else {
                       Toast.makeText(MainActivity.this, "Please enter all the field", Toast.LENGTH_SHORT).show();
                   }

               }
            }
        });


    }

    private boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
    private void getToken(){


    }
}