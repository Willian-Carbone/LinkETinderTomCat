package br.com.williancarbone.model.objetos

import br.com.williancarbone.model.enuns.Especialidade
import br.com.williancarbone.model.enuns.Estado
import groovy.transform.Canonical

@Canonical

class Candidato extends Usuario{

    String cpf
    Integer idade

    Candidato (String nome, String cpf, Integer idade, String email, String cep, Estado estado, String descricao, ArrayList<Especialidade> especialidades){
        super(nome,email,cep,estado,descricao,especialidades)
        this.cpf=cpf
        this.idade=idade

    }



}
