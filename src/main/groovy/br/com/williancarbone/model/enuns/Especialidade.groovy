package br.com.williancarbone.model.enuns

enum Especialidade {

    PT ("phyton"),
    JAV ("java"),
    ANG ("Angular"),
    SPR ("Spring"),
    HT ("Html"),
    CS ("Css"),
    C("C++")

    String valor

    Especialidade (String valor){

        this.valor = valor

    }

    static Especialidade localizarEnum(String texto) {
        return values().find { it.valor.equalsIgnoreCase(texto) }
    }

}