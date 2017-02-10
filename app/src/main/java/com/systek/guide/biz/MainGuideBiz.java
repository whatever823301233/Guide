package com.systek.guide.biz;

import com.systek.guide.bean.Exhibit;
import com.systek.guide.bean.MyBeacon;
import com.systek.guide.iBiz.IMainGuideBiz;
import com.systek.guide.db.handler.ExhibitHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Qiang on 2016/8/10.
 */
public class MainGuideBiz implements IMainGuideBiz {

    private Executor executorService;
    public MainGuideBiz(){
        executorService= Executors.newCachedThreadPool();
    }
/*

    @Override
    public void findExhibits(String museumId, final Collection<Beacon> beacons, final OnInitBeanListener listener) {


        AsyncTask<String,Integer,List<Exhibit>> task = new AsyncTask<String,Integer,List<Exhibit>>(){

            @Override
            protected List<Exhibit> doInBackground(String... params) {
                String museumId=params[0];
                List<MyBeacon> myBeacons= BeaconHandler.queryBeacons(beacons);
                if(myBeacons!=null){
                    List<Exhibit> exhibitList= ExhibitHandler.queryExhibit(museumId,myBeacons);
                    if(exhibitList!=null&&exhibitList.size()>0){
                        LogUtil.i("","搜索结果 "+exhibitList.size());
                        return exhibitList;
                    }else{
                        LogUtil.i("","搜索结果为空 ");
                        return Collections.emptyList();
                    }
                }
                LogUtil.i("","MyBeacon==null");
                return Collections.emptyList();
            }

            @Override
            protected void onPostExecute(List<Exhibit> exhibits) {
                if(exhibits!=null){
                    listener.onSuccess(exhibits);
                }else{
                    listener.onFailed();
                }
            }
        };
        task.executeOnExecutor(executorService,museumId);
    }

    @Override
    public void getExhibits ( String museumId, Collection<Beacon> beacons, OnBeaconCallback callback ) {
        CustomBeaconManager.getInstance().calculateDistance(beacons);
        List<SystekBeacon> exhibitLocateBeacons = CustomBeaconManager.getInstance().getExhibitLocateBeacons();
        if (callback==null){return;}
        List<MyBeacon> beaconList=changeToBeaconList(exhibitLocateBeacons,20.0);
        if(beaconList==null||beaconList.size()==0){return;}
        List<Exhibit> exhibitBeansList=searchExhibitByBeacon(beaconList);
        if (exhibitBeansList==null||exhibitBeansList.size()==0) {return;}
        // TODO: 2016/9/27
        callback.getNearestBeacon(beaconList.get(0));
        callback.getExhibits(exhibitBeansList);
        callback.getNearestExhibit(exhibitBeansList.get(0));
    }
*/

    @Override
    public boolean checkHasPlay ( Exhibit exhibit ) {
        return false;// TODO: 2016/11/30
    }


    /**
     * 根据beacon集合找出展品集合
     * @param beaconBeans
     * @return
     */
    private static List<Exhibit> searchExhibitByBeacon(List<MyBeacon> beaconBeans) {

        List<Exhibit> exhibitList= new ArrayList<>();

        if(beaconBeans==null||beaconBeans.size()==0){return exhibitList;}
        String museumId= beaconBeans.get(0).getMuseumId();
            /*遍历beacon结合，获得展品列表*/
        for(int i=0;i<beaconBeans.size();i++){
            MyBeacon beacon=beaconBeans.get(i);
            if(beacon==null){continue;}
            String beaconId=beacon.getId();
            List<Exhibit> tempList= ExhibitHandler.queryExhibit(museumId,beaconId);
            if(tempList==null||tempList.size()==0){continue;}
            /*for(Exhibit beaconBean:tempList){
                beaconBean.setDistance(beaconBeans.get(i).getDistance());
            }*/
                /*去重复*/
            exhibitList.removeAll(tempList);
            exhibitList.addAll(tempList);
        }
        return exhibitList;
    }

}
