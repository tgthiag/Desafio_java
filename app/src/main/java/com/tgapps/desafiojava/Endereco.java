package com.tgapps.desafiojava;

public class Endereco {
    String logradouro;
    String bairro;
    String localidade;
    String uf;

    public String getLogradouro() {
        return logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public String getUf() {
        return uf;
    }

    @Override
    public String toString() {
        return "Endereço{" + "logradouro=" + logradouro + ", bairro=" + bairro + ", localidade=" + localidade + ", Estado=" + uf + '}';
    }
}