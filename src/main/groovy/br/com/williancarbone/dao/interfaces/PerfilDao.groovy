package br.com.williancarbone.dao.interfaces


interface PerfilDao <TipoPerfil,DadoNovo> {

    Integer criarPerfil(TipoPerfil objeto)

    void removerPerfil(String identificador)

    void editarPerfil(Integer identificador,DadoNovo dadoNovo)

   Map capturarInformacoesPerfil(String identificador)

    Integer capturarId(String identificador)

    Boolean buscarExistenciaCredencial(String credencial)




}