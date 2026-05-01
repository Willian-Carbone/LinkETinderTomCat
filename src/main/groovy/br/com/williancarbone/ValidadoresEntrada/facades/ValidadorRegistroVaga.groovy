package br.com.williancarbone.ValidadoresEntrada.facades


import br.com.williancarbone.ValidadoresEntrada.ValidadorI
import br.com.williancarbone.ValidadoresEntrada.factory.ValidadorFactory
import br.com.williancarbone.exceptions.DadoInvalido
import br.com.williancarbone.exceptions.DadoNaoInformado
import br.com.williancarbone.util.TextConversorUtil


class ValidadorRegistroVaga {

    void validarDadosParaRegistroVaga(Map infos){
        List camposObrigatorios = ['nome', 'descricao', 'contratante', 'requisitos']

        ValidadorFactory facValidador=new ValidadorFactory()

        camposObrigatorios.each { campo ->
            if (!infos.containsKey(campo) || infos[campo] == null) {
                throw new DadoNaoInformado("O campo '${campo}' está faltando no cadastro.")
            }
        }

        ValidadorI valEspec = facValidador.fabricarValidador("especialidade")
        ValidadorI valCnpj = facValidador.fabricarValidador("cnpj")


        if(!valEspec.validarDado(infos.requisitos as List<String>)){throw  new DadoInvalido("requisitos informados não sao um conjunto valido")}
        if(!valCnpj.validarDado(TextConversorUtil.removerNaoDigitos(infos.contratante as String))) {throw  new DadoInvalido("cnpj invalido")}
    }

}
