package com.easybee.calculator;

import java.text.ParseException;
import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    //Variables to be used in the class
    private Calendar calendar;
    private TextView calculated, startDate, endDate;
    private int year_x, month_x, day_x, year_y, month_y, day_y;
    private Button start, end, calculate;
    static final int START_ID = 0, END_ID = 1;
    static int GLOBAL_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing TextViews
        calculated = (TextView) findViewById(R.id.calculatedDate);
        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);

        //Initializing Buttons
        start = (Button) findViewById(R.id.start);
        end = (Button) findViewById(R.id.end);
        calculate = (Button) findViewById(R.id.calculate);

        calendar = Calendar.getInstance();

        year_y = year_x = calendar.get(Calendar.YEAR);
        month_y = month_x = calendar.get(Calendar.MONTH);
        day_y = day_x = calendar.get(Calendar.DAY_OF_MONTH);

        startDate.setText(String.format("%d/%d/%d",month_x,day_x,year_x));
        endDate.setText(String.format("%d/%d/%d", month_x, day_x, year_x));

        showDialog(START_ID);

        start.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GLOBAL_ID = START_ID;
                        showDialog(START_ID);
                    }
                }
        );

        end.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GLOBAL_ID = END_ID;
                        showDialog(END_ID);
                    }
                }
        );

        calculate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            Date birthDate = sdf.parse(String.format("%d/%d/%d",month_x,day_x,year_x)); //Yeh !! It's my date of birth :-)
                            Age age = calculateAge(birthDate);
                            calculated.setText(age.toString());
                        } catch (ParseException e) {
                            calculated.setText("Error!! Try again.");
                        }
                    }
                }
        );
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (GLOBAL_ID == START_ID) {
                year_x = year;
                month_x = monthOfYear;
                day_x = dayOfMonth;
                startDate.setText(String.format("%d/%d/%d",month_x,day_x,year_x));
            } else if (GLOBAL_ID == END_ID) {
                year_y = year;
                month_y = monthOfYear;
                day_y = dayOfMonth;
                endDate.setText(String.format("%d/%d/%d", month_y, day_y, year_y));
            }
        }
    };

    @Override
    protected Dialog onCreateDialog(int ID) {
        if (ID == 0){
            return new DatePickerDialog(this, datePickerListener, year_x, month_x, day_x);
        } else if (ID == 1){
            return new DatePickerDialog(this, datePickerListener, year_y, month_y, day_y);
        } else {
            return null;
        }
    }

    private Age calculateAge(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;

        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());

        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.set(year_y, month_y, day_y);
        //now.setTimeInMillis(currentTime);

        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;

        //Get difference between months
        months = currMonth - birthMonth;

        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }

        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }

        //Create new Age object
        return new Age(days, months, years);
    }
}
