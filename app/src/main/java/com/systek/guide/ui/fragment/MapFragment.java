package com.systek.guide.ui.fragment;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nimbledevices.indoorguide.GuideManager;
import com.nimbledevices.indoorguide.GuideManagerListener;
import com.nimbledevices.indoorguide.MovementModeListener;
import com.nimbledevices.indoorguide.RoutingListener;
import com.nimbledevices.indoorguide.ZoneListener;
import com.systek.guide.R;
import com.systek.guide.base.util.LogUtil;
import com.systek.guide.ui.iView.IMapView;
import com.systek.guide.presenter.MapPresenter;
import com.systek.guide.ui.BaseFragment;
import com.systek.guide.ui.widget.MyFloorPlanViewPager;

import java.io.File;

public class MapFragment extends BaseFragment implements IMapView,
        GuideManagerListener,
        LocationListener,
        ZoneListener,
        RoutingListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String nddFileName = "2a21-635-building-04-1.ndd";

    private String museumId;

    private OnFragmentInteractionListener mListener;
    private MyFloorPlanViewPager mapView;
    private GuideManager guideManager;

    private boolean useRoutedLocation;

    private MapPresenter presenter;
    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean isUseRoutedLocation() {
        return useRoutedLocation;
    }
    @Override
    public void setUseRoutedLocation(boolean useRoutedLocation) {
        this.useRoutedLocation = useRoutedLocation;
    }

    @Override
    public void setUserLocation(Location location) {
        if(mapView != null){
            mapView.setUserLocation(location);
        }
    }

    @Override
    public void setRoute(double[][] triplets) {
        mapView.setRoute(triplets);
    }

    @Override
    public void setTitle() {
        mListener.setMapTitle();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param museumId Parameter 1.
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(String museumId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, museumId);
        fragment.setArguments(args);
        return fragment;
    }


    private void initMap() {
        try{
            guideManager = GuideManager.getInstance(getHoldingActivity());
            guideManager.setCompassAllowed(false);
            guideManager.setAccelerometerAllowed(false);
            guideManager.setGyroscopeAllowed(false);
            guideManager.addGuideManagerListener(this,getHoldingActivity());
            guideManager.addZoneListener(this,getHoldingActivity());
            //guideManager.addMovmementModeListener(movementModeListener,this);
            loadNdd();
        }catch ( Exception e ){
            e.printStackTrace();
        }
    }

    private void loadNdd() {
        // TODO: 2016/12/2
        File nddTarget = new File(Environment.getExternalStorageDirectory(), nddFileName);
        guideManager.setNDD(nddTarget);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(getTag(),"onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getTag(),"onCreate");
        presenter = new MapPresenter(this);
        if (getArguments() != null) {
            museumId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(getTag(),"onViewCreated");
        initMap();
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.i(getTag(),"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getTag(),"onResume");
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(getTag(),"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(getTag(),"onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(getTag(),"onSaveInstanceState");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getTag(),"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(getTag(),"onDetach");
        mListener = null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mapView = (MyFloorPlanViewPager)findViewById(R.id.map_view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }


    private MovementModeListener movementModeListener = new MovementModeListener() {

        @Override
        public void didStartWalking() {
            Log.i(getTag(),"didStartWalking");
        }

        @Override
        public void didStopWalking() {
            Log.i(getTag(),"didStopWalking");
        }
    };



    @Override
    public void onLocationChanged(Location location) {
        presenter.onLocationChanged(location);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle provider) {
        LogUtil.i(getTag(), provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        LogUtil.i(getTag(), provider );
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(getTag(), provider );
    }

    @Override
    public void onNDDLoaded() {
        mapView.setDataSource(guideManager);
        guideManager.requestLocationUpdates(this,getHoldingActivity());
        guideManager.requestRoutingUpdates(this,getHoldingActivity());
        guideManager.requestZoneUpdates(this,getHoldingActivity());

       /* List<JSONObject> list = guideManager.getPOIs("OfficeG");
        for(JSONObject j: list) {
            ///Overriding background color like in iOS example
            try {
                j.put("backgroundColor", "#FF4081");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MyPOIMarkerView marker = new MyPOIMarkerView(mapView.getContext(), j);
            Bundle bundle = new Bundle();
            bundle.putString("POI",j.toString());
            marker.setExtras(bundle);
            marker.setOnClickListener(new MyPOIMarkerView.OnMarkerClickListener() {
                @Override
                public void onClick(FloorPlanMarker mMarker, Location location, Bundle extras) {
                    String ext = (String) extras.get("POI");
                    //Toast.makeText(MainActivity.this,ext,Toast.LENGTH_SHORT).show();
                    mapView.removeMarker(mMarker);
                }

            });
            mapView.addMarker(marker);
        }
*/
        //guideManager.startRouting("Room GA01");
        guideManager.startRouting("Room GE01");

    }

    @Override
    public void onNDDLoadFailure(Throwable throwable) {
        LogUtil.e(getTag(),throwable.toString());
    }

    @Override
    public void onError(Throwable throwable) {
        LogUtil.e(getTag(),throwable.toString());
    }

    @Override
    public void onLocationOnRouteChanged(Location location) {
        presenter.onLocationOnRouteChanged(location);
    }

    @Override
    public void onStatusOnRouteChanged(int i, int i1, int i2, int i3) {
        //Log.i(TAG," i = "+i+" i1 = "+i1+" i2 = "+i2+" i3 = "+i3);
    }

    @Override
    public void onRouteChange(double[][] doubles) {
        presenter.onRouteChange(doubles);
    }

    @Override
    public void onRouteCalculationFailure(int i, String msg) {
        presenter.onRouteCalculationFailure(i,msg);
    }

    @Override
    public void onZoneEntered(long l, String s) {
        Toast.makeText(getHoldingActivity(),l +" "+s,Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onZoneExited(long l, String s) {
        Toast.makeText(getHoldingActivity(),l +" "+s,Toast.LENGTH_SHORT ).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void setMapTitle();
    }
}
