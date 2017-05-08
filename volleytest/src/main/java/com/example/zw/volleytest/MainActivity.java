package com.example.zw.volleytest;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    //请求事例 ：  http://www.tuling123.com/openapi/api?key=6b91798cba1745a482dbba98aaadfaaa&info=你好
    private  String path = "http://www.tuling123.com/openapi/api";
    private  String key = "6b91798cba1745a482dbba98aaadfaaa";

    TextView mTextView ;
    Button bt_StringGet;
    Button bt_JsonObjectGet;
    Button bt_JsonArrayGet;
    Button bt_ImageRequest;
    Button bt_StringPost;
    Button bt_JsonObjectPost;
    EditText mEditText;
    ImageView mImageView;

    RequestQueue mRequestQueue = MyApplication.getRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setListener();


    }

    private void setListener() {
        bt_StringGet.setOnClickListener(this);
        bt_JsonObjectGet.setOnClickListener(this);
        bt_JsonArrayGet.setOnClickListener(this);
        bt_ImageRequest.setOnClickListener(this);
        bt_StringPost.setOnClickListener(this);
        bt_JsonObjectPost.setOnClickListener(this);
    }

    private void initViews() {
        mTextView= (TextView) findViewById(R.id.tv);
        mEditText=(EditText) findViewById(R.id.ed);
        bt_StringGet= (Button) findViewById(R.id.bt_StringGet);
        bt_JsonObjectGet= (Button) findViewById(R.id.bt_JsonObjectGet);
        bt_JsonArrayGet= (Button) findViewById(R.id.bt_JsonArraryGet);
        bt_ImageRequest=(Button)findViewById(R.id.bt_ImageRequest);
        bt_StringPost=(Button)findViewById(R.id.bt_StringPost);
        bt_JsonObjectPost=(Button)findViewById(R.id.bt_JsonPost);
        mImageView=(ImageView)findViewById(R.id.imageView);
    }




    //获得 输入框里的信息 并添加到 url
    @NonNull
    private String getInput() {
        String s= mEditText.getText().toString();
        mEditText.setText("");
        StringBuilder sb = new StringBuilder(path);
        if (s.equals("") || TextUtils.isEmpty(s)) {
            s="你好";
        }
        return sb.append("?key=").append(key).append("&info=").append(s).toString();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.bt_StringGet :
                volleyStringGet();
                break;
            case R.id.bt_JsonObjectGet :
                volleyJsonObjectGet();
                break;
            case R.id.bt_JsonArraryGet :
                volleyJsonArrayGet();
                break;
            case R.id.bt_ImageRequest:
                getImage();
                break;
            case R.id.bt_StringPost:
                volleyStringPost();
                break;
            case R.id.bt_JsonPost:
                volleyJsonObjectPost();
                break;


        }

    }

    /**如果在服务器的返回数据的header中没有指定字符集那么就会默认使用 ISO-8859-1 字符集。
     ISO-8859-1的别名叫做Latin1。这个字符集支持部分是用于欧洲的语言，不支持中文~
     很不能理解为什么将这个字符集作为默认的字符集。Volley这个框架可是要用在网络通信的环境中的。
     吐槽也没有用，我们来看一下如何来解决中文乱码的问题。有以下几种解决方式：
     在服务器的返回的数据的header的中contentType加上charset=UTF-8的声明。
     当你无法修改服务器程序的时候，可以定义一个新的子类。覆盖parseNetworkResponse这个方法，直接使用UTF-8对服务器的返回数据进行转码。   */
    //使用 此方法 返回结果会产生乱码问题 ，需要 重写 parseNetworkResponse（）。
    private void volleyJsonObjectPost() {

        String s =mEditText.getText().toString();
        mEditText.setText("");
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("info", s);
        JSONObject jsonObject = new JSONObject(map);

        JsonObjectRequest jsonObjectRequset = new JsonObjectRequest(Request.Method.POST, path, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                    mTextView.setText(jsonObject.toString());
                try {
                    if (jsonObject.getInt("code") == 100000){
                        String message=jsonObject.getString("text");
                        mTextView.setText(message);
                    }else {
                        mTextView.setText("错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mTextView.setText("JsonObjectPost请求失败");
            }
        }){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(new String(response.data,"UTF-8"));
                        return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return Response.error(new ParseError(e));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return Response.error(new ParseError(e));
                    }
            }
        };

        jsonObjectRequset.setTag("JsonObjectPost");
        mRequestQueue.add(jsonObjectRequset);
    }


    //Post方法将请求参数 放在Body中 不是放在 头中。 所以URL 有所不同
    private void volleyStringPost() {

        final String s =mEditText.getText().toString();
        mEditText.setText("");
        StringRequest stringRequest =new StringRequest(Request.Method.POST, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //对String 进行截取
                // {"code":100000,"text":"你好"}
                int start=s.lastIndexOf(":") +2 ;
                int end=s.lastIndexOf("\"");
                String message=s.substring(start,end);
                Log.i(TAG, "onResponse: s =   "+s);
                mTextView.setText(message);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mTextView.setText("Post请求失败");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //在这里设置需要post的参数
                Map<String, String> map = new HashMap<String, String>();
                map.put("key",  key);
                map.put("info", s);
                return map;
            }
        };

        stringRequest.setTag("StringPost");
        mRequestQueue.add(stringRequest);

    }


    /**还有个ImageLoader 可以搜索郭霖 博客*/
    private void getImage() {
        String url ="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1493976545046&di=51ab503ab4d979e85668989dbaaee7a4&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fforum%2Fpic%2Fitem%2Fb8389b504fc2d562c68ba67fe71190ef77c66ca4.jpg";

        ImageRequest imageRequest =new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this,"获取图片失败",Toast.LENGTH_SHORT).show();
            }
        });

        imageRequest.setTag("ImageRequest");
        mRequestQueue.add(imageRequest);
    }

    private void volleyJsonArrayGet() {
        String url =getInput();
        Log.i(TAG, "volleyJsonArrayGet: url="+url);

        JsonArrayRequest mJsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                mTextView.setText(jsonArray.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mTextView.setText("对不起，数据返回类型不是JSonArray,请求失败");
            }
        });
        mJsonArrayRequest.setTag("JsonArraryGet");
        mRequestQueue.add(mJsonArrayRequest);

    }

    private void volleyJsonObjectGet() {
        String url =getInput();
        Log.i(TAG, "volleyJsonGet: url ="+ url);
        JsonObjectRequest mJsonObjectRequset = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt("code") == 100000){
                        String message=jsonObject.getString("text");
                        mTextView.setText(message);
                    }else {
                        mTextView.setText("错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mTextView.setText("对不起，网络请求失败");
            }
        });

        mJsonObjectRequset.setTag("JsonObjectRequest");
        mRequestQueue.add(mJsonObjectRequset);
    }

    private void volleyStringGet() {
        String url = getInput();
        Log.i(TAG, "volleyStringGet: url ="+url);

        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //对String 进行截取
                // {"code":100000,"text":"你好"}
                int start=s.lastIndexOf(":") +2 ;
                int end=s.lastIndexOf("\"");
                String message=s.substring(start,end);
                Log.i(TAG, "onResponse: s =   "+s);
                mTextView.setText(message);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mTextView.setText("对不起，网络请求失败");
            }
        });

        mStringRequest.setTag("StringRequest");
        mRequestQueue.add(mStringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll("StringRequest");
        mRequestQueue.cancelAll("JsonObjectRequest");
        mRequestQueue.cancelAll("JsonArraryGet");
        mRequestQueue.cancelAll("ImageRequest");
        mRequestQueue.cancelAll("StringPost");
        mRequestQueue.stop();
    }
}
