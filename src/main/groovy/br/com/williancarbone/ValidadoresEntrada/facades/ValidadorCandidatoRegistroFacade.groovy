package br.com.williancarbone.ValidadoresEntrada.facades


import br.com.williancarbone.ValidadoresEntrada.ValidadorI
import br.com.williancarbone.exceptions.DadoInvalido
import br.com.williancarbone.exceptions.DadoNaoInformado

class ValidadorCandidatoRegistroFacade extends ValidadorUsuarioRegistroFacade{

    private ValidadorI valCpf = facValidador.fabricarValidador("cpf")
    private ValidadorI valIdade=facValidador.fabricarValidador("idade")

    void validarDadosParaRegistroCandidato(Map infos){
        super.validarComum(infos)

        List camposObrigatorios = ["idade","cpf"]

        camposObrigatorios.each { campo ->
            if (!infos.containsKey(campo) || infos[campo] == null) {
                throw new DadoNaoInformado("O campo '${campo}' está faltando no cadastro.")
            }
        }

        if(!valCpf.validarDado(infos.cpf as String)) {throw  new DadoInvalido("cpf invalido")}
        if(!valIdade.validarDado(infos.idade as Integer)){throw new DadoInvalido("Idade invalida")}


    }


}
