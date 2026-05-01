package br.com.williancarbone.dao.bases

import groovy.sql.Sql


abstract class BaseDao {
    protected Sql sql


    BaseDao(Sql sql){
        this.sql = sql
    }


}
