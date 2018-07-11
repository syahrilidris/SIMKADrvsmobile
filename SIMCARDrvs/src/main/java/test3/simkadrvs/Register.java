package test3.simkadrvs;

import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acs.smartcard.Reader;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity implements Observer {

    //http://www.gerbangrevolusi.com/Image
    protected final String TAG = getClass().getSimpleName();
    String byidURL = "http://aril.16mb.com/simcard/insert.php";
    String byinfo = "http://aril.16mb.com/simcard/SimInfo.php";
    private static final String ServerAddress = "152.3.214.12:8080";
    private static final int CAMERA_REQUEST = 1888;
    private static final int BARCODE_REQUEST = 12;
    private static final int MY_REQUEST_CODE =5;

    private ImageView imageDoc;
    private ImageView imageSource;
    private EditText Name;
    private EditText PassportNo;
    private EditText ID;
    private EditText DOB;
    private EditText Nationality;
    private EditText Gender;
    private EditText PUK1;
    private EditText PUK2;
    private EditText PhoneNo;
    private EditText SerialNo;
    private EditText Address;
    private EditText Address2;
    private EditText Address3;
    private EditText Postcode;
    private EditText City;
    private EditText State;
    private Button Register;
    private Button Save;
    private Bitmap mphoto;
    private Bitmap mphoto2;
    private Spinner spinner;
    private Spinner spinner2;
    private boolean imageLoaded = false;
    String AccountNo ;
    String DocNo;
    String ActivDt;
    private String PersonID;
    String repl;
    private ProgressDialog progress;
    private TextView ShowID;
    private Boolean connectstate = false;
    public String camera;

    String myString;


    //DOB
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;


    //FP
    String FPID;

    String checkuser;
    String adduser;

    public static final String sURL = "http://gerbang2017.ddns.net:8081/soap/IIBiometric";

    //private final String NAMESPACE = "urn:uBiometricIntf-IIBiometric";
    private final String SOAP_ACTION_Add = "urn:uBiometricIntf-IIBiometric#Add";
    private final String METHOD_NAME_Add = "Add";

    ////
    public static final String NAMESPACE = "urn:uBiometricIntf-IIBiometric";
    //AddUserWSQ
    public static final String SOAP_ACTION_AddUserWSQ = "urn:uBiometricIntf-IIBiometric#AddUserWSQ";
    public static final String METHOD_NAME_AddUserWSQ = "AddUserWSQ";
    ///

    public static final String path = "/storage/emulated/0/Image.wsq";
    public static final String mrzpath = "/storage/emulated/0/mrz.txt";


    //MYKAD
    static byte[] byteAPDU=null;
    static byte[] respAPDU=null;
    static byte[] byteAPDU2=null;
    static byte[] respAPDU2=null;

    static byte[] byteJPNDF2=null;
    static byte[] respJPNDF2=null;
    static byte[] byteJPNDF2_1=null;
    static byte[] respJPNDF2_1=null;

    static byte[] byteJPNDF3=null;
    static byte[] respJPNDF3=null;
    static byte[] byteJPNDF3_1=null;
    static byte[] respJPNDF3_1=null;

    static byte[] byteJPNDF4=null;
    static byte[] respJPNDF4=null;
    static byte[] byteJPNDF4_1=null;
    static byte[] respJPNDF4_1=null;

    private int iSlotNum = 0;

    String offset, offset2;
    byte v1, v2;

    //Fingerprint byte
    byte[] Fp1;
    byte[] Fp2;
    BioAction bio = null;


    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private static final String[] powerActionStrings = { "Power Down",
            "Cold Reset", "Warm Reset" };

    private static final String[] stateStrings = { "Unknown", "Absent",
            "Present", "Swallowed", "Powered", "Negotiable", "Specific" };

    private static final String[] featureStrings = { "FEATURE_UNKNOWN",
            "FEATURE_VERIFY_PIN_START", "FEATURE_VERIFY_PIN_FINISH",
            "FEATURE_MODIFY_PIN_START", "FEATURE_MODIFY_PIN_FINISH",
            "FEATURE_GET_KEY_PRESSED", "FEATURE_VERIFY_PIN_DIRECT",
            "FEATURE_MODIFY_PIN_DIRECT", "FEATURE_MCT_READER_DIRECT",
            "FEATURE_MCT_UNIVERSAL", "FEATURE_IFD_PIN_PROPERTIES",
            "FEATURE_ABORT", "FEATURE_SET_SPE_MESSAGE",
            "FEATURE_VERIFY_PIN_DIRECT_APP_ID",
            "FEATURE_MODIFY_PIN_DIRECT_APP_ID", "FEATURE_WRITE_DISPLAY",
            "FEATURE_GET_KEY", "FEATURE_IFD_DISPLAY_PROPERTIES",
            "FEATURE_GET_TLV_PROPERTIES", "FEATURE_CCID_ESC_COMMAND" };

    private static final String[] propertyStrings = { "Unknown", "wLcdLayout",
            "bEntryValidationCondition", "bTimeOut2", "wLcdMaxCharacters",
            "wLcdMaxLines", "bMinPINSize", "bMaxPINSize", "sFirmwareID",
            "bPPDUSupport", "dwMaxAPDUDataSize", "wIdVendor", "wIdProduct" };

    private static final int DIALOG_VERIFY_PIN_ID = 0;
    private static final int DIALOG_MODIFY_PIN_ID = 1;
    private static final int DIALOG_READ_KEY_ID = 2;
    private static final int DIALOG_DISPLAY_LCD_MESSAGE_ID = 3;
    static final int READ_BLOCK_SIZE = 100;
    private UsbManager mManager;
    private Reader mReader;
    private PendingIntent mPermissionIntent;

    String dev; // device name
    String Mykad;
    String MykadDF2 = "";
    String MykadDF3 = "";
    String MykadDF4;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {

                synchronized (this) {

                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                        if (device != null) {

                            // Open reader
                            System.out.println("Opening reader: " + device.getDeviceName() + "...");
                            Toast.makeText(getApplicationContext(), "Reader Open", Toast.LENGTH_LONG).show();
                            //new OpenTask().execute(device);
                            mReader.open(device);
                        }

                    } else {

                        System.out.println("Permission denied for device " + device.getDeviceName());
                        Toast.makeText(getApplicationContext(), "Reader Denied", Toast.LENGTH_LONG).show();
                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                synchronized (this) {

                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (device != null && device.equals(mReader.getDevice())) {

                        // Close reader
                        // logMsg("Closing reader...");
                        mReader.close();
                    }
                }
            }
        }
    };

    private class PowerParams {

        public int slotNum;
        public int action;
    }

    private class PowerResult {

        public byte[] atr;
        public Exception e;
    }

    private class PowerTask extends AsyncTask<PowerParams, Void, PowerResult> {

        @Override
        protected PowerResult doInBackground(PowerParams... params) {

            PowerResult result = new PowerResult();

            try {

                result.atr = mReader.power(params[0].slotNum, params[0].action);

            } catch (Exception e) {

                result.e = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(PowerResult result) {

            System.out.println("Result : " + result.e);

            if (result.e != null) {
                String test = result.e.toString();
                if(test.contains("RemovedCardException"))
                {
                    Toast.makeText(getApplicationContext(), "Card Not Inserted", Toast.LENGTH_LONG).show();

                }
                if(test.contains("UnresponsiveCardException"))
                {
                    Toast.makeText(getApplicationContext(), "Please Insert Card Correctly", Toast.LENGTH_LONG).show();
                }
                if(test.contains("reader is not opened"))
                {
                    Toast.makeText(getApplicationContext(), "Reader Not Detected", Toast.LENGTH_LONG).show();
                }

                //System.out.println(result.e.toString());

            }
            else {

                // Show ATR
                if (result.atr != null) {

                    System.out.println("ATR:");
                    //logBuffer(result.atr, result.atr.length);
                    Protocol();

                } else {
                    System.out.println("ATR: None");
                }
            }
        }
    }

    private class SetProtocolResult {

        public int activeProtocol;
        public Exception e;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(cameraIntent, CAMERA_REQUEST);

        Register = (Button)findViewById(R.id.btnSend);
        Save = (Button)findViewById(R.id.button2);
        Name = (EditText)findViewById(R.id.Name);
        ID = (EditText)findViewById(R.id.ID);
        DOB = (EditText)findViewById(R.id.DOB);
        DOB.setInputType(InputType.TYPE_NULL);
        DOB.requestFocus();
        Nationality = (EditText)findViewById(R.id.Nationality);
        Gender = (EditText)findViewById(R.id.Gender);
        PUK1 = (EditText)findViewById(R.id.PUK1);
        PUK2 = (EditText)findViewById(R.id.PUK2);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        PhoneNo = (EditText)findViewById(R.id.PhoneNo);
        SerialNo = (EditText)findViewById(R.id.SerialNo);
        Address = (EditText)findViewById(R.id.Address1);
        Address2 = (EditText)findViewById(R.id.Address2);
        Address3 = (EditText)findViewById(R.id.Address3);
        Postcode = (EditText)findViewById(R.id.Postcode);
        City = (EditText)findViewById(R.id.City);
        State = (EditText)findViewById(R.id.State);
        ShowID = (TextView)findViewById(R.id.txtResult);
        imageSource = (ImageView)findViewById(R.id.imagesource);
        imageDoc = (ImageView) findViewById(R.id.imageDoc);

        Bundle b = getIntent().getExtras();
        if(b!=null){
            ImageView iv_photo=(ImageView)findViewById(R.id.imagesource);
            Bundle extras = getIntent().getExtras();
            Bitmap bmp = (Bitmap) extras.getParcelable("Bitmap");
            iv_photo.setImageBitmap(bmp);
        }
        else {}






        ImageButton imPassport = (ImageButton) findViewById(R.id.imPassport);
        imPassport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ReadMRZ();

            }
        });


        Button readBarcode = (Button) findViewById(R.id.btnBarcode);
        readBarcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //  ReadMRZ();
                ReadBarcode2();

            }
        });



        SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMddmmss");
        Date todayDate = new Date();
        AccountNo = currentDate.format(todayDate);


        SimpleDateFormat current = new SimpleDateFormat("yyyy-MM-dd");
        Date activationdate = new Date();
        ActivDt = current.format(activationdate);

        //DOB
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        setDateTimeField();

        isConnectedToInternet();


        // Get USB manager
        mManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // Initialize reader
        mReader = new Reader(mManager);
        mReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNum, int prevState, int currState) {

                if (prevState < Reader.CARD_UNKNOWN
                        || prevState > Reader.CARD_SPECIFIC) {
                    prevState = Reader.CARD_UNKNOWN;
                }

                if (currState < Reader.CARD_UNKNOWN
                        || currState > Reader.CARD_SPECIFIC) {
                    currState = Reader.CARD_UNKNOWN;
                }

                // Create output string
                final String outputString = "Slot " + slotNum + ": "
                        + stateStrings[prevState] + " -> "
                        + stateStrings[currState];

                // Show output
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {//logMsg(outputString);
                    }
                });
            }
        });

        // Register receiver for USB permission
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mReceiver, filter);

        // Initialize reader spinner
        for (UsbDevice device : mManager.getDeviceList().values()) {
            if (mReader.isSupported(device)) {
                dev = device.getDeviceName();
            }
        }

        if (dev != null) {

            // For each device
            for (UsbDevice device : mManager.getDeviceList().values()) {

                // If device name is found
                if (dev.equals(device.getDeviceName())) {

                    // Request permission
                    mManager.requestPermission(device,
                            mPermissionIntent);

                    //requested = true;
                    break;
                }
            }
        }




        //Register.setOnClickListener(this);

        //bio = new BioAction(this,this);
        //bio.connect(false);
        //bio = new BioAction(this,this);
        //bio.connect(false);
        //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.putExtra("android.intent.extra.quickCapture", true);
        //startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void ReadMRZ()
    {
        Toast.makeText(Register.this, "Reading MRZ", Toast.LENGTH_LONG).show();

       /* Intent mrz = new Intent(Intent.ACTION_MAIN);
        mrz.setComponent(new ComponentName("com.regula.documentreader", "com.regula.documentreader.Splash"));
      //mrz.putExtra("Print", "Admin;"+ID.getText().toString()+";"+PhoneNo.getText().toString()+";"+formattedDate);
        startActivity(mrz);*/
        Intent intent = new Intent();
        intent.setClassName("com.regula.documentreader", "com.regula.documentreader.CaptureActivity");
        startActivityForResult(intent, 5);

        Toast.makeText(Register.this,"Start reading", Toast.LENGTH_LONG).show();


    }


    public void VerifyMykad()
    {
        Toast.makeText(Register.this, "Verify Fingerprint on MyKad", Toast.LENGTH_LONG).show();

       /* Intent mrz = new Intent(Intent.ACTION_MAIN);
        mrz.setComponent(new ComponentName("com.regula.documentreader", "com.regula.documentreader.Splash"));
      //mrz.putExtra("Print", "Admin;"+ID.getText().toString()+";"+PhoneNo.getText().toString()+";"+formattedDate);
        startActivity(mrz);*/
        Intent intent = new Intent();
        intent.setClassName("com.morpho.Verify", "com.morpho.Verify.ConnectionActivity");
        startActivity(intent);

       // Toast.makeText(Register.this,"Start reading", Toast.LENGTH_LONG).show();

    }



    @Override
    public void update(Observable observable, Object data) {

    }

    private void setDateTimeField() {
        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                DOB.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------Delete File-------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public static void deleteFiles(String path) {

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) { }
        }
    }

    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------Refresh Gallery---------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{"storage/emulated/0/DCIM/Camera/"}, new String[]{"image/jpeg"}, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File("/storage/emulated/0/DCIM/Camera/");
            Uri contentUri = Uri.fromFile(f);
            Log.e("ExternalStorage", "-> uri=" + contentUri);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);


        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

     /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------Checking Internet Connection---------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        //Toast.makeText(FaceRecognition.this, "Connected", Toast.LENGTH_LONG).show();
                        connectstate = true;
                        return true;

                    }

        }
        //Toast.makeText(FaceRecognition.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        connectstate = false;
        return false;
    }

    public void btn_FP(View view)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.morpho.morphosample", "com.morpho.morphosample.ConnectionActivity"));
        startActivity(intent);
    }

    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------Send Fingerprint To Server---------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public void SendFP()
    {
        AddUserAsyncTask myAsync = new AddUserAsyncTask();
        myAsync.execute();
    }

    public class AddUserAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Register.this, "Waiting", "Sending", true);
            super.onPreExecute();

        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            InputStream is=null;
            byte[] array= new byte[1000000];
            //byte[] array= null;

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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_AddUserWSQ);
                //request.addProperty("firstNo",100);
                //request.addProperty("Second",300);

                PropertyInfo prop = new PropertyInfo();
                prop.setName("UserID");//Server Parameter name 1
                prop.setValue(FPID); //Param 1 Value
                //prop.setType(String.class);
                prop.setType(String.class);
                request.addProperty(prop);

                prop = new PropertyInfo();
                prop.setName("myValue");//Server Parameter name 2
                prop.setValue(array);//Param 2 Value
                prop.setType(byte[].class);
                request.addProperty(prop);

                SoapSerializationEnvelope sse=new SoapSerializationEnvelope(SoapEnvelope.VER11);
                new MarshalBase64().register(sse);
                sse.dotNet=true;
                sse.setOutputSoapObject(request);
                HttpTransportSE htse = new HttpTransportSE(sURL);
                htse.call(SOAP_ACTION_AddUserWSQ, sse);
                //Object response = (Object) sse.getResponse();
                //SoapObject response = (SoapObject) sse.getResponse();
                Vector<SoapObject> result = (Vector<SoapObject>) sse.getResponse();

                //Inside your for loop
                SoapObject so = result.get(0);

                Log.d(TAG, so.toString());
                Log.d(TAG, String.valueOf(so.getPropertyCount()));
                Log.d(TAG, so.getProperty(0).toString());
                Log.d(TAG, so.getProperty(1).toString());

                adduser = so.getProperty(1).toString();


                //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                //e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {

        }

        @Override
        protected void onPostExecute(Integer result) {

            if(adduser.equals("1"))
            {
                //Toast.makeText(getApplicationContext(), "Successfully Insert", Toast.LENGTH_LONG).show();
                SearchingTask task = new SearchingTask();
                task.execute();
            }
            else if(adduser.equals("-9001") )
            {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Wrong File", Toast.LENGTH_LONG).show();
            }
            else if(adduser.equals("-9002"))
            {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Duplicate User", Toast.LENGTH_LONG).show();
            }

            //TextView myTv = (TextView) findViewById(R.id.textView2);
            //myTv.setText("giRet: "+Integer.toString(giRet));
            super.onPostExecute(result);
        }
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





    public void OnRegister(View v)
    {
        if(imageSource.getDrawable() == null)
        {
            Toast.makeText(Register.this, "Image not insert", Toast.LENGTH_LONG).show();
        }
        else {
            //Toast.makeText(Register.this, "Hello World", Toast.LENGTH_LONG).show();
           FPID = ID.getText().toString();
            SendFP();
           // SearchingTask task = new SearchingTask();
           // task.execute();
            //addperson1();
        }
    }

    /*public void onClick(View v) {
        if (v == Register) {
            if(imageSource.getDrawable() == null)
            {
                Toast.makeText(Register.this, "Image not insert", Toast.LENGTH_LONG).show();
            }
            else {
                SearchingTask task = new SearchingTask();
                task.execute();
            }
            //registerUser();
        }
    }*/

    public class SearchingTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            //progress = ProgressDialog.show(Register.this, "Waiting", "Sending", true);
        }

        @Override
        protected String doInBackground(String... params) {return addperson();}

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (result != null) {
                if (result.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Successfully Register", Toast.LENGTH_LONG).show();
                    Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_LONG).show();
                } else {
                    //resultAdapter.update(result);        bio = new BioAction(this,this);
                    //Toast.makeText(Register.this, "Successfully Register, PERSON-ID = " + repl, Toast.LENGTH_LONG).show();
                    //ShowID.setVisibility(View.VISIBLE);
                   // ShowID.setText("ID : " + repl);
                    //Register.setEnabled(false);
                    //testing();
                   // Register.setClickable(false);
                    Register.setVisibility(View.INVISIBLE);
                }
            } else {
                if(connectstate == false)
                {
                    Toast.makeText(Register.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
                else
                Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_LONG).show();
            }

        }
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            if (mphoto != null) {
                mphoto.recycle();
            }
            mphoto = (Bitmap) data.getExtras().get("data");
            imageSource.setImageBitmap(mphoto);
            deleteFiles("storage/emulated/0/DCIM/Camera/");
            callBroadCast();
            //testing();
        }
        else {

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                //Toast.makeText(MainActivity.this, "CONTENT: " + scanContent + " FORMAT: " + scanFormat, Toast.LENGTH_LONG).show();
                SerialNo.setText(scanContent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }*/


    public void getFP()
    {
        byteJPNDF3=null;
        respJPNDF3=null;
        byteJPNDF3_1=null;
        respJPNDF3_1=null;
        //convbyte();

        byteJPNDF3 = atohex("00 A4 04 00 0A A0 00 00 00 74 4A 50 4E 00 10");
        respJPNDF3 = JPNDF3(byteJPNDF3);
    }

    private byte[] JPNDF3(byte[] data)
    {
        int t = 7;

        byte[] response = new byte[512];
        int responseLength = 0;
        try {
            System.out.println("***COMMAND APDU***");
            System.out.println("IFD - " + getHexString(data, data.length));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (iSlotNum >= 0) {
            try {
                responseLength = mReader.transmit(iSlotNum, data, data.length, response, response.length);
                byte[] GetResponse = {(byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0x05};
                responseLength = mReader.transmit(iSlotNum, GetResponse, GetResponse.length, response, response.length);
                for(int i = 0; i <= 2; i ++)
                {
                    convbyte(t);
                    // t = t+255;
                    System.out.println("value t : " + t);


                    if(i == 2)
                    {
                        //System.out.println("Results - " + v2 + ":" + v1);
                        byte[] JPNDF3_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_1, JPNDF3_1.length, response, response.length);
                        byte[] JPNDF3_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x05, (byte) 0x02, (byte) 0x02, (byte) 0x00};
                        //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_2, JPNDF3_2.length, response, response.length);
                        byte[] JPNDF3_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x02};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_3, JPNDF3_3.length, response, response.length);


                       /* //System.out.println("Results - " + v2 + ":" + v1);
                        byte[] JPNDF3_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x9F, (byte) 0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_1, JPNDF3_1.length, response, response.length);
                        byte[] JPNDF3_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0D, (byte) 0x04, (byte) 0x9F, (byte) 0x00};
                        //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_2, JPNDF3_2.length, response, response.length);
                        byte[] JPNDF3_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x9F};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_3, JPNDF3_3.length, response, response.length);

                    }
                    else if(i == 1)
                    {
                        byte[] JPNDF3_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_1, JPNDF3_1.length, response, response.length);
                        byte[] JPNDF3_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0E, (byte) 0x03, (byte) 0xFF, (byte) 0x00};
                        //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_2, JPNDF3_2.length, response, response.length);
                        byte[] JPNDF3_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0xFF};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_3, JPNDF3_3.length, response, response.length);
                        t = t+159;*/
                    }
                    else if(i == 1)
                    {
                        //System.out.println("Results - " + v2 + ":" + v1);
                        byte[] JPNDF3_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_1, JPNDF3_1.length, response, response.length);
                        byte[] JPNDF3_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x06, (byte) 0x01, (byte) 0xFF, (byte) 0x00};
                        //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_2, JPNDF3_2.length, response, response.length);
                        byte[] JPNDF3_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0xFF};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_3, JPNDF3_3.length, response, response.length);
                        t = t+2;
                    }


                    else {

                        //System.out.println("Results - " + v2 + ":" + v1);
                        byte[] JPNDF3_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_1, JPNDF3_1.length, response, response.length);
                        byte[] JPNDF3_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) v2, (byte) v1, (byte) 0xFF, (byte) 0x00};
                        //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_2, JPNDF3_2.length, response, response.length);
                        byte[] JPNDF3_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0xFF};
                        responseLength = mReader.transmit(iSlotNum, JPNDF3_3, JPNDF3_3.length, response, response.length);
                        t = t+255;
                    }

                    MykadDF3 = MykadDF3 + getHexString(response, responseLength).replaceAll("90 00", "");
                    //A1.setText(MykadDF2);
                    System.out.println("ICC2 - " + MykadDF3);
                }
                //FileHelper.saveToFile2(MykadDF3);
                //getImage();
                //getFP();
                byte[] fp = toByteArray(MykadDF3.replaceAll(" ", ""));
                File f1 = new File("/storage/emulated/0/os2.pkmn");
                saveBytesToFile2(fp,f1);

               /* byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                responseLength = mReader.transmit(iSlotNum, JPNDF2_2, JPNDF2_2.length, response, response.length);
                byte[] JPNDF2_3 = {(byte)0xCC,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0xFF};
                responseLength = mReader.transmit(iSlotNum, JPNDF2_3, JPNDF2_3.length, response, response.length);*/

            } catch (Exception e) {
                System.out.println("****************************************");
                System.out.println("       ERROR transmit: Review APDU  ");
                System.out.println("****************************************");
                responseLength = 0;
                byte[] ra = Arrays.copyOf(response, responseLength);
                response = null;
                return (ra);
            }
           /* try {
                System.out.println("");
                MykadDF2 = MykadDF2 + " " +  getHexString(response, responseLength);
                //DF2();
                //System.out.println("ICC2 - " + getHexString(response, responseLength));
                System.out.println("ICC2 - " + MykadDF2);
                byte[] ra2 = Arrays.copyOf(response, responseLength);
                respJPNDF2_1 = ra2;

            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        byte[] ra = Arrays.copyOf(response, responseLength);
        response = null;
        return (ra);
    }


    public static void saveBytesToFile2(byte[] bytes, File outputFile) throws FileNotFoundException, IOException {
        FileOutputStream out = new FileOutputStream(outputFile);
        out.flush();
        out.write(bytes);
    }



    public static byte[] toByteArray(String data) {
        try {
            int dataLen = data.length();
            if (dataLen == 0) {
                return null;
            }
            byte[] result = new byte[(dataLen / 2)];
            for (int idx = 0; idx < dataLen; idx = (idx + 1) + 1) {
                result[idx / 2] = (byte) Integer.parseInt(data.substring(idx, idx + 2), 16);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] toBytesFromHex(String hex) {
        byte rc[] = new byte[hex.length() / 2];
        for (int i = 0; i < rc.length; i++) {
            String h = hex.substring(i * 2, i * 2 + 2);
            int x = Integer.parseInt(h, 16);
            rc[i] = (byte) x;
        }
        return rc;
    }

    public void getImage()
    {
        String result = MykadDF2;
        String data = result.replaceAll(" ", "");
        ShowImage(toBytesFromHex(data));
    }

    public void ShowImage(byte[] data)
    {

        imageSource.setImageDrawable(null);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        imageSource.setImageBitmap(bitmap);

        Toast.makeText(Register.this, "Please Verify your MyKad Fingerprint", Toast.LENGTH_LONG).show();
        VerifyMykad();
    }


    public void registerUser()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, byidURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, "response: " + response);
                        //SimInfo();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof TimeoutError)
                        {
                            Log.d(TAG, error.getMessage());
                        }
                        if (error instanceof NetworkError) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                            //Log.d(TAG, "Cannot connect to Internet...Please check your connection!");
                        }

                        //System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){

                //Converting Bitmap to String
                //String image = getStringImage(mphoto);

                Map<String,String> params = new HashMap<String, String>();

                params.put("docno", repl);
                params.put("name",Name.getText().toString());
                params.put("pass",spinner2.getSelectedItem().toString());
                params.put("id",ID.getText().toString());

                //Convert
                /*String YEAR = DOB.getText().toString().substring(0,4);
                String MONTH = DOB.getText().toString().substring(4,6);
                String DAY = DOB.getText().toString().substring(6,8);

                String dob = YEAR + "-" + MONTH + "-" + DAY;*/
                //System.out.println("DATE : " + YEAR + "-" + MONTH + "-" + DAY);

                params.put("dob", DOB.getText().toString());
                params.put("gender", Gender.getText().toString());
                params.put("nationality", Nationality.getText().toString());
                params.put("add", Address.getText().toString());
                params.put("add2", Address2.getText().toString());
                params.put("add3", Address3.getText().toString());
                params.put("postcode", Postcode.getText().toString());
                params.put("city", City.getText().toString());
                params.put("state", State.getText().toString());

                //FP1 = bytesToHexString(thumbprint1);
                // FP2 = bytesToHexString(thumbprint2);
                // params.put("fp1", FP1);
                // params.put("fp2", FP2);


                return params;
            }

        };

        //stringRequest.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);




        //final String username = name.getText().toString();
        //final String email = Email.getText().toString().trim();

        /*StringRequest stringRequest = new StringRequest(Request.Method.POST, byidURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //SimInfo();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof TimeoutError)
                        {
                            Log.d(TAG, error.getMessage());
                            registerUser();
                        }

                        //System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){

                //Converting Bitmap to String
                //String image = getStringImage(mphoto);

                Map<String,String> params = new HashMap<String, String>();

                params.put("docno", repl);
                params.put("name",Name.getText().toString());
                params.put("pass",spinner2.getSelectedItem().toString());
                params.put("id",ID.getText().toString());

                //Convert
                String YEAR = DOB.getText().toString().substring(0,4);
                String MONTH = DOB.getText().toString().substring(4,6);
                String DAY = DOB.getText().toString().substring(6,8);

                String dob = YEAR + "-" + MONTH + "-" + DAY;
                //System.out.println("DATE : " + YEAR + "-" + MONTH + "-" + DAY);

                params.put("dob", dob);
                params.put("gender", Gender.getText().toString());
                params.put("nationality", Nationality.getText().toString());
                params.put("add", Address.getText().toString());
                params.put("add2", Address2.getText().toString());
                params.put("add3", Address3.getText().toString());
                params.put("postcode", Postcode.getText().toString());
                params.put("city", City.getText().toString());
                params.put("state", State.getText().toString());
                //FP1 = bytesToHexString(thumbprint1);
               // FP2 = bytesToHexString(thumbprint2);
               // params.put("fp1", FP1);
               // params.put("fp2", FP2);


                return params;
            }

        };

        //stringRequest.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);*/
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
                        Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_LONG).show();

                        Intent intent3 = new Intent(Intent.ACTION_MAIN);
                        intent3.setComponent(new ComponentName("com.taztag.testthermalprinter", "com.taztag.testthermalprinter.MainActivity"));
                        intent3.putExtra("Print", "Admin;"+ID.getText().toString()+";"+PhoneNo.getText().toString()+";"+formattedDate);
                        startActivity(intent3);

                        onBackPressed();
                        /*Intent intent2 = new Intent(Intent.ACTION_MAIN);
                        intent2.setComponent(new ComponentName("com.taztag.testthermalprinter", "com.taztag.testthermalprinter.MainActivity"));
                        startActivity(intent2);*/
                        //Save.setClickable(false);
