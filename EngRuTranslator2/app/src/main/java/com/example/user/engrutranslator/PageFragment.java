package com.example.user.engrutranslator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStorageState;

public class PageFragment extends android.support.v4.app.Fragment {

    private int pageNumber;
    public boolean recordsAreMerged = false;
    int numberOfOccurrences = 0;

    // Создаём пустой массив для хранения имен котов
    ArrayList<String> dictionary = new ArrayList<String>();
    //MyListAdapter adapter = new MyListAdapter(getActivity(), dictionary);
    MyListAdapter adapter;

    int secretClicks = 0;

    private boolean isScrollEnabled = true;
    int mLastFirstVisibleItem;
    int mLastFirstItem;
    private boolean scrollMoves = false;
    boolean mIsScrollingUp = false;
    int gridPosition = 0;
    boolean textForViewing = false;
    int WordIndex = 0;


    private boolean EnglishTextLayout = false;
    char[] ArrayEnglishCharacters = {'h', 'j', 'k', 'l', 'y', 'u', 'i', 'o', 'p', '[', ']', 'n', 'm',
            'g', 'f', 'd', 's', 'a', 'b', 'v', 'c', 'x', 'z', 't', 'r', 'e', 'w', 'q', '`'};
    List engList = Arrays.asList(ArrayEnglishCharacters);

    char[] ArrayRussianCharacters = {'р', 'о', 'л', 'д', 'ж', 'э', 'н', 'г', 'ш', 'щ', 'з', 'х', 'ъ', 'т', 'ь', 'б', 'ю',
            'п', 'а', 'в', 'ы', 'ф', 'и', 'м', 'с', 'ч', 'я', 'е', 'к', 'у', 'ц', 'й', 'ё'};
    List rusList = Arrays.asList(ArrayRussianCharacters);


    private WebView mWebView;

    public void enableScroll(boolean isScrollEnabled ) {
        this.isScrollEnabled = isScrollEnabled ;
    }

    public void sort(ArrayList<String> arrayList ) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View page = inflater.inflate(R.layout.fragment_page, null);

        final GridView gridView = (GridView) page.findViewById(R.id.gridView);
        SparseBooleanArray chosen = gridView.getCheckedItemPositions();

        boolean sortingDisabled = false;

        /*for (int i = 0; i < chosen.size(); i++)
        {
            if (i % 2 == 0) {
                if(chosen.get(i) != chosen.get(i+1)) {
                    sortingDisabled = true;
                }
            }
        }
        */

        for (int i = 0; i < dictionary.size(); i++)
        {
            if (i % 2 == 0) {
                if((dictionary.get(i).charAt(0) == '+') != (dictionary.get(i+1).charAt(0) == '+')) {
                    sortingDisabled = true;
                }
            }
        }

        if (!sortingDisabled && dictionary.size() % 2 == 0) {
            List<Integer> checkedList = new ArrayList<Integer>();
            for (int i = dictionary.size()-1; i >= 0; i--) {
                if ((dictionary.get(i).charAt(0) == '+')) {
                    //gridView.setItemChecked(i, false);

                    //dictionary.add(dictionary.size(), dictionary.get(i));
                    if (i % 2 == 0) {
                        dictionary.add(dictionary.size() - 1, dictionary.get(i));
                        //gridView.setItemChecked(dictionary.size()-3, true);
                    } else {
                        dictionary.add(dictionary.size(), dictionary.get(i));
                        //gridView.setItemChecked(dictionary.size()-2, true);
                    }
                    checkedList.add(i);
                    dictionary.remove(i);
                }
            }

           /*for (int i = checkedList.size() - 1; i >= 0; i--) {
                gridView.setItemChecked(gridView.getCount() - i - 1, true);
            }*/
        }else {
            Toast.makeText(getActivity().getApplicationContext(), "Сортировка запрещена",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void mergeRecords(ArrayList<String> arrayList ) {

        /*
        for (int i = arrayList.size(); i >= 0; i--) {

        }
        */
        if (!recordsAreMerged) {
            if (arrayList.size() % 2 == 0) {

                for (int i = 0; i < arrayList.size(); i++) {
                    // if (i % 2 == 0) {
                    arrayList.set(i, arrayList.get(i) + "~" + arrayList.get(i + 1));
                    arrayList.remove(i + 1);
                    //}

                }

                recordsAreMerged = true;
            }
        }

    }

    public void splitRecords(ArrayList<String> arrayList ) {

        /*
        for (int i = arrayList.size(); i >= 0; i--) {

        }
        */
        if(recordsAreMerged) {
            //if (arrayList.size() % 2 == 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (i % 2 == 0) {
                    String[] result = arrayList.get(i).split("~");
                    arrayList.set(i, result[0]);
                    arrayList.add(i + 1, result[1]);
                }

            }

            recordsAreMerged = false;
            //}
        }

    }

