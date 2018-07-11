package test3.simkadrvs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    private GridMenuFragment mGridMenuFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

       // Bundle bundle = getIntent().getExtras();
       // String value1 = bundle.getString("Username");
      //  String value2 = bundle.getString("LoginTime");
        this.setTitle("SIMCARDrvs Recognition");

        mGridMenuFragment = GridMenuFragment.newInstance(R.drawable.back);

       // Toast.makeText(getApplicationContext(),value1, Toast.LENGTH_LONG).show();
     //   Toast.makeText(getApplicationContext(),"Successfully Logged In",Toast.LENGTH_LONG).show();
     //   UserLog.findViewById(R.id.tvLog);
        //TextView UserLog = (TextView)findViewById(R.id.tvLog);
        //UserLog.setText("Username : "+value1);



        test();
        setupGridMenu();

        mGridMenuFragment.setOnClickMenuListener(new GridMenuFragment.OnClickMenuListener() {
            @Override
            public void onClickMenu(GridMenu gridMenu, int position) {
                /*Toast.makeText(MainMenu.this, "Title:" + gridMenu.getTitle() + ", Position:" + position,
                        Toast.LENGTH_SHORT).show();*/

                getMonthNumber(position);
            }
        });
    }


    public int getMonthNumber(int month) {

        int monthNumber = 0;




        switch (month) {
            case 0:
              startActivity(new Intent(MainMenu.this, FaceRecognition.class));
                //Intent i = new Intent("com.taztag.testthermalprinter.testThermalPrinter.MainActivity");
                //i.putExtra("Print", "test");
               // Intent intent3 = new Intent(Intent.ACTION_MAIN);
                //intent3.setComponent(new ComponentName("com.taztag.testthermalprinter", "com.taztag.testthermalprinter.MainActivity"));
                //intent3.putExtra("Print", "test");
                //startActivity(intent3);

                break;
           /* case 1:
                startActivity(new Intent(MainMenu.this, CustomerInfo.class));
                break;*/
            case 1:
               // startActivity(intent2);
                //Toast.makeText(getApplicationContext(),"MATCHING",Toast.LENGTH_LONG).show();
               startActivity(new Intent(MainMenu.this, QueryData.class));
               // Intent intent = new Intent(MainMenu.this, CustomerInfo.class);
                //intent.putExtra("Status", "Fingerprint");
               // startActivity(intent);

                break;
           /* case 3:

                //intent2.putExtra("Status", "Face");

                startActivity(new Intent(MainMenu.this, Register.class));
                break;*/
        }

        return monthNumber;
    }


    private void test()
    {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_frame, mGridMenuFragment);
        tx.addToBackStack(null);
        tx.commit();
    }

    private void setupGridMenu() {
        List<GridMenu> menus = new ArrayList<>();
        menus.add(new GridMenu("Face Rec", R.mipmap.face));
        //menus.add(new GridMenu("Fingerprint", R.mipmap.fp));
        menus.add(new GridMenu("Enquiry", R.mipmap.profile));
       //menus.add(new GridMenu("Register", R.mipmap.user));

        mGridMenuFragment.setupMenu(menus);
    }

    @Override
    public void onBackPressed() {
        /*if (0 == getSupportFragmentManager().getBackStackEntryCount()) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }*/
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        finish();
        startActivity(new Intent(MainMenu.this, Login.class));

    }
}
