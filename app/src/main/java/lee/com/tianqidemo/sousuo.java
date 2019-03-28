package lee.com.tianqidemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class sousuo extends AppCompatActivity {
    private GridView sousuo;
    private String[] res = {"定位", "北京", "上海", "广州", "深圳", "天津", "杭州", "东莞", "宁波", "西安"
            , "成都", "重庆", "南京", "苏州", "武汉", "厦门", "福州", "昆明", "沈阳", "长春", "大连", "济南", "太原"
            , "南宁", "长沙", "石家庄", "南昌", "哈尔滨", "合肥", "郑州"};    //建立一个数组，保存定位地点和热门城市列表
    private LocationManager locationManager;
    private String locationProvider;
    protected String[] weatherResult;
    private EditText mEditText;
    private ListView list_key;
    private TextView r_city, hint;
    private CloseReceiver mCloseReceiver;  //接收关闭命令
    private SQLiteDatabase mSQLiteDatabase;
    private DatabaseUtil databaseUti = new DatabaseUtil();
    final int RESULT_CODE = 30;
    private Context context = null;
    private List<City> citylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();  //初始化view
        getLocation();
        initdb();  //将assests中的数据复制到data/data/lee.com.tianqidemo/databases/
        re_close();  //接收关闭广播
        setListener();
        setAdp();   //绑定适配器
    }
    protected void setAdp() {  //GridView绑定适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(sousuo.this,   //适配器
                android.R.layout.simple_list_item_1, res);
        sousuo.setAdapter(adapter);         //绑定适配器
    }
    protected void initdb() {//初始化db
        try {
            context = this.createPackageContext("lee.com.tianqidemo",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mSQLiteDatabase = databaseUti.packDataBase(context);
    }
    protected void initView() {  //初始化view
        setContentView(R.layout.activity_sousuo);
        mEditText = (EditText) findViewById(R.id.key);            //绑定
        hint = (TextView) findViewById(R.id.hint);
        mEditText.setSingleLine(true);
        list_key = (ListView) findViewById(R.id.list_key);
        sousuo = (GridView) findViewById(R.id.gv);
        r_city = (TextView) findViewById(R.id.r_city);
    }
    protected void setListener() {   //监控GridView中的点击
        sousuo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   //监控点击项目
                JSONAnalysis_city(res[position]);  //调用函数获取天气情况
            }
        });
        list_key.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = citylist.get(position).getName();  //取出该项的城市名
                JSONAnalysis_city(name);   //获取该城市的天气情况
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String[] columns = {"province", "city", "name", "pinyin", "py", "phoneCode", "areaCode"};  //查询列
                String selection = "name like ?";//过滤器
                String key = mEditText.getText().toString().trim();
                String[] selectionArgs = new String[]{mEditText.getText().toString().trim() + "%"};
                citylist = databaseUti.query(mSQLiteDatabase, columns, selection, selectionArgs);  //获取符合的城市
                if (key == null || key.equals("")) {
                    list_key.setVisibility(View.GONE);
                    hint.setVisibility(View.GONE);
                    sousuo.setVisibility(View.VISIBLE);
                    r_city.setText("选择热门城市");
                }
                if (null != key && !key.equals("")) {
                    updateListView(citylist);  //更新listview
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    protected void re_close() {   //接收关闭广播
        mCloseReceiver = new CloseReceiver(); //接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("CLOSE");
        this.registerReceiver(mCloseReceiver, filter);
        unregisterReceiver(mCloseReceiver);
    }
    private void updateListView(List<City> list) {
        List<String> names = new ArrayList<String>();
        sousuo.setVisibility(View.GONE);
        list_key.setVisibility(View.VISIBLE);
        if (list.size() == 0) {
            list_key.setVisibility(View.GONE);
            r_city.setVisibility(View.GONE);
            r_city.setVisibility(View.VISIBLE);
            hint.setText("无匹配城市");
            hint.setVisibility(View.VISIBLE);
        } else {
            r_city.setText("请选择城市");
            hint.setVisibility(View.GONE);
            for (int i = 0; i < list.size(); i++) {
                names.add(list.get(i).getName() + "-" + list.get(i).getCity() + "," + list.get(i).getProvince());
            }
            ArrayAdapter<String> searchCityAdapter = new ArrayAdapter<String>(this,   //适配器
                    android.R.layout.simple_list_item_1, names);
            list_key.setAdapter(searchCityAdapter);
        }
    }
    protected void getLocation() {  //获取当前位置
        //动态的请求权限
        if(Build.VERSION.SDK_INT >= 23) {  //判断安卓版本是否大于等于6.0
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
//当前Activity没有获得READ_CONTACTS权限时
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 0x11);
            }
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        //获取地理位置管理器
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);

        //正常
        if (location != null) {                         //自动识别当前城市
            Thread_getlaction myThread = new Thread_getlaction();
            myThread.setLocation(location);     //将res[0]替换为定位城市
            myThread.start();
        }
    }
    protected String JSONAnalysis_location(String string) {             //解析经纬度—定位json的函数
        JSONObject object = null;
        try {
            object = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {           //由经纬度得到城市名
            JSONObject getJsonObj = object.getJSONObject("result");
            String city = getJsonObj.getJSONObject("addressComponent").getString("city");
            String district = getJsonObj.getJSONObject("addressComponent").getString("district");
            String[] columns = {"province", "city", "name", "pinyin", "py", "phoneCode", "areaCode"};  //查询列
            String selection = "name = ?";//过滤器
            String[] selectionArgs = new String[]{district};
            if(databaseUti.querySingle(mSQLiteDatabase, columns, selection, selectionArgs))   //判断数据库中是否有该地区
            {
                return district;
            }
            else {
                return city;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT)
                    .show();
            return null;
        }
    }
    protected void JSONAnalysis_city(String city) {    //解析天气预报json的函数  实况天气
        Thread_getweather myThread1 = new Thread_getweather();
        myThread1.setCity(city);
        myThread1.start();
    }
    public class Thread_getweather extends Thread {
        private String city;
        private void setCity(String city) {
            this.city = city;
        }
        private String getCity() {
            return this.city;
        }
        public void run() {
            try {
                URL url = new URL("https://free-api.heweather.com/v5/weather/?city=" + getCity() + "&key=dc52d184436b458ca79d22a9d806d4a3");
                 /*   *
                    * 这里网络请求使用的是类HttpURLConnection，另外一种可以选择使用类HttpClient。
                    */
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setRequestMethod("GET");//使用GET方法获取
                conn.setConnectTimeout(5000);
                int code = conn.getResponseCode();
                if (code == 200) {  //code=200 表示访问成功
                    InputStream is = conn.getInputStream();
                    String result = HttpUtils.readMyInputStream(is);
                    JSONObject object = new JSONObject(result);
                    JSONArray getJsonAry = object.getJSONArray("HeWeather5");       //解析json数据
                    JSONObject getJsonObj = getJsonAry.getJSONObject(0);
                    String city_cond = getJsonObj.getJSONObject("basic").getString("city")     //城市名称
                            + "|" + getJsonObj.getJSONObject("now").getJSONObject("cond").getString("txt");
                    String temp = getJsonObj.getJSONObject("now").getString("tmp") + "℃";//温度
                    String SD = getJsonObj.getJSONObject("now").getString("hum") + "%";//湿度
                    String wind = getJsonObj.getJSONObject("now").getJSONObject("wind").getString("dir")   //风向
                            + getJsonObj.getJSONObject("now").getJSONObject("wind").getString("sc") + "级";     //风力
                    String city_aqi = "N/A";
                    String cond_id=getJsonObj.getJSONObject("now").getJSONObject("cond").getString("code");  //天气状况代码
                    try {
                        city_aqi = getJsonObj.getJSONObject("aqi").getJSONObject("city").getString("aqi")
                                + " " + getJsonObj.getJSONObject("aqi").getJSONObject("city").getString("qlty");  //aqi和级别
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String[] forecast = new String[3];        //未来三天预报
                    for (int i = 0; i < 3; i++) {
                        forecast[i] = "温度：" + getJsonObj.getJSONArray("daily_forecast").getJSONObject(i).getJSONObject("tmp").getString("min")
                                + "~" + getJsonObj.getJSONArray("daily_forecast").getJSONObject(i).getJSONObject("tmp").getString("max") + "℃"
                                + " 湿度：" + getJsonObj.getJSONArray("daily_forecast").getJSONObject(i).getString("hum") + "%"
                                + "\n\t\t\t\t\t\t天气状况：" + getJsonObj.getJSONArray("daily_forecast").getJSONObject(i).getJSONObject("cond").getString("txt_d");
                    }
                    String[] code_id = new String[3];
                    for (int j = 0; j < 3; j++) {
                        code_id[j] = getJsonObj.getJSONArray("daily_forecast").getJSONObject(j).getJSONObject("cond").getString("code_d");
                    }
                    weatherResult = new String[]{city_cond, temp, SD, wind, city_aqi, forecast[0], forecast[1], forecast[2],
                            code_id[0], code_id[1], code_id[2],cond_id};
                    information_return();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(sousuo.this, "获取数据失败", Toast.LENGTH_SHORT)
                        .show();
                Looper.loop();
            }
        }
    }
    public void information_return() {
        Intent mIntent = new Intent(sousuo.this, zhu.class);
        Bundle b = new Bundle();      //将天气预报信息封装  并发送
        b.putStringArray("weather", weatherResult);
        mIntent.putExtras(b);
        setResult(RESULT_CODE, mIntent);
        finish();
    }
    public class Thread_getlaction extends Thread{
        private Location mLocation;
        private void setLocation(Location location){
            mLocation=location;
        }
        public void run() {
            try {
                URL url = new URL("http://api.map.baidu.com/geocoder/v2/?location=" + mLocation.getLatitude() + "," + mLocation.getLongitude()
                        + "&output=json&pois=1&ak=noQAvLLG14GpQcw38UlDFM2mRQUbbyKD");        //调用百度Geocoding API 获取经纬度—定位json
/*
         这里网络请求使用的是类HttpURLConnection，另外一种可以选择使用类HttpClient。
*/
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setRequestMethod("GET");//使用GET方法获取
                conn.setConnectTimeout(5000);
                InputStream is = conn.getInputStream();
                String result = HttpUtils.readMyInputStream(is);
                res[0] = JSONAnalysis_location(result);   //将获取城市名
                Looper.prepare();
                Toast.makeText(sousuo.this,"当前定位："+JSONAnalysis_location(result),Toast.LENGTH_LONG).show();
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
                res[0]="null";
                Looper.prepare();
                Toast.makeText(sousuo.this,"定位失败",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }
    class CloseReceiver extends BroadcastReceiver {//结束程序

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();

        }
    }
}

