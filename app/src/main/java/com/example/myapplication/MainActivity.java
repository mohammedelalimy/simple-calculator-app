package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 100;

    // UI elements
    private TextView no1, no2, answer;
    private Button add, mul, div, sub, equal, historyButton;

    // History
    private ArrayList<String> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request external storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_EXTERNAL_STORAGE);
        }

        // Initialize UI elements
        no1 = findViewById(R.id.first_no);
        no2 = findViewById(R.id.second_no);
        add = findViewById(R.id.add);
        mul = findViewById(R.id.mul);
        div = findViewById(R.id.div);
        sub = findViewById(R.id.sub);
        equal = findViewById(R.id.equals);
        answer = findViewById(R.id.answer);
        historyButton = findViewById(R.id.history_button);

        // Set click listeners
        add.setOnClickListener(operationClickListener);
        sub.setOnClickListener(operationClickListener);
        mul.setOnClickListener(operationClickListener);
        div.setOnClickListener(operationClickListener);
        equal.setOnClickListener(equalClickListener);
        historyButton.setOnClickListener(historyButtonClickListener);
    }

    // Global variable to store the current operation
    private String currentOperation = "";

    // OnClickListener for arithmetic operations
    private final View.OnClickListener operationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Determine the operation based on which operation button was clicked
            if (v == add) {
                currentOperation = "addition";
            } else if (v == sub) {
                currentOperation = "subtraction";
            } else if (v == mul) {
                currentOperation = "multiplication";
            } else if (v == div) {
                currentOperation = "division";
            }

            // Add calculation to history
            addToHistory();
        }
    };

    // OnClickListener for equals button
    private final View.OnClickListener equalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Get operands
            String operand1 = no1.getText().toString();
            String operand2 = no2.getText().toString();

            // Check if both operands are filled
            if (operand1.isEmpty() || operand2.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both operands", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform the calculation
            double result = calculateResult(Double.parseDouble(operand1), Double.parseDouble(operand2));

            // Update the answer TextView with the result
            answer.setText(String.valueOf(result));

            // Add calculation to history
            addToHistory();
        }
    };

    // Method to calculate the result based on the operation
    private double calculateResult(double operand1, double operand2) {
        // Perform the operation based on the currentOperation
        switch (currentOperation) {
            case "addition":
                return operand1 + operand2;
            case "subtraction":
                return operand1 - operand2;
            case "multiplication":
                return operand1 * operand2;
            case "division":
                if (operand2 != 0) {
                    return operand1 / operand2;
                } else {
                    Toast.makeText(MainActivity.this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                    return 0; // Handle division by zero error
                }
            default:
                return 0; // Default return value
        }
    }


    // OnClickListener for history button
    private final View.OnClickListener historyButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Show history
            showHistory();
        }
    };

    // Add current calculation to history
    private void addToHistory() {
        // Get operands and operator
        String operand1 = no1.getText().toString();
        String operand2 = no2.getText().toString();
        String operator = ""; // Determine the operator used

        // Determine the operator based on which operation button was clicked
        if (add.isPressed()) {
            operator = "+";
        } else if (sub.isPressed()) {
            operator = "-";
        } else if (mul.isPressed()) {
            operator = "x";
        } else if (div.isPressed()) {
            operator = "/";
        }

        // Check if both operands and operator are filled
        if (!operand1.isEmpty() && !operand2.isEmpty() && !operator.isEmpty()) {
            // Perform the calculation to get the result
            double result = calculateResult(Double.parseDouble(operand1), Double.parseDouble(operand2));

            // Format the history entry including the result
            String historyEntry = operand1 + " " + operator + " " + operand2 + " = " + result;

            // Add to history list
            history.add(historyEntry);

            // Save history to external storage
            saveHistoryToStorage();
        }
    }

    // Save history to external storage
    private void saveHistoryToStorage() {
        // Check if external storage write permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, show a message or handle the situation accordingly
            Toast.makeText(this, "Permission denied, cannot save history", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with saving history to external storage
        // Define the file name and path
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, "calculator_history.txt");

        try {
            // Append history to file
            FileWriter writer = new FileWriter(file, true); // Append mode
            writer.append("\n"); // Add a newline separator before appending new entry
            writer.append(history.get(history.size() - 1)); // Append the latest history entry
            writer.flush();
            writer.close();
            Toast.makeText(this, "History saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Show history to the user
    private void showHistory() {
        // Create a StringBuilder to construct the history message
        StringBuilder historyMessage = new StringBuilder();
        for (String entry : history) {
            historyMessage.append(entry).append("\n"); // Append each history entry
        }

        // Convert the history message to CharSequence
        CharSequence historyText = historyMessage.toString();

        // Show the history in a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculation History");
        builder.setMessage(historyText); // Pass the CharSequence
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Check if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with app initialization
            } else {
                Toast.makeText(this, "Permission denied, cannot save history", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
