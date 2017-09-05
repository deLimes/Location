package com.example.delimes.geolocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class PageFragment2 extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    public SupportMapFragment mapFragment;

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
        final View page=inflater.inflate(R.layout.fragment_page2, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        TextView pageHeader=(TextView)result.findViewById(R.id.displayText);
//        String header = String.format("Фрагмент %d", pageNumber+1);
//        pageHeader.setText(header);

        //MyWebViewClient view = new MyWebViewClient();
        //final WebView mWebView = (WebView) page.findViewById(R.id.webView);

        return page;
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
        android.support.v4.app.Fragment frag1 = fragments.get(0);
        android.support.v4.app.Fragment frag2 = fragments.get(1);

        ((PageFragment)frag1).mMap = mMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
