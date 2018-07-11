package test3.simkadrvs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class tes extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private String temp;
    String searchingID = "0";
    //EditText ID;
    String ID;
    Button test1;
    Button test2;
    String byidURL = "http://aril.16mb.com/Image/CheckUser.php";
    String getid = "http://aril.16mb.com/Image/getid.php";
    String getidserial = "http://aril.16mb.com/Image/getidserial.php";
    String repl;
    private ProgressDialog progress;
    Spinner searchingtype;

    private SearchView simpleSearchView;

    //Fingerprint byte
    byte[] Fp1;
    byte[] Fp2;
    BioAction bio = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tes);

        //ID = (EditText)findViewById(R.id.ID);
        test1 = (Button)findViewById(R.id.test1);
        test2 = (Button)findViewById(R.id.test2);
        searchingtype = (Spinner)findViewById(R.id.spinner3);

        simpleSearchView = (SearchView) findViewById(R.id.searchView); // inititate a search view
        simpleSearchView.setOnQueryTextListener(this);
    }

    public boolean onQueryTextSubmit(String query) {
        // do something on text submit
        System.out.println("Result : " + query + " " + "Searching Type : " + searchingtype.getSelectedItem().toString());
        ID = query;
        Choose(searchingtype.getSelectedItem().toString());
        return false;
    }

    public int Choose(String month) {
        progress = ProgressDialog.show(tes.this, "Waiting", "Sending", true);
        int Choose = 0;

        switch (month) {
            case "PhoneNo":
                getID();
                break;
            case "SimSerialNo":
                getIDSerial();
                break;
            case "DocumentNo":
                Query();
                break;
        }

        return Choose;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // do something when text changes
        return false;

    }

    public void simreg(View v)
    {
        Intent i = new Intent(new Intent(tes.this, CustomerInfo.class));
        i.putExtra("ID", temp);
        System.out.println(temp);
        startActivity(i);
        //finger();
    }

    public void siminfo(View v)
    {
        Intent i = new Intent(new Intent(tes.this, OperatorInfo.class));
        i.putExtra("ID", temp);
        System.out.println(temp);
        startActivity(i);
    }

    public void search(View v)
    {
        progress = ProgressDialog.show(tes.this, "Waiting", "Sending", true);
        //temp = ID.getText().toString();
        //Query();
        System.out.println(searchingtype.getSelectedItem().toString());
        if(searchingtype.getSelectedItem().toString().contains("PhoneNo") )
        {
            getID();
        }

        if(searchingtype.getSelectedItem().toString().contains("SimSerialNo"))
        {
            getIDSerial();
        }

        if(searchingtype.getSelectedItem().toString().contains("DocumentNo"))
        {
            Query();
        }
        /*else
        {

            Query();
        }*/
        //test1.setVisibility(View.VISIBLE);
        //test2.setVisibility(View.VISIBLE);
        //temp = ID.getText().toString();
       // System.out.println(temp);
    }

    public void Query() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, byidURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);

                            //Creating JsonObject from response String
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String test = response.toString();
                            System.out.println("response" + test);
                            repl = test.replaceAll("[^0-9]", "");
                            System.out.println(repl);
                            if(repl.contains("1"))
                            {
                                progress.dismiss();
                                test1.setVisibility(View.VISIBLE);
                                test2.setVisibility(View.VISIBLE);
                            }
                            if(repl.contains("0"))
                            {
                                progress.dismiss();
                                test1.setVisibility(View.INVISIBLE);
                                test2.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "ID Not Exist", Toast.LENGTH_LONG).show();
                            }

                        }//end FirstExample
                        catch (JSONException ex) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof TimeoutError)
                        {
                            //Log.d(TAG, error.getMessage());
                            Query();
                        }
                        if (error instanceof NetworkError) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                            //Log.d(TAG, "Cannot connect to Internet...Please check your connection!");
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                if(searchingID=="0")
                {
                    //Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
                    System.out.println("Hello World1");
                    //parameters.put("AccNo", ID.getText().toString());
                    //temp = ID.getText().toString();
                    parameters.put("AccNo", ID);
                    temp = ID;
                }
                else
                {
                    System.out.println("Hello World");
                    parameters.put("AccNo", searchingID);
                    temp = searchingID;
                    searchingID="0";
                }
                return parameters;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getID()
    {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, getid,
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
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(), "ID Not Exist", Toast.LENGTH_SHORT).show();
                                test1.setVisibility(View.INVISIBLE);
                                test2.setVisibility(View.INVISIBLE);
                            }
                            else {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject customer = jsonArray.getJSONObject(i);

                                    searchingID = customer.getString("DocumentNo");

                                }
                                //Verify();
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
                        if (error instanceof NetworkError) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                            //Log.d(TAG, "Cannot connect to Internet...Please check your connection!");
                        }
                        System.out.println(error);
                    }

                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                //parameters.put("AccNo", ID.getText().toString());
                //searchingID = ID.getText().toString();
                parameters.put("AccNo", ID);
                searchingID = ID;
                return parameters;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getIDSerial()
    {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, getidserial,
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
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(), "ID Not Exist", Toast.LENGTH_SHORT).show();
                                test1.setVisibility(View.INVISIBLE);
                                test2.setVisibility(View.INVISIBLE);
                            }
                            else {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject customer = jsonArray.getJSONObject(i);

                                    searchingID = customer.getString("DocumentNo");

                                }
                                //Verify();
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
                        if (error instanceof NetworkError) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                            //Log.d(TAG, "Cannot connect to Internet...Please check your connection!");
                        }
                        System.out.println(error);
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                //parameters.put("id", ID.getText().toString());
                //searchingID = ID.getText().toString();
                parameters.put("id", ID);
                searchingID = ID;
                return parameters;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
