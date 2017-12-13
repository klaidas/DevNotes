package ku00015.devnotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    boolean lock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        final EditText notesEdit = (EditText) findViewById(R.id.noteText);
        if(notesEdit != null){
            notesEdit.addTextChangedListener(new TextWatcher() {
                SpannableString spannable;
                Object span;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    /*
                     * Add Spans and Remove Lock
                     */
                    if(s.charAt(start) == '{') {
                        span = new Object();
                        spannable = new SpannableString(s);
                        spannable.setSpan(span, start, (start + count), Spanned.SPAN_COMPOSING);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //If no lock set
                    if (!lock) {
                        lock = true;
                        int start = spannable.getSpanStart(span);

                        /*
                         * If Space -> Change to Tab
                         * [[CHANGE: String tab should be class variable with default 8 spaces(7) allow user to change setting]]
                         */
                        //if (s.charAt(start) == ' ') {
                        //    Log.d("klid", "ello moyt");
                        //String tab = "       ";
                        //s.insert(start + 1, tab);
                        //    s.append(":)");
                        //}
                        if (s.charAt(start) == '{') {
                            String finishCurly = "\n\n}";
                            //s.insert(start + 1, finishCurly);
                            //s.append("jj");
                            Log.d("klid", "123");
                        }

                        /*
                         * Reset Spans and Release Lock
                         */
                        spannable.removeSpan(span);
                        spannable = null;
                        span = null;
                        lock = false;
                    }
                }
            });
        }


    }
}
