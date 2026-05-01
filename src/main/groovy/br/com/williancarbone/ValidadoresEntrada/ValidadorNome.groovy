package br.com.williancarbone.ValidadoresEntrada

class ValidadorNome implements ValidadorI<String>{

    @Override
    boolean validarDado(String nome) {
        return nome &&  (nome ==~ /(?U)^\p{L}{2,} [\p{L} ]+$/)
    }
}
