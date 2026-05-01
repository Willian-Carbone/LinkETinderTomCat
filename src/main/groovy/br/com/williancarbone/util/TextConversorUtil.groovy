package br.com.williancarbone.util

import java.text.Normalizer

class TextConversorUtil {

    static String converterParaNomeEnum(String entrada){
        if(entrada==null){return ""}

       return Normalizer.normalize(entrada,Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","").replaceAll(" ","").toUpperCase()

    }

    static String removerNaoDigitos(String dado) {
        if (dado == null){
           return ""
        }

        return dado.replaceAll(/\D/, "")
    }
}
