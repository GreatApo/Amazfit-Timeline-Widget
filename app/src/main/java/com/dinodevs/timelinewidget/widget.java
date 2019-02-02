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
import android.view.Gravity;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import clc.sliteplugin.flowboard.AbstractPlugin;
import clc.sliteplugin.flowboard.ISpringBoardHostStub;

public class widget extends AbstractPlugin {

    // Tag for logging purposes.
    private static String TAG = "TimelineWidget";
    // Version
    private String version = "n/a";

    // Activity variables
    private boolean isActive = false;
    private Context mContext;
    private View mView;

    private ListView lv;
    private ArrayList<HashMap<String, String>> eventsList;

    private long next_event;
    private String calendarEvents;
    private String header_pattern;
    private String time_pattern;

    // Constants
    private static final String HEADER_PATTERN_12H = "hh:mm a\nEEEE, d MMMM";
    private static final String HEADER_PATTERN_24H = "HH:mm\nEEEE, d MMMM";
    private static final String ELEMENT_PATTERN = "EEEE, d MMMM";
    private static final String TIME_PATTERN_12H = "hh:mm a";
    private static final String TIME_PATTERN_24H = "HH:mm";
    private static final String DATE_PATTERN = "dd/MM/yyyy";


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
        refresh_time();

        // Calendar Events Data
        eventsList = new ArrayList<>();
        lv = (ListView) this.mView.findViewById(R.id.list);

        loadCalendarEvents();
    }

    // Attach listeners
    @SuppressLint("ClickableViewAccessibility")
    private void initListeners(){
        // About button event
        TextView time = this.mView.findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh_time();
                widget.this.toast("Timeline Widget v" + widget.this.version + " by GreatApo");
            }
        });
        // Refresh events
        time.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                refresh_time();
                loadCalendarEvents();
                widget.this.toast("Events were refreshed");
                return true;
            }
        });
        // Scroll to top
        TextView top = this.mView.findViewById(R.id.backToTop);
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView list = widget.this.mView.findViewById(R.id.list);
                list.setSelectionAfterHeaderView();
                //widget.this.toast("▲ Top");
            }
        });
    }

    private void refresh_time(){
        is24h();
        TextView time = this.mView.findViewById(R.id.time);
        time.setText( dateToString(Calendar.getInstance(),header_pattern) );
    }

    boolean is24h;
    private void is24h(){
        is24h = Settings.System.getString(mContext.getContentResolver(), "time_12_24").equals("24");
        Log.d(TAG, "Timeline init is24h: " + is24h);

        if (is24h) {
            header_pattern = HEADER_PATTERN_24H;
            time_pattern = TIME_PATTERN_24H;
        } else {
            header_pattern = HEADER_PATTERN_12H;
            time_pattern = TIME_PATTERN_12H;
        }
    }

    private void loadCalendarEvents() {
        eventsList = new ArrayList<>();
        next_event = 0;
        is24h();

        // Load data
        calendarEvents = Settings.System.getString(mContext.getContentResolver(), "CustomCalendarData");

        if(calendarEvents !=null && !calendarEvents.isEmpty() ) {
            try {
                // Check if correct form of JSON
                JSONObject json_data = new JSONObject(calendarEvents);

                // If there are events
                if (json_data.has("events")) {
                    int event_number = json_data.getJSONArray("events").length();

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10); // Show only future events + 10 minutes old
                    long current_time = calendar.getTimeInMillis();
                    String current_loop_date = "";

                    // Get data
                    for (int i = 0; i < event_number; i++) {
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

                        if (!data.getString(2).equals("") && !data.getString(2).equals("null")) {
                            calendar.setTimeInMillis(Long.parseLong(data.getString(2)));

                            if (current_time > calendar.getTimeInMillis()) {
                                // Event expired, go to next
                                continue;
                            }
                            if (next_event == 0) // Hence this is the next event
                                next_event = calendar.getTimeInMillis();

                            start = dateToString(calendar, time_pattern);

                            // Insert day separator, or not :P
                            if (!current_loop_date.equals(dateToString(calendar, ELEMENT_PATTERN))) {
                                current_loop_date = dateToString(calendar, ELEMENT_PATTERN);
                                // Is it today?
                                if (current_loop_date.equals(dateToString(Calendar.getInstance(), ELEMENT_PATTERN))) {
                                    current_loop_date = mContext.getResources().getString(R.string.today);
                                }
                                HashMap<String, String> date_elem = new HashMap<>();
                                date_elem.put("title", "");
                                date_elem.put("subtitle", current_loop_date);
                                date_elem.put("dot", "");
                                eventsList.add(date_elem);
                            }
                        } else {
                            // Event has no date, go to next
                            continue;
                        }

                        // No end
                        if (!data.getString(3).equals("") && !data.getString(3).equals("null")) {
                            calendar.setTimeInMillis(Long.parseLong(data.getString(3)));
                            end = " - " + dateToString(calendar, time_pattern);
                        }

                        // All day events
                        if ((start.startsWith("00") || start.startsWith("12")) && data.getString(3).equals("null")) {
                            start = mContext.getResources().getString(R.string.all_day);
                            end = "";
                        }

                        // Location
                        if (!data.getString(4).equals("") && !data.getString(4).equals("null")) {
                            location = "\n@ " + data.getString(4);
                        }

                        event.put("subtitle", start + end + location);
                        event.put("dot", mContext.getResources().getString(R.string.bull));
                        // adding events to events list
                        eventsList.add(event);
                    }
                }
            } catch (JSONException e) {
                //default
                HashMap<String, String> event = new HashMap<>();
                event.put("title", mContext.getResources().getString(R.string.no_events));
                //event.put("description", "-");
                //event.put("start", "-");
                //event.put("end", "-");
                //event.put("location", "-");
                //event.put("account", "-");
                event.put("subtitle", "-");
                event.put("dot", "");
                eventsList.add(event);
            }
        }

        if(eventsList.isEmpty()){
            HashMap<String, String> elem = new HashMap<>();
            elem.put("title", "\n"+mContext.getResources().getString(R.string.no_events));
            elem.put("subtitle", mContext.getResources().getString(R.string.no_events_description));
            elem.put("dot", "" );
            eventsList.add(elem);
        }

        ListAdapter adapter = new SimpleAdapter(mContext, eventsList, R.layout.list_item, new String[]{"title", "subtitle", "dot"}, new int[]{R.id.title, R.id.description, R.id.dot});
        lv.setAdapter(adapter);
    }


    // Toast wrapper
    private void toast (String message) {
        Toast toast = Toast.makeText(this.mContext, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    // Convert a date to format
    private String dateToString (Calendar date) {
        return (new SimpleDateFormat(DATE_PATTERN, Locale.US)).format(date.getTime());
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
            // If an event expired OR new events OR changed 12/24h
            if ( next_event+10*1000 < Calendar.getInstance().getTimeInMillis() || !calendarEvents.equals(Settings.System.getString(mContext.getContentResolver(), "CustomCalendarData")) || is24h != Settings.System.getString(mContext.getContentResolver(), "time_12_24").equals("24") ) {
                // Refresh timeline
                loadCalendarEvents();
            }
            refresh_time();
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