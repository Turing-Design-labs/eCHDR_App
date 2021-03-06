package com.echdr.android.echdrapp.ui.enrollment_form;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.FormField;

import org.hisp.dhis.android.core.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class OptionSetFieldHolder extends FieldHolder {

    private final Spinner spinner;
    private List<Option> optionList;
    private String fieldUid;
    private String fieldCurrentValue;

    OptionSetFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        fieldUid = fieldItem.getUid();
        fieldCurrentValue = fieldItem.getValue();

        setUpSpinner(fieldItem.getOptionSetUid());

        if (fieldCurrentValue != null)
            setInitialValue(fieldCurrentValue);
    }

    private void setUpSpinner(String optionSetUid) {
        optionList = Sdk.d2().optionModule().options().byOptionSetUid().eq(optionSetUid).blockingGet();
        List<String> optionListNames = new ArrayList<>();
        optionListNames.add(label.getText().toString());
        for (Option option : optionList) optionListNames.add(option.displayName());
        spinner.setAdapter(new ArrayAdapter<>(itemView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, optionListNames));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    if (fieldCurrentValue == null || !Objects.equals(fieldCurrentValue, optionList.get(i - 1).code()))
                        valueSavedListener.onValueSaved(fieldUid, optionList.get(i - 1).code());
                } else if (fieldCurrentValue != null)
                    valueSavedListener.onValueSaved(fieldUid, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setInitialValue(String selectedCode) {
        for (int i = 0; i < optionList.size(); i++)
            if (Objects.equals(optionList.get(i).code(), selectedCode))
                spinner.setSelection(i + 1);
    }

}
