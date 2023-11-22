var database = require("../database/config");

function buscarUltimasMedidasJa(id) {

    instrucaoSql = ''

    if (process.env.AMBIENTE_PROCESSO == "producao") {
        instrucaoSql = ``;
    } else if (process.env.AMBIENTE_PROCESSO == "desenvolvimento") {
        instrucaoSql = `
        
        `;
    } else {
        console.log("\nO AMBIENTE (produção OU desenvolvimento) NÃO FOI DEFINIDO EM app.js\n");
        return
    }

    console.log("Executando a instrução SQL: \n" + instrucaoSql);
    return database.executar(instrucaoSql);
}



function colJanela(id) {

  if (process.env.AMBIENTE_PROCESSO == "producao") {
      var instrucao = ``;
  } else if (process.env.AMBIENTE_PROCESSO == "desenvolvimento") {
      var instrucao = `SELECT Janela_atual
      FROM Janela
      WHERE fkMaquina = ${id}
      ORDER BY idJanela DESC
      LIMIT 1;`;
  } else {
      console.log("\nO AMBIENTE (produção OU desenvolvimento) NÃO FOI DEFINIDO EM app.js\n");
      return
  }

  console.log("Executando a instrução SQL: \n" + instrucao);
  return database.executar(instrucao);

}
module.exports = {
    buscarUltimasMedidasJa,
    colJanela
}
