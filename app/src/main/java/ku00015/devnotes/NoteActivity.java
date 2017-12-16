package ku00015.devnotes;

import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class NoteActivity extends AppCompatActivity {

    private ArrayList<String> descriptors = new ArrayList<>(Arrays.asList("public ", "private ", "protected ", "static ", "final ", " extends ",
            " implements ", " class ", "import ", "package ", "super", " void "));

    private ArrayList<String> dataTypes = new ArrayList<>(Arrays.asList("int", "String", "boolean", "byte", "char", "short", "long",
            "float", "double", "List", "ArrayList"));

    private ArrayList<String> descriptorsOther = new ArrayList<>(Arrays.asList("return ", "this", "null", "true", "false", "new"));

    private ArrayList<String> equalsOperators = new ArrayList<>(Arrays.asList("==", "!=", ">=", "=>", "<=", "=<", "+=", "=+", "-=", "=-"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        final EditText notesEdit = (EditText) findViewById(R.id.noteText);
        if(notesEdit != null){
            notesEdit.addTextChangedListener(new TextWatcher() {

                boolean lock = false;
                boolean backspace = false;
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
                            spannable.setSpan(span, start, (start + count), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        /*
                         * If Text Entered was a " " WHERE the current index isn't 0 AND the index before was a "\n" OR " "
                         */
                        if(s.charAt(start) == ' '){
                            if(start != 0){
                                if(s.charAt(start - 1) == '\n' || s.charAt(start - 1) == ' '){
                                    span = new Object();
                                    spannable = new SpannableString(s);
                                    spannable.setSpan(span, start, (start + count), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }
                        }
                    } else{
                        /*
                         * If Deleting Tab Spaces
                         */
                        if(start > 7){
                            if(s.subSequence(start - 7, start).toString().equals("       ")){
                                backspace = true;
                                span = new Object();
                                spannable = new SpannableString(s);
                                spannable.setSpan(span, start - 7, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                        int end = spannable.getSpanEnd(span);

                        /*
                         * If Backspace back into 8 spaces (tab) -> Delete Tab.
                         */

                        if(backspace) {
                            if (end >= 7) {
                                if(s.subSequence(start, end).toString().equals("       ")){
                                    s.delete(start, end);
                                    backspace = false;
                                }
                            }
                        }else {
                            /*
                             * If Space -> Change to Tab
                             * [[CHANGE: String tab should be class variable with default 8 spaces(7) allow user to change setting]]
                             */
                            if (s.charAt(start) == ' ' && (s.charAt(start - 1) == '\n' || s.charAt(start - 1) == ' ')) {
                                String tab = "       ";
                                s.insert(start + 1, tab);
                            }
                            /*
                             * If Curly Brace -> NewLine and AutoComplete Curly Brace
                             */
                            else if (s.charAt(start) == '{') {
                                String tab = "        ";
                                String spaces = "";
                                for(int i = 0; i < getNumSpacesOfLine(s, getLineStartIndex(s,start)); i++) spaces = spaces + " ";

                                String finishCurly = "\n" + spaces + tab + "\n" + spaces + "}";
                                s.insert(start + 1, finishCurly);
                            }
                        }
                        /*
                         * Reset Spans and Release Lock
                         */
                        spannable.removeSpan(span);
                        spannable = null;
                        span = null;
                        lock = false;
                    }

                    /*
                     * Auto-Colouring for Descriptors
                     */
                    for(int i = 0; i < descriptors.size(); i++) {
                        if(s.toString().contains(descriptors.get(i))){
                            if(s.toString().lastIndexOf(descriptors.get(i)) >= 0){
                                ForegroundColorSpan descColor = new ForegroundColorSpan(ContextCompat.getColor(NoteActivity.this.getApplicationContext(), R.color.color2));
                                int index = s.toString().lastIndexOf(descriptors.get(i));
                                s.setSpan(descColor, index, index + descriptors.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                    /*
                     * Auto-Colouring for Datatypes
                     */
                    for(int i = 0; i < dataTypes.size(); i++){
                        if(s.toString().contains(dataTypes.get(i))) {
                            if(s.toString().lastIndexOf(dataTypes.get(i)) >= 0){
                                ForegroundColorSpan dataColor = new ForegroundColorSpan(ContextCompat.getColor(NoteActivity.this.getApplicationContext(), R.color.color4));
                                int index = s.toString().lastIndexOf(dataTypes.get(i));
                                s.setSpan(dataColor, index, index + dataTypes.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                    /*
                     * Auto-Colouring for Other Descriptors
                     */
                    for(int i = 0; i < descriptorsOther.size(); i++){
                        if(s.toString().contains(descriptorsOther.get(i))){
                            if(s.toString().lastIndexOf(descriptorsOther.get(i)) >= 0){
                                ForegroundColorSpan desc2Color = new ForegroundColorSpan(ContextCompat.getColor(NoteActivity.this.getApplicationContext(), R.color.color3));
                                int index = s.toString().lastIndexOf(descriptorsOther.get(i));
                                s.setSpan(desc2Color, index, index + descriptorsOther.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                }
            });
        }


    }

    private int getLineStartIndex(Editable s, int start){
        return s.toString().lastIndexOf('\n', start) + 1; // Check if actually  + 1
    }
    private int getNumSpacesOfLine(Editable s, int lineStartIndex){
        String sub = s.toString().substring(lineStartIndex);
        int c = 0;
        for(int i = 0; i < sub.length(); i++){
            if(sub.charAt(i) == ' '){
                c++;
            } else{
                break;
            }
        }
        return c;
    }
}
