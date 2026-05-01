package br.com.williancarbone.ValidadoresEntrada.facades

import br.com.williancarbone.ValidadoresEntrada.ValidadorCep
import br.com.williancarbone.ValidadoresEntrada.ValidadorEmail
import br.com.williancarbone.ValidadoresEntrada.ValidadorEspecialidade
import br.com.williancarbone.ValidadoresEntrada.ValidadorEstado
import br.com.williancarbone.ValidadoresEntrada.ValidadorI
import br.com.williancarbone.ValidadoresEntrada.ValidadorNome
import br.com.williancarbone.ValidadoresEntrada.factory.ValidadorFactory
import br.com.williancarbone.exceptions.DadoInvalido
import br.com.williancarbone.exceptions.DadoNaoInformado


abstract class ValidadorUsuarioRegistroFacade {

    protected ValidadorFactory facValidador= new ValidadorFactory()

    protected ValidadorI valEmail=facValidador.fabricarValidador("email")
    protected ValidadorI valNome=facValidador.fabricarValidador("nome")
    protected ValidadorI valEstado = facValidador.fabricarValidador("estado")
    protected ValidadorI valEspec = facValidador.fabricarValidador("especialidade")
    protected ValidadorI valCep = facValidador.fabricarValidador("cep")


    protected void validarComum(Map infos) {

        List camposObrigatorios = ['nome', 'email', 'cep', 'estado','especialidades','descricao']

        camposObrigatorios.each { campo ->
            if (!infos.containsKey(campo) || infos[campo] == null) {
                throw new DadoNaoInformado("O campo '${campo}' está faltando no cadastro.")
            }
        }


            if (!valNome.validarDado(infos.nome as String)) throw new DadoInvalido("Nome inválido.")
            if (!valEmail.validarDado(infos.email as String)) throw new DadoInvalido("E-mail inválido.")
            if (!valCep.validarDado(infos.cep as String)) throw new DadoInvalido("CEP inválido.")
            if (!valEstado.validarDado(infos.estado as String)) throw new DadoInvalido("Estado inválido.")
            if(!valEspec.validarDado(infos.especialidades as List<String>)){throw  new DadoInvalido("Especialidades informadas não sao um conjunto valido")}

    }
}
