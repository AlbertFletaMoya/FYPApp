package com.project.fypapp.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.project.fypapp.R;

import java.util.Calendar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class MonthYearPickerDialog extends DialogFragment {
        private final DatePickerDialog.OnDateSetListener listener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            Calendar cal = Calendar.getInstance();

            View dialog = inflater.inflate(R.layout.dialog_month_year_picker, null);
            final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);
            final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);

            monthPicker.setDisplayedValues(new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});

            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

            int year = cal.get(Calendar.YEAR);
            yearPicker.setMinValue(1950);
            yearPicker.setMaxValue(year);
            yearPicker.setValue(year);

            builder.setView(dialog)
                    // Add action buttons
                    .setPositiveButton(R.string.set, (dialog12, id) -> listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0))
                    .setNegativeButton(R.string.cancel, (dialog1, id) -> MonthYearPickerDialog.this.getDialog().cancel());
            return builder.create();
        }
}
