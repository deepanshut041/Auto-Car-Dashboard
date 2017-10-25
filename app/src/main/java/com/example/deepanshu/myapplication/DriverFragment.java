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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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

import static android.R.id.message;
import static java.net.Proxy.Type.HTTP;


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
                    sendPost();
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

    class AuthRequestReceive extends AsyncTask<String, Void, String> {

        private String ci;
        private byte[] symmkey;
        private PublicKey publickey;
        private String xml;
        private Date certexp;
        private String pid;
        private byte[] encSessionKey;
        private String authResponse;
        private String output;
        private byte[] pidbytes;
        private boolean otpres=false;

        private byte[] generateSessionKey() throws Exception {
            KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
            kgen.init(256);
            SecretKey key = kgen.generateKey();
            symmkey = key.getEncoded();

            return symmkey;
        }

        private String makeOTPRequestString(String s) throws Exception{
            String xml;
            return xml="<Auth uid=\""+s+"\" tid=\"public\" sa=\"public\" ver=\"1.6\" ac=\"public\" "
                    + " txn=\""+getTimeStamp(s)+"\" lk=\"MKHmkuz-MgLYvA54PbwZdo9eC3D5y7SVozWwpNgEPysVqLs_aJgAVOI\">"+
                    "<Uses pi=\"y\" pa=\"n\" pfa=\"n\" bio=\"n\" pin=\"n\" otp=\"n\"/>"+
                    "<Meta udc=\"PriyaTest\" pip=\"127.0.0.1\" fdc=\"NC\" idc=\"NA\" lot=\"P\" lov=\"110092\" />"
                    //Add the encrypted session key
                    +"<Skey ci=\""+ci+"\">"+generateRSASessionKey(symmkey)+"</Skey>"
                    +"<Data type=\"X\">"+generateData(symmkey,pidbytes)+"</Data>"
                    +"<Hmac>"+generateHMAC(symmkey,pidbytes)+"</Hmac></Auth>";
        }
        private String generateRSASessionKey(byte[] data) throws IOException, GeneralSecurityException {
            // encrypt the session key with the public key
            try{
                Cipher pkCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

                pkCipher.init(Cipher.ENCRYPT_MODE, publickey);
                encSessionKey = pkCipher.doFinal(data);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return Base64.encodeToString(encSessionKey, Base64.DEFAULT);
        }


        private String generateData(byte[] raw, byte[] clear) throws Exception {
            //generate the Data element
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(clear);
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        }
        private String generateHMAC(byte[] raw,byte[] clear) throws Exception
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");


            md.update(clear);
            byte[] digest = md.digest();
            return generateData(raw,digest);
            //The HMAC can now be encrypted with the same method as generation of Data Block

        }
        private String getTimeStamp(String s) {
            // TODO Auto-generated method stub

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMMddhhmmss"+"."+"SSS");
            String txn = s+ dateFormat.format(new Date());
            return txn;

        }
        private String getTimeStampn()
        {
            SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return date.format(new Date());
        }
        private void makePIDBlock(String name,String gender) throws UnsupportedEncodingException {

            pid="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
                    "<Pid ts=\""+getTimeStampn()+"\" ver=\"1.0\">"
                    +"<Demo><Pi ms=\"P\" mv=\"60\" name=\""+name+"\" gender=\""+gender+"\"/></Demo>"
                    +"</Pid>";
            pidbytes=pid.getBytes("UTF-8");
				/*
				 *
				 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
				 * <Pid ts="2015-05-27T13:18:14" xmlns="http://www.uidai.gov.in/authentication/uid-auth-request-data/1.0">
				 * <Demo><Pi ms="P" mv="100" name="Jaideep Walia" /></Demo></Pid>
				 */


        }
        private void getCertificateIdentifier() {
            SimpleDateFormat ciDateFormat = new SimpleDateFormat("yyyyMMdd");
            ciDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            ci= ciDateFormat.format(this.certexp);

        }

        @Override
        protected String doInBackground(String... params) {
            //android.os.Debug.waitForDebugger();
            try{
                InputStream reader=getActivity().getAssets().open("uidai_auth_stage.cer");



                CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");

                X509Certificate cert = (X509Certificate) certFactory.generateCertificate(reader);
                publickey = cert.getPublicKey();
                certexp = cert.getNotAfter();
                getCertificateIdentifier();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Could not intialize encryption module", e);
            }
            try {
                generateSessionKey();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            try {
                makePIDBlock(params[1],params[2]);
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }


            try {
                xml=makeOTPRequestString(params[0]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            sendAuthRequest(params[0]);





            try {
                parseXML();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return "onPostExecute";

        }


        protected void onPostExecute(String s)
        {

            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();


        }
        protected void sendAuthRequest(String s)
        {



            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://192.168.42.74:8080/uidwebservice/webresources/MyPath/");
            stringBuilder.append(s);


            String url=stringBuilder.toString();

            HttpClient httpclient=new DefaultHttpClient();
            HttpPost httppost=new HttpPost(url);



            try{
                StringEntity se=new StringEntity(xml,HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setEntity(se);
                HttpResponse httpresponse=httpclient.execute(httppost);
                HttpEntity authresentity=httpresponse.getEntity();
                authResponse= EntityUtils.toString(authresentity);


            }
            catch(final Exception e)
            {
                Handler handler=new Handler(getActivity().getMainLooper());
                handler.post(new Runnable(){
                    public void run(){
                        Toast.makeText(getActivity(),"Please check your data connection.",Toast.LENGTH_LONG).show();
                    }
                });


            }
        }
        protected void parseXML() throws XmlPullParserException, IOException
        {XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp=factory.newPullParser();
            xpp.setInput(new StringReader(authResponse));
            int event=xpp.getEventType();
            String ret="";
            String name;
            int errorcode;
            while(event!=XmlPullParser.END_DOCUMENT)
            {
                name =xpp.getName();
                switch(event)
                {
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.START_TAG:
                        if(name.equals("AuthRes")){
                            ret=xpp.getAttributeValue(null,"ret");
                            int count=xpp.getAttributeCount();
                            if("y".equals(ret))
                            {
                                output="Authentication Successful !";
                            }
                            else
                            if("n".equals(ret))
                            {
                                output="ERROR!\n";
                                errorcode=Integer.parseInt(xpp.getAttributeValue(null,"err"));
                                new ErrorCodesAuth();
                                output+= errorcode + " "+ErrorCodesAuth.errcodes.get((Integer.valueOf(errorcode)));}
                        }
                        break;
                }
                event=xpp.next();
            }
            if("".equals(output))
                output="ERROR! Check your internet connection";
            Handler handler=new Handler(getActivity().getMainLooper());
            handler.post(new Runnable(){
                public void run(){
                    Toast.makeText(getBaseContext(),output, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

}
