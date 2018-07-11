package test3.simkadrvs;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerInfo extends AppCompatActivity implements Observer {



//fp
//FP
    String FPID;
    String checkuser;
    int giRet = -100;
    public static final String TAG = "TestFileActivity";
    public static final String sURL = "http://gerbang2017.ddns.net:8081/soap/IIBiometric";
    public static final String SOAP_ACTION_MatchFinger_one2n_WSQ = "urn:uBiometricIntf-IIBiometric#MatchFinger_one2n_WSQ";
    public static final String METHOD_NAME_MatchFinger_one2n_WSQ = "MatchFinger_one2n_WSQ";
    public static final String path = "/storage/emulated/0/Image.wsq";
    public static final String NAMESPACE = "urn:uBiometricIntf-IIBiometric";
    private ProgressDialog progress;
    String byoperator = "http://aril.16mb.com/simcard/operator.php";
    String byinfo = "http://aril.16mb.com/simcard/SimInfo.php";
    //http://www.gerbangrevolusi.com/test
    String byidURL = "http://aril.16mb.com/simcard/query.php";
    String getDocument = "http://aril.16mb.com/simcard/getDocno.php";
    String byfinger = "http://aril.16mb.com/simcard/gambar.php";
    String testURL = "http://152.3.214.12:8080/arges-service/api/person/query?app-id=test&app-key=test&person-id=";
    private ImageView custimage;
    private TextView tvName;
    private TextView tvPass;
    private TextView tvID;
    private TextView tvdob;
    private TextView tvgender;
    private TextView tvNationality;
    private TextView tvAdd1;
    private TextView tvAdd2;
    private TextView tvAdd3;
    private TextView tvPostcode;
    private TextView tvCity;
    private TextView tvState;
    String Info;
    String Info2;
    private Spinner spinner2;
    private TextView PhoneNo;
    private TextView SerialNo;
    private TextView PUK1;
    private TextView PUK2;


    String DocNo;
    String id;
    String FaceID;
    private boolean imageLoaded = false;
    String imageurl;

    //Fingerprint byte
    byte[] Fp1;
    byte[] Fp2;
    BioAction bio = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);

       // Intent intent = getIntent();
        //Bundle bundle = intent.getExtras();
        //id = bundle.getString("ID");
        //System.out.println("Customer:" + DocNo);

        tvName = (TextView)findViewById(R.id.tvName);
        tvPass = (TextView)findViewById(R.id.tvPassport);
        tvID = (TextView)findViewById(R.id.tvID);
        tvdob = (TextView)findViewById(R.id.tvDOB);
        tvgender = (TextView)findViewById(R.id.tvGender);
        tvNationality = (TextView)findViewById(R.id.tvNationality);
        tvAdd1 = (TextView)findViewById(R.id.tvAdd1);
        tvAdd2 = (TextView)findViewById(R.id.tvAdd2);
        tvAdd3 = (TextView)findViewById(R.id.tvAdd3);
        tvPostcode = (TextView)findViewById(R.id.tvPostcode);
        tvCity = (TextView)findViewById(R.id.tvCity);
        tvState = (TextView)findViewById(R.id.tvState);
        custimage = (ImageView)findViewById(R.id.Custimage);

       // Query();
       // bio = new BioAction(this,this);
        //bio.connect(false);
        // Verify();

        Intent intent2 = new Intent(Intent.ACTION_MAIN);
        intent2.setComponent(new ComponentName("com.morpho.morphosample", "com.morpho.morphosample.ConnectionActivity"));
        startActivity(intent2);

        ImageButton lookup = (ImageButton)findViewById(R.id.imageButton7);
        lookup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               GetMatch();
            }
        });

        Button theButton2 = (Button)findViewById(R.id.btnCancel);
        theButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Button theButton = (Button)findViewById(R.id.btnProceed);
        theButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (theButton.getText().toString()=="Add New Number")
                {
                    //if add new number
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addSim);
                    linearLayout.setVisibility(View.VISIBLE);
                               }
                else
                {
                    onBackPressed();
                    startActivity(new Intent(CustomerInfo.this, Register.class));
                }
            }
        });


       // GetDocNo();
       // Query();

        Button btnAddSim = (Button)findViewById(R.id.btnAddSim);
        btnAddSim.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SimInfo();
            }
        });


    }

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    String formattedDate = df.format(c.getTime());
    public void SimInfo()
    {
        //final String username = name.getText().toString();
        //final String email = Email.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, byinfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CustomerInfo.this, "Successfully Registered", Toast.LENGTH_LONG).show();

                        Intent intent3 = new Intent(Intent.ACTION_MAIN);
                        intent3.setComponent(new ComponentName("com.taztag.testthermalprinter", "com.taztag.testthermalprinter.MainActivity"));
                        intent3.putExtra("Print", "Admin;"+Info2+";"+PhoneNo.getText().toString()+";"+formattedDate);
                        startActivity(intent3);
                        onBackPressed();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof TimeoutError)
                        {
                            //Log.d(TAG, error.getMessage());
                            SimInfo();
                        }
                        if (error instanceof NetworkError) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                            //Log.d(TAG, "Cannot connect to Internet...Please check your connection!");
                        }
