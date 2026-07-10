package com.example.moderncalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private TextView tvExpression;
    private String currentInput = "0";
    private String operator = "";
    private double firstOperand = Double.NaN;
    private boolean isNewOp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        tvDisplay = findViewById(R.id.tvDisplay);
        tvExpression = findViewById(R.id.tvExpression);

        // Number and Dot buttons
        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener numberClickListener = v -> {
            MaterialButton button = (MaterialButton) v;
            String text = button.getText().toString();
            if (isNewOp) {
                currentInput = text.equals(".") ? "0." : text;
                isNewOp = false;
            } else {
                if (text.equals(".") && currentInput.contains(".")) return;
                currentInput += text;
            }
            updateDisplay();
        };

        for (int id : numberButtons) {
            View btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(numberClickListener);
        }

        // Operator buttons
        findViewById(R.id.btnPlus).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> setOperator("−"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> setOperator("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> setOperator("÷"));

        // Function buttons
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());
        findViewById(R.id.btnBackspace).setOnClickListener(v -> backspace());
        findViewById(R.id.btnPlusMinus).setOnClickListener(v -> toggleSign());
        findViewById(R.id.btnPercent).setOnClickListener(v -> applyPercent());

        // Long click on backspace to clear all (AC behavior)
        findViewById(R.id.btnBackspace).setOnLongClickListener(v -> {
            resetCalculator();
            updateDisplay();
            return true;
        });
    }

    private void setOperator(String op) {
        try {
            if (!Double.isNaN(firstOperand) && !isNewOp) {
                calculate();
            }
            firstOperand = Double.parseDouble(currentInput);
            operator = op;
            tvExpression.setText(formatResult(firstOperand) + " " + operator);
            isNewOp = true;
        } catch (NumberFormatException ignored) {}
    }

    private void calculate() {
        if (Double.isNaN(firstOperand) || operator.isEmpty()) return;

        try {
            double secondOperand = Double.parseDouble(currentInput);
            double result = 0;

            switch (operator) {
                case "+": result = firstOperand + secondOperand; break;
                case "−": result = firstOperand - secondOperand; break;
                case "×": result = firstOperand * secondOperand; break;
                case "÷":
                    if (secondOperand != 0) {
                        result = firstOperand / secondOperand;
                    } else {
                        tvDisplay.setText("Error");
                        resetCalculator();
                        return;
                    }
                    break;
            }

            tvExpression.setText(formatResult(firstOperand) + " " + operator + " " + formatResult(secondOperand));
            currentInput = formatResult(result);
            firstOperand = Double.NaN;
            operator = "";
            isNewOp = true;
            updateDisplay();
        } catch (NumberFormatException ignored) {}
    }

    private void backspace() {
        if (isNewOp) {
            currentInput = "0";
            tvExpression.setText("");
        } else if (currentInput.length() > 1) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            if (currentInput.equals("-")) currentInput = "0";
        } else {
            currentInput = "0";
            isNewOp = true;
        }
        updateDisplay();
    }

    private void toggleSign() {
        if (currentInput.equals("0")) return;
        if (currentInput.startsWith("-")) {
            currentInput = currentInput.substring(1);
        } else {
            currentInput = "-" + currentInput;
        }
        updateDisplay();
    }

    private void applyPercent() {
        try {
            double value = Double.parseDouble(currentInput) / 100.0;
            currentInput = formatResult(value);
            isNewOp = true;
            updateDisplay();
        } catch (NumberFormatException ignored) {}
    }

    private void updateDisplay() {
        tvDisplay.setText(currentInput);
    }

    private void resetCalculator() {
        currentInput = "0";
        firstOperand = Double.NaN;
        operator = "";
        isNewOp = true;
        tvExpression.setText("");
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return new DecimalFormat("0.########").format(result);
        }
    }
}
