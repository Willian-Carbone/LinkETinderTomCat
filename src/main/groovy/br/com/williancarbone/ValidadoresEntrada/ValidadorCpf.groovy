package br.com.williancarbone.ValidadoresEntrada

class ValidadorCpf implements ValidadorI<String>{
    @Override
    boolean validarDado(String cpf) {
        return  cpf && (cpf ==~ /\d{3}\.\d{3}\.\d{3}-\d{2}/ || cpf ==~ /\d{11}/)
    }
}
