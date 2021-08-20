package com.emre.android_relational_queries_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button queryA, queryB, queryC, clearResults;
    private ResultAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queryA = findViewById(R.id.queryA);
        queryB = findViewById(R.id.queryB);
        queryC = findViewById(R.id.queryC);
        clearResults = findViewById(R.id.clearResults);
        resultList = findViewById(R.id.resultList);
        progressDialog = new ProgressDialog(this);

        queryA.setOnClickListener(view -> {
            progressDialog.show();
            ParseQuery<ParseObject> publisherQuery = new ParseQuery<>("Publisher");
            publisherQuery.whereEqualTo("name", "Acacia Publishings");
            try {
                ParseObject publisher = publisherQuery.getFirst();
                ParseQuery<ParseObject> bookQuery = new ParseQuery<ParseObject>("Book");
                bookQuery.whereEqualTo("publisher", publisher);
                bookQuery.findInBackground((objects, e) -> {
                    progressDialog.hide();
                    if (e == null) {
                        initData(objects);
                    } else {
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ParseException e) {
                progressDialog.hide();
                e.printStackTrace();
            }
        });
        queryB.setOnClickListener(view -> {
            progressDialog.show();
            ParseQuery<ParseObject> bookQuery = new ParseQuery<>("Book");

            Calendar calendar = Calendar.getInstance();
            calendar.set(2010,1,1,59,59,59);
            Date date = calendar.getTime();

            bookQuery.whereGreaterThan("publishingDate", date);

            ParseQuery<ParseObject> bookStoreQuery = new ParseQuery<>("BookStore");
            bookStoreQuery.whereMatchesQuery("books",bookQuery);
            bookStoreQuery.findInBackground((objects, e) -> {
                progressDialog.hide();
                if (e==null){
                  initData(objects);
              } else {
                  Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
              }
            });
        });
        queryC.setOnClickListener(view -> {
            progressDialog.show();

            ParseQuery<ParseObject> authorQuery = new ParseQuery<>("Author");
            authorQuery.whereEqualTo("name","Aaron Writer");
            try {
                ParseObject authorA = authorQuery.getFirst();
                ParseQuery<ParseObject> bookQuery = new ParseQuery<>("Book");
                bookQuery.whereEqualTo("authors",authorA);

                ParseQuery<ParseObject> bookStoreQuery = new ParseQuery<>("BookStore");
                bookStoreQuery.whereMatchesQuery("books",bookQuery);

                bookStoreQuery.findInBackground((objects, e) -> {
                    progressDialog.hide();
                    if (e==null){
                        initData(objects);
                    } else {
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (ParseException e) {
                progressDialog.hide();
                e.printStackTrace();
            }

        });

        clearResults.setOnClickListener(view -> {
            adapter.clearList();
        });
    }

    private void initData(List<ParseObject> objects) {
        adapter = new ResultAdapter(this, objects);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        resultList.setAdapter(adapter);
    }
}