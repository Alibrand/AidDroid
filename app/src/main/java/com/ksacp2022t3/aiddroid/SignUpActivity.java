package com.ksacp2022t3.aiddroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    EditText txt_email,txt_password,txt_confirm_password;
    TextView txt_sign_in;
    AppCompatButton btn_next;
    RadioGroup radio_account_type;
    ProgressDialog progressDialog;
    ImageView btn_back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        txt_sign_in = findViewById(R.id.txt_sign_in);
        btn_next = findViewById(R.id.btn_next);
        radio_account_type = findViewById(R.id.radio_account_type);
        btn_back = findViewById(R.id.btn_back);

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Creating Account");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity. class);
                startActivity(intent);
                finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email =txt_email.getText().toString();
                String str_txt_password =txt_password.getText().toString();
                String str_txt_confirm_password =txt_confirm_password.getText().toString();
                RadioButton selected_radio=findViewById(radio_account_type.getCheckedRadioButtonId()) ;
                String account_type=selected_radio.getText().toString();



                if(str_txt_email.isEmpty())
                {
                     txt_email.setError("Required Field");
                     return;
                }
                if(!isValidEmail(str_txt_email))
                {
                    txt_email.setError("Bad formatted email...it should looks like example@mail.com");
                    return;
                }


                if(str_txt_password.isEmpty())
                {
                     txt_password.setError("Required Field");
                     return;
                }
                if(!isValidPassword(str_txt_password))
                {
                    txt_password.setError("Password should be at least 8 characters and contains mix of numbers ,small and capital letters");
                    return;
                }

                if(str_txt_confirm_password.isEmpty())
                {
                     txt_confirm_password.setError("Required Field");
                     return;
                }
                if(!str_txt_confirm_password.equals(str_txt_password))
                {
                    txt_confirm_password.setError("Passwords don't match");
                    txt_password.setError("Passwords don't match");
                    return;
                }

                if(account_type.equals("Normal User"))
                {
                    Intent intent = new Intent(SignUpActivity.this,HealthFormActivity. class);
                    intent.putExtra("email",str_txt_email);
                    intent.putExtra("password",str_txt_password);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(SignUpActivity.this,CenterFormActivity. class);
                    intent.putExtra("email",str_txt_email);
                    intent.putExtra("password",str_txt_password);
                    startActivity(intent);
                }








            }
        });





    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }
}