//                        Log.d(TAG, error.getMessage());
                        //System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){

                //Converting Bitmap to String
                //String image = getStringImage(mphoto);

                Map<String,String> params = new HashMap<String, String>();

                spinner2 = (Spinner) findViewById(R.id.spinner2);
                PhoneNo = (TextView) findViewById(R.id.PhoneNo);
                SerialNo = (TextView) findViewById(R.id.SerialNo);
                PUK1 = (TextView) findViewById(R.id.PUK1);
                PUK2 = (TextView) findViewById(R.id.PUK2);

                params.put("docty", Info); //
                params.put("docno", Info2);
                params.put("operator",spinner2.getSelectedItem().toString());
                params.put("phoneNo",PhoneNo.getText().toString());
                params.put("serialno", SerialNo.getText().toString());
                params.put("puk1",PUK1.getText().toString());
                params.put("puk2", PUK2.getText().toString());

                System.out.println (Info);
                System.out.println (Info2);
                System.out.println (spinner2.getSelectedItem().toString());
                System.out.println (PhoneNo.getText().toString());
                System.out.println (PUK1.getText().toString());
                System.out.println (PUK2.getText().toString());


                return params;
            }

        };

        // stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        //bio.disconnect();
        //System.out.println("Sim" + stringRequest);
    }

    public byte[] streamToBytes (InputStream is){
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }


    public class MatchFinger_one2n_WSQAsyncTask extends AsyncTask<Integer, Integer, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                Thread.sleep(10000);


            }

            catch(Exception e){}

        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            InputStream is=null;
            byte[] array= new byte[1000000];


            ///*
            //String path = Environment.getExternalStorageDirectory().getAbsolutePath();  //"/sdcard/Cho.jpg";
            //System.out.println("Path : " + path);
            //String path = "/sdcard/TestFolder/cho.wsq";
            try{
                is = new FileInputStream(path);
                if (path != null) {
                    try {
                        array=streamToBytes(is);
                    } finally {
                        is.close();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                try {
                    throw new IOException("Unable to open file (cho.wsq)");
                } catch (IOException e1) {

                    e1.printStackTrace();
                }
            }
            //*/
            try{
                //Create request
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_MatchFinger_one2n_WSQ);
                //request.addProperty("firstNo",100);
                //request.addProperty("Second",300);
                PropertyInfo prop = new PropertyInfo();
//                prop.setName("UserID");//인자명이 틀리면 안된다.
//                prop.setValue("ChoFile");
//                prop.setType(String.class);
//                request.addProperty(prop);
//
//                prop = new PropertyInfo();
                prop.setName("myValue");
                prop.setValue(array);
                prop.setType(byte[].class);
                request.addProperty(prop);

                SoapSerializationEnvelope sse=new SoapSerializationEnvelope(SoapEnvelope.VER11);
                new MarshalBase64().register(sse);
                sse.dotNet=true;
                sse.setOutputSoapObject(request);
                HttpTransportSE htse = new HttpTransportSE(sURL);
                htse.call(SOAP_ACTION_MatchFinger_one2n_WSQ, sse);
                //Object response = (Object) sse.getResponse();
                //SoapObject response = (SoapObject) sse.getResponse();
                Vector<SoapObject> result = (Vector<SoapObject>) sse.getResponse();

                //Inside your for loop
                SoapObject so = result.get(0);

                Log.d(TAG, so.toString());
                Log.d(TAG, String.valueOf(so.getPropertyCount()));
                Log.d(TAG, so.getProperty(0).toString().replace(" ",""));
                Log.d(TAG, so.getProperty(1).toString());
              //  Toast.makeText(getApplicationContext(), so.getProperty(0).toString().replace(" ",""), Toast.LENGTH_LONG).show();
                id = so.getProperty(0).toString().replace(" ","");
                //Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                GetDocNo();
                Query();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {

        }

        @Override
        protected void onPostExecute(Integer result) {
        //    TextView myTv = (TextView) findViewById(R.id.textView2);
//            myTv.setText("giRet: "+Integer.toString(giRet));
            super.onPostExecute(result);
        }


    }

    Context mContext;
    public void GetMatch()
    {


        progress = ProgressDialog.show(CustomerInfo.this, "Waiting", "Processing Fingerpring for Matching", true);
        MatchFinger_one2n_WSQAsyncTask myAsync = new MatchFinger_one2n_WSQAsyncTask();

        myAsync.execute();
        ImageButton lookup = (ImageButton)findViewById(R.id.imageButton7);
        lookup.setVisibility(View.GONE);





    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public void GetDocNo()
    {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, getDocument,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);

                            //Creating JsonObject from response String
                            JSONObject jsonObject = new JSONObject(response.toString());

                            //extracting json array from response string
                            JSONArray jsonArray = jsonObject.getJSONArray("customer");


                            if(jsonArray.length() == 0)
                            {
                                Toast.makeText(getApplicationContext(), "Subscriber Exist", Toast.LENGTH_SHORT).show();
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject customer = jsonArray.getJSONObject(i);

                                DocNo = customer.getString("DocNo");
                                //finger();
                                Query();

                            }
                        }
                        catch (JSONException ex)
                        {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);
                return parameters;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void finger()
    {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, byfinger,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);

                            //Creating JsonObject from response String
                            JSONObject jsonObject = new JSONObject(response.toString());

                            //extracting json array from response string
                            JSONArray jsonArray = jsonObject.getJSONArray("customer");
                            System.out.println("test" + jsonArray.length());

                            if(jsonArray.length() == 0)
                            {
                                Toast.makeText(getApplicationContext(), "Subscriber Does Not Exist", Toast.LENGTH_SHORT).show();
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject customer = jsonArray.getJSONObject(i);

                                String fp1 = customer.getString("FP1");
                                String fp2 = customer.getString("FP2");

                                Fp1 = hexStringToByteArray(fp1);
                                Fp2 = hexStringToByteArray(fp2);

                                System.out.println("FP1 : " + Fp1);
                                System.out.println("FP2 : " + Fp2);

                                Verify();

                                System.out.println();
                                //check the window
                            }
                            //Verify();
                        }
                        catch (JSONException ex)
                        {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("AccNo", DocNo);
                return parameters;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void Verify()
    {
        bio.disconnect();
        bio = new BioAction(this,this);
        bio.connect(false);
        bio.load(Fp1, Fp2);
        if(bio.verify())
        {
            //Toast.makeText(this, "finger print success", Toast.LENGTH_LONG).show();
            //bio.disconnect();
            Query();
            //Log.d("MHNEXUS", "finger print success");
        } else {
            Toast.makeText(this, "finger print failed", Toast.LENGTH_LONG).show();
            // Log.d("MHNEXUS", "finger print failed");
        }
    }
    @Override
    public void update(Observable observable, Object data) {

    }

    public void Query()
    {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, byidURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            progress.dismiss();
                            //Creating JsonObject from response String
                            JSONObject jsonObject = new JSONObject(response.toString());

                            //extracting json array from response string
                            JSONArray jsonArray = jsonObject.getJSONArray("customer");
                            System.out.println("test" + jsonArray.length());

                            if(jsonArray.length() == 0)
                            {
                                Toast.makeText(getApplicationContext(), "Subscriber Does Not Exist", Toast.LENGTH_SHORT).show();
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject customer = jsonArray.getJSONObject(i);

                                String name = customer.getString("Name");
                                String pass = customer.getString("DocumentTy");
                                String id = customer.getString("DocumentNo");
                                String dob = customer.getString("DOB");
                                String gender = customer.getString("Gender");
                                String nationality = customer.getString("Nationality");
                                String add1 = customer.getString("Address1");
                                String add2 = customer.getString("Address2");
                                String add3 = customer.getString("Address3");
                                String postcode = customer.getString("Postcode");
                                String city = customer.getString("City");
                                String state = customer.getString("State");


                                if ( name == "null") {
                                    name = "-";
                                } //Account No
                                tvName.setText(name);

                                if (pass == "null") {
                                    pass = "-";
                                } // DocType
                                tvPass.setText(pass);
                                Info= pass;

                                if (id == "null") {
                                    id = "-";
                                } // DocType
                                tvID.setText(id);
                                Info2=id;

                                if (dob == "null") {
                                    dob = "-";
                                } //DocCountry
                                tvdob.setText(dob);

                                if (gender == "null") {
                                    gender = "-";
                                } //DocCountry
                                tvgender.setText(gender);

                                if (nationality == "null") {
                                    nationality = "-";
                                } //DocCountry
                                tvNationality.setText(nationality);

                                if (add1 == "null") {
                                    add1 = "-";
                                } //DocCountry
                                tvAdd1.setText(add1);

                                if (add2 == "null") {
                                    add2 = "-";
                                } //DocCountry
                                tvAdd2.setText(add2);

                                if (add3 == "null") {
                                    add3 = "-";
                                } //Address1
                                tvAdd3.setText(add3);

                                if (postcode == "null") {
                                    postcode = "-";
                                } //DocCountry
                                tvPostcode.setText(postcode);

                                if (city == "null") {
                                    city = "-";
                                } //Address1
                                tvCity.setText(city);

                                if (state == "null") {
                                    state = "-";
                                } //Address1
                                tvState.setText(state);
                                getFaceID();
                                simcardinfo();



                            }
                        }
                        catch (JSONException ex)
                        {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("AccNo", id);
                return parameters;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /*private void getImage() {

        String imgUrl = imageurl;
        System.out.println("imgURL 1: " + imgUrl);
        Picasso.with(this).load(imgUrl).into(imageSource);
        //aq.id(R.id.imageView).progress(R.id.imageView).image(imgUrl, true, true, 0, R.drawable.user);

    }*/

    private void getImage() {

        String imgUrl = "http://152.3.214.12:8080/arges-service/api/person/face-image/" + FaceID + "?app-id=test&app-key=test";
        System.out.println("imgURL 1: " + imgUrl);
        Picasso.with(this).load(imgUrl).into(custimage);
        progress.dismiss();
        Button theButton = (Button)findViewById(R.id.btnProceed);
        theButton.setVisibility(View.VISIBLE);
        theButton.setText("Add New Number");
        Button theButton2 = (Button)findViewById(R.id.btnCancel);
        theButton2.setVisibility(View.VISIBLE);
       // bio.disconnect();
        //aq.id(R.id.imageView).progress(R.id.imageView).image(imgUrl, true, true, 0, R.drawable.user);

    }
    public void simcardinfo()
    {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, byoperator,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);

                            //Creating JsonObject from response String
                            JSONObject jsonObject = new JSONObject(response.toString());

                            System.out.println("SIM query" + response.toString());

                            //extracting json array from response string
                            JSONArray jsonArray = jsonObject.getJSONArray("customer");
                            System.out.println("test" + jsonArray.length());

                            if(jsonArray.length() == 0)
                            {

                                Toast.makeText(getApplicationContext(), "Subscriber Does Not Exist", Toast.LENGTH_SHORT).show();
                            }

                            TextView simInfodisplay = (TextView) findViewById(R.id.TVsimdet);
                            System.out.println("SIM LENGTH: "+jsonArray.length());
                            System.out.println("On display: "+simInfodisplay.getText().toString());
                            simInfodisplay.setText("\n\nSIM CARD INFORMATION");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject customer = jsonArray.getJSONObject(i);

                                String docno = customer.getString("DocumentNo");
                                String operator = customer.getString("Operator");
                                String phoneNo = customer.getString("PhoneNo");
                                String simserialno = customer.getString("SimSerialNo");

                                //imageurl = customer.getString("Image");

                                simInfodisplay.setText(simInfodisplay.getText().toString()+ "\n\n Operator : " + operator
                                        + "\n  PhoneNo : " + phoneNo
                                        + "\n  SimSerialNo : " + simserialno);

                             /*   HashMap<String,String> persons = new HashMap<String,String>();
                                persons.put("DocumentNo",docno);
                                persons.put("Operator",operator);
                                persons.put("PhoneNo", phoneNo);
                                persons.put("SimSerialNo", simserialno);*/
                                //   personList.add(persons);
                            }
                            System.out.println("On display2: "+simInfodisplay.getText().toString());

                         /*   ListAdapter adapter = new SimpleAdapter(
                                    FaceRecognition.this, personList, R.layout.dblist,
                                    new String[]{"Operator","PhoneNo", "SimSerialNo"},
                                    new int[]{R.id.operator, R.id.PhoneNo, R.id.textView21}
                            );*/
                            // list.setAdapter(adapter);
                        }

                        catch (JSONException ex)
                        {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("AccNo", Info2);
                // System.out.println("Docno : " + DocNo);
                return parameters;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getFaceID()
    {
        final RequestQueue queue = Volley.newRequestQueue(this);
        String url = testURL + DocNo;
        System.out.println(url);

        // Request a string response from the provided URL.
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        // mTextView.setText("Response is: "+ response);
                        System.out.println("Response" + response);
                        Pattern p = Pattern.compile("\\[(.*?)\\]");
                        Matcher m = p.matcher(response);

                        while(m.find()) {
                            System.out.println("Group 1: " + m.group(1));
                            FaceID = m.group(1);
                        }
                        getImage();
                        //System.out.println(FaceID);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
