package br.com.williancarbone.ValidadoresEntrada

class ValidadorIdade implements  ValidadorI<Integer>{
    @Override
    boolean validarDado(Integer idade) {

        if (idade == null) {
            return false

        }

        return (idade as Integer)>=18
    }
}
