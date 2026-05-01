package br.com.williancarbone.ValidadoresEntrada

class ValidadorCep implements ValidadorI<String>{

    @Override
    boolean validarDado(String cep) {
        return cep && cep ==~ /\d{5}-\d{3}/ || cep ==~ /\d{8}/
    }
}
