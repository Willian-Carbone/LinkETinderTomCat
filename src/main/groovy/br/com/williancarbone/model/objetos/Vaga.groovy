package br.com.williancarbone.model.objetos

import br.com.williancarbone.model.enuns.Especialidade
import groovy.transform.Canonical

@Canonical

class Vaga {
    String nome
    String descricao
    String contratante
    List<Especialidade> requisitos



}