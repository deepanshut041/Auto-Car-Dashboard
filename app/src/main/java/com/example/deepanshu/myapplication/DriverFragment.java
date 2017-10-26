package com.example.deepanshu.myapplication;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverFragment extends Fragment {
    private EditText driverName, driverUid, driverDl, driverDob;
    private TextView requestMsg;
    private Button submitButton;
    private String urlAdress = "http://192.168.1.113:3001/api/drivers/new";

    public DriverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_driver, container, false);
        driverName = (EditText) rootView.findViewById(R.id.driver_name_editText);
        driverDob = (EditText) rootView.findViewById(R.id.driver_dob_editText);
        driverUid = (EditText) rootView.findViewById(R.id.driver_uid_editText);
        driverDl = (EditText) rootView.findViewById(R.id.driver_dl_editText);
        requestMsg = (TextView) rootView.findViewById(R.id.driver_register_msg);
        submitButton = (Button) rootView.findViewById(R.id.driver_register_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = driverName.getText().toString();
                String dob = driverDob.getText().toString();
                String uid = driverUid.getText().toString();
                String dlno = driverDl.getText().toString();
                if ((name.length() > 2)&&(dob.length() > 2 )&&(uid.length() > 2 )&&(dlno.length() > 2 )){

                }else {
                    requestMsg.setText("Please enter information correctly");
                    requestMsg.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });
        return rootView;
    }

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlAdress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("driver_name", driverName.getText().toString());
                    jsonParam.put("driver_uid", driverUid.getText().toString());
                    jsonParam.put("driver_dob", driverDob.getText().toString());
                    jsonParam.put("driver_dl", driverDl.getText().toString());

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    if(conn.getResponseCode() == 200){
                        requestMsg.setText("User Added SuccessFully");
                    }
                    else{
                        requestMsg.setText("Unable to add User");
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


}
