package br.com.williancarbone.service.Base

interface BuscadorInfo {

    Map capturarInfosDoPerfil(String credencial)

    Integer capturarIdPerfil(String credencial)

    List<Map> buscarVagasParaOPerfil(String credencial)

    buscarMatchsParaOPerfil(String credencial)

}