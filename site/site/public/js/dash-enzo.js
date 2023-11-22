var contadorRegistro = 0;
var contadorDadoEstavel = 0;
var usoDisco = 0;
var totalDisco = 0;



function colJanela(fkRobo) {
    fkRobo = document.getElementById("maquinas-ativas").value;
    console.log(fkRobo);
    fetch("/janelas/colJanela", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            idMaquinaServer: fkRobo
        })
    })
    .then(function (response) {
        if (response.ok) {
            response.json().then(function (resposta) {
                const janelaAtual = resposta[0].Janela_atual;
                janelas.innerHTML = janelaAtual;
            });
        } else {
            console.error('Nenhum dado encontrado ou erro na API');
        }
    })
    .catch(function (error) {
        console.error(`Erro na obtenção dos dados: ${error.message}`);
    });
}


function obterDadosGrafico() {
    fkRobo = document.getElementById("maquinas-ativas").value;
    tempoHistorico = document.getElementById("tempo-historico").value;
    const selectedOption =
        document.getElementById("maquinas-ativas").options[
        document.getElementById("maquinas-ativas").selectedIndex
        ];
    const idProcess = selectedOption.getAttribute("data-info");
    id_maquina.innerHTML = idProcess;

    fetch(`/componentes/medidas/${fkRobo}/${tempoHistorico}/7`, {
        cache: "no-store",
    })
        .then(function (response) {
            if (response.ok) {
                response.json().then(function (resposta) {
                    console.log(resposta);

                    plotarGrafico(resposta, fkRobo);
                });
            } else {
                console.error("Nenhum dado encontrado ou erro na API");
            }
        })
        .catch(function (error) {
            console.error(`Erro na obtenção dos dados p/ gráfico: ${error.message}`);
        });
}




function plotarGrafico(resposta, fkRobo) {
    // CRIANDO GRÁFICO CPU
    let labelCpu = [];

    // Criando estrutura para plotar gráfico - dados
    let dadosCpu = {
        labels: labelCpu,
        datasets: [
            {
                label: "Cpu",
                data: [],
                fill: true,
                borderColor: "rgb(75, 192, 192)",
                backgroundColor: "rgb(75, 192, 192, 0.400)",
                tension: 0.1,
            },
        ],
    };

    resposta.forEach((registro) => {
        if (registro.nomeComponente == "Porcentagem da CPU") {
            labelCpu.push(registro.HorarioFormatado);
            dadosCpu.datasets[0].data.push(registro.dado);
        }
    });
    // Inserindo valores recebidos em estrutura para plotar o gráfico
    // for (i = 0; i < resposta.length; i++) {
    //     var registro = resposta[i];
    //     labels.push(registro.HorarioDado);
    //     dados.datasets[0].data.push(registro.dado);
    //     // dados.datasets[1].data.push(registro.temperatura);
    // }

    console.log("----------------------------------------------");
    console.log("O gráfico de CPU será plotado com os respectivos valores:");
    console.log("Labels:");
    console.log(labelCpu);
    console.log("Dados:");
    console.log(dadosCpu);
    console.log("----------------------------------------------");

    // Criando estrutura para plotar gráfico - config
    const configCpu = {
        type: "line",
        data: dadosCpu,
    };

    var grafico1 = document.getElementById(`myChartCanvas1`);

    if (Chart.getChart(grafico1)) {
        Chart.getChart(grafico1).destroy();
    }

    // Adicionando gráfico criado em div na tela
    let myChart = new Chart(document.getElementById(`myChartCanvas1`), configCpu);

    console.log("iniciando plotagem do gráfico Memória ...");

    // ----------------------------------------------------------------------------------
    // CRIANDO GRÁFICO DA MEMÓRIA
    let labelMemoria = [];

    // Criando estrutura para plotar gráfico - dados
    let dadosMemoria = {
        labels: labelMemoria,
        datasets: [
            {
                label: "Memória",
                data: [],
                fill: true,
                borderColor: "rgb(75, 192, 192)",
                backgroundColor: "rgb(75, 192, 192, 0.400)",
                tension: 0.1,
            },
        ],
    };

    console.log("----------------------------------------------");
    console.log(
        'Estes dados foram recebidos pela funcao "obterDadosGrafico" e passados para "plotarGrafico":'
    );
    resposta.forEach((registro) => {
        if (registro.nomeComponente == "Porcentagem da Memoria") {
            labelMemoria.push(registro.HorarioFormatado);
            dadosMemoria.datasets[0].data.push(registro.dado);
        }
    });
    // Inserindo valores recebidos em estrutura para plotar o gráfico
    // for (i = 0; i < resposta.length; i++) {
    //     var registro = resposta[i];
    //     labels.push(registro.HorarioDado);
    //     dados.datasets[0].data.push(registro.dado);
    //     // dados.datasets[1].data.push(registro.temperatura);
    // }

    console.log("----------------------------------------------");
    console.log("O gráfico será plotado com os respectivos valores:");
    console.log("Labels:");
    console.log(labelMemoria);
    console.log("Dados:");
    console.log(dadosMemoria);
    console.log("----------------------------------------------");

    // Criando estrutura para plotar gráfico - config
    const configMemoria = {
        type: "line",
        data: dadosMemoria,
    };

    var grafico2 = document.getElementById(`myChartCanvas2`);

    if (Chart.getChart(grafico2)) {
        Chart.getChart(grafico2).destroy();
    }
    // Adicionando gráfico criado em div na tela
    let myChart2 = new Chart(
        document.getElementById(`myChartCanvas2`),
        configMemoria
    );

    // ----------------------------------------------------------------------------------
    // CRIANDO GRÁFICO DA REDE
    let labelRede = [];

    // Criando estrutura para plotar gráfico - dados
    let dadosRede = {
        labels: labelRede,
        datasets: [
            {
                label: "Latencia em milissegundos (ms)",
                data: [],
                fill: true,
                borderColor: "rgb(75, 192, 192)",
                backgroundColor: "rgb(75, 192, 192, 0.400)",
                tension: 0.1,
            },
        ],
    };

    console.log("----------------------------------------------");
    console.log(
        'Estes dados foram recebidos pela funcao "obterDadosGrafico" e passados para "plotarGrafico":'
    );
    let contadorRegistro = 0;
    let contadorDadoEstavel = 0;

    resposta.forEach((registro) => {
        if (registro.nomeComponente == "Latencia de Rede") {
            labelRede.push(registro.HorarioFormatado);
            dadosRede.datasets[0].data.push(registro.dado);
            contadorRegistro++;

            if (registro.dado > 80) {
                rede_estado_geral.innerHTML = "Crítico";
                rede_estado_geral.style.color = "red";
            } else if (registro.dado > 50) {
                rede_estado_geral.innerHTML = "Instável";
                rede_estado_geral.style.color = "orange";
            } else {
                contadorDadoEstavel++;
            }

            if (contadorDadoEstavel >= 7) {
                rede_estado_geral.innerHTML = "Estável";
                rede_estado_geral.style.color = "green";
                contadorDadoEstavel = 0; // Reinicialize o contador de dados estáveis
            }
        }
    });

    // Inserindo valores recebidos em estrutura para plotar o gráfico
    // for (i = 0; i < resposta.length; i++) {
    //     var registro = resposta[i];
    //     labels.push(registro.HorarioDado);
    //     dados.datasets[0].data.push(registro.dado);
    //     // dados.datasets[1].data.push(registro.temperatura);
    // }

    console.log("----------------------------------------------");
    console.log("O gráfico será plotado com os respectivos valores:");
    console.log("Labels:");
    console.log(labelMemoria);
    console.log("Dados:");
    console.log(dadosMemoria);
    console.log("----------------------------------------------");

    // Criando estrutura para plotar gráfico - config
    const configRede = {
        type: "line",
        data: dadosRede,
    };
    var grafico4 = document.getElementById(`myChartCanvas4`);

    if (Chart.getChart(grafico4)) {
        Chart.getChart(grafico4).destroy();
    }

    // Adicionando gráfico criado em div na tela
    let myChart4 = new Chart(
        document.getElementById(`myChartCanvas4`),
        configRede
    );

    setTimeout(
        () =>
            atualizarGrafico(
                fkRobo,
                dadosCpu,
                myChart,
                myChart2,
            ),
        10000
    );
}

