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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        final EditText notesEdit = (EditText) findViewById(R.id.noteText);
        if(notesEdit != null){
            notesEdit.addTextChangedListener(new TextWatcher() {
                boolean lock = false;
                SpannableString spannable;
                Object span;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    /*
                     * If Not a Backspace [AND]
                     */
                    if(before == 0) {
                        /*
                         * If Text Entered was a "{" [OR]
                         */
                        if(s.charAt(start) == '{') {
                            span = new Object();
                            spannable = new SpannableString(s);
                            spannable.setSpan(span, start, (start + count), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                        /*
                         * If Text Entered was a " " WHERE the current index isn't 0 AND the index before was a "\n" OR " "
                         */
                        if(s.charAt(start) == ' '){
                            if(start != 0){
                                if(s.charAt(start - 1) == '\n' || s.charAt(start - 1) == ' '){
                                    span = new Object();
                                    spannable = new SpannableString(s);
                                    spannable.setSpan(span, start, (start + count), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                }
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //If no lock set
                    if (!lock && spannable != null) {
                        lock = true;
                        int start = spannable.getSpanStart(span);

                        /*
                         * If Space -> Change to Tab
                         * [[CHANGE: String tab should be class variable with default 8 spaces(7) allow user to change setting]]
                         */
                        if (s.charAt(start) == ' ' && (s.charAt(start - 1) == '\n' || s.charAt(start - 1) == ' ')) {
                            String tab = "       ";
                            s.insert(start + 1, tab);
                        }
                        if (s.charAt(start) == '{') {
                            String finishCurly = "\n\n}";
                            s.insert(start + 1, finishCurly);
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
