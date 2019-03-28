package lee.com.tianqidemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by admin on 2017/2/9.
 * 用于将assets中db文件复制到data/data/lee.com.tianqidemo/databases/
 */

public class DatabaseUtil {
    private static String table = "city";  //表名
    @SuppressLint("SdCardPath")
    public static SQLiteDatabase packDataBase(Context context){   //存放数据库
        // /data/data/lee.com.demo/databases目录是准备放 SQLite 数据库的地方，也是 Android 程序默认的数据库存储目录
        // 数据库名为 city.db
        String DB_PATH = "/data/data/lee.com.tianqidemo/databases/";
        String DB_NAME = "city.db";
        // 检查 SQLite 数据库文件是否存在
        if (!(new File(DB_PATH + DB_NAME)).exists()) {
            // 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
            File f = new File(DB_PATH);
            // 如 database 目录不存在，新建该目录
            if (!f.exists()) {
                f.mkdir();
            }
            try {
                // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
                InputStream is = context.getAssets().open(DB_NAME);
                // 输出流,在指定路径下生成db文件
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                // 文件写入
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                // 关闭文件流
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
    }
    public static boolean querySingle(SQLiteDatabase sqliteDB, String[] columns, String selection, String[] selectionArgs) {  //判断定位到的区县能否获取天气预报
        try {
            Cursor cursor = sqliteDB.query(table, columns, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {         //找到第一个符合的数据
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
return false;
    }
    public List<City> query(SQLiteDatabase sqliteDB,String[] columns, String selection, String[] selectionArgs) {   //获取查询城市列表
        List<City> cityInfoList = new ArrayList<City>();
        City cityInfo = null;
        Cursor cursor = sqliteDB.query(table, columns, selection, selectionArgs, null, null, "_id desc");
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String province = cursor.getString(cursor
                        .getColumnIndex("province"));
                String city = cursor.getString(cursor.getColumnIndex("city"));
                String name=cursor.getString(cursor.getColumnIndex("name"));
                String pinyin = cursor.getString(cursor.getColumnIndex("pinyin"));
                String py=cursor.getString(cursor.getColumnIndex("py"));
                String phoneCode = cursor.getString(cursor.getColumnIndex("phoneCode"));
                String areaCode = cursor.getString(cursor.getColumnIndex("areaCode"));
                cityInfo = new City(province, city, name ,pinyin ,py , phoneCode ,areaCode);
                cityInfoList.add(cityInfo);
            }
        }
        cursor.close();
        return cityInfoList;
    }
}
