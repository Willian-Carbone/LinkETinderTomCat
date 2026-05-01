package br.com.williancarbone.model.objetos

import br.com.williancarbone.model.enuns.Especialidade
import br.com.williancarbone.model.enuns.Estado





class Empresa extends Usuario{
    String cnpj
    String pais

    Empresa (String nome, String cnpj, String pais, String cep, String email, Estado estado, String descricao, List<Especialidade> especialidades){
        super (nome,email,cep,estado,descricao, especialidades)
        this.cnpj=cnpj
        this.pais=pais

    }


}
