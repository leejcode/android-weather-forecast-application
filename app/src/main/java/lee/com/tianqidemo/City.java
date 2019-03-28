package lee.com.tianqidemo;

/**
 * Created by admin on 2017/2/10.
 */

public class City {   //City类型   用与接收city.db中的数据
    private String province;
    private String city;
    private String name;
    private String pinyin;
    private String py;
    private String phoneCode;
    private String areaCode;
    public City(String province, String city,String name, String pinyin, String py, String phoneCode, String areaCode) {        //整体赋值
        this.province = province;
        this.city = city;
        this.name=name;
        this.pinyin = pinyin;
        this.py = py;
        this.phoneCode = phoneCode;
        this.areaCode = areaCode;
    }

    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public String getPinyin() {
        return pinyin;
    }
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPy() {
        return py;
    }
    public void setPy(String py) {
        this.py = py;
    }

    public String getAreaCode() {
        return areaCode;
    }
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPhoneCode() {
        return phoneCode;
    }
    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }



}
