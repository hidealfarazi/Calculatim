package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;

    // Buffer ekspresi input kalkulator (misal: "10 + 5 * 2")
    private final StringBuilder expression = new StringBuilder();
    private boolean isResultDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
    }

    private void initViews() {
        tvResult = findViewById(R.id.tvResult);

        // Setup semua tombol angka (0 sampai 9)
        int[] numButtonIds = {
            R.id.btnNum0,
            R.id.btnNum1,
            R.id.btnNum2,
            R.id.btnNum3,
            R.id.btnNum4,
            R.id.btnNum5,
            R.id.btnNum6,
            R.id.btnNum7,
            R.id.btnNum8,
            R.id.btnNum9
        };

        for (int id : numButtonIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> onNumberClick(btn.getText().toString().trim()));
            }
        }

        // Tombol Operasi
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSubtract = findViewById(R.id.btnSubtract);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide = findViewById(R.id.btnDivide);

        if (btnAdd != null) btnAdd.setOnClickListener(v -> onOperatorClick("+"));
        if (btnSubtract != null) btnSubtract.setOnClickListener(v -> onOperatorClick("-"));
        if (btnMultiply != null) btnMultiply.setOnClickListener(v -> onOperatorClick("*"));
        if (btnDivide != null) btnDivide.setOnClickListener(v -> onOperatorClick("/"));

        // Tombol Kontrol
        Button btnClear = findViewById(R.id.btnClear);
        Button btnEnter = findViewById(R.id.btnEnter);

        if (btnClear != null) btnClear.setOnClickListener(v -> onClearClick());
        if (btnEnter != null) btnEnter.setOnClickListener(v -> onEnterClick());
    }

    private void onNumberClick(String digit) {
        // Jika sedang menampilkan hasil perhitungan sebelumnya dan menekan angka baru,
        // mulai ekspresi perhitungan baru
        if (isResultDisplayed) {
            expression.setLength(0);
            isResultDisplayed = false;
        }

        if (expression.length() == 0) {
            expression.append(digit);
        } else {
            String current = expression.toString();
            // Hindari angka 0 berulang di awal bilangan (misal: "00" atau "+ 00")
            if (current.equals("0")) {
                if (digit.equals("0")) {
                    return;
                }
                expression.setLength(0);
                expression.append(digit);
            } else if (current.endsWith(" 0")) {
                if (digit.equals("0")) {
                    return;
                }
                expression.deleteCharAt(expression.length() - 1);
                expression.append(digit);
            } else {
                expression.append(digit);
            }
        }

        updateDisplay();
    }

    private void onOperatorClick(String op) {
        // Jika baru selesai menghitung hasil, operator akan melanjutkan dari hasil tersebut
        if (isResultDisplayed) {
            isResultDisplayed = false;
        }

        // Jika belum ada angka sama sekali, mulai dari 0
        if (expression.length() == 0) {
            expression.append("0").append(" ").append(op).append(" ");
            updateDisplay();
            return;
        }

        String current = expression.toString();
        // Jika operator terakhir ditekan berurutan, ganti dengan operator terbaru
        if (endsWithOperator(current)) {
            expression.delete(expression.length() - 3, expression.length());
        }

        expression.append(" ").append(op).append(" ");
        updateDisplay();
    }

    private void onClearClick() {
        expression.setLength(0);
        isResultDisplayed = false;
        tvResult.setText("0");
    }

    private void onDeleteClick() {
        if (isResultDisplayed) {
            onClearClick();
            return;
        }

        if (expression.length() == 0) {
            return;
        }

        String current = expression.toString();
        if (endsWithOperator(current)) {
            // Hapus operator beserta spasi (" <op> ")
            expression.delete(expression.length() - 3, expression.length());
        } else {
            // Hapus 1 karakter digit
            expression.deleteCharAt(expression.length() - 1);
        }

        updateDisplay();
    }

    private void onEnterClick() {
        if (expression.length() == 0) {
            openSecondActivity("0", "Nilai: 0");
            return;
        }

        String expr = expression.toString().trim();

        // Validasi jika operator berada di akhir tanpa angka lanjutan
        if (endsWithOperator(expression.toString())) {
            Toast.makeText(this, "Masukkan angka berikutnya terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] tokens = expr.split("\\s+");
        if (tokens.length == 1) {
            openSecondActivity(tokens[0], "Nilai: " + tokens[0]);
            return;
        }

        // Kalkulasi ketat dari kiri ke kanan (tanpa prioritas operator)
        Double result = null;
        try {
            result = Double.parseDouble(tokens[0]);

            for (int i = 1; i < tokens.length; i += 2) {
                String op = tokens[i];
                if (i + 1 >= tokens.length) break;

                double nextNum = Double.parseDouble(tokens[i + 1]);

                switch (op) {
                    case "+":
                        result = result + nextNum;
                        break;
                    case "-":
                        result = result - nextNum;
                        break;
                    case "*":
                        result = result * nextNum;
                        break;
                    case "/":
                        if (nextNum == 0) {
                            Toast.makeText(this, "Tidak dapat membagi dengan nol", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        result = result / nextNum;
                        break;
                    default:
                        break;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format angka tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (result == null) {
            return;
        }

        String resultStr = formatNumber(result);
        String detailStr = expr + " = " + resultStr;

        // Simpan hasil ke ekspresi agar pengguna bisa melihat dan melanjutkan operasi
        expression.setLength(0);
        expression.append(resultStr);
        isResultDisplayed = true;
        tvResult.setText(resultStr);

        openSecondActivity(resultStr, detailStr);
    }

    private boolean endsWithOperator(String s) {
        return s.endsWith(" + ") || s.endsWith(" - ") || s.endsWith(" * ") || s.endsWith(" / ");
    }

    private void updateDisplay() {
        if (expression.length() == 0) {
            tvResult.setText("0");
        } else {
            tvResult.setText(expression.toString());
        }
    }

    private void openSecondActivity(String result, String detail) {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("EXTRA_RESULT", result);
        intent.putExtra("EXTRA_DETAIL", detail);
        startActivity(intent);
    }

    private String formatNumber(double number) {
        if (Double.isInfinite(number) || Double.isNaN(number)) {
            return "Error";
        }
        if (number == (long) number) {
            return String.valueOf((long) number);
        } else {
            DecimalFormat df = new DecimalFormat("#.########", DecimalFormatSymbols.getInstance(Locale.US));
            return df.format(number);
        }
    }
}
