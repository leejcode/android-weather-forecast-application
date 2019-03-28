package lee.com.tianqidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Calendar;


public class zhu extends AppCompatActivity {
    private TextView wendu;             // 绑定数据
    private TextView didian;
    private TextView shidu;
    private TextView wind;
    private TextView city_aqi;
    private TextView fc1,fc2,fc3;
    private ImageView iv1,iv2,iv3;
    private FloatingActionButton add;
    private long firstTime = 0;//记录用户首次点击返回键的时间
    private CloseReceiver mCloseReceiver;  //接收关闭命令
    final int REQUEST_CODE=1;
    final int RESULT_CODE=30;
    private Bundle re;//接收回传数据
    private RelativeLayout back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getSahrePreference();
        re_close();//接收关闭指令
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(zhu.this,sousuo.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        //初始化
        ImageLoader.getInstance().init(configuration);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {       //沉浸化UI
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
    protected void initView() { //初始化view
        setContentView(R.layout.activity_zhu);
        wendu = (TextView) findViewById(R.id.wendu);
        didian = (TextView) findViewById(R.id.didian);
        shidu = (TextView)findViewById(R.id.content_hum);
        wind=(TextView)findViewById(R.id.content_wind) ;
        city_aqi=(TextView)findViewById(R.id.content_api);
        fc1=(TextView)findViewById(R.id.fc1);
        fc2=(TextView)findViewById(R.id.fc2);
        fc3=(TextView)findViewById(R.id.fc3);
        iv1=(ImageView)findViewById(R.id.iv1);
        iv2=(ImageView)findViewById(R.id.iv2);
        iv3=(ImageView)findViewById(R.id.iv3);
        add=(FloatingActionButton)findViewById(R.id.add);
        back=(RelativeLayout)findViewById(R.id.content_zhu);
    }
    protected void re_close(){   //接收
        mCloseReceiver=new CloseReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("CLOSE");
        this.registerReceiver(mCloseReceiver,filter);
        unregisterReceiver(mCloseReceiver);
    }
    @Override
    public void onBackPressed() {           //实现双击返回退出
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(zhu.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            Intent state = new Intent();//发送关闭广播
            state.setAction("CLOSE");
            sendBroadcast(state);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE&&resultCode==RESULT_CODE)
            try {
                re = data.getExtras();
                set_xml(re);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected void set_xml(Bundle re){
        try{
            String[] weather = re.getStringArray("weather");
            didian.setText(weather[0]);
            wendu.setText(weather[1]);
            shidu.setText(weather[2]);
            wind.setText(weather[3]);
            city_aqi.setText(weather[4]);
            fc1.setText("今天：" + weather[5]);
            fc2.setText("明天：" + weather[6]);
            fc3.setText("后天："+weather[7]);
            String[] url = {"http://files.heweather.com/cond_icon/" + weather[8] + ".png"
                    ,"http://files.heweather.com/cond_icon/" + weather[9] + ".png"
                    ,"http://files.heweather.com/cond_icon/" + weather[10] + ".png"};
            ImageLoader.getInstance().displayImage(url[0], iv1);
            ImageLoader.getInstance().displayImage(url[1], iv2);
            ImageLoader.getInstance().displayImage(url[2], iv3);
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if(6<hour&&hour<18) {
                if (Integer.valueOf(weather[11])==100)
                    back.setBackground(getResources().getDrawable(R.drawable.day_clearsky));
                else if(Integer.valueOf(weather[11])==102||Integer.valueOf(weather[11])==103)
                    back.setBackground(getResources().getDrawable(R.drawable.day_partlycloudy));
                else if(Integer.valueOf(weather[11])==101||Integer.valueOf(weather[11])==104)
                    back.setBackground(getResources().getDrawable(R.drawable.day_cloudy));
                else if (Integer.valueOf(weather[11]) < 300)
                    back.setBackground(getResources().getDrawable(R.drawable.day_clearsky));
                else if (Integer.valueOf(weather[11]) < 400)
                    back.setBackground(getResources().getDrawable(R.drawable.day_rain));
                else if(Integer.valueOf(weather[11]) < 500)
                    back.setBackground(getResources().getDrawable(R.drawable.day_snow));
                else
                    back.setBackground(getResources().getDrawable(R.drawable.day_fog));
            }
            else
            {
                if (Integer.valueOf(weather[11])==100)
                    back.setBackground(getResources().getDrawable(R.drawable.night_clearsky));
                else if(Integer.valueOf(weather[11])==102||Integer.valueOf(weather[11])==103)
                    back.setBackground(getResources().getDrawable(R.drawable.night_partlycloudy));
                else if(Integer.valueOf(weather[11])==101||Integer.valueOf(weather[11])==104)
                    back.setBackground(getResources().getDrawable(R.drawable.night_cloudy));
                else if (Integer.valueOf(weather[11]) < 300)
                    back.setBackground(getResources().getDrawable(R.drawable.night_clearsky));
                else if (Integer.valueOf(weather[11]) < 400)
                    back.setBackground(getResources().getDrawable(R.drawable.night_rain));
                else if(Integer.valueOf(weather[11]) < 500)
                    back.setBackground(getResources().getDrawable(R.drawable.night_snow));
                else
                    back.setBackground(getResources().getDrawable(R.drawable.day_fog));}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void  setSharedPreference()  {//保存天气信息
        try {
            SharedPreferences   weather_info = getSharedPreferences("weather_info", 0);
            SharedPreferences.Editor editor = weather_info.edit();
            Bundle re_save = re;
            String[] weather = re_save.getStringArray("weather");
            for (int i = 0; i < weather.length; i++) {
                editor.putString("weather" + i, weather[i]);
            }
            editor.commit();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getSahrePreference(){  //读取天气信息
        try{
            SharedPreferences weather_info = getSharedPreferences("weather_info", 0);
            String[] weatherinfo=new String[12];
            for(int i=0;i<12;i++)
            {
                weatherinfo[i]=weather_info.getString("weather"+i,"");//缺省值
            }
            Bundle s=new Bundle();
            s.putStringArray("weather",weatherinfo);
            set_xml(s);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        setSharedPreference();
    }
    class CloseReceiver extends BroadcastReceiver {//结束程序
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
