package ku00015.devnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onJavaClick(View view) {
        Intent javaIntent = new Intent(this, JavaNote.class);
        startActivity(javaIntent);
    }


    public void onCppClick(View view) {
        Intent cppIntent = new Intent(this, CppNote.class);
        startActivity(cppIntent);
    }
}
