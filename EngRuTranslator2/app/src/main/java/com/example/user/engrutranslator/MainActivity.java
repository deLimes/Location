package com.example.user.engrutranslator;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStorageState;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewPager pager=(ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
       return true;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();

        List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        android.support.v4.app.Fragment frag1 = fragments.get(0);
        android.support.v4.app.Fragment frag2 = fragments.get(1);

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_save:
                ((PageFragment)frag1).saveDictionary();

                return true;
            case R.id.action_restore:
                ((PageFragment)frag1).fillDictionary();
                ((PageFragment)frag1).updateAdapter();

                return true;
            default:
                //return true;
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

   /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();

    }


    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url)

        {
            view.loadUrl(url.toString());
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            Toast.makeText(getApplicationContext(), "Finish", Toast.LENGTH_SHORT).show();
            //view.loadUrl("javascript:alert('Prived');");
            //view.loadUrl("javascript:document.getElementById(\"gt-res-listen\").click();");


        }
    }

    private class MyCustomChromeClient extends WebChromeClient
    {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //return super.onJsAlert(view, url, message, result);

            Toast toast = Toast.makeText(getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            return true;
        }
    }

    public class MyFragmentAdapter extends FragmentPagerAdapter {
        public MyFragmentAdapter(FragmentManager mgr) {
            super(mgr);
        }
        @Override
        public int getCount() {
            return(2);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            if(position == 0) {
                return (PageFragment.newInstance(position));
            }else{
                return (PageFragment2.newInstance(position));
            }
        }
    }




}


