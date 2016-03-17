package com.easybee.calculator;

import java.text.ParseException;
import java.util.Calendar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Variables to be used in the class
    private TextView calculatedDate;
    private Button calculateButton;
    private DatePicker startDate, endDate;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);

        //Initializing TextViews
        calculatedDate = (TextView) findViewById(R.id.calculatedDate);

        //Initializing Buttons
        calculateButton = (Button) findViewById(R.id.calculate);

        //Initializing DatePickers
        startDate = (DatePicker) findViewById(R.id.startDate);
        endDate = (DatePicker) findViewById(R.id.endDate);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calculate();
            }
        });

        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.bg, options);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        mImageView.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.bg, size.x, 100));*/
    }

    private void calculate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date birthDate = sdf.parse(String.format("%d/%d/%d",startDate.getMonth(),startDate.getDayOfMonth(),startDate.getYear())); //Yeh !! It's my date of birth :-)
            Age age = calculateAge(birthDate);
            calculatedDate.setText(age.toString());
        } catch (ParseException e) {
            calculatedDate.setText("Error!! Try again.");
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
        now.set(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth());
        //now.setTimeInMillis(currentTime);

        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openWebsite(MenuItem item){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.myeasybee.com"));
        startActivity(browserIntent);
    }

    public void refresh(MenuItem item){
        Calendar cal1 = Calendar.getInstance();

        startDate.updateDate(cal1.get(Calendar.YEAR),cal1.get(Calendar.MONTH),cal1.get(Calendar.DAY_OF_MONTH));
        endDate.updateDate(cal1.get(Calendar.YEAR),cal1.get(Calendar.MONTH),cal1.get(Calendar.DAY_OF_MONTH));

        calculatedDate.setText("0 Years, 0 Months, 0 Days");
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
