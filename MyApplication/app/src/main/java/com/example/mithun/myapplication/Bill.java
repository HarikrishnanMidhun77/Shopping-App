package com.example.mithun.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Bill extends AppCompatActivity {
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;
    String message;
    String[] items;
    String[] elems;
    TextView tot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Intent intent = getIntent();
       tot=(TextView)findViewById(R.id.txtTotal);
        message = intent.getStringExtra("bill_items");
        Toast.makeText(getApplicationContext(), "Items: " + message, Toast.LENGTH_SHORT).show();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new MoviesAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareMovieData();
    }
    private void prepareMovieData() {
        Movie movie;
        String it,comp,price,qty;
        float total=0;
        items=message.split(",");
        for(int i=1;i<items.length;i++){
            elems=items[i].split(":");
            it=elems[0];
            comp=elems[1];
            price=elems[2];
            qty=elems[3];
            total=total+(Float.valueOf(price)*Float.valueOf(qty));
            movie= new Movie(comp, it, price+"("+qty+")");
            movieList.add(movie);
        }
        movie= new Movie("Total", "amount",String.valueOf(total));
        movieList.add(movie);
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
                Intent intent = new Intent(this, MainActivity.class);
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
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
