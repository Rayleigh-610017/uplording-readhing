package com.example.fileapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "saved_data.txt";

    private EditText editText;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EdgeToEdgeのインセット対応
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText = findViewById(R.id.editText);
        textViewResult = findViewById(R.id.textViewResult);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonRead = findViewById(R.id.buttonRead);
        Button buttonAdd = findViewById(R.id.buttonAdd); // 追記ボタンの紐付け

        // 【保存ボタン】（上書き保存：MODE_PRIVATE）
        buttonSave.setOnClickListener(v -> {
            String textToSave = editText.getText().toString();
            if (textToSave.isEmpty()) {
                Toast.makeText(this, "保存する文字列を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }

            try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
                fos.write(textToSave.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(this, "ファイルを上書き保存しました", Toast.LENGTH_SHORT).show();
                editText.setText("");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "保存に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });

        // 【追記ボタン】（既存データの後ろに追加：MODE_APPEND）
        buttonAdd.setOnClickListener(v -> {
            String textToAdd = editText.getText().toString();
            if (textToAdd.isEmpty()) {
                Toast.makeText(this, "追記する文字列を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }

            // 改行コード（\n）を挟んで追記する
            String dataWithNewLine = textToAdd + "\n";

            try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND)) {
                fos.write(dataWithNewLine.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(this, "ファイルに追記しました", Toast.LENGTH_SHORT).show();
                editText.setText("");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "追記に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });

        // 【読み取りボタン】
        buttonRead.setOnClickListener(v -> {
            StringBuilder stringBuilder = new StringBuilder();
            try (FileInputStream fis = openFileInput(FILE_NAME);
                 InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                textViewResult.setText(stringBuilder.toString().trim());
                Toast.makeText(this, "ファイルを読み込みました", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "ファイルが存在しません", Toast.LENGTH_SHORT).show();
                textViewResult.setText("データがありません");
            }
        });
    }
}