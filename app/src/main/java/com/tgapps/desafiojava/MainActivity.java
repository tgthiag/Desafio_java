package com.tgapps.desafiojava;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tgapps.desafiojava.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();

        ArrayList<Integer> numList = new ArrayList<>();

//        BUTTONS HIDE AND SHOW LAYOUT
        binding.bt1.setOnClickListener(v -> {
            hideView(binding.containerNumeros);
        });
        binding.bt2.setOnClickListener(v -> {
            hideView(binding.containerCep);
        });
        binding.bt3.setOnClickListener(v -> {
            hideView(binding.containerPerfect);
        });
        binding.bt4.setOnClickListener(v -> {
            hideView(binding.containerTabuada);
        });

//        1-ORDER AND SAVE NUMBERS
        AtomicReference<Boolean> sent = new AtomicReference<>(false);
        binding.btNumEnviar.setOnClickListener(v -> {
            if (sent.get()) {
                numList.removeAll(numList);
                sent.set(false);
            }
            if (!binding.editNumeros.getText().toString().equals("")) {
                int numText = Integer.parseInt(binding.editNumeros.getText().toString());
                numList.add(numText);
                Collections.sort(numList);
                binding.numResultado.setText(numList.toString().replace("[", "").replace("]", ""));
                binding.editNumeros.setText("");
            }
        });

        binding.btNumSalvar.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(this)) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
                }
            }
            String stringToDownload = "{'value' : '" + numList.toString().replace("[", "").replace("]", "") + "'}";
            String fileName = "meus_numeros.txt";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(stringToDownload.getBytes());
                sent.set(true);
                binding.numResultado.setText("Arquivo salvo em: " + file);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

//        2-CEP API
        binding.btCepEnviar.setOnClickListener(v -> {
            startCep();
        });

//      3-PERFECT NUMBER CALC
        binding.btPerfectEnviar.setOnClickListener(v -> {
            perfectNumber(Integer.parseInt(binding.editPerfect.getText().toString()));
        });

//      4-ARITHMETIC TABLE
        Spinner staticSpinner = binding.spinnerTabuada;
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.operators_array, android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staticSpinner.setAdapter(staticAdapter);
        binding.btTabuadaEnviar.setOnClickListener(v -> {
            tabuada(Integer.parseInt(binding.editTabuada.getText().toString()));
        });
    }


    private void hideView(View v) {
        binding.containerNumeros.setVisibility(View.GONE);
        binding.containerCep.setVisibility(View.GONE);
        binding.containerPerfect.setVisibility(View.GONE);
        binding.containerTabuada.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
    }

    private void startCep() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cep = binding.editCep.getText().toString();
                Endereco endereco = null;
                if (!binding.editCep.getText().toString().equals("")) {
                    try {
                        endereco = ServicoDeCep.buscaEnderecoPelo(cep);
                        Endereco finalEndereco = endereco;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.cepResultado.setText(MessageFormat.format("{0},\n{1},\n{2}-{3}", finalEndereco.getLogradouro(), finalEndereco.getBairro(), finalEndereco.getLocalidade(), finalEndereco.getUf()));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.cepResultado.setText("Verifique sua conexão com a internet ou se o CEP digitado está correto.");
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.cepResultado.setText("Tente novamente");
                        }
                    });
                }
            }
        }).start();
    }

    private void perfectNumber(int number) {
        int temp = 0;
        for (int i = 1; i <= number / 2; i++) {
            if (number % i == 0) {
                temp += i;
            }
        }
        if (temp == number && temp != 0) {
            binding.perfectResultado.setText(number + " é um número perfeito.");
        } else {
            binding.perfectResultado.setText(number + " não é um número perfeito.");
        }
    }

    @SuppressLint("DefaultLocale")
    public void tabuada(int number) {
        String selectedItem = binding.spinnerTabuada.getSelectedItem().toString();
        String str = "";
        String calculo = "";

        for (int i = 0; i <= 10; i++) {
            if (selectedItem.equals("+")) {
                calculo = String.format("%d + %d = %d", number, i, number + i);
            } else if (selectedItem.equals("-")) {
                calculo = String.format("%d - %d = %d", number, i, number - i);
            } else if (selectedItem.equals("x")) {
                calculo = String.format("%d x %d = %d", number, i, number * i);
            } else if (selectedItem.equals("÷")) {
                calculo = String.format("%d ÷ %d = %.2f", number, i, (double) number / i);
            }
            str = str.concat(calculo + "\n");

        }
        popup(this, str);
    }

    private void popup(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setGravity(Gravity.CENTER);
        builder.setView(textView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}