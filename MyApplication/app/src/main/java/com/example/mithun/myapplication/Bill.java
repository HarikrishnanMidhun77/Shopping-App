package com.example.mithun.myapplication;


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.support.v7.widget.helper.ItemTouchHelper;

public class Bill extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;
    SharedPreferences shared;
    String message;
    String[] items;
    String[] elems;
    TextView tot;
    ShoDb sdb;
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_new);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(Bill.this, MainActivity.class);
                startActivity(intent);
            }
        });

        sdb=new ShoDb(this,null);
        Intent intent = getIntent();
        tot = (TextView) findViewById(R.id.txtTot);
        message = intent.getStringExtra("bill_items");
        Toast.makeText(getApplicationContext(), "Items: " + message, Toast.LENGTH_SHORT).show();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new MoviesAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        prepareMovieData();

ItemTouchHelper.SimpleCallback itemTouchHelperCallback2=new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Bill.this);
        alertDialog.setTitle("Enter Quantity");
        alertDialog.setMessage(movieList.get(viewHolder.getAdapterPosition()).getGenre());

        final EditText input = new EditText(Bill.this);
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
                       String q = input.getText().toString();
                        String i=movieList.get(viewHolder.getAdapterPosition()).getId();
                        final Movie deletedItem = movieList.get(viewHolder.getAdapterPosition());
                        final int deletedIndex = viewHolder.getAdapterPosition();
                        sdb.updateHandler(i,q);
                        String ot  = tot.getText().toString().replaceAll("[^0-9].", "");
                        String[] od= deletedItem.getYear().split("\\(.");
                        Float t= Float.valueOf(ot)-Float.valueOf(od[0]);
                        t= t+(Float.valueOf(q)*Float.valueOf(od[0]));
                        tot.setText("Total:    Rs. "+String.valueOf(t));
                        deletedItem.setYear(od[0]+"("+q+")");
                        mAdapter.notifyDataSetChanged();
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
};
        new ItemTouchHelper(itemTouchHelperCallback2).attachToRecyclerView(recyclerView);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
                if (viewHolder instanceof MoviesAdapter.MyViewHolder) {
                    // get the removed item name to display it in snack bar
                    String name = movieList.get(viewHolder.getAdapterPosition()).getTitle();

                    // backup of removed item for undo purpose
                    final Movie deletedItem = movieList.get(viewHolder.getAdapterPosition());
                    final int deletedIndex = viewHolder.getAdapterPosition();

                    // remove the item from recycler view
                    //
                    mAdapter.removeItem(viewHolder.getAdapterPosition());

                    String ot  = tot.getText().toString().replaceAll("[^0-9].", "");
                   String[] od= deletedItem.getYear().split("\\(");
                    String oq = od[1].replaceAll("[^0-9].", "");
                    String[] t2=oq.split("\\)");
                    oq=t2[0];
                   Float t= Float.valueOf(ot)-(Float.valueOf(od[0])*Float.valueOf(oq));

                   // finish();
                   // startActivity(getIntent());

                    mAdapter.notifyDataSetChanged();
                    tot.setText("Total:    Rs. "+String.valueOf(t));
                   // tot.setText(String.valueOf(t));
                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // undo is selected, restore the deleted item
                            mAdapter.restoreItem(deletedItem, deletedIndex);
                            String ot  = tot.getText().toString().replaceAll("[^0-9].", "");
                            String[] od= deletedItem.getYear().split("\\(");
                            String oq = od[1].replaceAll("[^0-9].", "");
                            String[] t2=oq.split("\\)");
                            oq=t2[0];
                           // Float t= Float.valueOf(ot)+Float.valueOf(od[0]);
                            Float t= Float.valueOf(ot)+(Float.valueOf(od[0])*Float.valueOf(oq));
                            tot.setText("Total:    Rs. "+String.valueOf(t));

                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                    shared = getSharedPreferences("ShoPref", MODE_PRIVATE);
                    String bil = (shared.getString("bno", ""));
                    sdb.deleteHandler(bil,deletedItem.getId());
                    Toast.makeText(getApplicationContext(), deletedItem.getGenre()+" is deleted from db", Toast.LENGTH_SHORT).show();

                }
            }



            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);
    }
 /*   private void prepareMovieData() {
        Movie movie;
        String it, comp, price, qty;
        float total = 0;
        items = message.split(",");
        for (int i = 1; i < items.length; i++) {
            elems = items[i].split(":");
            it = elems[0];
            comp = elems[1];
            price = elems[2];
            qty = elems[3];
            total = total + (Float.valueOf(price) * Float.valueOf(qty));
            movie = new Movie(comp, it, price + "(" + qty + ")");
            movieList.add(movie);
        }*/

    private void prepareMovieData() {
        Movie movie;
        String it, comp, price, qty,id;
        float total = 0;
        shared = getSharedPreferences("ShoPref", MODE_PRIVATE);
        String bil = (shared.getString("bno", ""));
        Cursor c=sdb.findHandler(bil);
       // if (c.moveToFirst()) {
        //    while (!c.isAfterLast()) {
        while (c.moveToNext()){
            id=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_id));
                it=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_type));
                comp=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_comp));
                price=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_price));
                qty=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_qnty));
                total = total + (Float.valueOf(price) * Float.valueOf(qty));
                movie = new Movie(id,comp, it, price + "(" + qty + ")");
                movieList.add(movie);
            }
       // }

     tot.setText("Total:    Rs. "+String.valueOf(total));
      //  movie = new Movie("Total", "amount", String.valueOf(total));
       // movieList.add(movie);
        // tot.setText(String.valueOf(total));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bill_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                // Toast.makeText(getApplicationContext(), "Items: " + items, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_print:
                shared = getSharedPreferences("ShoPref", MODE_PRIVATE);
                String bil = (shared.getString("bno", ""));

                createPDF();

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/shopify/"+bil+".pdf");

                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file),"application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent2 = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent2);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }


                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void createPDF() {
        Document doc = new Document();


        try {
            shared = getSharedPreferences("ShoPref", MODE_PRIVATE);
            String bil = (shared.getString("bno", ""));

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/shopify/";
            boolean ab;
            File dir = new File(path);
            if (!dir.exists())
                ab = dir.mkdir();

            Log.d("PDFCreator", "PDF Path: " + path);


            File file = new File(dir, bil+".pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            doc.setPageSize(PageSize.A4);
            doc.addCreationDate();
            doc.addAuthor("Shopify");
            doc.addCreator("Hari");

            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 26.0f;

            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            Font paraFont = new Font(Font.FontFamily.COURIER);

           /* BaseFont myF=BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
            Font mOrderDetailsTitleFont = new Font(myF, 36.0f, Font.NORMAL, BaseColor.BLACK);*/
// Creating Chunk
            Chunk titl = new Chunk("Shopify Bill (" + curTime() + ")", paraFont);

            Paragraph p0 = new Paragraph(titl);
            p0.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(p0);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));

            String it, comp, price, qty, str;
            float total = 0;
            items = message.split(",");

            Paragraph p1[] = new Paragraph[50];
            // Font paraFont= new Font(Font.FontFamily.COURIER);



            // if (c.moveToFirst()) {
            //    while (!c.isAfterLast()) {
            int i=0;
            Cursor c=sdb.findHandler(bil);
            while (c.moveToNext()){
                it=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_type));
                comp=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_comp));
                price=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_price));
                qty=c.getString(c.getColumnIndex(ShoDb.COLUMN_item_qnty));
                total = total + (Float.valueOf(price) * Float.valueOf(qty));

                str = "Item :     " + comp+"( "+it + ")\nprice       :Rs. " + price + "\nQuantity         :" + qty;
                p1[i] = new Paragraph(str);
                p1[i].setAlignment(Paragraph.ALIGN_CENTER);
                p1[i].setFont(paraFont);
                doc.add(p1[i]);

                doc.add(new Paragraph(""));
                doc.add(new Chunk(lineSeparator));
                doc.add(new Paragraph(""));
             i++;
            }


          /*  for (int i = 1; i < items.length; i++) {
                elems = items[i].split(":");
                it = elems[0];
                comp = elems[1];
                price = elems[2];
                qty = elems[3];
                total = total + (Float.valueOf(price) * Float.valueOf(qty));

                str = "Item :     " + it + "\nprice       :" + price + "\nQuantity         :" + qty;
                p1[i] = new Paragraph(str);
                p1[i].setAlignment(Paragraph.ALIGN_CENTER);
                p1[i].setFont(paraFont);
                doc.add(p1[i]);

                doc.add(new Paragraph(""));
                doc.add(new Chunk(lineSeparator));
                doc.add(new Paragraph(""));
            }*/


            Chunk titl2 = new Chunk("Total:     " + String.valueOf(total), paraFont);

            Paragraph tt = new Paragraph(titl2);
            tt.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(tt);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


           /* Paragraph p1 = new Paragraph(Data);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);*/

          /*  ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.ic_launcher_foreground);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);

            //add image to document
            doc.add(myImg);*/

            //set footer
            Phrase footerText = new Phrase("This is an example of a footer");
            // HeaderFooter pdfFooter = new HeaderFooter(footerText, false);
            //  doc.setFooter(pdfFooter);

            Toast.makeText(getApplicationContext(), "Bill stored in " + path, Toast.LENGTH_LONG).show();

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
            Toast.makeText(getApplicationContext(), "Failed to print bill", Toast.LENGTH_LONG).show();
        } finally {
            doc.close();
        }

    }

    public String curTime() {
        Time t = new Time(Time.getCurrentTimezone());
        t.setToNow();
        String date1 = t.format("%Y/%m/%d");

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa",
                Locale.ENGLISH);
        String var = dateFormat.format(date);
        String horafecha = var + " - " + date1;
        return horafecha;
    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MoviesAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = movieList.get(viewHolder.getAdapterPosition()).getTitle();

            // backup of removed item for undo purpose
            final Movie deletedItem = movieList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
          //  mAdapter.removeItem(viewHolder.getAdapterPosition());
            shared = getSharedPreferences("ShoPref", MODE_PRIVATE);
            String bil = (shared.getString("bno", ""));
            sdb.deleteHandler(bil,deletedItem.getId());
            finish();
            startActivity(getIntent());
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position, movieList.size());
            mAdapter.notifyDataSetChanged();
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }


}
