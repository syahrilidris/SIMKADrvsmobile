package test3.simkadrvs;

/**
 * Created by ngkarchai on 10/03/16.
 */

import android.content.Context;
import android.util.Log;

import com.morpho.android.usb.USBManager;
import com.pradotec.biometric.BiometricLib;

import java.util.Observer;

public class BioAction {

    private BiometricLib bio;
    private static boolean isConnected = false;
    private static boolean isPowerOn = false;
    private static boolean isPermission = false;
    private static final int VOLATILE_MODE = 0;
    private static final int NON_VOLATILE_MODE = 1;
    private static int selectedMode = 0;

    private String err;
    private int result  = 0;
    private byte[] fingerprint;
    private Context context;

    private Observer parent;
    private String TAG = "BIOMETRIC";

    public BioAction(Observer parent, Context context)
    {
        this.context = context;
        this.parent = parent;
        bio = new BiometricLib();
        fingerprint=null;

        usbInitialize(context);
    }
    public void usbInitialize(Context context) {
        USBManager.getInstance().initialize(context, "com.datasonic.demoapplication.USB_PERMISSION");
    }
    public boolean powerOnMtkDevice() {
        boolean result = false;
        try {
                result = true;

        } catch(Exception e) {
            e.printStackTrace();}
        return result;
    }

    public boolean powerOffMtkDevice() {
        boolean result = false;
        try {
        } catch(Exception e) { }
        return result;
    }
    public void capture()
    {
        fingerprint=null;
        Thread commandThread = (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int len;
                    byte[] data = new byte[2048];
                    len = bio.BIOMETRIC_Capture(data);
                    if ( len == -1) {
                        err = bio.BIOMETRIC_GetLastErrorDesc();

                        Log.d(TAG,"capture  : " + err);
                        return;
                    }
                    fingerprint = new byte[len];
                    for(int i = 0; i < len; i ++)
                        fingerprint[i] = data[i];
                    if(len<=0)
                    {
                        err = bio.BIOMETRIC_GetLastErrorDesc();
                        //Toast.makeText(context,"Capture failed",Toast.LENGTH_SHORT).show();
                        Log.d("zakat",err);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                err = bio.BIOMETRIC_GetLastErrorDesc();
                Log.d("Zakat",err);
            }
        }));

        commandThread.start();
    }

    public byte[] getFinger()
    {
        return fingerprint;
    }

    public void load(final byte[] finger1,final byte[] finger2){
        bio.BIOMETRIC_EnrollMatch(finger1,finger1.length);
        err = bio.BIOMETRIC_GetLastErrorDesc();

        bio.BIOMETRIC_EnrollMatch(finger2,finger2.length);
        err = bio.BIOMETRIC_GetLastErrorDesc();
    }

    public boolean verify()
    {
        boolean vr = bio.BIOMETRIC_Verify();
        err = bio.BIOMETRIC_GetLastErrorDesc();
        Log.d(TAG, err);

        return  vr;
        /*
        result = 0;
        Thread commandThread = (new Thread(new Runnable()
        {
            @Override
            public void run() {


            }
        }));
        commandThread.start();
        */
    }
    public int getVerifyResult()
    {
        return result;
    }
    /**
     * Connect to the Biometric device
     * @return
     */
    public boolean connect(boolean direct)
    {
       boolean re = bio.BIOMETRIC_ON();
        err = bio.BIOMETRIC_GetLastErrorDesc();

        int retryCount=5;
        if(direct) {
            retryCount = 0;
        }
        while(!powerOnMtkDevice()&&retryCount>0)
        {
            try {
                Thread.sleep(300);
                retryCount--;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        //usbInitialize(context);
        try {
            Thread.sleep(1000);
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        byte[] datain = new byte[1];
        byte[] dataout = new byte[1];

        if(retryCount<=0) {
            return false;
        }
        boolean con = false;
        int retry = 5;
        while((!con) && retry>0) {
            con = bio.BIOMETRIC_Connect();
            retry--;

            try {
                Thread.sleep(333);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!con)
        {
            err = bio.BIOMETRIC_GetLastErrorDesc();
            //System.out.println("con error : " + err);
            //return false;
        }
        //con = bio.BIOMETRIC_Init();
        boolean ob = bio.BIOMETRIC_ViewerSetup(parent);
        err = bio.BIOMETRIC_GetLastErrorDesc();

        datain[0]=(byte)0;
        bio.BIOMETRIC_Control(0xF300, datain, 1, dataout);
        err = bio.BIOMETRIC_GetLastErrorDesc();

        bio.BIOMETRIC_Control(0xF302, datain, 1, dataout);
        err = bio.BIOMETRIC_GetLastErrorDesc();

        bio.BIOMETRIC_Control(0xF304, datain, 1, dataout);
        err = bio.BIOMETRIC_GetLastErrorDesc();

        bio.BIOMETRIC_Init();
        err = bio.BIOMETRIC_GetLastErrorDesc();


        datain[0] = (byte) 0; //INFO_CBM_TEMPLATE_PK_COMP (refer to BiometricLib.class)

        bio.BIOMETRIC_Control(0xF300, datain, 1, dataout);
        err = bio.BIOMETRIC_GetLastErrorDesc();

        datain[0] = (byte) 10;
        bio.BIOMETRIC_Control(0xF302, datain, 1, dataout); // setTimeout 10 secs
        err = bio.BIOMETRIC_GetLastErrorDesc();

        return true;
    }

    /**
     * Disconnect the device
     * @return
     */
    public boolean disconnect()
    {
        if(!bio.BIOMETRIC_Disconnect())
            return false;
        return powerOffMtkDevice();
    }
}
