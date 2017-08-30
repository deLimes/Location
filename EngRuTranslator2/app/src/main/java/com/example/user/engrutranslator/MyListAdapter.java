package com.example.user.engrutranslator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.MessagePattern;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MyListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private  ArrayList<String> values;
    public  ArrayList<String> copyValues;
    public  ArrayList<String> restoreValues;
    private boolean pairedMark = false;
    public android.support.v4.app.Fragment frag1;
    public android.support.v4.app.Fragment frag2;
    public EditText edit_Text;
    public boolean textForViewing;
    //public boolean isScrollEnabled = true;
    //public TextView textView;
    public int numberOfOccurrences = 0;

    private boolean EnglishTextLayout = false;
    char[] ArrayEnglishCharacters = {'h', 'j', 'k', 'l', 'y', 'u', 'i', 'o', 'p', '[', ']', 'n', 'm',
            'g', 'f', 'd', 's', 'a', 'b', 'v', 'c', 'x', 'z', 't', 'r', 'e', 'w', 'q', '`'};

    char[] ArrayRussianCharacters = {'р', 'о', 'л', 'д', 'ж', 'э', 'н', 'г', 'ш', 'щ', 'з', 'х', 'ъ', 'т', 'ь', 'б', 'ю',
            'п', 'а', 'в', 'ы', 'ф', 'и', 'м', 'с', 'ч', 'я', 'е', 'к', 'у', 'ц', 'й', 'ё'};


    public MyListAdapter(Context context, ArrayList<String> values, EditText editText, boolean textForViewing, List<android.support.v4.app.Fragment> fragments) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
        this.copyValues = (ArrayList<String>)values.clone();
        this.frag1 = fragments.get(0);
        this.frag2 = fragments.get(1);
        this.edit_Text = editText;
        this.textForViewing = textForViewing;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter mfilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list

                    results.values = restoreValues;
                    results.count = restoreValues.size();

                    //((PageFragment)frag1).recordsAreMerged = false;
                }
                else {
                    // We perform filtering operation
                    List<String> nWordsList = new ArrayList<String>();

                    for (String p : copyValues) {
                        if (p.toUpperCase()
                                .contains(constraint.toString().toUpperCase()))
                            nWordsList.add(p);
                    }

                    results.values = nWordsList;
                    results.count = nWordsList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                //ArrayList<String> values2 = (ArrayList<String>)filterResults.values;
                //clear();
                //notifyDataSetChanged();
                //addAll(values);

                // Now we have to inform the adapter about the new list filtered
//                if (filterResults.count == 0) {
//                    notifyDataSetInvalidated();
//                 //   values = copyValues;
//                  //  notifyDataSetChanged();
//                }else {
//                    values = (ArrayList<String>) filterResults.values;
//                    //notifyDataSetChanged();
//                }
                //values = (ArrayList<String>) filterResults.values;
                values.clear();
                values.addAll((ArrayList<String>) filterResults.values);
                notifyDataSetChanged();
                //notifyDataSetInvalidated();

            }
        };
        //return super.getFilter();
        return mfilter;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {


        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        final EditText editText = (EditText) rowView.findViewById(R.id.edit_text);
        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
        //////////////////////////////////////////////////////////////////////////
        try {
            final SpannableStringBuilder text = new SpannableStringBuilder(values.get(position).substring(1, values.get(position).length()));
            final ForegroundColorSpan style = new ForegroundColorSpan(Color.BLUE);
            text.setSpan(style, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            //////////////////////////////////////////////////////////////////////////

            //editText.setText(text);

            String string = values.get(position).substring(2, values.get(position).length());
            if (position % 2 != 0) {
                if (((values.get(position - 1).charAt(0) == '+')) && ((values.get(position).charAt(0) == '-'))) {
                    editText.setText("");
                } else {
                    editText.setText(string);
                }
            } else {
                editText.setText(string);
                //editText.setFocusable(false);
            }

            editText.setFocusableInTouchMode(false);

            //editText.setTooltipText(text);


            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0) {
                    if (s.toString().charAt(s.toString().length()-1) == '\n') {

                        String original = values.get(position);
                        original = original.substring(1, original.length());
                        String answer =  editText.getText().toString().substring(0, s.toString().length()-1);
                        String comparison = original +  "\r\n" + answer;
                        final SpannableStringBuilder text = new SpannableStringBuilder(comparison);

                        for (int i=comparison.length()- answer.length(); i < comparison.length(); i++){
                            if(i < comparison.length()) {
                                if (comparison.charAt(i) == comparison.charAt(comparison.length()-(answer.length()+original.length()+2)+(i-2-(original.length())))) {
                                    final ForegroundColorSpan style = new ForegroundColorSpan(Color.rgb(0, 100, 0));
                                    text.setSpan(style, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                } else {
                                    final ForegroundColorSpan style = new ForegroundColorSpan(Color.RED);
                                    text.setSpan(style, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                }
                            }else{
                                final ForegroundColorSpan style = new ForegroundColorSpan(Color.RED);
                                text.setSpan(style, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }

                        }

                        //editText.setText(values.get(position) + "\r\n" + text);//"\r\n"
                        editText.setText(text);//"\r\n"
                    }
                }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //values.set(position, editText.getText().toString());
                }
            });


        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editText.getText().toString().length()>0) {

                    Character Symbol = editText.getText().toString().charAt(0);
                    boolean EnglishLayout = false;//engList.indexOf(Symbol) != -1;
                    boolean RussianLayout = false;//rusList.indexOf(Symbol) != -1;

                    for (int i = 0; i < ArrayEnglishCharacters.length; i++) {
                        if (ArrayEnglishCharacters[i] == Symbol) {
                            EnglishLayout = true;
                        }
                    }

                    for (int i = 0; i < ArrayRussianCharacters.length; i++) {
                        if (ArrayRussianCharacters[i] == Symbol) {
                            RussianLayout = true;
                        }
                    }

                    if (EnglishLayout != RussianLayout) {
                        EnglishTextLayout = EnglishLayout;
                    }

                    String text = "https://translate.google.com/?hl=ru#en/ru/" + editText.getText().toString();
                    if (EnglishTextLayout) {
                        text = "https://translate.google.com/?hl=ru#en/ru/" + editText.getText().toString();
                    } else {
                        text = "https://translate.google.com/?hl=ru#ru/en/" + editText.getText().toString();
                    }


                   ////////////////// List<android.support.v4.app.Fragment> fragments = context.getFragmentManager().getFragments();
                    ////////////////////android.support.v4.app.Fragment frag2 = fragments.get(1);
                    //Bundle bundle = new Bundle();
                    //bundle.putInt("Перевести", text);
                    //frag2.setArguments(bundle);
                    WebView mWebView = frag2.getView().findViewById(R.id.webView);
                    mWebView.loadUrl(text);

                    Toast toast = Toast.makeText(context,
                            "Попытка перевода",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();

                    textForViewing = true;
                    edit_Text.setText(editText.getText().toString());
                }else{
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                }

            }
        });


       /* parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast toast = Toast.makeText(context,
                        "parent_onClick",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();

            }
        });*/


            editText.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*Toast toast = Toast.makeText(context,
                        "onKey",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();*/
                    // TODO Auto-generated method stub
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            //editText.setText(values.get(position)+"\r\n"+editText.getText().toString());//"\r\n"

                            String original = values.get(position);
                            original = original.substring(2, original.length());
                            String answer = editText.getText().toString().substring(0, editText.getText().toString().length());
                            String comparison = original + "\r\n" + answer;//"\r\n"
                            final SpannableStringBuilder text = new SpannableStringBuilder(comparison);

                            for (int i = comparison.length() - answer.length(); i < comparison.length(); i++) {
                                if (i < comparison.length()) {
                                    if (comparison.charAt(i) == comparison.charAt(comparison.length() - (answer.length() + original.length() + 2) + (i - 2 - (original.length())))) {
                                        final ForegroundColorSpan style = new ForegroundColorSpan(Color.rgb(0, 100, 0));
                                        text.setSpan(style, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    } else {
                                        final ForegroundColorSpan style = new ForegroundColorSpan(Color.RED);
                                        text.setSpan(style, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    }
                                } else {
                                    final ForegroundColorSpan style = new ForegroundColorSpan(Color.RED);
                                    text.setSpan(style, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                }

                            }

                            //editText.setText(values.get(position) + "\r\n" + text);//"\r\n"
                            editText.setText(text);//"\r\n"
                        }
                    }
                    return false;
                }
            });


            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                /*
                Toast toast = Toast.makeText(context,
                        "onFocusChange"+b,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                */

                    numberOfOccurrences = 0;

                    //final TextView textView = (TextView) findViewById(R.id.textView);
                    //textView.setText(editText.getText());


               /* //проверим скрул
                boolean k = true;
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (i % 2 != 0) {
                        View a = parent.getChildAt(i);
                        if (((EditText) a.findViewById(R.id.edit_text)).getText().toString() == "") {
                            k = false;
                        }
                    }
                }
                isScrollEnabled = k;*/
                    //parent.setScrollContainer(false);

                    //if (values.size()>0) {/////////////proverka
                        if (!b) {
                            editText.setFocusableInTouchMode(false);
                            if (!editText.getText().toString().equals("")) {
                                editText.setText(values.get(position).substring(1, values.get(position).length()));
                            }
                        }
                    //}
                }
            });


        /*
        //не работает
        ((AdapterView)parent).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast toast = Toast.makeText(context,
                        "(AdapterView)parent).setOnItemClickListener",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();

            }
        });
        */

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    //String value = values.get(position);
                    StringBuilder value = new StringBuilder (values.get(position));
                    if (b) {
                        value.setCharAt(0, '+');
                        values.set(position, value.toString());
                    } else {
                        value.setCharAt(0, '-');
                        values.set(position, value.toString());
                    }


                    if (!pairedMark) {
                        if (position % 2 == 0) {
                            View a = parent.getChildAt(parent.getChildCount() - (((AdapterView) parent).getLastVisiblePosition() - position));
                            if (a != null) {
                                CheckBox itemCheckBox = (CheckBox) a.findViewById(R.id.checkBox);
                                EditText itemEditText = (EditText) a.findViewById(R.id.edit_text);
                                if (b) {
                                    if (position != 0) {
                                        itemEditText.setText("");
                                    }
                                } else {
                                    itemEditText.setText(values.get(position + 1).substring(2, values.get(position + 1).length()));
                                    pairedMark = true;
                                    itemCheckBox.setChecked(false);
                                    //values.set(position, value.replace(value.charAt(0), '-'));
                                }
                            }
                        } else {
                            View a = parent.getChildAt(parent.getChildCount() - (((AdapterView) parent).getLastVisiblePosition() - (position - 2)));
                            if (a != null) {
                                CheckBox itemCheckBox = (CheckBox) a.findViewById(R.id.checkBox);
                                if (b) {
                                    pairedMark = true;
                                    itemCheckBox.setChecked(true);
                                    //values.set(position, value.replace(value.charAt(0), '+'));
                                    editText.setText(values.get(position).substring(2, values.get(position).length()));
                                } else {
                                    editText.setText("");
                                }

                            }
                        }

                    }

                    pairedMark = false;


                }
            });
            ////////////////////////////////////////////////////////////////////////////////////////////////
            //if (rowView != null) {
/*
            View b = parent.getChildAt(position);

            if (b != null) {

                if (((CheckedTextView) b).isChecked()) {
                    CheckedTextView a = rowView.findViewById(R.id.checkBox);
                    //a.setVisibility(View.INVISIBLE);
                    a.setChecked(true);
                }

            }*/
            //}

            if (values.get(position).charAt(0) == '+') {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            if (values.get(position).charAt(1) == '+') {
                editText.setBackgroundColor(Color.YELLOW);
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////

        }catch (Exception e){
            e.printStackTrace();
        }

        return rowView;

    }
}