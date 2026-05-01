package br.com.williancarbone.ValidadoresEntrada

import br.com.williancarbone.model.enuns.Estado
import br.com.williancarbone.util.TextConversorUtil



class ValidadorEstado implements ValidadorI<String>{
    @Override
    boolean validarDado(String possivelEstado) {

        return possivelEstado && Estado.any{it.name()== TextConversorUtil.converterParaNomeEnum(possivelEstado)}

    }
}
