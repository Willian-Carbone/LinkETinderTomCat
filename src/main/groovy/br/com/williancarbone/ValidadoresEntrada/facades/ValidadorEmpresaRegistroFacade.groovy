package br.com.williancarbone.ValidadoresEntrada.facades


import br.com.williancarbone.ValidadoresEntrada.ValidadorI
import br.com.williancarbone.exceptions.DadoInvalido
import br.com.williancarbone.exceptions.DadoNaoInformado

class ValidadorEmpresaRegistroFacade extends ValidadorUsuarioRegistroFacade{

    private ValidadorI valCnpj = facValidador.fabricarValidador("cnpj")


    void validarDadosParaRegistroEmpresa(Map infos){
        super.validarComum(infos)

        List camposObrigatorios = ["pais","cnpj"]

        camposObrigatorios.each { campo ->
            if (!infos.containsKey(campo) || infos[campo] == null) {
                throw new DadoNaoInformado("O campo '${campo}' está faltando no cadastro.")
            }
        }

        if(!valCnpj.validarDado(infos.cnpj as String)) {throw  new DadoInvalido("cnpj invalido")}



    }
}
