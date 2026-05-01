package br.com.williancarbone.infra.conexoes

import br.com.williancarbone.exceptions.FalhaCriacaoConexao
import groovy.sql.Sql

class ConexaoPostGresBase implements CriadorConexao{


    @Override
    Sql criarConexao() {

        String url = 'jdbc:postgresql://localhost:5432/LinkEtinder'
        String usuario = 'willian'
        String senha = '5550178'
        String driver = 'org.postgresql.Driver'

        try {

            return Sql.newInstance(url, usuario, senha, driver)
        }

        catch (Exception ignored){
            throw new FalhaCriacaoConexao("Banco de dados esta fora do ar")
        }
    }
}
