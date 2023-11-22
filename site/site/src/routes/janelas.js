var express = require("express");
var router = express.Router();

var janelasController = require("../controllers/janelasController");

router.get("/medidas/:id/:tempo/:linhas", function (req, res) {
    janelasController.buscarUltimasMedidas(req, res);
});


module.exports = router;