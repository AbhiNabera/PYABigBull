package nabera.ranjan.abhinabera.pyabigbull.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nabera.ranjan.abhinabera.pyabigbull.Dashboard.MainActivity;

public class LoginActivity extends AppCompatActivity {

    EditText userID, password;
    Button login;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(nabera.ranjan.abhinabera.pyabigbull.R.layout.activity_login);
        getSupportActionBar().hide();

        userID = (EditText) findViewById(nabera.ranjan.abhinabera.pyabigbull.R.id.userID);
        password = (EditText) findViewById(nabera.ranjan.abhinabera.pyabigbull.R.id.password);
        login = (Button) findViewById(nabera.ranjan.abhinabera.pyabigbull.R.id.login);
        register = (TextView) findViewById(nabera.ranjan.abhinabera.pyabigbull.R.id.registerText);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(nabera.ranjan.abhinabera.pyabigbull.R.anim.enter, nabera.ranjan.abhinabera.pyabigbull.R.anim.exit);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(nabera.ranjan.abhinabera.pyabigbull.R.anim.enter, nabera.ranjan.abhinabera.pyabigbull.R.anim.exit);
            }
        });
    }
}
