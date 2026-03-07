package com.aakash.travelcompanion;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerCategory, spinnerFrom, spinnerTo;
    EditText editTextValue;
    Button btnConvert;
    TextView textViewResult;

    String[] currencyUnits = {"USD", "AUD", "EUR", "JPY", "GBP"};
    String[] fuelUnits = {"MPG", "km/L", "Gallons (US)", "Liters", "Nautical Miles", "Kilometers"};
    String[] tempUnits = {"Celsius", "Fahrenheit", "Kelvin"};
    String[] categories = {"💰 Currency", "⛽ Fuel & Distance", "🌡 Temperature"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        editTextValue = findViewById(R.id.editTextValue);
        btnConvert = findViewById(R.id.btnConvert);
        textViewResult = findViewById(R.id.textViewResult);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(catAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateUnitSpinners(0);
        btnConvert.setOnClickListener(v -> performConversion());
    }

    void updateUnitSpinners(int categoryIndex) {
        String[] units;
        if (categoryIndex == 0) units = currencyUnits;
        else if (categoryIndex == 1) units = fuelUnits;
        else units = tempUnits;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, units);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    void performConversion() {
        String inputStr = editTextValue.getText().toString().trim();

        if (inputStr.isEmpty()) {
            Toast.makeText(this, "⚠ Please enter a value!", Toast.LENGTH_SHORT).show();
            return;
        }

        double inputValue;
        try {
            inputValue = Double.parseDouble(inputStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "⚠ Please enter a valid number!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();
        int category = spinnerCategory.getSelectedItemPosition();

        if (fromUnit.equals(toUnit)) {
            textViewResult.setText("Same unit: " + inputValue + " " + fromUnit);
            Toast.makeText(this, "ℹ Same unit selected — no conversion needed.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category == 1 && inputValue < 0) {
            Toast.makeText(this, "⚠ Fuel/Distance values cannot be negative!", Toast.LENGTH_SHORT).show();
            return;
        }

        double result = 0;
        if (category == 0) {
            result = convertCurrency(inputValue, fromUnit, toUnit);
        } else if (category == 1) {
            result = convertFuel(inputValue, fromUnit, toUnit);
        } else {
            result = convertTemperature(inputValue, fromUnit, toUnit);
        }

        textViewResult.setText(String.format("%.4f %s", result, toUnit));
    }

    double convertCurrency(double value, String from, String to) {
        double usd = toUSD(value, from);
        return fromUSD(usd, to);
    }

    double toUSD(double value, String currency) {
        switch (currency) {
            case "USD": return value;
            case "AUD": return value / 1.55;
            case "EUR": return value / 0.92;
            case "JPY": return value / 148.50;
            case "GBP": return value / 0.78;
            default: return value;
        }
    }

    double fromUSD(double usd, String currency) {
        switch (currency) {
            case "USD": return usd;
            case "AUD": return usd * 1.55;
            case "EUR": return usd * 0.92;
            case "JPY": return usd * 148.50;
            case "GBP": return usd * 0.78;
            default: return usd;
        }
    }

    double convertFuel(double value, String from, String to) {
        if ((from.equals("MPG") || from.equals("km/L")) &&
                (to.equals("MPG") || to.equals("km/L"))) {
            double kmL = from.equals("MPG") ? value * 0.425 : value;
            return to.equals("MPG") ? kmL / 0.425 : kmL;
        }
        if ((from.equals("Gallons (US)") || from.equals("Liters")) &&
                (to.equals("Gallons (US)") || to.equals("Liters"))) {
            double liters = from.equals("Gallons (US)") ? value * 3.785 : value;
            return to.equals("Gallons (US)") ? liters / 3.785 : liters;
        }
        if ((from.equals("Nautical Miles") || from.equals("Kilometers")) &&
                (to.equals("Nautical Miles") || to.equals("Kilometers"))) {
            double km = from.equals("Nautical Miles") ? value * 1.852 : value;
            return to.equals("Nautical Miles") ? km / 1.852 : km;
        }
        Toast.makeText(this, "⚠ These units are not compatible!", Toast.LENGTH_LONG).show();
        return 0;
    }

    double convertTemperature(double value, String from, String to) {
        double celsius;
        switch (from) {
            case "Celsius":    celsius = value; break;
            case "Fahrenheit": celsius = (value - 32) / 1.8; break;
            case "Kelvin":     celsius = value - 273.15; break;
            default:           celsius = value;
        }
        switch (to) {
            case "Celsius":    return celsius;
            case "Fahrenheit": return (celsius * 1.8) + 32;
            case "Kelvin":     return celsius + 273.15;
            default:           return celsius;
        }
    }
}