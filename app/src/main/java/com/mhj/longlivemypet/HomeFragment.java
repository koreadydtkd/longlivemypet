package com.mhj.longlivemypet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class HomeFragment extends Fragment {
    ImageView imgWeather; //날씨 아이콘
    ImageView imgdog;
    TextView txtWeather; //날씨 텍스트
    TextView txtTemp; //온도 텍스트
    TextView txtRain; //강수량 텍스트
    TextView txtWind; // 풍속 텍스트
    TextView txtRainper; // 강수 확율 텍스트
    TextView txtWet; // // 습도 텍스트
    TextView txtcoment;

    // url 구성
    String address;
    String sKey;
    String row;
    String basedate;
    String basetime;
    String nx;
    String ny;

    SimpleDateFormat dateText = new SimpleDateFormat("yyyyMMdd"); //날짜 표시 형식
    SimpleDateFormat hourText = new SimpleDateFormat("HHmm"); //시간 표시 형식

    String simpletime; //api 시간(현재 시간이 아니다) fcstTime
    String today; //오늘 날짜
    String yesterday; //어제 날짜
    long numtime; //현재 시간
    Random random = new Random();

    LocationManager manager;
    private RequestQueue queue;
    private static final String TAG = "HomeFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        imgWeather = rootView.findViewById(R.id.imgWeather);
        imgdog = rootView.findViewById(R.id.imgdog);
        txtWeather = rootView.findViewById(R.id.txtWeather);
        txtTemp = rootView.findViewById(R.id.txtTemp);
        txtWind = rootView.findViewById(R.id.txtWind);
        txtRainper = rootView.findViewById(R.id.txtRainper);
        txtWet = rootView.findViewById(R.id.txtWet);
        txtRain= rootView.findViewById(R.id.txtRain);
        txtcoment = rootView.findViewById(R.id.txtcoment);

        address = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?";
        sKey = "serviceKey=swlEfys72fvDOcvi5LfN3wFzJEdZoWn0QblHo2VWxkyff4hoQ83W5mEI5MGXQ2TRkYK%2BXR%2B8NtErVqrp%2BOUIGg%3D%3D";
        row = "&numOfRows=100&dataType=JSON&pageNo=1";

        Date today = new Date();
        setTime(today);
        setDay(today);

        String weather = address + sKey + row + basedate + basetime + nx + ny;
        Log.e("WeatherFragment", weather);

        queue = Volley.newRequestQueue(getContext());
        startLocationService();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                String weather = address + sKey + row + basedate + basetime + nx + ny;
                //여기에 딜레이 후 시작할 작업들을 입력
                tellMetheWeather(weather);
            }
        }, 500);

        sleeptime();

        return rootView;
    }

    public void setTime(Date date) {
        String now = hourText.format(date);
        numtime = Long.parseLong(now);
        basetimeCalc();
        timeCalc();
    }
    public void setDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        today = dateText.format(date);
        yesterday = dateText.format(calendar.getTime());

        if(numtime >= 0 && numtime < 300){
            basedate = "&base_date=" + yesterday;
        }else if(numtime >=300 && numtime < 600) {
            basedate = "&base_date=" + yesterday;
        } else{
            basedate = "&base_date=" + dateText.format(date);
        }
    }

    public void basetimeCalc(){
        if(numtime >= 0 && numtime < 300){
            basetime = "&base_time=2000";
        }else if(numtime >=300 && numtime < 600){
            basetime = "&base_time=2300";
        }else if(numtime >=600 && numtime < 900){
            basetime = "&base_time=0200";
        }else if(numtime >=900 && numtime < 1200){
            basetime = "&base_time=0500";
        }else if(numtime >=1200 && numtime < 1500){
            basetime = "&base_time=0800";
        }else if(numtime >=1500 && numtime < 1800){
            basetime = "&base_time=1100";
        }else if(numtime >=1800 && numtime < 2100){
            basetime = "&base_time=1400";
        }else if(numtime >=2100 && numtime <= 2359){
            basetime = "&base_time=1700";
        }
    }

    public void timeCalc(){
        if(numtime >= 0 && numtime < 300){
            simpletime = "0000";
        }else if(numtime >=300 && numtime < 600){
            simpletime = "0300";
        }else if(numtime >=600 && numtime < 900){
            simpletime = "0600";
        }else if(numtime >=900 && numtime < 1200){
            simpletime = "0900";
        }else if(numtime >=1200 && numtime < 1500){
            simpletime = "1200";
        }else if(numtime >=1500 && numtime < 1800){
            simpletime = "1500";
        }else if(numtime >=1800 && numtime < 2100){
            simpletime = "1800";
        }else if(numtime >=2100 && numtime <= 2359){
            simpletime = "2100";
        }
    }

    public void sunnyday(){
        int r = random.nextInt(5);
        if(r == 0) {
            txtcoment.setText("날씨가 좋네요.");
            imgdog.setImageResource(R.drawable.doglol);
        } else if(r== 1){
            txtcoment.setText("멍!!");
            imgdog.setImageResource(R.drawable.dogstand);
        } else if(r== 2) {
            txtcoment.setText("주인님 화이팅");
            imgdog.setImageResource(R.drawable.doglol);
        } else if(r== 3) {
            txtcoment.setText("주인님! \n산책가요");
            imgdog.setImageResource(R.drawable.doghappy);
        } else if(r== 4) {
            txtcoment.setText("오늘 간식은 뭔가요?");
            imgdog.setImageResource(R.drawable.dogstand);
        }
    }

    public void cloudyday(){
        int r = random.nextInt(5);
        if(r == 0) {
            txtcoment.setText("구름이 조금 보이네요");
            imgdog.setImageResource(R.drawable.doglol);
        } else if(r== 1){
            txtcoment.setText("멍!!");
            imgdog.setImageResource(R.drawable.dogstand);
        } else if(r== 2) {
            txtcoment.setText("주인님 화이팅");
            imgdog.setImageResource(R.drawable.doglol);
        } else if(r== 3) {
            txtcoment.setText("주인님! \n산책가요");
            imgdog.setImageResource(R.drawable.doghappy);
        } else if(r== 4) {
            txtcoment.setText("음 생각중 이에요");
            imgdog.setImageResource(R.drawable.dogstand);
        }
    }

    public void darkday(){
        int r = random.nextInt(4);
        if(r == 0) {
            txtcoment.setText("날씨가 칙칙하네요");
            imgdog.setImageResource(R.drawable.dogstand);
        } else if(r== 1){
            txtcoment.setText("왈!");
            imgdog.setImageResource(R.drawable.dogstand);
        } else if(r== 2) {
            txtcoment.setText("비가 올수도....");
            imgdog.setImageResource(R.drawable.dogstand);
        } else if(r== 3) {
            txtcoment.setText("오늘 산책은 포기");
            imgdog.setImageResource(R.drawable.dogstand);
        }
    }

    public void rainday(){
        int r = random.nextInt(5);
    }

    public void sleeptime(){
        int r = random.nextInt(5);
        if(numtime >= 1800 && numtime < 2100){
            if(r == 0) {
                txtcoment.setText("저녁입니다.");
                imgdog.setImageResource(R.drawable.doglol);
            }else if(r== 1){
                txtcoment.setText("저녁식사는 드셨나요?");
                imgdog.setImageResource(R.drawable.dogstand);
            }else if(r== 2){
                txtcoment.setText("해가 지네요");
                imgdog.setImageResource(R.drawable.doglol);
            }else if(r== 3){
                txtcoment.setText("킁킁킁");
                imgdog.setImageResource(R.drawable.dogwalk);
            }else if(r== 4){
                txtcoment.setText("멍!멍!");
                imgdog.setImageResource(R.drawable.doglol);
            }
        } else if(numtime >= 2100 && numtime < 2200){
            txtcoment.setText("안영히 주무세요.");
            imgdog.setImageResource(R.drawable.dogsleep);
        } else if(numtime >= 2200 && numtime <= 2359){
            txtcoment.setText("zzzzzZ");
            imgdog.setImageResource(R.drawable.dogsleep);
        } else if(numtime >= 0000 && numtime <= 0600){
            txtcoment.setText("zzzzzZ");
            imgdog.setImageResource(R.drawable.dogsleep);
        }
    }

    private void startLocationService() {
        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(location != null){
                // 기준점 : 날짜변경선 영국 그리니티천문대
                double latitude = location.getLatitude(); // 위도 - 가로좌표
                double longitude = location.getLongitude(); // 경도 - 세로좌표
                int lat = (int)latitude;
                int lon = (int)longitude;
                nx = "&nx=" + lat;
                ny = "&ny=" + lon;
                String message = "startLocationService() 최근 위치\nLat: " + latitude + "\nLon: " + longitude;
                Log.e(TAG, message);
            }else{
                Log.e(TAG, "위치로드 실패");
            }
        }catch (SecurityException e){
            Log.e(TAG, "오류발생");
            e.printStackTrace();
        }
    }

    public void tellMetheWeather(String url) {
        Log.e("tellMetheWeather", "가동중");

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject list = response.getJSONObject("response");
                    JSONObject body = list.getJSONObject("body");
                    JSONObject items = body.getJSONObject("items");
                    JSONArray item = items.getJSONArray("item");
                    for (int i = 0; i < item.length(); i++) {
                        JSONObject object = item.getJSONObject(i);
                        double rain = 0;
                        if(object.getString("fcstTime").equals(simpletime) && object.getString("fcstDate").equals(today) ) {
                            Log.e("WeatherFragment", object.getString("category"));

                            if(object.getString("category").equals("POP")){
                                txtRainper.setText(object.get("fcstValue").toString() + "%");
                                Log.e("WeatherFragment", object.get("fcstValue").toString());
                            }

                            if(object.getString("category").equals("PTY")){
                                Log.e("WeatherFragment", object.get("fcstValue").toString());
                                rain = Double.parseDouble(object.get("fcstValue").toString());

                                if(rain >= 1){
                                    imgWeather.setImageResource(R.drawable.rain);
                                    txtWeather.setText("비");
                                    txtcoment.setText("오늘은 실내에 있죠");
                                    imgdog.setImageResource(R.drawable.dogstand);
                                } else if(rain >= 2 ){
                                    imgWeather.setImageResource(R.drawable.alert);
                                    txtWeather.setText("비또는 눈");
                                    txtcoment.setText("오늘은 실내에 있죠");
                                    imgdog.setImageResource(R.drawable.dogstand);
                                } else if(rain >= 3){
                                    imgWeather.setImageResource(R.drawable.snow);
                                    txtWeather.setText("눈");
                                    txtcoment.setText("눈이 와요 \n주인님");
                                    imgdog.setImageResource(R.drawable.doghappest);
                                } else if(rain >= 4){
                                    imgWeather.setImageResource(R.drawable.lightrain);
                                    txtWeather.setText("소나기");
                                }
                            }

                            if(object.getString("category").equals("R06")){

                                txtRain.setText(object.get("fcstValue").toString() + "mm");

                            }

                            if(object.getString("category").equals("REH")){
                                txtWet.setText(object.get("fcstValue").toString() + "%");
                                Log.e("WeatherFragment", object.get("fcstValue").toString() + "%");
                            }

                            if(object.getString("category").equals("SKY")) {
                                if(rain == 0) {
                                    int sky = Integer.parseInt(object.get("fcstValue").toString());
                                    Log.e("WeatherFragment", sky + "단계");
                                    if (sky == 1 || sky == 2) {
                                        Log.e("WeatherFragment", numtime + "시");
                                        if (numtime >= 0600 && numtime < 1800) {
                                            imgWeather.setImageResource(R.drawable.sunny);
                                            txtWeather.setText("맑음");
                                            sunnyday();
                                        } else {
                                            imgWeather.setImageResource(R.drawable.night);
                                            txtWeather.setText("맑음");
                                        }
                                    } else if (sky == 3) {
                                        if (numtime >= 0600 && numtime < 1800) {
                                            imgWeather.setImageResource(R.drawable.cloud);
                                            txtWeather.setText("구름 조금");
                                            cloudyday();
                                        } else {
                                            imgWeather.setImageResource(R.drawable.cloudnight_118960);
                                            txtWeather.setText("구름 조금");
                                        }
                                    } else if (sky == 4) {
                                        imgWeather.setImageResource(R.drawable.cloundy_118962);
                                        txtWeather.setText("흐림");
                                        imgdog.setImageResource(R.drawable.dogstand);
                                        darkday();
                                    }
                                }
                            }

                            if (object.getString("category").equals("T3H")) {
                                txtTemp.setText(object.get("fcstValue").toString() + "ºc");
                                Log.e("WeatherFragment", object.get("fcstValue").toString() + "ºc");
                            }

                            if(object.getString("category").equals("WSD")){
                                double w = Double.parseDouble(object.get("fcstValue").toString());
                                if(w < 4){
                                    txtWind.setText("약한바람");
                                } else if( w >= 4 && w < 9){
                                    txtWind.setText("약간강한바람");
                                } else if( w >= 9 && w < 14){
                                    txtWind.setText("강한바람");
                                } else if(w > 14){
                                    txtWind.setText("매우강한바람");
                                }
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TEST", "오류");
                error.printStackTrace();
            }
        });
        jsonRequest.setTag(TAG);
        queue.add(jsonRequest);
    }

    public void println(String data) {
        Log.d("WeatherFragment", data);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        manager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

}