    public void updateAdapter() {


//        try {
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View page = inflater.inflate(R.layout.fragment_page, null);
//
//
//        final GridView gridView = (GridView) page.findViewById(R.id.gridView);
//
//        Adapter adapter = gridView.getAdapter();
//        //((ArrayAdapter<String>)adapter).getFilter().filter("some text");
//
//        //updateAdapter
//        ArrayList<String> dictionaryCopy =  (ArrayList<String>)dictionary.clone();
//        ((ArrayAdapter<String>)adapter).clear();
//        ((ArrayAdapter<String>)adapter).addAll(dictionaryCopy);
//        //((ArrayAdapter<String>)adapter).addAll(dictionaryCopy.toArray(new String[dictionaryCopy.size()]));
//
//        //gridView.setAdapter(null);
//        //gridView.setAdapter((ArrayAdapter<String>)adapter);
//        //gridView.clearTextFilter();//фыва
//        gridView.setFilterText("     ");//фыва
//        gridView.clearTextFilter();//фыва
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        //updateAdapter

        ArrayList<String> dictionaryCopy =  (ArrayList<String>)dictionary.clone();
        adapter.clear();
        adapter.addAll(dictionaryCopy);


        adapter.notifyDataSetChanged();

        //adapter.getFilter().filter("      ");
        //adapter.restoreValues = (ArrayList<String>)dictionary.clone();
        adapter.restoreValues = new ArrayList<String>(dictionaryCopy);
        adapter.getFilter().filter("");
        adapter.notifyDataSetChanged();



        /*
        for (int i = 0; i < gridView.getChildCount(); i++) {
            if (i % 2 != 0) {
                View a = gridView.getChildAt(i);
                if (a != null) {
                    //a.setWillNotDraw(true);
                    //a.setVisibility(View.GONE);
                    if(((CheckedTextView) gridView.getChildAt(i-1)).isChecked()&& !((CheckedTextView)a).isChecked()) {
                        a.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        */

    }

    public void fillDictionary() {

        String[] array = getResources().getStringArray(R.array.dictionary);
        /*for(int i = 0; i < array.length; i++) {
            arrayList.add(array[i]);
        }*/

        //dictionary = new ArrayList<String>(Arrays.asList(array));
        dictionary.clear();
        dictionary.addAll(new ArrayList<String>(Arrays.asList(array)));
        adapter.notifyDataSetChanged();

    }

