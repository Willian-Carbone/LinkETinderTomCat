package br.com.williancarbone.dao.interfaces

interface Matchable {

    List<Map> buscarMatchs(String identificador)
    List<Map> buscarVagas(String identificador)

}