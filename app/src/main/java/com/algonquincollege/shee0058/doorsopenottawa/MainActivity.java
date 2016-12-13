package com.algonquincollege.shee0058.doorsopenottawa;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.shee0058.doorsopenottawa.model.Building;
import com.algonquincollege.shee0058.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Doors Open Ottawa App - Display all the data from Doors Open Ottawa
 * and give the location using google map API
 *
 * @author Owen Sheehan (shee0058@algonquinlive.com)
 */

public class MainActivity extends ListActivity {

    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    private static final String ABOUT_DIALOG_TAG;

    static {
        ABOUT_DIALOG_TAG = "About Dialog";
    }

    private ProgressBar pb;
    private List<MyTask> tasks;
    private ListView list;

    private List<Building> buildingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        ListView list = (ListView) findViewById(android.R.id.list);
        final SwipeRefreshLayout refresher = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        if (isOnline()) {
            requestData(REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }

        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                getListView().setOnItemClickListener(this);
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("name", buildingList.get(position).getName());
                intent.putExtra("description", buildingList.get(position).getDescription());
                intent.putExtra("address", buildingList.get(position).getAddress());
                //intent.putExtra("open_hours", buildingList.get(position).getOpenHours());
                Log.i(buildingList.get(position).getName(), "LOGGED");
                startActivity(intent);
            }
        });

        list.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

        refresher.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        requestData(REST_URI);
                        refresher.setRefreshing(false);
                        pb.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        Log.e("TAG", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TAG", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyTask task = new MyTask();
        task.execute(IMAGES_BASE_URL + "users/logout");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show(getFragmentManager(), ABOUT_DIALOG_TAG);
        }

        if (item.getItemId() == R.id.action_post_data) {
            Intent intent = new Intent(getApplicationContext(), AddActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        switch (item.getItemId()) {
            case R.id.action_sort_name_asc:
                Collections.sort(buildingList, new Comparator<Building>() {
                    @Override
                    public int compare(Building lhs, Building rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                break;

            case R.id.action_sort_name_dsc:
                Collections.sort(buildingList, Collections.reverseOrder(new Comparator<Building>() {
                    @Override
                    public int compare(Building lhs, Building rhs) {
                        return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                    }
                }));
                break;
        }
        item.setChecked(true);
        ((BuildingAdapter) getListAdapter()).notifyDataSetChanged();
        return true;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {

            String content = HttpManager.getData(params[0], "shee0058", "password");
            buildingList = BuildingJSONParser.parseFeed(content);
            Log.i("BUILDING", content);
            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = result;
            updateDisplay();
        }
    }
}