    public void saveDictionary() {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View page = inflater.inflate(R.layout.fragment_page, null);


        final GridView gridView = (GridView) page.findViewById(R.id.gridView);

        splitRecords(dictionary);

        try {

            //int k = 1/0;//////////del

            if (!getExternalStorageState().equals(
                    MEDIA_MOUNTED)) {
                Toast.makeText(getActivity(), "SD-карта не доступна: " + getExternalStorageState(), Toast.LENGTH_SHORT).show();
                return;
            }
            // получаем путь к SD
            File sdPath = getExternalStorageDirectory();
            // добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath());// + "/mytextfile.txt");
            // создаем каталог
            sdPath.mkdirs();
            // формируем объект File, который содержит путь к файлу
            File sdFile = new File(sdPath, "dictionary.txt");
            try {
                // открываем поток для записи
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                // пишем данные
                //bw.write("Содержимое файла на SD");
                for (int i = 0; i < dictionary.size(); i++) {
                    /*if(i < dictionary.size()-1){
                        bw.write(dictionary.get(i)+";");//"\r\n"
                    }else{
                        bw.write(dictionary.get(i));
                    }*/
                    bw.write(dictionary.get(i)+";"+ gridView.isItemChecked(i) +"\r\n");//
                }
                // закрываем поток
                bw.close();
                Toast.makeText(getActivity().getBaseContext(), "File saved: " + sdFile.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            /*
            FileOutputStream fileout = openFileOutput(fileName.getCanonicalPath(), MODE_PRIVATE);

            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);

            for (int i = 0; i < dictionary.size(); i++) {
                if(i < dictionary.size()-1){
                    outputWriter.write(dictionary.get(i)+";");//"\r\n"
                }else{
                    outputWriter.write(dictionary.get(i));
                }
            }
            outputWriter.close();

            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_SHORT).show();
            */

        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
//        //return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//
//        // Операции для выбранного пункта меню
//        switch (id) {
//            case R.id.action_save:
//                saveDictionary();
//                return true;
//            case R.id.action_restore:
//                dictionary.clear();
//                fillDictionary();
//                //saveDictionary();
//
////                List<Fragment> fragments = getFragmentManager().getFragments();
////                Fragment frag = fragments.get(0);
////                ((PageFragment) frag.getActivity()).fillDictionary();
//
////                final GridView gridView = (GridView) findViewById(R.id.gridView);
////                Adapter adapter = gridView.getAdapter();
////                try {
////                    ((ArrayAdapter<String>)adapter).notifyDataSetChanged();
////                }catch (Exception e) {
////                    e.printStackTrace();
////                }
//                ////////updateAdapter();
//                return true;
//            default:
//                //return true;
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View page=inflater.inflate(R.layout.fragment_page, container, false);
//        TextView pageHeader=(TextView)result.findViewById(R.id.displayText);
//        String header = String.format("Фрагмент %d", pageNumber+1);
//        pageHeader.setText(header);


        //View rootView = inflater.inflate(R.layout.fragment_find_people, container, false);

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final GridView gridView = (GridView) page.findViewById(R.id.gridView);
        final EditText editText = (EditText) page.findViewById(R.id.editText);

        //gridView.setFastScrollEnabled(true);

        /*
        // Создаём пустой массив для хранения имен котов
        final ArrayList<String> dictionary = new ArrayList<String>();
        */

        // Создаём адаптер ArrayAdapter, чтобы привязать массив к ListView



        /*
        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, dictionary);
                */



        /////////////////////////////////////////////////////////////////////
         //MyListAdapter adapter = new MyListAdapter(getActivity(), dictionary);
        List<android.support.v4.app.Fragment> fragments = getFragmentManager().getFragments();
        android.support.v4.app.Fragment frag1 = fragments.get(0);

        adapter = new MyListAdapter(getActivity(), dictionary, editText, textForViewing, fragments);
        adapter.textForViewing = textForViewing;
//        adapter.textView = (TextView) page.findViewById(R.id.textView);
        ////////////////////////////////////////////////////////////////////////

        adapter.setNotifyOnChange(true);

        // Привяжем массив через адаптер к GridView
        gridView.setAdapter(adapter);
        gridView.setNumColumns(2);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridView.setTextFilterEnabled(true);

        //////////////////////////////////////////////////////////////////////////////////////////////////
        //скрыть ответы
        final View button = page.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for (int i = 0; i < gridView.getChildCount(); i++) {
                    View a = gridView.getChildAt(i);

                    int position = gridView.getFirstVisiblePosition() + i;
                    //String value = dictionary.get(position);
                    StringBuilder value = new StringBuilder (dictionary.get(position));

                    final CheckBox checkBox = (CheckBox) a.findViewById(R.id.checkBox);
                    if (i % 2 != 0) {

                        final EditText editText = (EditText) a.findViewById(R.id.edit_text);
                        //if (a != null) {
                        //a.setWillNotDraw(true);
                        //a.setVisibility(View.GONE);
                        editText.setText("");

                        checkBox.setChecked(false);
                        value.setCharAt(0, '-');
                        dictionary.set(position, value.toString());
                        //}
                    } else {
                        //gridView.setItemChecked(i, true);
                        //CheckedTextView item = (CheckedTextView) a;
                        //item.setChecked(true);
                        //boolean l = item.isChecked();
                        //gridView.setItemChecked(gridView.getFirstVisiblePosition() + i, true);
                        checkBox.setChecked(true);
                        value.setCharAt(0, '+');
                        dictionary.set(position, value.toString());

                    }
                }

                //updateAdapter();
                /*
                int j= gridView.getFirstVisiblePosition();

                for (int i = 0; i < dictionary.size(); i++) {
                    CheckedTextView a = (CheckedTextView) gridView.getChildAt(i);

                    if (a != null && a.isChecked()){
                        //gridView.setItemChecked(i, true);
                    }
                }
                */


                //проверим скрул
                boolean b = true;
                for (int i = 0; i < gridView.getChildCount(); i++) {
                    if (i % 2 != 0) {
                        View a = gridView.getChildAt(i);
                        if (((EditText) a.findViewById(R.id.edit_text)).getText().toString() == "") {
                            b = false;
                        }
                    }
                }
                enableScroll(b);

            }

        });

        //Показать ответы
        final View button2 = page.findViewById(R.id.button2);
        if(button!=null) {
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    for (int i = 0; i < gridView.getChildCount(); i++) {
                        View a = gridView.getChildAt(i);

                        int position = gridView.getFirstVisiblePosition() + i;
                        //String value = dictionary.get(position);
                        StringBuilder value = new StringBuilder (dictionary.get(position));

                        //if (a != null) {
                        //a.setWillNotDraw(false);
                        //a.setVisibility(View.VISIBLE);
                        //gridView.isItemChecked(position);
                        //a.setVisibility(View.GONE);

                        //}
                        final EditText editText = (EditText) a.findViewById(R.id.edit_text);
                        editText.setText(dictionary.get(gridView.getFirstVisiblePosition() + i).substring(2, dictionary.get(gridView.getFirstVisiblePosition() + i).length()));
//                        if (i % 2 == 0) {
//                            //gridView.setItemChecked(gridView.getFirstVisiblePosition() + i, gridView.isItemChecked(i + 1));
//                            final CheckBox checkBox = (CheckBox) a.findViewById(R.id.checkBox);
//                            checkBox.setChecked(((CheckBox) (gridView.getChildAt(i + 1)).findViewById(R.id.checkBox)).isChecked());
//                        }

                        final CheckBox checkBox = (CheckBox) a.findViewById(R.id.checkBox);
                        checkBox.setChecked(false);
                        value.setCharAt(0, '-');
                        dictionary.set(position, value.toString());
                    }

                    //проверим скрул
                    boolean b = true;
                    for (int i = 0; i < gridView.getChildCount(); i++) {
                        if (i % 2 != 0) {
                            View a = gridView.getChildAt(i);
                            if (((EditText) a.findViewById(R.id.edit_text)).getText().toString() == "") {
                                b = false;
                            }
                        }
                    }
                    enableScroll(b);
                }
            });
        }
        //сортировать
        final View button3 = page.findViewById(R.id.button3);
        if(button!=null) {
            button3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //dictionary.clear();
                    //del
                    //FileInputStream fileIn=openFileInput("mytextfile.txt");

                    //gridView.onInterceptTouchEvent(MotionEvent.ACTION_MOVE)
                    //gridView.setScrollingCacheEnabled(false);

                    //enableScroll(!isScrollEnabled);

                /*
                String s = "true";
                Boolean b1 = Boolean.valueOf(s);

                boolean b2 = Boolean.parseBoolean(s);

                String l = "1";

                Boolean b = l.equals("1");
                */
                    //del

                    //adapter.notifyDataSetChanged();


                    //Collections.sort(dictionary);
                    //adapter.notifyDataSetChanged();

                    SparseBooleanArray chosen = gridView.getCheckedItemPositions();

                    boolean sortShortList = false;

                    final ArrayList<String> shortList = new ArrayList<String>();
                    final ArrayList<Integer> positions = new ArrayList<Integer>();

                    for (int i = 0; i < dictionary.size(); i++) {
                        if (i % 2 == 0) {
                            if (((dictionary.get(i).charAt(0) == '+')) != ((dictionary.get(i + 1).charAt(0) == '+'))) {
                                sortShortList = true;
                                shortList.add(dictionary.get(i));
                                shortList.add(dictionary.get(i + 1));
                                positions.add(i);
                                positions.add(i + 1);
                            }
                        }
                    }

                    if (sortShortList) {

                        mergeRecords(shortList);
                        Collections.sort(shortList);
                        splitRecords(shortList);

                        for (int i = 0; i < positions.size(); i++) {
                            //if (i % 2 == 0) {
                            //dictionary.set(positions.get(i), shortList.get(i+1));
                            dictionary.set(positions.get(i), shortList.get(i));
                            //}
                        }


                    } else {

                        sort(dictionary);

                        final ArrayList<String> list = new ArrayList<String>();

                        for (int i = 0; i < dictionary.size(); i++) {
                            if (dictionary.get(i).charAt(0) == '+') {
                                list.add(list.size(), dictionary.get(i));
                            }
                        }

                        for (int i = 0; i < list.size(); i++) {
                            dictionary.remove(dictionary.size() - 1);
                        }

                        mergeRecords(dictionary);
                        Collections.sort(dictionary);
                        splitRecords(dictionary);

                        for (int i = 0; i < list.size(); i++) {
                            dictionary.add(dictionary.size(), list.get(i));
                        }

                    }
                    adapter.notifyDataSetChanged();
                    updateAdapter();
                }
            });
        }

        //Del.
        final View button5 = page.findViewById(R.id.button5);
        if(button!=null) {
            button5.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    String message;

                    if (WordIndex % 2 == 0) {
                        message = dictionary.get(WordIndex).substring(2,  dictionary.get(WordIndex).length())+"~"
                                + dictionary.get(WordIndex + 1).substring(2,  dictionary.get(WordIndex + 1).length());
                    }else {
                        message = dictionary.get(WordIndex - 1).substring(2,  dictionary.get(WordIndex - 1).length())+"~"
                                + dictionary.get(WordIndex).substring(2,  dictionary.get(WordIndex).length());
                    };
                    builder.setTitle("Удалить запись?")
                            .setMessage(message)
                            .setCancelable(true)

                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int arg1) {
                                    try {
                                        if (WordIndex % 2 == 0) {
                                            dictionary.remove(WordIndex);
                                            dictionary.remove(WordIndex);
                                        } else {
                                            dictionary.remove(WordIndex - 1);
                                            dictionary.remove(WordIndex - 1);
                                        }
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    adapter.notifyDataSetChanged();
                                    updateAdapter();
                                    ////////////////////////////////////////////////////////////////updateAdapter();
                                    Toast.makeText(getActivity().getApplicationContext(), "Запись удалена",
                                            Toast.LENGTH_SHORT).show();

                                }
                            })

                            .setNegativeButton("Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    }
                            );

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
        }
        //Поменять
        final View button4 = page.findViewById(R.id.button4);
        if(button!=null) {
            button4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //try {
                    if (dictionary.size() % 2 == 0) {
                        for (int i = 0; i < dictionary.size(); i++) {
                            if (i % 2 == 0) {
                                dictionary.add(i + 2, dictionary.get(i));
                                dictionary.remove(i);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        updateAdapter();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Должно быть четное количество карточек",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        //перемешать
        final View button7 = page.findViewById(R.id.button7);
        if(button!=null) {
            button7.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    SparseBooleanArray chosen = gridView.getCheckedItemPositions();

                    boolean shuffleShortList = false;

                    final ArrayList<String> shortList = new ArrayList<String>();
                    final ArrayList<Integer> positions = new ArrayList<Integer>();

                    for (int i = 0; i < dictionary.size(); i++) {
                        if (i % 2 == 0) {
                            if (((dictionary.get(i).charAt(0) == '+')) != ((dictionary.get(i + 1).charAt(0) == '+'))) {
                                shuffleShortList = true;
                                shortList.add(dictionary.get(i));
                                shortList.add(dictionary.get(i + 1));
                                positions.add(i);
                                positions.add(i + 1);
                            }
                        }
                    }

                    if (shuffleShortList) {

                        mergeRecords(shortList);
                        Collections.shuffle(shortList);
                        splitRecords(shortList);

                        for (int i = 0; i < positions.size(); i++) {
                            //if (i % 2 == 0) {
                            //dictionary.set(positions.get(i), shortList.get(i+1));
                            dictionary.set(positions.get(i), shortList.get(i));
                            //}
                        }


                    } else {


                        //Collections.shuffle(Arrays.asList(arr))
                        //dictionary.toArray().shuffle(Arrays.asList(arr))
                /*
                Integer[] ints = new Integer[10];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = i;
                }
                */
                        //List<Integer> lst = Arrays.asList(ints);
                        sort(dictionary);

                        final ArrayList<String> list = new ArrayList<String>();

                        for (int i = 0; i < dictionary.size(); i++) {
                            if ((dictionary.get(i).charAt(0) == '+')) {
                                list.add(list.size(), dictionary.get(i));
                                //dictionary.remove(chosen.keyAt(i));
                            }
                        }

                        for (int i = 0; i < list.size(); i++) {
                            dictionary.remove(dictionary.size() - 1);
                        }

                        mergeRecords(dictionary);
                        Collections.shuffle(dictionary);
                        splitRecords(dictionary);

                        for (int i = 0; i < list.size(); i++) {
                            dictionary.add(dictionary.size(), list.get(i));
                        }
                    }

                    adapter.notifyDataSetChanged();
                    updateAdapter();

                }
            });
        }
//////////////////////////////////////////////////////////////////////////////////////////////////
//        ////////////////////////////////////////////////////////////////////
//        final View button8 = new Button(this);//findViewById(R.id.button8);
//        button8.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Должно быть четное количество карточек",
//                        Toast.LENGTH_SHORT).show();
//
//                //mWebView.loadUrl("javascript:(function b(){document.getElementById('gt-res-listen').click();})");
//                //mWebView.loadUrl("javascript:document.getElementById('gt-res-listen').click();");
//                //mWebView.loadUrl("javascript:if (document.getElementById) {alert(\"Prived!!\");}");
//                //mWebView.loadUrl("javascript:(alert(\"Prived!!\");)");
//
//               // mWebView.loadUrl("javascript:alert('Prived');");
//                //mWebView.loadUrl("javascript:document.getElementById('gt-res-listen').click();");
//                String js = "javascript:document.getElementById('gt-res-listen').click();";
//                //js = "javascript:alert('Prived');";
//                js = "javascript:(function(){"+
//                        "l=document.getElementById('gt-res-listen');"+
//                        "e=document.createEvent('HTMLEvents');"+
//                        "e.initEvent('click',true,true);"+
//                        "l.dispatchEvent(e);"+
//                        "})()";
//
//                js = "javascript:l=document.getElementById('gt-res-listen');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);";
//                js = "javascript:(function(){document.getElementById('gt-res-listen').click();})()";
//                js = "javascript:(function(){alert(document.getElementById('result_box')));})()";
//                js = "javascript:alert(document.getElementById('result_box').textContent);";
//                js = "javascript:document.querySelector('button[type=\"gt-res-listen\"]').click()";
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    mWebView.evaluateJavascript(js, new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String s) {
//
//                            Toast.makeText(getApplicationContext(), s,
//                                    Toast.LENGTH_SHORT).show();
//
//                        }
//
//                });
//                }else{
//                    mWebView.loadUrl(js);
//                }
//            }
//            });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE){
                    //gridView.setFastScrollEnabled(false);
                }
                //boolean mIsScrollingUp = false;

                //adapter.numberOfOccurrences++;

                       /* if (absListView.getId() == gridView.getId()) {
                        final int currentFirstVisibleItem = gridView.getFirstVisiblePosition();
                        if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                            mIsScrollingUp = false;
                        } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                            mIsScrollingUp = true;
                        }

                        mLastFirstVisibleItem = currentFirstVisibleItem;
*/


                if (numberOfOccurrences > 2) {
                    if (mIsScrollingUp) {
                        gridView.smoothScrollToPosition(0);
                    } else {
                        gridView.smoothScrollToPosition(gridView.getCount());
                    }
                    gridView.setFastScrollEnabled(true);
                }
                        /*
                        Toast toast2 = Toast.makeText(getApplicationContext(),
                                ""+mIsScrollingUp,
                                Toast.LENGTH_SHORT);
                        toast2.setGravity(Gravity.TOP, 0, 0);
                        toast2.show();
                        */
                //}

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {


                    /*final TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText(""+ (mLastFirstItem - i) +"/"+ mIsScrollingUp +"/"+ adapter.numberOfOccurrences);
*/
                numberOfOccurrences++;

                if (mLastFirstItem - i < 0) {
                    mIsScrollingUp = false;
                } else if (mLastFirstItem - i > 0) {
                    mIsScrollingUp = true;
                }else if (mLastFirstItem == i) {
                    //scrollMoves = false;
                    numberOfOccurrences = 0;
                }
                mLastFirstItem = i;


                   /* if(adapter.numberOfOccurrences > 5) {
                        if (mIsScrollingUp) {
                    //        gridView.smoothScrollToPosition(0);
                            scrollMoves = true;
                        } else {
                      //      gridView.smoothScrollToPosition(gridView.getCount());
                            scrollMoves = true;
                        }
                        adapter.numberOfOccurrences = 0;
                    }*/

            }

        });
        // }


        gridView.setOnTouchListener(new AdapterView.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                /*//Не работает
                final CheckBox itemCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
                itemCheckBox.setChecked(!itemCheckBox.isChecked());
                //Не работает*///олдж




                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                   /* //проверим скрул
                    boolean b = true;
                    for (int i = 0; i < gridView.getChildCount(); i++)
                    {
                        if (i % 2 != 0) {
                            View a = gridView.getChildAt(i);
                            if(((EditText) a.findViewById(R.id.edit_text)).getText().toString().equals("")) {
                                b = false;
                            }
                        }
                    }
                    enableScroll(b);*/

                    //adapter.numberOfOccurrences++;
                   /* Toast toast2 = Toast.makeText(getApplicationContext(),
                            ""+adapter.numberOfOccurrences,
                            Toast.LENGTH_SHORT);
                    toast2.setGravity(Gravity.TOP, 0, 0);
                    toast2.show();*/


                    /*final TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText(""+ !scrollMoves +"/"+ adapter.numberOfOccurrences);
*/
                    //if(!scrollMoves && adapter.numberOfOccurrences > 3) {
                    /*if(adapter.numberOfOccurrences > 3) {
                        if (mIsScrollingUp) {
                            gridView.smoothScrollToPosition(0);
                            //scrollMoves = true;
                        } else {
                            gridView.smoothScrollToPosition(gridView.getCount());
                            //scrollMoves = true;
                        }
                        //adapter.numberOfOccurrences = 0;
                    }*/




                }
                return false;
            }
        });