// Esta função *atualizarGrafico* atualiza o gráfico que foi renderizado na página,
// buscando a última medida inserida em tabela contendo as capturas,

//     Se quiser alterar a busca, ajuste as regras de negócio em src/controllers
//     Para ajustar o "select", ajuste o comando sql em src/models
function atualizarGrafico(
    fkRobo,
    dadosCpu,
    myChart,
    myChart2,
) {

    if(tempoHistorico == "atual"){
        fetch(`/componentes/medidas/${fkRobo}/${tempoHistorico}/1`, {
            cache: "no-store",
        })
            .then(function (response) {
                if (response.ok) {
                    response.json().then(function (novoRegistro) {
                        console.log(`Dados atuais do gráfico:`);
                        console.log(novoRegistro);
                        novoRegistro.forEach((registro) => {
                            if (registro.nomeComponente == "Porcentagem da CPU") {
                                if (
                                    registro.HorarioFormatado ==
                                    dadosCpu.labels[dadosCpu.labels.length - 1]
                                ) {
                                    console.log(
                                        "Como não há dados novos para captura de cpu, o gráfico não atualizará."
                                    );
                                } else {
                                    // tirando e colocando valores no gráfico
                                    dadosCpu.labels.shift(); // apagar o primeiro
                                    dadosCpu.labels.push(registro.HorarioFormatado); // incluir um novo momento
    
                                    dadosCpu.datasets[0].data.shift(); // apagar o primeiro de umidade
                                    dadosCpu.datasets[0].data.push(registro.dado); // incluir uma nova medida de umidade
    
                                    myChart.update();
                                }
                            }
                        });
    
                        // Altere aqui o valor em ms se quiser que o gráfico atualize mais rápido ou mais devagar
                        setTimeout(
                            () =>
                                atualizarGrafico(
                                    fkRobo,
                                    dadosCpu,
                                    myChart,
                                    myChart2,
                                ),
                            10000
                        );
                    });
                } else {
                    console.error("Nenhum dado encontrado ou erro na API");
                    // Altere aqui o valor em ms se quiser que o gráfico atualize mais rápido ou mais devagar
                    setTimeout(
                        () =>
                            atualizarGrafico(
                                fkRobo,
                                dadosCpu,
                                myChart,
                                myChart2,
                            ),
                        10000
                    );
                }
            })
            .catch(function (error) {
                console.error(`Erro na obtenção dos dados p/ gráfico: ${error.message}`);
            });
    } else{
        obterDadosGrafico()
    }
    
}

function limparDados(){
    var grafico1 = document.getElementById(`myChartCanvas1`);
    var grafico2 = document.getElementById(`myChartCanvas2`);
    if (Chart.getChart(grafico2)) {
        Chart.getChart(grafico2).destroy();
    }

    if (Chart.getChart(grafico1)) {
        Chart.getChart(grafico1).destroy();
    }

}
