package com.example.delimes.geolocation;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class PageFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private View page;
    private View page2;

    private TextView tvEnabledGPS;
    private TextView tvStatusGPS;
    private TextView tvLocationGPS;
    private TextView tvEnabledNet;
    private TextView tvStatusNet;
    private TextView tvLocationNet;

    private TextView tvTextView;
    private EditText editTextRadius;

    private LocationManager locationManager;
    private StringBuilder sbGPS = new StringBuilder();
    private StringBuilder sbNet = new StringBuilder();

    private Marker markerGPS;
    private Marker markerNet;

    private SupportMapFragment mapFragment;
    private android.support.v4.app.Fragment frag2;
    //GoogleMap map;
    public GoogleMap mMap;
    private static final int DEFAULT_ZOOM = 17;
    private DBHelper dbHelper;
    private ArrayList<String> items = new ArrayList<>();
    private DataAdapter adapter;
    //ArrayList<Marker> markersList = new ArrayList<>();;
    private Map<String, Marker> courseMarkers = new HashMap<String, Marker>();

    private final String LOG_TAG = "myLogs";
    private Address p1 = null;
    private LatLng p2 = null;
    private LatLng currentPosition;
    public LatLngBounds bounds;
    private double radius;
    private Circle circle = null;
    private Polygon polygon = null;
    private Timer mTimer = new Timer();
    private MyTimerTask mMyTimerTask = new MyTimerTask();
    private long networkTS;
    private boolean gpsTimeSetting = true;
    private boolean timeUpdate = false;

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.fragment_page, container, false);
        page2 = inflater.inflate(R.layout.fragment_page2, container, false);

        List<android.support.v4.app.Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        //frag1 = fragments.get(0);
        frag2 = fragments.get(1);

        tvEnabledGPS = (TextView) page.findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) page.findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) page.findViewById(R.id.tvLocationGPS);
        tvEnabledNet = (TextView) page.findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) page.findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) page.findViewById(R.id.tvLocationNet);

        tvTextView = (TextView) page.findViewById(R.id.textView);
        editTextRadius = (EditText) page.findViewById(R.id.editTextRadius);

        GridView gridView = (GridView) page.findViewById(R.id.gridView);


        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        View btnLocationSettings = page.findViewById(R.id.btnLocationSettings);
        btnLocationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        // Привяжем массив через адаптер к GridView
        adapter = new DataAdapter(getContext(), items);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(6);
        gridView.setVerticalSpacing(35);//android:verticalSpacing="35dp"


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                TextView itemTextView = (TextView) itemClicked.findViewById(R.id.text_view);

                if(itemTextView.getText().toString().contains("ID = ")){

                    ///////////////////////////////////////////
                    // подключаемся к БД
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    int delCount = db.delete("mytable", itemTextView.getText().toString(), null);
//                    markersList.get(position).remove();
//                    markersList.remove(position);

                    items.remove(position); //"id"
                    items.remove(position); //"address"
                    items.remove(position); //"latitude"
                    items.remove(position); //"longitude"
                    items.remove(position); //"stockBegan"
                    items.remove(position); //"stockEnd"

                    adapter.notifyDataSetChanged();

                    String ID = itemTextView.getText().toString().replace("ID = ", "");
                    if (courseMarkers.containsKey(ID)) {

                        //1. Remove the Marker from the GoogleMap
                        courseMarkers.get(ID).remove();

                        //2. Remove the reference to the Marker from the HashMap
                        courseMarkers.remove(ID);

                    }
                }
                return false;
            }
        });

        editTextRadius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //editTextRadius.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    radius = Double.valueOf(editTextRadius.getText().toString());
                    ((PageFragment2)frag2).radius = radius;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(getContext());

        ///////////////////////////////////////////
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int addressColIndex = c.getColumnIndex("address");
            int latitudeColIndex = c.getColumnIndex("latitude");
            int longitudeColIndex = c.getColumnIndex("longitude");
            int stockBeganColIndex = c.getColumnIndex("stockBegan");
            int stockEndColIndex = c.getColumnIndex("stockEnd");

            do {
                // получаем значения по номерам столбцов
                items.add("ID = " + c.getInt(idColIndex));
                items.add("address = " + c.getString(addressColIndex));
                items.add("latitude = " + c.getString(latitudeColIndex));
                items.add("longitude = " + c.getString(longitudeColIndex));
                items.add("stockBegan = " + c.getString(stockBeganColIndex));
                items.add("stockEnd = " + c.getString(stockEndColIndex));


                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        }
        adapter.notifyDataSetChanged();
        ///////////////////////////////////////////

        init();

        return page;
    }

    private void init() {

        radius = Double.valueOf(editTextRadius.getText().toString());
        ((PageFragment2)frag2).radius = radius;

//        GridView gridView = (GridView) page.findViewById(R.id.gridView);
//        gridView.requestFocus();

        //View current = getActivity().getCurrentFocus();
        //if (current != null) current.clearFocus();

        //editTextRadius.clearFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextRadius.getWindowToken(), 0);

        //networkTS = Calendar.getInstance().getTimeInMillis();



    }

    @Override
    public void onResume() {
        super.onResume();

//        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }


        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
        }catch (SecurityException e)
        {

            e.printStackTrace();
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "SecurityException:"+ e.toString(),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return;
        }
        checkEnabled();


    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
