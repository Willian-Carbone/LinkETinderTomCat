package br.com.williancarbone.model.objetos

import br.com.williancarbone.model.enuns.Especialidade
import groovy.transform.Canonical

@Canonical

class EspecialidadeUsuario {
    Integer idUsuario
    Especialidade especialidade
}

