package com.example.delimes.geolocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class PageFragment2 extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    public SupportMapFragment mapFragment;

    private View page;
    private View page2;

    private android.support.v4.app.Fragment frag1;

    public double radius;

    public static PageFragment2 newInstance(int page) {
        PageFragment2 fragment = new PageFragment2();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        page = inflater.inflate(R.layout.fragment_page, container, false);
        page2 = inflater.inflate(R.layout.fragment_page2, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        TextView pageHeader=(TextView)result.findViewById(R.id.displayText);
//        String header = String.format("Фрагмент %d", pageNumber+1);
//        pageHeader.setText(header);

        //MyWebViewClient view = new MyWebViewClient();
        //final WebView mWebView = (WebView) page.findViewById(R.id.webView);

        return page2;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //List<Fragment> fragments = getFragmentManager().getFragments();
        //Fragment frag = fragments.get(0);
        //((PageFragment) frag.getActivity()).fillDictionary();

        List<android.support.v4.app.Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        frag1 = fragments.get(0);
        android.support.v4.app.Fragment frag2 = fragments.get(1);

        ((PageFragment)frag1).mMap = mMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

//                if(bounds.contains(latLng))
//                {
//                    mMap.addMarker(new MarkerOptions()
//                            .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
//                            .title("test"));
//                }

                ((PageFragment)frag1).addStock(latLng);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                //double radius = Double.valueOf(((EditText) page.findViewById(R.id.editTextRadius)).getText().toString());
                ((PageFragment)frag1).bounds = ((PageFragment)frag1).toBounds(marker.getPosition(), radius);
                ((PageFragment)frag1).addItemsToMap();

            }
        });
    }

}