//////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked,
                                    int position, long id) {

                secretClicks = 0;
                numberOfOccurrences = 0;

                final TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(((AppCompatCheckedTextView) itemClicked).getText());

                gridView.requestFocus();

                SparseBooleanArray chosen = gridView.getCheckedItemPositions();
                if (position % 2 == 0) {
                    //gridView.setItemChecked(position + 1, chosen.get(position));
                    View a = gridView.getChildAt(gridView.getChildCount()-(gridView.getLastVisiblePosition()-position));
                    if (a != null) {
                        final EditText editText = (EditText) a.findViewById(R.id.edit_text);
                        //a.setWillNotDraw(chosen.get(position));
                        if(gridView.isItemChecked(position)){
                            //a.setVisibility(View.INVISIBLE);
                            editText.setText("");
                        }else{
                            //a.setVisibility(View.VISIBLE);
                            editText.setText(dictionary.get(gridView.getChildCount()-(gridView.getLastVisiblePosition()-position)));
                        }
                    }

                    if(!chosen.get(position ) && chosen.get(position  + 1)) {
                        gridView.setItemChecked(position + 1, false);
                    }

                } else {
                    View a = gridView.getChildAt(position );
                    //a.getVisibility();
                    //a.getWindowVisibility();

                    gridView.setItemChecked(position  , !chosen.get(position - 1));
                    if(!chosen.get(position - 1)) {
                        gridView.setItemChecked(position  - 1, chosen.get(position ));
                    }

                    //itemClicked.setWillNotDraw(!chosen.get(position ));
                    if(gridView.isItemChecked(position)){
                        itemClicked.setVisibility(View.VISIBLE);
                    }else{
                        itemClicked.setVisibility(View.INVISIBLE);
                    }

                }

                //проверим скрул
                int b = 0;
                for (int i = 0; i < gridView.getChildCount(); i++)
                {
                    View a = gridView.getChildAt(i);
                    b += a.getVisibility();
                }
                enableScroll(!(b > 0));

            }
        });
