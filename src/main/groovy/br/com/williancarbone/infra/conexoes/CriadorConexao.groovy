package br.com.williancarbone.infra.conexoes

import groovy.sql.Sql

interface CriadorConexao {

    Sql criarConexao()


}