//            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
            try {

            showLocation(locationManager.getLastKnownLocation(provider));

            }catch (SecurityException e)
            {

                e.printStackTrace();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        "SecurityException:"+ e.toString(),
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null || mMap == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));

            if (markerGPS != null) {
                markerGPS.remove();
            }

            timeUpdate = true;

            markerGPS = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker())
                    .title("GPS")
                    .draggable(true));
            markerGPS.setTag("Ваше местоположение по данным GPS");


        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));

            if(markerNet != null){
                markerNet.remove();
            }
            markerNet = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("Net")
                    .draggable(true));
            markerNet.setTag("Ваше местоположение по данным сети");

        }



        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(),
                        location.getLongitude()), DEFAULT_ZOOM));


       ////////////////////////////////////////////////////////////////////////////////////////////
        //This is the current user-viewable region of the map
        //LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        radius = Double.valueOf(editTextRadius.getText().toString());

        bounds = toBounds(currentPosition, radius);
        addItemsToMap();

        p1 = getAddressFromLocation(location);
        if (p1!=null) {

            Toast toast = Toast.makeText(getContext(),
                    p1.toString(),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            tvTextView.setText(p1.getAddressLine(0));


//            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
//            radius = Double.valueOf(editTextRadius.getText().toString());

//            bounds = toBounds(currentPosition, radius);
//            addItemsToMap();

//            List<android.support.v4.app.Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
//            android.support.v4.app.Fragment frag1 = fragments.get(0);
//            android.support.v4.app.Fragment frag2 = fragments.get(1);
//            ((PageFragment2)frag2).bounds = bounds;


//            p2 = getLocationFromAddress(p1.getAddressLine(0));
//
//            if(p2!= null && bounds.contains(p2))
//            {
//                mMap.addMarker(new MarkerOptions()
//                        .position(p2).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                        .title("Address")
//                        .draggable(true ));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                        p2, DEFAULT_ZOOM));
//            }

        }


//        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }

        try {

        Location GPSlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (GPSlocation != null && timeUpdate ){
            timeUpdate = false;
            networkTS = GPSlocation.getTime();
        }

        if (networkTS != 0L && gpsTimeSetting){
            // delay 0ms, repeat in 5000ms
            mTimer.schedule(mMyTimerTask, 0, 60 * 1000);//обновляется каждую минуту (update time)
            gpsTimeSetting = false;

        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        Date resultdate = new Date(networkTS);

        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                "networkTS:"+ sdf.format(resultdate),
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();






        }catch (SecurityException e)
        {

            e.printStackTrace();
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "SecurityException:"+ e.toString(),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return;
        }



        /////////////////////////////////////////////////////////////////////////////////////////

    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public LatLng getLocationFromAddress(String strAddress)
    {
        Geocoder coder= new Geocoder(getContext());

        if(!coder.isPresent()){

            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "!coder.isPresent()",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();

            return null;
        }
        List<Address> address;
        //LatLng p2 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p2 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p2;

    }

    public Address getAddressFromLocation(Location location)
    {
        Geocoder coder= new Geocoder(getContext());
        if(!coder.isPresent()){
            return null;
        }
        List<Address> address;
        //Address p1 = null;

        try
        {
            address = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
            if(address==null)
            {
                return null;
            }

            p1 = address.get(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }

    public Address getAddressFromLocation(LatLng latLng)
    {
        Geocoder coder= new Geocoder(getContext());
        if(!coder.isPresent()){
            return null;
        }
        List<Address> address;
        //Address p1 = null;

        try
        {
            address = coder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(address==null)
            {
                return null;
            }

            p1 = address.get(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

    public LatLng computeOffset(LatLng from, double distance, double heading) {
        distance /= 6371000;//EARTH_RADIUS;
        heading = toRadians(heading);
        // http://williams.best.vwh.net/avform.htm#LL
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double cosDistance = cos(distance);
        double sinDistance = sin(distance);
        double sinFromLat = sin(fromLat);
        double cosFromLat = cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading);
        double dLng = atan2(
                sinDistance * cosFromLat * sin(heading),
                cosDistance - sinFromLat * sinLat);
        return new LatLng(toDegrees(asin(sinLat)), toDegrees(fromLng + dLng));
    }


    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                computeOffset(center, distanceFromCenterToCorner, 45.0);

        LatLng northwestCorner =
                computeOffset(center, distanceFromCenterToCorner, 315.0);
        LatLng southeastCorner =
                computeOffset(center, distanceFromCenterToCorner, 135.0);


        //write circkle
        if(circle != null){
            circle.remove();
        }
        circle = mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.RED));
        //.fillColor(Color.BLUE));


        //write polygon
        PolygonOptions polygoneOptions = new PolygonOptions()
                .add(southwestCorner).add(southeastCorner)
                .add(northeastCorner).add(northwestCorner)
                .strokeColor(Color.CYAN).strokeWidth(10);

        if(polygon != null){
            polygon.remove();
        }
        polygon = mMap.addPolygon(polygoneOptions);


        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    public void addStock(LatLng latLng) {

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // получаем данные из полей ввода
        Address adr = getAddressFromLocation(latLng);
        if (adr == null) {
            return;
        }
        String address = adr.getAddressLine(0);
        LatLng latLngShares = getLocationFromAddress(address);
        if (latLngShares == null) {
            return;
        }

        Calendar rightNow = Calendar.getInstance();


        String latitude = Double.toString(latLngShares.latitude);
        String longitude = Double.toString(latLngShares.longitude);
        String stockBegan = Long.toString(rightNow.getTimeInMillis());
        rightNow.add(Calendar.MINUTE, 5);
        String stockEnd = Long.toString(rightNow.getTimeInMillis());


        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(LOG_TAG, "--- Insert in mytable: ---");
        // подготовим данные для вставки в виде пар: наименование столбца - значение

        cv.put("address", address);
        cv.put("latitude", latitude);
        cv.put("longitude", longitude);
        cv.put("stockBegan", stockBegan);
        cv.put("stockEnd", stockEnd);

        // вставляем запись и получаем ее ID
        long rowID = db.insert("mytable", null, cv);
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);

        items.add("ID = " + rowID);
        items.add("address = " + address);
        items.add("latitude = " + latitude);
        items.add("longitude = " + longitude);
        items.add("stockBegan = " + stockBegan);
        items.add("stockEnd = " + stockEnd);


        adapter.notifyDataSetChanged();

        LatLng latLngShares2 = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        //markersList.add(
        if (!courseMarkers.containsKey(Long.toString(rowID))) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLngShares2).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .title(Long.toString(rowID))
                    //.snippet(address));
                    .snippet("Подробнее..."));
            //marker.setTag(address + "\r\n" + "Акция проводится в Интернете и распространяется на граждан Украины.");
            marker.setTag(address + "\r\n" + " Акция проводится в Интернете и распространяется на граждан Украины.Акция проводится в Интернете и распространяется на граждан Украины.Акция проводится в Интернете и распространяется на граждан Украины.Акция проводится в Интернете и распространяется на граждан Украины.");
            courseMarkers.put(Long.toString(rowID), marker);
        }
        //);

    }

    public void addItemsToMap()
    {
        if(this.mMap != null)
        {
            //bounds This is the current user-viewable region of the map

            //Loop through all the items that are available to be placed on the map
            ///////////////////////////////////////////
            // подключаемся к БД
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            Cursor c = db.query("mytable", null, null, null, null, null, null);

            // ставим позицию курсора на первую строку выборки
            // если в выборке нет строк, вернется false
            if (c.moveToFirst()) {

                // определяем номера столбцов по имени в выборке
                int idColIndex = c.getColumnIndex("id");
                int addressColIndex = c.getColumnIndex("address");
                int latitudeColIndex = c.getColumnIndex("latitude");
                int longitudeColIndex = c.getColumnIndex("longitude");
                int stockBeganColIndex = c.getColumnIndex("stockBegan");
                int stockEndColIndex = c.getColumnIndex("stockEnd");


                do {
                    // получаем значения по номерам столбцов
                    int ID = c.getInt(idColIndex);
                    String address = c.getString(addressColIndex);
                    double latitude = Double.valueOf(c.getString(latitudeColIndex));
                    double longitude = Double.valueOf(c.getString(longitudeColIndex));
                    long stockBegan = Long.valueOf(c.getString(stockBeganColIndex));
                    long stockEnd = Long.valueOf(c.getString(stockEndColIndex));

                    LatLng latLngShares = new LatLng(latitude, longitude);
                    //if (networkTS != 0L && gpsTimeSetting){
                    boolean stockIsValid = (networkTS >= stockBegan) && (networkTS <= stockEnd);
                    if (bounds.contains(latLngShares) && stockIsValid) {
                        //markersList.add(

                        if (!courseMarkers.containsKey(Integer.toString(ID))) {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(latLngShares).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                    .title(Integer.toString(ID))
                                    .snippet("Подробнее..."));
                            marker.setTag(address + "\r\n" + "Акция проводится в Интернете и распространяется на граждан Украины.");
                            courseMarkers.put(Integer.toString(ID), marker);

                        }
                        //);
                    } else {
                        if (courseMarkers.containsKey(Integer.toString(ID))) {

                            //1. Remove the Marker from the GoogleMap
                            courseMarkers.get(Integer.toString(ID)).remove();

                            //2. Remove the reference to the Marker from the HashMap
                            courseMarkers.remove(Integer.toString(ID));

                        }
                    }

                    // переход на следующую строку
                    // а если следующей нет (текущая - последняя), то false - выходим из цикла
                } while (c.moveToNext());
            }
        }
    }


    class DBHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "myDB";

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }



        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "address text,"
                    + "latitude text,"
                    + "longitude text,"
                    + "stockBegan text,"
                    + "stockEnd text"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

    }

    public class DataAdapter extends ArrayAdapter<String> {

        private final Context mContext;
        private ArrayList<String> values;

        // Конструктор
        public DataAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.list_item, values);
            // TODO Auto-generated constructor stub
            this.values = values;
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            final LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.list_item, parent, false);

            TextView text_view = (TextView) rowView.findViewById(R.id.text_view);
            text_view.setText(values.get(position));
//            TextView label = (TextView) convertView;
//
//            if (convertView == null) {
//                convertView = new TextView(mContext);
//                label = (TextView) convertView;
//            }
//            label.setText(values.get(position));
//            return (convertView);
            return (rowView);
        }

        // возвращает содержимое выделенного элемента списка
        public String getItem(int position) {
            return values.get(position);
        }

    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            /*
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "dd:MMMM:yyyy HH:mm:ss a", Locale.getDefault());
            final String strDate = simpleDateFormat.format(calendar.getTime());
 */
            if (networkTS != 0L) {
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTimeInMillis(networkTS);
                rightNow.add(Calendar.MINUTE, 1);//update time
                networkTS = rightNow.getTimeInMillis();
            }




            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //LocalDateTime plusMinutes(long minutes)


                    addItemsToMap();

                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
                    Date resultdate = new Date(networkTS);

                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "+networkTS+:"+ sdf.format(resultdate),
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();



                }
            });



        }
    }

}
