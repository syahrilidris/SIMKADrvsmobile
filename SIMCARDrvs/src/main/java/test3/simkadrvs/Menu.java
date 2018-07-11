package test3.simkadrvs;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Menu extends AppCompatActivity {

    String personid;
    String repl;
    String byidURL = "http://aril.16mb.com/Image/CheckUser.php";
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

   /* public void btn_reg(View v)
    {
        //startActivity(new Intent(Menu.this, Register.class));
        //showInputDialog();}
        startActivity(new Intent(Menu.this, Register.class));}*/

    public void btn_Enq(View v)
    {
        startActivity(new Intent(Menu.this, tes.class));
    }

    public void btn_Face(View v)
    {
        startActivity(new Intent(Menu.this, FaceRecognition.class));
    }

    public void btn_MRZ(View v)
    {
        /*Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.regula.documentreader.full");
        if (LaunchIntent != null) {
            // We found the activity now start the activity
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(LaunchIntent);
        } else {
            // Bring user to the market or let them choose an app?
            Toast.makeText(getApplicationContext(), "Apps Not Working", Toast.LENGTH_LONG).show();
        }
        //startActivity(LaunchIntent);*/

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.regula.documentreader", "com.regula.documentreader.CaptureActivity"));
        startActivity(intent);
    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Menu.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Menu.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progress = ProgressDialog.show(Menu.this, "Waiting", "Sending", true);
                        System.out.println("Hello, " + editText.getText());
                        personid = editText.getText().toString();
                        Query();
                        //resultText.setText("Hello, " + editText.getText());

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
                                Intent i = new Intent(Menu.this, AddNumber.class);
                                i.putExtra("ID", personid);
                                startActivity(i);

                            }
                            if(repl.contains("0"))
                            {
                                progress.dismiss();
                                startActivity(new Intent(Menu.this, Register.class));
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
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("AccNo", personid);
                return parameters;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