*/

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(getContext().getApplicationContext(),
                        "setOnItemClickListener",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            }
        });


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                StringBuilder value = new StringBuilder (dictionary.get(position));
                if (value.charAt(1) == '+') {
                    itemClicked.setBackgroundColor(Color.WHITE);
                    value.setCharAt(1, '-');
                    dictionary.set(position, value.toString());

                }else{
                    itemClicked.setBackgroundColor(Color.YELLOW);
                    value.setCharAt(1, '+');
                    dictionary.set(position, value.toString());
                }

                final int pos = position;
                WordIndex = position;
/*
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Удалить запись?")
                        .setMessage(((TextView) itemClicked).getText())
                        .setCancelable(true)

                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                dictionary.remove(pos);
                                updateAdapter();
                                Toast.makeText(getApplicationContext(), "Запись удалена",
                                        Toast.LENGTH_SHORT).show();

                            }
                        })

                        .setNegativeButton("Нет",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );

                AlertDialog alert = builder.create();
                alert.show();
               */
                final EditText itemEditText = (EditText) itemClicked.findViewById(R.id.edit_text);
                //itemEditText.setFocusableInTouchMode(true);

//                if(itemEditText.getText().toString().length()>0) {
//
//                    Character Symbol = itemEditText.getText().toString().charAt(0);
//                    boolean EnglishLayout = false;//engList.indexOf(Symbol) != -1;
//                    boolean RussianLayout = false;//rusList.indexOf(Symbol) != -1;
//
//                    for (int i = 0; i < ArrayEnglishCharacters.length; i++) {
//                        if (ArrayEnglishCharacters[i] == Symbol) {
//                            EnglishLayout = true;
//                        }
//                    }
//
//                    for (int i = 0; i < ArrayRussianCharacters.length; i++) {
//                        if (ArrayRussianCharacters[i] == Symbol) {
//                            RussianLayout = true;
//                        }
//                    }
//
//                    if (EnglishLayout != RussianLayout) {
//                        EnglishTextLayout = EnglishLayout;
//                    }
//
//                    String text = "https://translate.google.com/?hl=ru#en/ru/" + itemEditText.getText().toString();
//                    if (EnglishTextLayout) {
//                        text = "https://translate.google.com/?hl=ru#en/ru/" + itemEditText.getText().toString();
//                    } else {
//                        text = "https://translate.google.com/?hl=ru#ru/en/" + itemEditText.getText().toString();
//                    }
//
//
//                    List<android.support.v4.app.Fragment> fragments = getFragmentManager().getFragments();
//                    android.support.v4.app.Fragment frag2 = fragments.get(1);
//                    //Bundle bundle = new Bundle();
//                    //bundle.putInt("Перевести", text);
//                    //frag2.setArguments(bundle);
//                    mWebView = frag2.getView().findViewById(R.id.webView);
//                    mWebView.loadUrl(text);
//
//                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
//                            "Попытка перевода",
//                            Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.TOP, 0, 0);
//                    toast.show();
//
//                    EditText editText = (EditText) getActivity().findViewById(R.id.editText);
//                    textForViewing = true;
//                    editText.setText(itemEditText.getText().toString());
//                }else{
//                    itemEditText.setFocusableInTouchMode(true);
//                }

                return false;
            }
        });


        // Прослушиваем нажатия клавиш
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        int delimiterPposition = editText.getText().toString().indexOf("~");
                        if (delimiterPposition != -1) {
                            String part = editText.getText().toString();
                            String structure = "-"+part.substring(0, delimiterPposition)+"~-"+part.substring(delimiterPposition+1, part.length());
                            dictionary.add(0, structure);

                            editText.setText("");
                            //editText.requestFocus();
                            //editText.setSelection(0);
                            SparseBooleanArray chosen = gridView.getCheckedItemPositions();

                            for (int i = 0; i < chosen.size(); i++) {
                                if (chosen.get(i)) {
                                    //chosen.delete(chosen.keyAt(i));
                                    //chosen.put(chosen.keyAt(i+1), true);
                                    gridView.setItemChecked(i, false);
                                    gridView.setItemChecked(i + 1, true);
                                    boolean checked = gridView.isItemChecked(i + 1);
                                    while (checked) {
                                        i++;
                                        checked = gridView.isItemChecked(i + 1);
                                        gridView.setItemChecked(i + 1, true);
                                    }
                                    //gridView.setItemChecked(i + 2, checked);
                                    //i = i + 2;
                                    i++;
                                }
                            }
                            //////////////////////////adapter.notifyDataSetChanged();
                            gridView.requestFocus();//ролд
                            return true;
                        }else{
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "Правильный формат: [-][слово][~][-][слово]",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }
                    }
                return false;
            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!adapter.textForViewing) {
                    if(!recordsAreMerged){
                        //adapter.restoreValues = (ArrayList<String>)dictionary.clone();
                        adapter.restoreValues = new ArrayList<String>(dictionary);
                    }
                    mergeRecords(dictionary);
                    adapter.copyValues = (ArrayList<String>)dictionary.clone();
                    ///////////////////adapter.notifyDataSetChanged();
                    /////////////////////////////////////////////////////////////////////////////////updateAdapter();

                    if (charSequence.length() == 0) {
                        splitRecords(dictionary);
                        ///////////////////////adapter.notifyDataSetChanged();
                        //////////////////////////////////////////////////////////////////////////updateAdapter();
                        //gridView.clearTextFilter();
                        //adapter.getFilter().filter("     ");
                        adapter.getFilter().filter("");
                    } else {
                        //gridView.setFilterText(charSequence.toString());//фыва
                        //adapter.getFilter().filter("");
                        adapter.getFilter().filter(charSequence);

                        //updateAdapter();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //adapter.getFilter().filter(editable);
                textForViewing = false;
                adapter.textForViewing = false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                splitRecords(dictionary);

                /*
                //updateAdapter
                ArrayList<String> dictionaryCopy =  (ArrayList<String>)dictionary.clone();
                adapter.clear();
                adapter.addAll(dictionaryCopy);
                gridView.clearTextFilter();//фыва
                //updateAdapter
                */
                if(!b){
                    adapter.notifyDataSetChanged();
                    updateAdapter();
                }
                gridView.setFastScrollEnabled(false);
                gridView.clearTextFilter();//фыва

            }
        });

        gridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                boolean d = b;
            }
        });

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      /* mWebView.setWebChromeClient(new WebChromeClient(){

           @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //return super.onJsAlert(view, url, message, result);

                Toast toast = Toast.makeText(getApplicationContext(),
                        message,
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return false;
            }
        });*/


