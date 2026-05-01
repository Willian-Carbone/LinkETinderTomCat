package br.com.williancarbone.model.enuns

enum Estado {

    ACRE("AC"),
    ALAGOAS("AL"),
    AMAPA("AP"),
    AMAZONAS("AM"),
    BAHIA("BH"),
    CEARA("CE"),
    DISTRITOFEDERAL("DF"),
    ESPIRITOSANTO("ES"),
    GOIAS("GO"),
    MARANHAO("MA"),
    MATOGROSSO("MT"),
    MATOGROSSODOSUL("MS"),
    MINASGERAIS("MG"),
    PARA("PA"),
    PARAIBA("PB"),
    PARANA("PR"),
    PERNAMBUCO("PE"),
    PIAUI("PI"),
    RIODEJANEIRO("RJ"),
    RIOGRANDEDONORTE("RN"),
    RIOGRANDEDOSUL("RS"),
    RONDONIA("RO"),
    RORAIMA("RR"),
    SANTACATARINA("SC"),
    SAOPAULO("SP"),
    SERGIPE("SE"),
    TOCANTINS("TO")


    String sigla

    Estado(String sigla) {
        this.sigla = sigla
    }


}