package com.dinodevs.timelinewidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import clc.sliteplugin.flowboard.AbstractPlugin;
import clc.sliteplugin.flowboard.ISpringBoardHostStub;

public class widget extends AbstractPlugin {

    // Tag for logging purposes.
    private static String TAG = "TimelineWidget";
    // Version
    public String version = "n/a";

    // Activity variables
    private boolean isActive = false;
    private Context mContext;
    private View mView;

    private ListView lv;
    private ArrayList<HashMap<String, String>> eventsList;


    // Set up the widget's layout
    @Override
    public View getView(Context paramContext) {
        // Save Activity variables
        this.mContext = paramContext;
        this.mView = LayoutInflater.from(paramContext).inflate(R.layout.widget_timeline, null);

        // Initialize variables
        Log.d(widget.TAG, "Starting widget...");
        this.init();

        // Attach event listeners
        Log.d(widget.TAG, "Attaching listeners...");
        this.initListeners();

        // Finish
        Log.d(widget.TAG, "Done...");
        return this.mView;
    }

    // Initialize widget
    private void init() {
        // Get widget version number
        try {
            PackageInfo pInfo = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
            this.version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Show Time/Date
        TextView time = this.mView.findViewById(R.id.time);
        //time.setText( dateToString(Calendar.getInstance(),"hh:mm a") );
        time.setText( dateToString(Calendar.getInstance(),"EEEE\nd MMMM") );
        //TextView date = this.mView.findViewById(R.id.date);
        //date.setText( dateToString(Calendar.getInstance(),"MM MMMM") );

        // Calendar Events Data
        eventsList = new ArrayList<>();
        lv = (ListView) this.mView.findViewById(R.id.list);

        loadCalendarEvents();
    }

    // Attach listeners
    @SuppressLint("ClickableViewAccessibility")
    private void initListeners(){
        // About button event
        TextView about = this.mView.findViewById(R.id.time);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widget.this.toast("Timeline Widget v" + widget.this.version + " by GreatApo");
            }
        });

        about.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loadCalendarEvents();
                widget.this.toast("Refreshing events...");
                return true;
            }
        });


    }

    // Refresh Calendar (set it to current month)
    private void refreshView() {
        // Refresh timeline
    }


    public void loadCalendarEvents() {
        eventsList = new ArrayList<>();
        // Load data
        String calendarEvents = Settings.System.getString(mContext.getContentResolver(), "CustomCalendarData");

        try {
            // Check if correct form of JSON
            JSONObject json_data = new JSONObject(calendarEvents);
            if( json_data.has("events") ){
                int event_number = json_data.getJSONArray("events").length();
                String last_date = "";
                //String[] titles = new String[event_number];

                // Get data
                for(int i=0; i<event_number; i++) {
                    //titles[i] = json_data.getJSONArray("events").getJSONArray(i).getString(0);

                    JSONArray data = json_data.getJSONArray("events").getJSONArray(i);
                    HashMap<String, String> event = new HashMap<>();

                    // adding each child node to HashMap key => value
                    event.put("title", data.getString(0));
                    //event.put("description", data.getString(1));
                    //event.put("start", data.getString(2));
                    //event.put("end", data.getString(3));
                    //event.put("location", data.getString(4));
                    //event.put("account", data.getString(5));

                    String start = "N/A";
                    String end = "";
                    String location = "";
                    Calendar calendar = Calendar.getInstance();
                    if(!data.getString(2).equals("") && !data.getString(2).equals("null")) {
                        calendar.setTimeInMillis(Long.parseLong(data.getString(2)));
                        start = dateToString( calendar,"hh:mm a" );

                        if( !last_date.equals(dateToString( calendar,"d MMMM" )) ){
                            last_date = dateToString( calendar,"d MMMM" );
                            HashMap<String, String> date_elem = new HashMap<>();
                            date_elem.put("title", last_date);
                            date_elem.put("subtitle", "" );
                            eventsList.add(date_elem);
                        }
                    }
                    if(!data.getString(3).equals("") && !data.getString(3).equals("null")) {
                        calendar.setTimeInMillis(Long.parseLong(data.getString(3)));
                        end = " - "+ dateToString(calendar, "hh:mm a");
                    }
                    if(!data.getString(4).equals("") && !data.getString(4).equals("null")) {
                        location = "\n@ "+data.getString(4);
                    }
                    event.put("subtitle", start+ end + location );
                    // adding events to events list
                    eventsList.add(event);
                }
            }else{
                HashMap<String, String> event = new HashMap<>();
                event.put("title", "No events");
                //event.put("description", "-");
                //event.put("start", "-");
                //event.put("end", "-");
                //event.put("location", "-");
                //event.put("account", "-");
                event.put("subtitle", "-");
                eventsList.add(event);
            }
        } catch (JSONException e) {
            //default
            HashMap<String, String> event = new HashMap<>();
            event.put("title", "No events");
            //event.put("description", "-");
            //event.put("start", "-");
            //event.put("end", "-");
            //event.put("location", "-");
            //event.put("account", "-");
            event.put("subtitle", "-");
            eventsList.add(event);
        }

        ListAdapter adapter = new SimpleAdapter(mContext, eventsList, R.layout.list_item, new String[]{"title", "subtitle"}, new int[]{R.id.title, R.id.description});
        lv.setAdapter(adapter);
    }


    // Toast wrapper
    private void toast (String message) {
        Toast.makeText(this.mContext, message, Toast.LENGTH_SHORT).show();
    }

    // Convert a date to format
    private String dateToString (Calendar date) {
        return (new SimpleDateFormat("dd/MM/yyyy", Locale.US)).format(date.getTime());
    }
    private String dateToString (Calendar date, String pattern) {
        return (new SimpleDateFormat(pattern, Locale.US)).format(date.getTime());
    }



    /*
     * Widget active/deactivate state management
     */

    // On widget show
    private void onShow() {
        // If view loaded (and was inactive)
        if (this.mView != null && !this.isActive) {
            String now = this.dateToString(Calendar.getInstance());
            String shown = this.dateToString(Calendar.getInstance());
            // If not the correct view
            if (!shown.equals(now)) {
                // Refresh the view
                this.refreshView();
            }
        }

        // Save state
        this.isActive = true;
    }

    // On widget hide
    private void onHide() {
        // Save state
        this.isActive = false;
    }


    // Events for widget hide
    @Override
    public void onInactive(Bundle paramBundle) {
        super.onInactive(paramBundle);
        this.onHide();
    }
    @Override
    public void onPause() {
        super.onPause();
        this.onHide();
    }
    @Override
    public void onStop() {
        super.onStop();
        this.onHide();
    }

    // Events for widget show
    @Override
    public void onActive(Bundle paramBundle) {
        super.onActive(paramBundle);
        this.onShow();
    }
    @Override
    public void onResume() {
        super.onResume();
        this.onShow();
    }



    /*
     * Below where are unchanged functions that the widget should have
     */

    // Return the icon for this page, used when the page is disabled in the app list. In this case, the launcher icon is used
    @Override
    public Bitmap getWidgetIcon(Context paramContext) {
        return ((BitmapDrawable) this.mContext.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
    }


    // Return the launcher intent for this page. This might be used for the launcher as well when the page is disabled?
    @Override
    public Intent getWidgetIntent() {
        //Intent localIntent = new Intent();
        //localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //localIntent.setAction("android.intent.action.MAIN");
        //localIntent.addCategory("android.intent.category.LAUNCHER");
        //localIntent.setComponent(new ComponentName(this.mContext.getPackageName(), "com.huami.watch.deskclock.countdown.CountdownListActivity"));
        return new Intent();
    }


    // Return the title for this page, used when the page is disabled in the app list. In this case, the app name is used
    @Override
    public String getWidgetTitle(Context paramContext) {
        return this.mContext.getResources().getString(R.string.app_name);
    }


    // Save springboard host
    private ISpringBoardHostStub host = null;

    // Returns the springboard host
    public ISpringBoardHostStub getHost() {
        return this.host;
    }

    // Called when the page is loading and being bound to the host
    @Override
    public void onBindHost(ISpringBoardHostStub paramISpringBoardHostStub) {
        // Log.d(widget.TAG, "onBindHost");
        //Store host
        this.host = paramISpringBoardHostStub;
    }


    // Not sure what this does, can't find it being used anywhere. Best leave it alone
    @Override
    public void onReceiveDataFromProvider(int paramInt, Bundle paramBundle) {
        super.onReceiveDataFromProvider(paramInt, paramBundle);
    }


    // Called when the page is destroyed completely (in app mode). Same as the onDestroy method of an activity
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
