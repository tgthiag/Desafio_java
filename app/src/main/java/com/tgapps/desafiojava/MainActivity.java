package com.tgapps.desafiojava;

import androidx.appcompat.app.AppCompatActivity;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.tgapps.desafiojava.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        binding.btNumEnviar.setOnClickListener(v->{
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            int numText = Integer.parseInt(binding.editNumeros.getText().toString());
            numList.add(numText);
            Collections.sort(numList);
            binding.numResultado.setText(numList.toString().replace("[","").replace("]", ""));
            binding.editNumeros.setText("");
        });
    }

    private void hideView(View v) {
        binding.containerNumeros.setVisibility(View.GONE);
        binding.containerCep.setVisibility(View.GONE);
        binding.containerPerfect.setVisibility(View.GONE);
        binding.containerTabuada.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
    }

}