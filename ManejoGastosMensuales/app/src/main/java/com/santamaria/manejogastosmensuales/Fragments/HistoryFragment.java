package com.santamaria.manejogastosmensuales.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.R;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import io.realm.Realm;

public class HistoryFragment extends Fragment implements NumberPicker.OnValueChangeListener {


    public static final String YEAR_BUNDLE_EXTRA = "year";
    public static String MONTH_BUNDLE_EXTRA = "month";

    private String[] mShortMonths;
    private NumberPicker mMonthSpinner = null;
    private NumberPicker mYearSpinner = null;
    private TextView currentMonthSelected = null;
    private int month;
    private int year;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        currentMonthSelected = (TextView) view.findViewById(R.id.currentMonthSelected);
        mShortMonths = new DateFormatSymbols().getMonths();

        // month
        mMonthSpinner = (NumberPicker) view.findViewById(R.id.NumberPickerMonth);
        mMonthSpinner.setMinValue(0);
        mMonthSpinner.setMaxValue(12 - 1);
        mMonthSpinner.setDisplayedValues(mShortMonths);
        mMonthSpinner.setOnValueChangedListener(this);
        month = mMonthSpinner.getValue();

        // year
        year = Calendar.getInstance().get(Calendar.YEAR);
        mYearSpinner = (NumberPicker) view.findViewById(R.id.NumberPickerYear);
        mYearSpinner.setMinValue(2017);
        mYearSpinner.setMaxValue(year);
        mYearSpinner.setWrapSelectorWheel(false);
        mYearSpinner.setOnValueChangedListener(this);


        // set the spinner values
        mYearSpinner.setValue(Calendar.getInstance().get(Calendar.YEAR));
        mMonthSpinner.setValue(Calendar.getInstance().get(Calendar.MONTH));

        onValueChange(mMonthSpinner, 0, 0);

        return view;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {


        if (numberPicker.getId() == R.id.NumberPickerMonth) {
            month = numberPicker.getValue() + 1;
        } else {
            year = numberPicker.getValue();
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MONTH_BUNDLE_EXTRA, month);
        bundle.putInt(YEAR_BUNDLE_EXTRA, year);

        Fragment fragment = new HistoryItemFragment();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().
                beginTransaction().
                replace(R.id.contentFrame, fragment)
                .commit();

        if (month == (int) Calendar.getInstance().get(Calendar.MONTH) + 1
                && year == (int) Calendar.getInstance().get(Calendar.YEAR)) {
            currentMonthSelected.setVisibility(View.VISIBLE);
        } else {
            currentMonthSelected.setVisibility(View.INVISIBLE);
        }


    }

}
