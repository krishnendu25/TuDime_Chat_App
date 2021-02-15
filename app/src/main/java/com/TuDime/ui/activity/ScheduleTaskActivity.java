package com.TuDime.ui.activity;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.TuDime.R;
import com.TuDime.db.QbUsersDbManager;
import com.TuDime.services.OnAlarmReceiver;
import com.TuDime.ui.Model.Calender_Model;
import com.TuDime.ui.Model.EventDecorator;
import com.TuDime.ui.adapter.Event_Show_adapter;
import com.TuDime.ui.adapter.listeners.Clickback;

import org.json.JSONArray;
import org.json.JSONObject;

import com.TuDime.Prefrences.SharedPrefsHelper;

public class ScheduleTaskActivity extends BaseActivity implements Clickback {

    private MaterialCalendarView Calender_view;
    private ListView Event_List;
    private QbUsersDbManager dbManager = QbUsersDbManager.getInstance(this);
    private ArrayList<String> Date_Selector = new ArrayList<>();
    private Calendar currentCalendar;
    private DateTimeFormatter FORMATTER;
    private ArrayList<CalendarDay> dates = new ArrayList<>();
    private ArrayList<Calender_Model> show_event_list = new ArrayList<>();
    private Event_Show_adapter event_show_adapter=null;
    private PendingIntent pi;
    private ImageView back;
    ArrayList<String> Date_Formater = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_schedule_task);
        hideActionbar();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             FORMATTER = DateTimeFormatter.ofPattern("dd/M/yyyy");
        }


        Instantiation();

        Intent i = new Intent(ScheduleTaskActivity.this, OnAlarmReceiver.class);
        pi = PendingIntent.getBroadcast(ScheduleTaskActivity.this, 0, i,
                PendingIntent.FLAG_ONE_SHOT);

        Set_Data_calender(dbManager.get_event_by_user(SharedPrefsHelper.getInstance().getQbUser().getLogin()));

        /*Calender_view.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

            }


        });*/
       //
        Calender_view.setOnDateLongClickListener(new OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {

                String s=   date.getDay()+"/"+date.getMonth()+"/"+date.getYear();
                Show_Event_Dialog( date.getDay()+"/"+date.getMonth()+"/"+date.getYear());

            }
        });


        Calender_view.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected)
            {

                String s=   date.getDay()+"/"+date.getMonth()+"/"+date.getYear();

                Cursor cursor = dbManager.get_event_by_date(s,SharedPrefsHelper.getInstance().getQbUser().getLogin());

                if (cursor.getCount()!=0)
                {
                    show_event_list.clear();
                    while (cursor.moveToNext())
                    {
                        Calender_Model calender_model = new Calender_Model();
                        calender_model.setDB_EVENT_DATE(cursor.getString(5));
                        calender_model.setDB_EVENT_DESC(cursor.getString(3));
                        calender_model.setDB_EVENT_NAME(cursor.getString(2));
                        calender_model.setDB_EVENT_TIME(cursor.getString(4));
                        calender_model.setDB_EVENT_NOTIFIATION(cursor.getString(6));
                        calender_model.setDB_QB_USER_ID(cursor.getString(1));
                        calender_model.setDB_COLUMN_ID(cursor.getInt(0));
                        show_event_list.add(calender_model);
                    }
                    event_show_adapter = new Event_Show_adapter(ScheduleTaskActivity.this,show_event_list);
                    Event_List.setAdapter(event_show_adapter);
                    event_show_adapter.notifyDataSetChanged();







                }else
                {
                    show_event_list.clear();
                    if (event_show_adapter!=null)
                    {
                        event_show_adapter.notifyDataSetChanged();
                    }

                    Calender_view.removeDecorators();
                    Set_Data_calender(dbManager.get_event_by_user(SharedPrefsHelper.getInstance().getQbUser().getLogin()));
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void Set_Data_calender(Cursor event_by_user) {
        if (event_by_user.getCount() != 0) {
            Date_Selector.clear();
            while (event_by_user.moveToNext()) {
                Date_Selector.add(event_by_user.getString(5));
            }
            dates.clear();
            for (int i=0 ; i<Date_Selector.size() ; i++)
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");

                try {


                    Date date = simpleDateFormat.parse(Date_Selector.get(i).toString());

                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    int year = calendar.get(Calendar.YEAR);
//Add one to month {0 - 11}
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                CalendarDay calendarDay=    CalendarDay.from(year,month,day);

                   dates.add(calendarDay);
                } catch (ParseException e) {
                    e.printStackTrace();
                }



            }
            Calender_view.addDecorator(new EventDecorator(Color.GREEN, dates));


        }
    }

    private void Show_Event_Dialog(String Date)
    {

        final Dialog popUpActivate = new Dialog(this);
        popUpActivate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUpActivate.setContentView(R.layout.set_event_calender);
        popUpActivate.show();

        TextView enter_edt_date = (TextView) popUpActivate.findViewById(R.id.enter_edt_date);
        TextView time = (TextView) popUpActivate.findViewById(R.id.enter_edt_time);
        EditText desc = (EditText) popUpActivate.findViewById(R.id.enter_edt_desc);
        EditText event_name_edit = (EditText) popUpActivate.findViewById(R.id.event_name_edit);
        enter_edt_date.setText(Date);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(ScheduleTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String timing = (selectedHour > 12 ? (selectedHour - 12) : selectedHour) + ":" + selectedMinute + (selectedHour > 12 ? " PM" : " AM");
                        time.setText(timing);

                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        popUpActivate.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpActivate.dismiss();
            }
        });

        popUpActivate.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calender_Model calender_model = new Calender_Model();
                calender_model.setDB_EVENT_DATE(enter_edt_date.getText().toString());
                calender_model.setDB_EVENT_DESC(desc.getText().toString().trim());
                calender_model.setDB_EVENT_NAME(event_name_edit.getText().toString().trim());
                calender_model.setDB_EVENT_TIME(time.getText().toString().trim());
                calender_model.setDB_EVENT_NOTIFIATION("ACTIVE");
                calender_model.setDB_QB_USER_ID(SharedPrefsHelper.getInstance().getQbUser().getLogin());
              boolean result=  dbManager.insertDataCAL(calender_model);
              if (result)
              {
                  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm ss");

                  Date date = new Date();
                  try {
                      date = simpleDateFormat.parse(enter_edt_date.getText().toString()+" "+time.getText().toString().trim());
                  } catch (ParseException e) {
                      e.printStackTrace();
                  }
                  Toast.makeText(getApplicationContext(),"Schedule Task Set",Toast.LENGTH_LONG).show();
                  popUpActivate.dismiss();
                  Set_Data_calender(dbManager.get_event_by_user(SharedPrefsHelper.getInstance().getQbUser().getLogin()));
                  try {
                      hideSoftKeypad(enter_edt_date);
                      createNotification (date.getTime(),event_name_edit.getText().toString().trim());
                  }catch (Exception e)
                  {}
              }else
              {
                  Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
              }

            }
        });




    }

    private void Instantiation() {
        Date_Formater.clear();
        Date_Formater.add("MM-DD-YYYY");
        Date_Formater.add("DD-MM-YYYY");
        Date_Formater.add("YYYY-MM-DD");



        Calender_view = findViewById(R.id.Calender_view);
        //Initialize calendar with date


        Event_List = (ListView) findViewById(R.id.Event_List);
       /* Event_List.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));*/
        back = (ImageView) findViewById(R.id.back);


    }


    @Override
    public void delete_position(int pos)
    {
        if (show_event_list.size()>0)
        {

            dbManager.Delete_Event(show_event_list.get(pos).getDB_COLUMN_ID());
            show_event_list.remove(pos);
            if (event_show_adapter!=null)
            {
                event_show_adapter.notifyDataSetChanged();
                Calender_view.removeDecorators();

                Set_Data_calender(dbManager.get_event_by_user(SharedPrefsHelper.getInstance().getQbUser().getLogin()));

            }

        }

    }
    public void createNotification(long time, String event_name) {


        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do You Want To Set This Reminder In Your System Calender?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", time);
                        intent.putExtra("allDay", true);
                        intent.putExtra("rrule", "FREQ=DAILY");
                        intent.putExtra("endTime", time+60*60*1000);
                        intent.putExtra("title", event_name);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();




    }


    void Show_Date_Fomatter_Popup()
    {

    }




    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }
}
