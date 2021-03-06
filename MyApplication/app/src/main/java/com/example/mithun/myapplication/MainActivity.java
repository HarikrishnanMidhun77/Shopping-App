package com.example.mithun.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.mithun.myapplication.ShoDb;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;
import java.util.Random;

import info.androidhive.barcode.BarcodeReader;

public class MainActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    String c="",i="",q="1",p="";
    BarcodeReader barcodeReader;
    String items="";
    String qty="1";
    SharedPreferences shared;
    ShoDb sdb;

   // int random = Random.nextInt(500);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sdb=new ShoDb(this,null);

        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, Bill.class);
                intent.putExtra("bill_items", items);
                startActivity(intent);
               // Toast.makeText(getApplicationContext(), "Items: " + items, Toast.LENGTH_SHORT).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onScanned(final Barcode barcode) {
        // play beep sound
       barcodeReader.playBeep();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(250);
                }catch(Exception e){
                   e.getStackTrace() ;
                }
                Toast.makeText(getApplicationContext(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
                items=items+","+ barcode.displayValue;
                String s=barcode.displayValue;
                String[] s2=s.split(":");

                i=s2[0];
                c=s2[1];
                p= s2[2];

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Enter Quantity");
                alertDialog.setMessage(s2[0]);

                final EditText input = new EditText(MainActivity.this);
                input.setText("1");
                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                input.setSelectAllOnFocus(true);
                input.setSelection(input.getText().length());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
               // alertDialog.setIcon(R.drawable.key);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                qty = input.getText().toString();
                                q=qty;
                              //  if (qty.compareTo("") == 0) {
                                   items=items+":"+qty;
                                shared = getSharedPreferences("ShoPref", MODE_PRIVATE);
                                String bil = (shared.getString("bno", ""));
                                sdb.addHandler(bil,c,i,q,p);
                               // }
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();


            }

        });

       // Toast.makeText(this,"hello"+c+i+q+p,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String s) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(this, HomeActivity.class);
       // startActivity(intent);
    }


}

