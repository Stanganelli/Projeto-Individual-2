var janelasModel = require("../models/janelasModel");

function buscarUltimasMedidasJa(req, res) {

    const limite_linhas = req.params.linhas;
    const tempo = req.params.tempo;
    var id = req.params.id;

    console.log(`Recuperando as ultimas ${limite_linhas} medidas`);

    janelasModel.buscarUltimasMedidas(id, tempo, limite_linhas).then(function (resultado) {
        if (resultado.length > 0) {
            res.status(200).json(resultado);
        } else {
            res.status(204).send("Nenhum resultado encontrado!")
        }
    }).catch(function (erro) {
        console.log(erro);
        console.log("Houve um erro ao buscar as ultimas medidas.", erro.sqlMessage);
        res.status(500).json(erro.sqlMessage);
    });
}


function colJanela(req, res) {

    var id = req.body.idMaquinaServer

    console.log(`Recuperando medidas em tempo real`);

    janelasModel.colJanela(id).then(function (resultado) {
        if (resultado.length > 0) {
            res.status(200).json(resultado);
        } else {
            res.status(204).send("Nenhum resultado encontrado!")
        }
    }).catch(function (erro) {
        console.log(erro);
        console.log("Houve um erro ao buscar as ultimas janelas do sistema.", erro.sqlMessage);
        res.status(500).json(erro.sqlMessage);
    });
}

module.exports = {
    buscarUltimasMedidasJa,
    colJanela
}