//                        ShowID.setVisibility(View.VISIBLE);
//                        ShowID.setText("ID : " + repl);
                        //System.out.println("SimResp" + response);
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

                params.put("docty", spinner.getSelectedItem().toString());
                params.put("docno", ID.getText().toString());
                params.put("operator",spinner2.getSelectedItem().toString());
                params.put("phoneNo",PhoneNo.getText().toString());
                params.put("serialno", SerialNo.getText().toString());
                params.put("puk1",PUK1.getText().toString());
                params.put("puk2", PUK2.getText().toString());


                return params;
            }

        };

       // stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        //bio.disconnect();
        //System.out.println("Sim" + stringRequest);
    }

    protected String addperson() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("app-id", "test");
        params.put("app-key", "test");
        params.put("person-name", Name.getText().toString());
        params.put("group-ids", "3");
        Bitmap bitmap = Bitmap.createBitmap(imageSource.getWidth(), imageSource.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        imageSource.draw(canvas);
        String test = toBase64Image(bitmap);
        params.put("image-data", test);
        try {
            byte[] resp = doPost("/person/create", params);
            //Log.i(TAG, new String(resp, "UTF-8"));
            System.out.println("Responses :" + new String(resp, "UTF-8"));
            PersonID = new String(resp,"UTF-8");
            //repl = PersonID.replaceAll("[^0-9]", "");
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(new String(resp,"UTF-8"));
            if (m.find(1)) {
                //t2.setText(m.group());
                repl = m.group();

            }
            //System.out.println("Doc no : " + repl);

            Refresh();
            //addFace();
            registerUser();
            //SimInfo();
            //CaptureFP();
            //testing();
            SimInfo();
            return repl;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    protected String toBase64Image(Bitmap image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] byteArray = bos.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    protected String addFace() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("app-id", "test");
        params.put("app-key", "test");
        params.put("person-id", repl);
        Bitmap bitmap = Bitmap.createBitmap(imageSource.getWidth(), imageSource.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        imageSource.draw(canvas);
        String test = toBase64Image(bitmap);
        params.put("image-data", test);
        System.out.println("test : " + test);

        //InputStream is2 = getClass().getResourceAsStream("/ali.png");

        try {
            byte[] resp = doPost("/person/append-face", params);
            //Log.i(TAG, new String(resp, "UTF-8"));
            System.out.println("Responses :" + new String(resp, "UTF-8"));
            //PersonID = new String(resp,"UTF-8");
            //String repl = PersonID.replaceAll("[^0-9]", "");
            // System.out.println(repl);
            Refresh();

            return null;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    protected String Refresh() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("app-id", "test");
        params.put("app-key", "test");
        params.put("person-id", repl);

        try {
            progress.dismiss();
            byte[] resp = doPost("/person/refresh", params);
            //Log.i(TAG, new String(resp, "UTF-8"));
            System.out.println("Responses :" + new String(resp, "UTF-8"));
            //PersonID = new String(resp,"UTF-8");
            //String repl = PersonID.replaceAll("[^0-9]", "");
            // System.out.println(repl);
            return null;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }


    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }



    static public String getServerUrlBase() {
        return "http://" + ServerAddress + "/arges-service/api";
    }

    public static class ServerException extends Throwable {
        private static final long serialVersionUID = 4017805196493169996L;

        private int code;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    protected byte[] doPost(String path, Map<String, String> parameters) throws Exception {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        String serverBase = getServerUrlBase();
        if (serverBase == null) {
            throw new Exception("Server's URL base is not set!");
        }

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(serverBase + path);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                urlConnection.setRequestProperty("Connection", "close");
            }
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            String postData = createQueryString(parameters);
            urlConnection.setFixedLengthStreamingMode(postData.getBytes().length);
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postData);
            out.close();
            int statusCode = urlConnection.getResponseCode();

            if (statusCode != HttpURLConnection.HTTP_OK) {
                if (statusCode == 500) {
                    try {
                        byte[] data = getResponseData(urlConnection);
                        ObjectMapper objectMapper = new ObjectMapper();
                        ServerException exp = objectMapper.readValue(data, ServerException.class);
                        Log.w(TAG, "Fail to search: " + exp.getMessage());
                    } catch (IOException e) {
                        Log.w(TAG, "Fail to search: " + statusCode);
                    }
                }
                throw new Exception("Access failure");
            }
            return getResponseData(urlConnection);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Bad URL [" + serverBase + path + "]: " + e.getMessage(), e);
            throw new Exception("Bad URL: " + serverBase + path);
        } catch (IOException e) {
            Log.e(TAG, "Access remote service error: " + e.getMessage(), e);
            throw new Exception("Access remote service error: " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private byte[] getResponseData(HttpURLConnection conn) throws IOException {
        InputStream inputStream = new BufferedInputStream(conn.getInputStream());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[16384];
        int n;
        while ((n = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, n);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';

    private String createQueryString(Map<String, String> parameters) {
        StringBuilder queryString = new StringBuilder();
        if (parameters != null) {
            boolean first = true;
            for (String name : parameters.keySet()) {
                if (!first) {
                    queryString.append(PARAMETER_DELIMITER);
                }
                try {
                    queryString.append(name).append(PARAMETER_EQUALS_CHAR).append(URLEncoder.encode(parameters.get(name), "UTF-8"));
                    if (first) {
                        first = false;
                    }
                } catch (UnsupportedEncodingException e) {
                    Log.w(TAG, "Encode http request param [" + name + "] error: " + e.getMessage());
                }
            }
        }
        return queryString.toString();
    }

    public void imageClick(View view) { //photo
        camera = "1";
        startCamera();
    }

    public void imageClick2(View view) { //supp doc
        camera = "2";
        startCamera2();
    }

    private void startCamera()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void startCamera2()
    {
        Intent cameraIntent2 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent2, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 5) {

            String[] separated = (FileHelper.ReadFile(Register.this)).split("\\;");


            spinner.setSelection(2);
            Name.setText(separated[0]);
            Gender.setText(separated[1]);
            ID.setText(separated[2]);
            Nationality.setText(separated[3]);

        }

        else if (requestCode == 1888 && resultCode == RESULT_OK) {

            /*if (mphoto != null) {
                mphoto.recycle();
            }

            if (mphoto2 != null) {
                mphoto2.recycle();
            }*/

            mphoto = (Bitmap) data.getExtras().get("data");
            mphoto2 = (Bitmap) data.getExtras().get("data");
            if (camera =="1") {
                imageSource.setImageBitmap(mphoto);
                deleteFiles("storage/emulated/0/DCIM/Camera/");
                callBroadCast();
            }

            if (camera =="2") {
                imageDoc.setImageBitmap(mphoto2);

            }


            else {


            }
        }


       else
        {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                Toast.makeText(Register.this, "CONTENT: " + scanContent + " FORMAT: " + scanFormat, Toast.LENGTH_LONG).show();
                SerialNo.setText(scanContent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }



    //Mykad


    public void powered()
    {
        // Set parameters
        PowerParams params = new PowerParams();
        params.slotNum = 0;
        params.action = 2;

        // Perform power action
        System.out.println("Slot " + 0 + ": " + powerActionStrings[2] + "...");
        new PowerTask().execute(params);
    }

    public void Protocol() {

        SetProtocolResult result = new SetProtocolResult();

        try {

            result.activeProtocol = mReader.setProtocol(0, Reader.PROTOCOL_T0);

        } catch (Exception e) {

            result.e = e;
        }

        SendAPDU1();
    }

    // Get Mykad Details
    public void DF1()
    {
        String ICName = Mykad.substring(0,240).replaceAll(" ", "");
        String ICID = Mykad.substring(360,399).replaceAll(" ", "");
        String ICGender = Mykad.substring(399,405).replaceAll(" ", "");
        String ICDOB = Mykad.substring(426,438).replaceAll(" ", "");

        Name.setText(convertHexToString(ICName));
        ID.setText(convertHexToString(ICID));

        String gend = convertHexToString(ICGender);

        if(gend.contains("L"))
        {
            Gender.setText("M");
        }
        else
        {
            Gender.setText("F");
        }
        String dob = ICDOB.substring(0,4) + "-" + ICDOB.substring(4, 6)  + "-" + ICDOB.substring(6, 8);
        DOB.setText(dob);
        //Nationality.setText(convertHexToString(ICCitizen));
    }

    public void DF2()
    {
        String ICAdd = MykadDF2.substring(0,90).replaceAll(" ", "");
        String ICAdd1 = MykadDF2.substring(90, 180).replaceAll(" ", "");
        String ICAdd2 = MykadDF2.substring(180,270).replaceAll(" ", "");
        String ICPcode = MykadDF2.substring(270, 278).replaceAll(" ","");
        String ICCity = MykadDF2.substring(276,354).replaceAll(" ", "");
        String ICState = MykadDF2.substring(354,432).replaceAll(" ", "");

        Address.setText(convertHexToString(ICAdd));
        Address2.setText(convertHexToString(ICAdd1));
        Address3.setText(convertHexToString(ICAdd2));
        Postcode.setText(ICPcode);
        City.setText(convertHexToString(ICCity));
        State.setText(convertHexToString(ICState));
    }

    public String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }
        System.out.println("Decimal : " + temp.toString());

        return sb.toString();
    }

    public void ClearText()
    {
        Name.setText("");
        ID.setText((""));
        Gender.setText((""));
        DOB.setText("");
        Nationality.setText((""));
        Address.setText("");
        Address2.setText("");
        Address3.setText("");

    }

    public void SendAPDU1() {
        byteAPDU = null;
        byteAPDU2=null;
        respAPDU=null;
        respAPDU2=null;

        byteJPNDF4=null;
        respJPNDF4=null;
        byteJPNDF4_1=null;
        respJPNDF4_1=null;


        byteJPNDF3=null;
        respJPNDF3=null;
        byteJPNDF3_1=null;
        respJPNDF3_1=null;
        //convbyte();

        byteJPNDF3 = atohex("00 A4 04 00 0A A0 00 00 00 74 4A 50 4E 00 10");
        respJPNDF3 = JPNDF3(byteJPNDF3);

        byteAPDU = atohex("00 A4 04 00 0A A0 00 00 00 74 4A 50 4E 00 10");
        respAPDU = transceives(byteAPDU);

        respAPDU = JPNDF2(byteAPDU);

        byteJPNDF4 = atohex("00 A4 04 00 0A A0 00 00 00 74 4A 50 4E 00 10");
        respJPNDF4 = JPNDF4(byteAPDU);
    }

    private static byte[] atohex(String data)
    {
        String hexchars = "0123456789abcdef";

        data = data.replaceAll(" ","").toLowerCase();
        if (data == null)
        {
            return null;
        }
        byte[] hex = new byte[data.length() / 2];

        for (int ii = 0; ii < data.length(); ii += 2)
        {
            int i1 = hexchars.indexOf(data.charAt(ii));
            int i2 = hexchars.indexOf(data.charAt(ii + 1));
            hex[ii/2] = (byte)((i1 << 4) | i2);
        }
        return hex;
    }

    private static byte hexStringToByte(String data) {
        return (byte) ((Character.digit(data.charAt(0), 16) << 4)
                + Character.digit(data.charAt(1), 16));
    }


    public void convbyte(int t)
    {
        int value = t;
        String test = Integer.toString(value, 16);
        String result;

        if(test.length() == 4)
        {
            result = test;
            String a1 = result.toString().substring(0,2);
            String a2 = result.toString().substring(2,4);
            offset = a1;
            offset2 = a2;
            System.out.println("Offsets - " + offset + ":" + offset2);

            v1 = hexStringToByte(offset);
            v2 = hexStringToByte(offset2);
            System.out.println("Results4 - " + v2 + ":" + v1);
        }
        else if (test.length() == 3)
        {
            result = ("0" + test);
            String a1 = result.toString().substring(0,2);
            String a2 = result.toString().substring(2,4);
            offset = a1;
            offset2 = a2;
            System.out.println("Offsets - " + offset + ":" + offset2);

            v1 = hexStringToByte(offset);
            v2 = hexStringToByte(offset2);
            System.out.println("Results3 - " + v2 + ":" + v1);
        }
        else if(test.length() == 2)
        {
            result = ("00" + test);
            String a1 = result.toString().substring(0,2);
            String a2 = result.toString().substring(2,4);
            offset = a1;
            offset2 = a2;

            v1 = hexStringToByte(offset);
            v2 = hexStringToByte(offset2);
            System.out.println("Results2 - " + v2 + ":" + v1);
        }
        else if(test.length() == 1)
        {
            result = ("000" + test);
            String a1 = result.toString().substring(0,2);
            String a2 = result.toString().substring(2,4);
            offset = a1;
            offset2 = a2;
            // v1 = Byte.valueOf(offset);
            // v2 = Byte.valueOf(offset2);

            /*int i = Integer.parseInt(offset,16);
            v1 = (byte) i;
            int j = Integer.parseInt(offset2,16);
            v2 = (byte) j;*/

            System.out.println("Results1 - " + v2 + ":" + v1);
            System.out.println("Offsets - " + offset + ":" + offset2);

            v1 = hexStringToByte(offset);
            v2 = hexStringToByte(offset2);
        }
    }



    public void getPhoto()
    {
        byteJPNDF2=null;
        respJPNDF2=null;
        byteJPNDF2_1=null;
        respJPNDF2_1=null;
        //convbyte();

        byteJPNDF2 = atohex("00 A4 04 00 0A A0 00 00 00 74 4A 50 4E 00 10");
        respJPNDF2 = JPNDF2(byteJPNDF2);

        // test1();
    }
    private byte[] JPNDF2(byte[] data)
    {
        int t = 3;

        byte[] response = new byte[4000];
        int responseLength = 0;
        try {
            System.out.println("***COMMAND APDU***");
            System.out.println("IFD - " + getHexString(data, data.length));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (iSlotNum >= 0) {
            try {
                responseLength = mReader.transmit(iSlotNum, data, data.length, response, response.length);
                byte[] GetResponse = {(byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0x05};
                responseLength = mReader.transmit(iSlotNum, GetResponse, GetResponse.length, response, response.length);
                for(int i = 0; i <= 15; i ++)
                {
                    convbyte(t);
                    //t = t+255;
                    System.out.println("value t : " + t);

                    if(i == 15)
                    {
                        //System.out.println("Results - " + v2 + ":" + v1);
                        byte[] JPNDF2_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_1, JPNDF2_1.length, response, response.length);
                        byte[] JPNDF2_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) v2, (byte) v1, (byte) 0x80, (byte) 0x00};
                        //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_2, JPNDF2_2.length, response, response.length);
                        byte[] JPNDF2_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x80};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_3, JPNDF2_3.length, response, response.length);
                        //t=t+175;
                    }

                    else  if(i == 16)
                    {
                        byte[] JPNDF2_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_1, JPNDF2_1.length, response, response.length);
                        byte[] JPNDF2_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) v2, (byte) v1, (byte) 0xFF, (byte) 0x00};
                        //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_2, JPNDF2_2.length, response, response.length);
                        byte[] JPNDF2_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0xFF};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_3, JPNDF2_3.length, response, response.length);
                    }
                    else {

                        //System.out.println("Results - " + v2 + ":" + v1);
                        byte[] JPNDF2_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_1, JPNDF2_1.length, response, response.length);
                        byte[] JPNDF2_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) v2, (byte) v1, (byte) 0xFF, (byte) 0x00};
                        //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_2, JPNDF2_2.length, response, response.length);
                        byte[] JPNDF2_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0xFF};
                        responseLength = mReader.transmit(iSlotNum, JPNDF2_3, JPNDF2_3.length, response, response.length);
                        t=t+255;
                    }
                    /*byte[] JPNDF2_1 = {(byte) 0xC8, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x78, (byte) 0x00};
                    responseLength = mReader.transmit(iSlotNum, JPNDF2_1, JPNDF2_1.length, response, response.length);
                    byte[] JPNDF2_2 = {(byte) 0xCC, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0xF4, (byte) 0x0E, (byte) 0x78, (byte) 0x00};
                    //byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                    responseLength = mReader.transmit(iSlotNum, JPNDF2_2, JPNDF2_2.length, response, response.length);
                    byte[] JPNDF2_3 = {(byte) 0xCC, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x78};
                    responseLength = mReader.transmit(iSlotNum, JPNDF2_3, JPNDF2_3.length, response, response.length);*/


                    MykadDF2 = MykadDF2 + getHexString(response, responseLength).replaceAll("90 00", "");
                    //A1.setText(MykadDF2);
                    // System.out.println("ICC2 - " + MykadDF2);
                }
                //FileHelper.saveToFile(MykadDF2);
                getImage();
                //test1();

               /* byte[] JPNDF2_2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x01,(byte)0x00,(byte)v2,(byte)v1,(byte)0xFF,(byte)0x00};
                responseLength = mReader.transmit(iSlotNum, JPNDF2_2, JPNDF2_2.length, response, response.length);
                byte[] JPNDF2_3 = {(byte)0xCC,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0xFF};
                responseLength = mReader.transmit(iSlotNum, JPNDF2_3, JPNDF2_3.length, response, response.length);*/

            } catch (Exception e) {
                System.out.println("****************************************");
                System.out.println("       ERROR transmit: Review APDU  ");
                System.out.println("****************************************");
                responseLength = 0;
                byte[] ra = Arrays.copyOf(response, responseLength);
                response = null;
                return (ra);
            }
           /* try {
                System.out.println("");
                MykadDF2 = MykadDF2 + " " +  getHexString(response, responseLength);
                //DF2();
                //System.out.println("ICC2 - " + getHexString(response, responseLength));
                System.out.println("ICC2 - " + MykadDF2);
                byte[] ra2 = Arrays.copyOf(response, responseLength);
                respJPNDF2_1 = ra2;

            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        byte[] ra = Arrays.copyOf(response, responseLength);
        response = null;
        return (ra);
    }


    private byte[] transceives (byte[] data) {
        byte[] response = new byte[512];
        int responseLength = 0;
        try {
            System.out.println("***COMMAND APDU***");
            System.out.println("IFD - " + getHexString(data, data.length));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (iSlotNum >= 0) {
            try {
                responseLength = mReader.transmit(iSlotNum, data, data.length, response, response.length);
                byte[] GetResponse = {(byte)0x00,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x05};
                responseLength = mReader.transmit(iSlotNum, GetResponse, GetResponse.length, response, response.length);
                byte[] JPNDF1 = {(byte)0xC8,(byte)0x32,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0xFF,(byte)0x00};
                //byte[] JPNDF1 = {(byte)0xC8,(byte)0x32,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0xFFF,(byte)0x00};
                responseLength = mReader.transmit(iSlotNum, JPNDF1, JPNDF1.length, response, response.length);
                byte[] JPNDF2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x99,(byte)0x00,(byte)0xFF,(byte)0x00};
                //byte[] JPNDF2 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0xFFF,(byte)0x00};
                responseLength = mReader.transmit(iSlotNum, JPNDF2, JPNDF2.length, response, response.length);
                byte[] JPNDF3 = {(byte)0xCC,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0xFF};
                //byte[] JPNDF3 = {(byte)0xCC,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0xFFF};
                responseLength = mReader.transmit(iSlotNum, JPNDF3, JPNDF3.length, response, response.length);
               /* byte[] JPNDF4 = {(byte)0xC8,(byte)0x32,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0xFA0,(byte)0x00};
                responseLength = mReader.transmit(iSlotNum, JPNDF4, JPNDF4.length, response, response.length);
                byte[] JPNDF5 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0xFA0,(byte)0x00};
                responseLength = mReader.transmit(iSlotNum, JPNDF5, JPNDF5.length, response, response.length);
                byte[] JPNDF6 = {(byte)0xCC,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0xFA0};
                responseLength = mReader.transmit(iSlotNum, JPNDF6, JPNDF6.length, response, response.length);*/

            } catch (Exception e) {
                System.out.println("****************************************");
                System.out.println("       ERROR transmit: Review APDU  ");
                System.out.println("****************************************");
                responseLength = 0;
                byte[] ra = Arrays.copyOf(response, responseLength);
                response = null;
                return (ra);
            }
            try {
                System.out.println("");
                Mykad =  getHexString(response, responseLength);
                DF1();
                System.out.println("ICC - " + getHexString(response, responseLength));
                byte[] ra2 = Arrays.copyOf(response, responseLength);
                System.out.println("Response : " + response[0] +" : " + responseLength);
                respAPDU2 = ra2;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] ra = Arrays.copyOf(response, responseLength);
        response = null;
        return (ra);
    }

    private static String getHexString(byte[] data,int slen) throws Exception
    {
        String szDataStr = "";
        for (int ii=0; ii < slen; ii++)
        {
            szDataStr += String.format("%02X ", data[ii] & 0xFF);
        }
        return szDataStr;
    }

    private byte[] JPNDF4(byte[] data)
    {
        byte[] response = new byte[512];
        int responseLength = 0;
        try {
            System.out.println("***COMMAND APDU***");
            System.out.println("IFD - " + getHexString(data, data.length));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (iSlotNum >= 0) {
            try {
                responseLength = mReader.transmit(iSlotNum, data, data.length, response, response.length);
                byte[] GetResponse = {(byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0x05};
                responseLength = mReader.transmit(iSlotNum, GetResponse, GetResponse.length, response, response.length);
                byte[] JPNDF4 = {(byte)0xC8,(byte)0x32,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x90,(byte)0x00};
                responseLength = mReader.transmit(iSlotNum, JPNDF4, JPNDF4.length, response, response.length);
                byte[] JPNDF5 = {(byte)0xCC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x90,(byte)0x00};
                responseLength = mReader.transmit(iSlotNum, JPNDF5, JPNDF5.length, response, response.length);
                byte[] JPNDF6 = {(byte)0xCC,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x90};
                responseLength = mReader.transmit(iSlotNum, JPNDF6, JPNDF6.length, response, response.length);

            } catch (Exception e) {
                System.out.println("****************************************");
                System.out.println("       ERROR transmit: Review APDU  ");
                System.out.println("****************************************");
                responseLength = 0;
                byte[] ra = Arrays.copyOf(response, responseLength);
                response = null;
                return (ra);
            }
            try {
                System.out.println("");
                MykadDF2 =  getHexString(response, responseLength);
                DF2();
                System.out.println("ICC2 - " + getHexString(response, responseLength));
                byte[] ra2 = Arrays.copyOf(response, responseLength);
                respJPNDF4_1 = ra2;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        byte[] ra = Arrays.copyOf(response, responseLength);
        response = null;
        return (ra);
    }

    public void btn_Read(View v)
    {
        ClearText();
        powered();
      //  getFP();
       /* try {
            getPhoto();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Photo Error: " +e.toString(), Toast.LENGTH_LONG).show();
        }*/
    }


    public void ReadBarcode2()
    {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
        //  BARCODE_REQUEST
    }


    public void ReadBarcode(View v)
    {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
      //  BARCODE_REQUEST
    }

}



class Utils
{
    public static void justSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e){}

    }
}