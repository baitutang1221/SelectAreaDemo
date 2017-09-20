package com.example.neo.selectareademo;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.neo.selectareademo.areaselect.com.mrwujay.cascade.model.CityModel;
import com.example.neo.selectareademo.areaselect.com.mrwujay.cascade.model.DistrictModel;
import com.example.neo.selectareademo.areaselect.com.mrwujay.cascade.model.ProvinceModel;
import com.example.neo.selectareademo.areaselect.com.mrwujay.cascade.service.XmlParserHandler;
import com.example.neo.selectareademo.areaselect.kankan.wheel.widget.OnWheelChangedListener;
import com.example.neo.selectareademo.areaselect.kankan.wheel.widget.WheelView;
import com.example.neo.selectareademo.areaselect.kankan.wheel.widget.adapters.ArrayWheelAdapter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by xiazhenjie on 2017/9/13.
 */

public class SelectAreaPopupWindow extends PopupWindow implements View.OnClickListener, OnWheelChangedListener {

    private View mView;
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Button mBtnConfirm;
    private Activity mActivity;

    public Handler mHandler;

    /**
     * ?????
     */
    protected String[] mProvinceDatas;
    /**
     * key - ? value - ??
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - ?? values - ??
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - ?? values - ???
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * ??????????
     */
    protected String mCurrentProviceName;
    /**
     * ???????????
     */
    protected String mCurrentCityName;
    /**
     * ???????????
     */
    protected String mCurrentDistrictName ="";

    /**
     * ???????????????
     */
    protected String mCurrentZipCode ="";

    public SelectAreaPopupWindow(Activity context, final Handler handler){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.select_activity_main, null);
        mActivity = context;
        mHandler = handler;

        //????PopupWindow??View
        this.setContentView(mView);
        //????PopupWindow??????????
        this.setWidth(LayoutParams.MATCH_PARENT);
        //????PopupWindow??????????
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //????PopupWindow???????????
        this.setFocusable(true);
        //????SelectPicPopupWindow?????????????
        this.setAnimationStyle(R.style.Animation);
        //????????ColorDrawable?????????
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //????SelectPicPopupWindow????????????
        this.setBackgroundDrawable(dw);

        setUpViews(mView);
        setUpListener();
        setUpData();
    }

    private void setUpViews(View mView) {
        mViewProvince = (WheelView) mView.findViewById(R.id.id_province);
        mViewCity = (WheelView) mView.findViewById(R.id.id_city);
        mViewDistrict = (WheelView) mView.findViewById(R.id.id_district);
        mBtnConfirm = (Button) mView.findViewById(R.id.btn_confirm);

    }

    private void setUpListener() {
        // ???change???
        mViewProvince.addChangingListener(this);
        // ???change???
        mViewCity.addChangingListener(this);
        // ???change???
        mViewDistrict.addChangingListener(this);
        //???onclick???
        mBtnConfirm.setOnClickListener(this);
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mActivity, mProvinceDatas));
        // ?????????????
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    /**
     * ?????????????????WheelView?????
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] { "" };
        }

        int pCurrent2 = mViewDistrict.getCurrentItem();
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[pCurrent2];

        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mActivity, areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     *?????????????????WheelView?????
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(mActivity, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = mActivity.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // ???????????xml?????????
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // ????xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // ?????????????????
            provinceList = handler.getDataList();
            //*/ ??????????????????????
            if (provinceList!= null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList!= null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i=0; i< provinceList.size(); i++) {
                // ???????????????
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j=0; j< cityList.size(); j++) {
                    // ??????????????????????
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k=0; k<districtList.size(); k++) {
                        // ????????????????/???????
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // ??/??????????????mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // ??-??/?????????????mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // ?-??????????????mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                Log.d("Neo","onClick");
                showSelectedResult();
                break;
            default:
                break;
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    private void showSelectedResult() {

        Message message = Message.obtain();
        message.what = 500;
        Bundle bundle = new Bundle();
        bundle.putString("provice", mCurrentProviceName);
        bundle.putString("city", mCurrentCityName);
        bundle.putString("district", mCurrentDistrictName);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }
}