////////////////////////////////////////////////////////////////////////////////////////////////////////////////
       /* MyWebView view = new MyWebView(this);
        //WebView view = (WebView) findViewById(R.id.webView);
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl("https://translate.google.com/?hl=ru#ru/en/Кукла");
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String url) {
                v.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView v, String url) {
              //  v.loadUrl("javascript:document.getElementById('gt-res-listen').click();");
            }
        });
        setContentView(view);
*/

        return page;
    }

    @Override
    public void onResume() {
        super.onResume();

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /*
    // выведем текст
        Toast.makeText(getApplicationContext(), ("onResume"),//((TextView) itemClicked).getText(),
                Toast.LENGTH_SHORT).show();
        */

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View page = inflater.inflate(R.layout.fragment_page, null);


        final GridView gridView = (GridView) page.findViewById(R.id.gridView);

        /*
        //Скрыть ответы
        if (!answersAreHidden) {
        //if (gridView.getChildCount() % 2 == 0) {
            if (gridView.getChildCount() > 0) {
                answersAreHidden = true;
            }
            for (int i = 0; i < gridView.getChildCount(); i++) {
                if (i % 2 != 0) {
                    View a = gridView.getChildAt(i);
                    if (a != null) {
                        a.setWillNotDraw(true);
                    }
                }
            }
        //}
        }*/


        dictionary.clear();
        /*
        try {
            FileInputStream fileIn=openFileInput("mytextfile.txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[100];
            String start="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                String[] result = readstring.split(";");

                for (int i = 0; i < result.length; i++) {
                    dictionary.add(result[i]);
                }
            }


            InputRead.close();
            Toast.makeText(getBaseContext(), "File restore successfully!",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        // проверяем доступность SD
        if (!getExternalStorageState().equals(
                MEDIA_MOUNTED)) {
            Toast.makeText(getActivity().getBaseContext(), "SD-карта не доступна: " + getExternalStorageState(), Toast.LENGTH_SHORT).show();
            return;
        }

        // получаем путь к SD
        File sdPath = getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath());// + "/mytextfile.txt");
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, "dictionary.txt");
        try {

            //int k = 1/0;//////////del

            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                String[] result = str.split(";");
                dictionary.add(result[0]);
                gridView.setItemChecked(dictionary.size()-1, Boolean.parseBoolean(result[1]));
            }
            Toast.makeText(getActivity().getBaseContext(), "File restore successfully!",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        adapter.copyValues = (ArrayList<String>)dictionary.clone();

        if(dictionary.size() == 0){
            fillDictionary();
        }

        //LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View page = inflater.inflate(R.layout.fragment_page, null);
//
//
//        final GridView gridView = (GridView) page.findViewById(R.id.gridView);
//
        Adapter adapter = gridView.getAdapter();

        try {
            ((ArrayAdapter<String>)adapter).notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
        }

        ///////////////////////////////////////////////////////////////////////////////////////////updateAdapter();

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        gridPosition = myPref.getInt("gridPosition", 0);
        //gridView.smoothScrollToPosition(LastVisiblePosition);
        //gridView.setSelection(gridView.getCount()-1);

       gridView.clearFocus();

        gridView.post(new Runnable() {
            @Override
            public void run() {
                gridView.requestFocusFromTouch();
                gridView.setSelection(gridPosition);
                gridView.requestFocus();
            }
        });

        //gridView.scrollTo(0, LastVisiblePosition);


//        /////////////////////////////////////////////////////////////////////////////////////////////////
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        List<View> pages = new ArrayList<View>();


//
//        View page = inflater.inflate(R.layout.activity_main, null);
//        pages.add(page);
//
//        page = inflater.inflate(R.layout.page, null);
//        TextView textView = (TextView) page.findViewById(R.id.text_view);
//        textView = (TextView) page.findViewById(R.id.text_view);
//        textView.setText("Страница 2");
//        pages.add(page);
//
//        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
//        ViewPager viewPager = new ViewPager(this);
//        viewPager.setAdapter(pagerAdapter);
//        viewPager.setCurrentItem(0);
//
//        setContentView(viewPager);
///////////////////////////////////////////////////////////////////////////////////////////////////



    }

    @Override
    public void onStop() {
        super.onStop();

        /////////////////////////////////////////////////////////////////////////////saveDictionary();

        final GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);

        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
        edit.putInt("gridPosition", gridView.getFirstVisiblePosition());
        edit.commit();

        /////////////////////////////////////////////////////////////////saveDictionary();
    }
}
