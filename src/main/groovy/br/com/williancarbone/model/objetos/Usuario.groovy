package br.com.williancarbone.model.objetos

import br.com.williancarbone.model.enuns.*
import groovy.transform.Canonical

@Canonical

abstract class Usuario {
    String nome
    String email
    String cep
    Estado estado
    String descricao
    List<Especialidade> especialidades
    Integer identificador =null
}