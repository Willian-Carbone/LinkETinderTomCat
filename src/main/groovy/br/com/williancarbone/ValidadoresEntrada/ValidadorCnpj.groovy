package br.com.williancarbone.ValidadoresEntrada

class ValidadorCnpj implements ValidadorI<String>{
    @Override
    boolean validarDado(String cnpj) {
        return cnpj && (cnpj ==~ /\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}/ || cnpj ==~ /\d{14}/)
    }
}
