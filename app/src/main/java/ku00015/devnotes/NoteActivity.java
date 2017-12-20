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
            " implements ", "class ", "import ", "package ", "super"));

    private ArrayList<String> dataTypes = new ArrayList<>(Arrays.asList("int ", "int[", "int>", "String ", "String>", "String[", "boolean ", "byte ",
            "byte[", "byte>", "char ", "char[", "char>", "short ", "short[", "short>", "long ", "long[", "long>", "float ", "float[", "float>",
            "double ", "double[", "double>", "List<", "ArrayList<", "void "));

    private ArrayList<String> descriptorsOther = new ArrayList<>(Arrays.asList("return ", "this", "null", "true", "false", "new"));

    private ArrayList<String> loopsConditionals = new ArrayList<>(Arrays.asList("if", "else", "switch", "case", "break", "default", "for",
            "while", "do"));


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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if(count > 0){
                        /*
                         * If Deleting a Space
                         */
                        if(start >= 7 && s.toString().charAt(start) == ' '){
                            /*
                             * If the space is part of a tabbed space
                             */
                            if (s.subSequence(start - 7, start + 1).toString().equals("        ")) {
                                backspace = true;
                                span = new Object();
                                spannable = new SpannableString(s);
                                spannable.setSpan(span, start - 7, start - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }

                }

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
                         * If New-Lined
                         */
                        if(s.charAt(start) == '\n') {
                            span = new Object();
                            spannable = new SpannableString(s);
                            spannable.setSpan(span, start, (start + count), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        /*
                         * If Text Entered was a " " WHERE the current index isn't 0 AND the index before was a "\n" OR " "
                         */
                        if(s.charAt(start) == ' '){
                            if(start != 0) {
                                if (s.charAt(start - 1) == '\n' || s.charAt(start - 1) == ' ') {
                                    span = new Object();
                                    spannable = new SpannableString(s);
                                    spannable.setSpan(span, start, (start + count), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }
                        }
                    } // Else if Backspace {
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
                         * [[String SubSequence START=INCLUSIVE, END=EXCLUSIVE (so need to +1)]]
                         */
                        if(backspace) {
                            if (end >= 6) {
                                if(s.subSequence(start, end + 1).toString().equals("       ")){
                                    s.delete(start, end + 1);
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
                             * If New-Lined -> Adopt the appropriate tabbing from previous line
                             */
                            else if (s.charAt(start) == '\n'){
                                String spaces = "";
                                for(int i = 0; i < getNumSpacesOfLine(s, getLineStartIndex(s, start - 1)); i++) spaces = spaces + " ";
                                s.insert(start + 1, spaces);
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
                     * Remove all Spans from Editable (When text is changed because may need to de-colourise). (clearSpans() doesn't work in this case)
                     */
                    ForegroundColorSpan[] allSpans = s.getSpans(0, s.length(), ForegroundColorSpan.class);
                    for (ForegroundColorSpan allSpan : allSpans) {
                        s.removeSpan(allSpan);
                    }
                    /*
                     * Auto-Colouring for Descriptors
                     */
                    for(int i = 0; i < descriptors.size(); i++) {
                        if(s.toString().contains(descriptors.get(i))){
                            int c = s.toString().indexOf(descriptors.get(i));
                            while (c >= 0) {
                                ForegroundColorSpan descColor = new ForegroundColorSpan(ContextCompat.getColor(NoteActivity.this.getApplicationContext(), R.color.color1));
                                s.setSpan(descColor, c, c + descriptors.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                c = s.toString().indexOf(descriptors.get(i), c + 1);
                            }
                        }
                    }
                    /*
                     * Auto-Colouring for Datatypes
                     */
                    for(int i = 0; i < dataTypes.size(); i++) {
                        if(s.toString().contains(dataTypes.get(i))){
                            int c = s.toString().indexOf(dataTypes.get(i));
                            while (c >= 0) {
                                ForegroundColorSpan descColor = new ForegroundColorSpan(ContextCompat.getColor(NoteActivity.this.getApplicationContext(), R.color.color3));
                                s.setSpan(descColor, c, c + dataTypes.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                c = s.toString().indexOf(dataTypes.get(i), c + 1);
                            }
                        }
                    }
                    /*
                     * Auto-Colouring for Other Descriptors
                     */
                    for(int i = 0; i < descriptorsOther.size(); i++) {
                        if(s.toString().contains(descriptorsOther.get(i))){
                            int c = s.toString().indexOf(descriptorsOther.get(i));
                            while (c >= 0) {
                                ForegroundColorSpan descColor = new ForegroundColorSpan(ContextCompat.getColor(NoteActivity.this.getApplicationContext(), R.color.color2));
                                s.setSpan(descColor, c, c + descriptorsOther.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                c = s.toString().indexOf(descriptorsOther.get(i), c + 1);
                            }
                        }
                    }
                    /*
                     * Auto-Colouring for Loops and Conditionals
                     */
                    for(int i = 0; i < loopsConditionals.size(); i++) {
                        if(s.toString().contains(loopsConditionals.get(i))){
                            int c = s.toString().indexOf(loopsConditionals.get(i));
                            while (c >= 0) {
                                ForegroundColorSpan descColor = new ForegroundColorSpan(ContextCompat.getColor(NoteActivity.this.getApplicationContext(), R.color.color2));
                                s.setSpan(descColor, c, c + loopsConditionals.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                c = s.toString().indexOf(loopsConditionals.get(i), c + 1);
                            }
                        }
                    }
                    /*
                     * Auto-Colouring for Quotes/ Strings
                     */
                    if(s.toString().contains("\"")){
                        /*
                         * If number of occurances of " is more than 1, Loop by getting quotation indexes and setting colour on them,
                         * update occurances left (take away the two you just colourised) and do this until occurances is less than 2.
                         */
                        int c = s.toString().split("\"").length - 1;
                        if(c > 1){
                            int firstQuote = s.toString().indexOf("\"", 0);
                            while(c > 1){
                                if(firstQuote >= 0){
                                    int secondQuote = s.toString().indexOf("\"", firstQuote + 1);
                                    if(secondQuote >= 0){
                                        ForegroundColorSpan quoteColor = new ForegroundColorSpan(ContextCompat.getColor(NoteActivity.this.getApplicationContext(), R.color.color4));
                                        s.setSpan(quoteColor, firstQuote, secondQuote + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        firstQuote = s.toString().indexOf("\"", secondQuote + 1);
                                    }
                                }
                                c = c - 2;
                            }
                        }
                    }
                }
            });
        }


    }

    private int getLineStartIndex(Editable s, int start){
        return s.toString().lastIndexOf('\n', start) + 1; // +1 Lands you AFTER \n and if there is no \n it lands you at index=0
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
