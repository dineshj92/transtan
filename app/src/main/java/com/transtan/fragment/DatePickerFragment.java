package com.transtan.fragment;

/**
 * Created by djayakum on 3/19/2018.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import com.transtan.R;

import java.util.Calendar;

/**
 * Created by jahid on 12/10/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        EditText dob= (EditText) getActivity().findViewById(R.id.userdob);
        EditText passwordDob= (EditText) getActivity().findViewById(R.id.downloadPassword);
        if(dob!=null) {
            dob.setText((day < 10 ? "0" + day : day) + "." + (month < 9 ? "0" + (month + 1) : (month + 1)) + "." + year);
        } else {
            passwordDob.setText((day < 10 ? "0" + day : day) + "." + (month < 9 ? "0" + (month + 1) : (month + 1)) + "." + year);
        }

    }
}