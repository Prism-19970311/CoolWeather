package com.prism.coolweather.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prism.coolweather.R;
import com.prism.coolweather.customwidget.MyProgressBar;
import com.prism.coolweather.db.City;
import com.prism.coolweather.db.County;
import com.prism.coolweather.db.Province;
import com.prism.coolweather.provider.SourceProvider;
import com.prism.coolweather.util.HttpUtil;
import com.prism.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *  Create by 棱镜  *
 **/

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    //private ProgressDialog progressDialog;    //现在用ProgressBar代替这个类
    /**
     * 在API level 26 中，ProgressDialog被声明不赞成使用，应使用的替代方法是ProgressBar
     利用下列代码实现ProgressBar的出现和消失
     progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
     progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
     如果想模仿ProgressDialog出现时，用户无法与界面继续交互的效果
     getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
     恢复用户与界面的交互
     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
     */
    private MyProgressBar myProgressBar;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     *  县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.tv_title);
        backButton = view.findViewById(R.id.bt_back);
        listView = view.findViewById(R.id.lv_listview);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY) {
                    queryCities();
                }else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvineceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = SourceProvider.SOURCE_ADDRESS;
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询全国所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvineceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = SourceProvider.SOURCE_ADDRESS + "/" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询全国所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = SourceProvider.SOURCE_ADDRESS + provinceCode + "/" + cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        showProgressBar();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                /**
                 * 通过 runOnUiThread()方法回到主线程处理逻辑
                 */
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        closeProgressBar();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("conty".equals(type)) {
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressBar();
                            if ("privince".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            }else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 现实进度对话框
     */
    private void showProgressBar() {
        /**
         * 在API level 26 中，ProgressDialog被声明不赞成使用，应使用的替代方法是ProgressBar
         利用下列代码实现ProgressBar的出现和消失
         progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
         progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
         如果想模仿ProgressDialog出现时，用户无法与界面继续交互的效果
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
         WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
         恢复用户与界面的交互
         getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
         */
        if (myProgressBar == null) {
            myProgressBar = new MyProgressBar(getActivity());
            myProgressBar.setText("加载失败");
        }
        myProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭进度条对话框
     */
    private void closeProgressBar() {
        if (myProgressBar != null) {
            myProgressBar.setVisibility(View.GONE);
        }
    }
}
