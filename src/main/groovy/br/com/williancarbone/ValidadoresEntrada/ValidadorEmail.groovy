package br.com.williancarbone.ValidadoresEntrada

class ValidadorEmail implements ValidadorI<String>{
    @Override
    boolean validarDado(String email) {
        return (email ==~ /[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}/)
    }
}
