package com.example.deepanshu.myapplication;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

/**
 * Created by Deepanshu on 10/26/2017.
 */

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
                +"<Hmac>"+generateHMAC(symmkey,pidbytes)+"</Hmac>"+
                "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" +
                "        <SignedInfo>" +
                "            <CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>" +
                "            <SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>" +
                "            <Reference URI=\"\">" +
                "                <Transforms>" +
                "                    <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>" +
                "                </Transforms>" +
                "                <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>" +
                "                <DigestValue>FhlOiMvf9mpRo2Ex0DPSUv90pZtsascfUhUhWGufCQo=</DigestValue>" +
                "            </Reference>" +
                "        </SignedInfo>" +
                "        <SignatureValue>UOUZWPBhnsEBjVlRFXOGkjE5hwsR9y4RQxX0CqkodTs8iaBY8B+YQFOsLuR9yA47Lw0gZpA+FJE4" +
                "L2eKNKXOy+iaDEtZnG93QLTlVlWhZNTs0k1mbnI8ZuPcRnwq2NIBvXb8UsA4O59Sq7eISa7VizuZ" +
                "CrVAnHmQFvaE4RpKapznbngVXGJZMTG41Lfxn76vZ9XDut9ZgyU7FXqpqJVU8oB3lcgu022n41pV" +
                "ZohKcFetGmqMkJ84DI3K9AN4yFynmKGfzQu7sIEBmpPOf9ZWzNVmeK8YmyzmR9a5G+Lr4rrA+8ub" +
                "XoYLSAJHyVtuNu1ZEHzw3Txnj6vagUdf6uS71Q==</SignatureValue>" +
                "    </Signature></Auth>"
                ;
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
            InputStream reader=null;
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
        try {
            sendAuthRequest(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

            /*
            try {
                parseXML();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } */


        return "onPostExecute";

    }


    protected void onPostExecute(String s)
    {

        //Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();


    }
    protected void sendAuthRequest(String s) throws IOException {



        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://auth.uidai.gov.in/1.6/public/"+s.charAt(0)+"/"+s.charAt(1)+"/MG41KIrkk5moCkcO8w-2fc01-P7I5S-6X2-X7luVcDgZyOa2LXs3ELI");
        String urlStr=stringBuilder.toString();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/xml");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(xml.toString());

            os.flush();
            os.close();
            String responseData = "";
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){//Response is okay
                String line = "";
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=reader.readLine()) != null) {
                        responseData += line;
                    }
                    Log.v("xml rexpose",responseData + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                // Server is down or webserver is changed.
                Log.v("xml rexpose", "not done failed");
            }
        }
        catch(final Exception e)
        {



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
    }

}
