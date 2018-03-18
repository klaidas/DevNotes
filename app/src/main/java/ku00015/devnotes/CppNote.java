package ku00015.devnotes;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CppNote extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
    }

    public void onBackClick(View view) {
        finish();
    }
}
