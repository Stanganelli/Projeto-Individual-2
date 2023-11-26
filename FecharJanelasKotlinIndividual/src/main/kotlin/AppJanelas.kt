import com.github.britooo.looca.api.core.Looca
import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import javax.swing.JOptionPane
import java.io.File
import java.util.Scanner
//novos iimports

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import kotlin.concurrent.thread
import java.time.LocalDateTime


class AppJanelas {
    var conexDb: JdbcTemplate
    var conexDbServer: JdbcTemplate
    var id = Looca().processador.id
    var cli = Scanner(System.`in`)
    val os = Looca().sistema.sistemaOperacional


    init {
        val dataSource = BasicDataSource()
        dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
        val serverName = "localhost"
        val mydatabase = "medconnect"
        dataSource.username = "medconnect"
        dataSource.password = "medconnect123"
        dataSource.url = "jdbc:mysql://$serverName/$mydatabase"
        conexDb = JdbcTemplate(dataSource)

        val dataSoruceServer = BasicDataSource()
        dataSoruceServer.url = "jdbc:sqlserver://52.7.105.138:1433;databaseName=medconnect;encrypt=false";
        dataSoruceServer.driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        dataSoruceServer.username = "sa"
        dataSoruceServer.password = "medconnect123"
        conexDbServer = JdbcTemplate(dataSoruceServer)
    }


    fun verificacao() {





        val qtdIds = conexDbServer.queryForObject(
            """
    SELECT COUNT(*) AS count FROM RoboCirurgiao WHERE idProcess = '$id'
    """,
            Int::class.java,
        )

        if (qtdIds == 0) {
            loguinMaquina()
        } else {
            coletaDeDados()
        }

    }
    fun deletarSql(janelaParaDeletar: String) {
        val sql = """
        DELETE FROM Janela_fechada 
        WHERE janela_a_fechar = '${janelaParaDeletar}'
    """
        try {
            val rowsAffected = conexDb.update(sql)
            println("$rowsAffected registros foram removidos da tabela 'Janela_fechada' onde janela_a_fechar = '${janelaParaDeletar}'.")
        } catch (e: Exception) {
            println("Ocorreu um erro ao tentar deletar registros: ${e.message}")
        }
    }


    fun fecharJanela(janelaParaDeletar: String) {
        if (os == "Windows") {
            println("Opa, estou na deleção")
            println(janelaParaDeletar)
            val windowHandle = User32.INSTANCE.FindWindow(null, janelaParaDeletar)

            if (windowHandle != null) {
                User32.INSTANCE.PostMessage(windowHandle, WinUser.WM_CLOSE, null, null)
                Thread.sleep(9000)

            } else {
                println("Janela não encontrada")
            }
        }
        deletarSql(janelaParaDeletar)
        coletaDeDados()
    }


    fun coletaDeDados() {
        println("Coletando dados")

        val idRobo = conexDbServer.queryForObject(
            "SELECT idRobo FROM RoboCirurgiao WHERE idProcess = ?",
            Int::class.java,
            id
        ) ?: 0

        while (true) {
            val janelaAtual = Looca().grupoDeJanelas.janelas.getOrNull(2)?.titulo?.toString()
            janelaAtual?.let {
                conexDbServer.update(
                    "INSERT INTO Janela (Janela_atual, ativo, fkMaquina) VALUES (?, ?, ?)",
                    it, 1, idRobo
                )
            }
            println(janelaAtual)

            val qtdProcessos = Looca().grupoDeProcessos.totalProcessos
            conexDbServer.update(
                "INSERT INTO registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (?, ?, ?, ?)",
                qtdProcessos, idRobo, 20, LocalDateTime.now()
            )
            println(qtdProcessos)



            val janelasExist = conexDbServer.queryForObject(
                """
    select count(*) as count from Janela_fechada
     where fkMaquina1 = $idRobo
    """,
                Int::class.java,
            )


            if(janelasExist == 0){
                coletaDeDados()

            }
            else{ var janelaRecente = conexDbServer.queryForObject(
                "SELECT janela_a_fechar FROM Janela_fechada WHERE fkMaquina1 = ? ORDER BY idJanela_fechada DESC LIMIT 1",
                String::class.java,
                idRobo
            )
                println(janelaRecente)

                janelaRecente?.let {
                    var sinal = conexDbServer.queryForObject(
                        "SELECT sinal_terminacao FROM Janela_fechada WHERE janela_a_fechar = ?",
                        Int::class.java,
                        it
                    )
                    sinal?.let {
                        if (it == 1) {
                            fecharJanela(janelaRecente)
                        }
                    }
                }}



            Thread.sleep(200 * 20000)
        }

    }


    fun installPyhon() {

        val roboId = conexDbServer.queryForObject(
            """
    select idRobo from RoboCirurgiao where idProcess = '$id'
    """,
            Int::class.java,
        )


        var nome = "SolucaoJanelas.py"
        var arqivoPython = File(nome)
        arqivoPython.writeText("\\n\" +\n" +
                "                    \"from mysql.connector import connect\\n\" +\n" +
                "                    \"import psutil\\n\" +\n" +
                "                    \"import platform\\n\" +\n" +
                "                    \"import time\\n\" +\n" +
                "                    \"import mysql.connector\\n\" +\n" +
                "                    \"from datetime import datetime\\n\" +\n" +
                "                    \"import ping3\\n\" +\n" +
                "                    \"import json\\n\" +\n" +
                "                    \"import requests\\n\" +\n" +
                "                    \"import pymssql\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"#alerta = {\\\"text\\\": \\\"alerta\\\"}\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"webhook = \\\"https://hooks.slack.com/services/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\\\"\\n\" +\n" +
                "                    \"#requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"idRobo = 1\\n\" +\n" +
                "                    \"#descomente abaixo quando for ora criar esse arquivo pelo kotlin\\n\" +\n" +
                "                    \"idRobo = ${roboId}\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"def mysql_connection(host, user, passwd, database=None):\\n\" +\n" +
                "                    \"    connection = connect(\\n\" +\n" +
                "                    \"        host=host,\\n\" +\n" +
                "                    \"        user=user,\\n\" +\n" +
                "                    \"        passwd=passwd,\\n\" +\n" +
                "                    \"        database=database\\n\" +\n" +
                "                    \"    )\\n\" +\n" +
                "                    \"    return connection\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"def bytes_para_gb(bytes_value):\\n\" +\n" +
                "                    \"    return bytes_value / (1024 ** 3)\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"def milissegundos_para_segundos(ms_value):\\n\" +\n" +
                "                    \"    return ms_value / 1000\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"connection = mysql_connection('localhost', 'medconnect', 'medconnect123', 'medconnect')\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"sqlserver_connection = pymssql.connect(server='52.7.105.138', database='medconnect', user='sa', password='medconnect123');\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"#Disco\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"meu_so = platform.system()\\n\" +\n" +
                "                    \"if(meu_so == \\\"Linux\\\"):\\n\" +\n" +
                "                    \"    nome_disco = '/'\\n\" +\n" +
                "                    \"    disco = psutil.disk_usage(nome_disco)\\n\" +\n" +
                "                    \"elif(meu_so == \\\"Windows\\\"):\\n\" +\n" +
                "                    \"    nome_disco = 'C:\\\\\\\\'\\n\" +\n" +
                "                    \"disco = psutil.disk_usage(nome_disco)\\n\" +\n" +
                "                    \"discoPorcentagem = disco.percent\\n\" +\n" +
                "                    \"discoTotal = \\\"{:.2f}\\\".format(bytes_para_gb(disco.total))\\n\" +\n" +
                "                    \"discoUsado = \\\"{:.2f}\\\".format(bytes_para_gb(disco.used)) \\n\" +\n" +
                "                    \"discoTempoLeitura = milissegundos_para_segundos(psutil.disk_io_counters(perdisk=False, nowrap=True)[4])\\n\" +\n" +
                "                    \"discoTempoEscrita = milissegundos_para_segundos(psutil.disk_io_counters(perdisk=False, nowrap=True)[5])\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"ins = [discoPorcentagem, discoTotal, discoUsado, discoTempoLeitura, discoTempoEscrita]\\n\" +\n" +
                "                    \"componentes = [10,11,12,13,14]\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"horarioAtual = datetime.now()\\n\" +\n" +
                "                    \"horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"#banco de contenção\\n\" +\n" +
                "                    \"cursor = connection.cursor()\\n\" +\n" +
                "                    \"server_cursor = sqlserver_connection.cursor()\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"queryExis = \\\"SELECT COUNT(*) AS count FROM RoboCirurgiao WHERE idRobo = 22\\\"\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"cursor.execute(queryExis)\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"result = cursor.fetchone()\\n\" +\n" +
                "                    \"if result[0] == 0:\\n\" +\n" +
                "                    \"    querys = \\\"INSERT INTO RoboCirurgiao (idRobo, modelo, fabricacao, fkStatus, idProcess, fkHospital) VALUES (22, 'Modelo A', 'contenção', 1, 'a', 1)\\\"\\n\" +\n" +
                "                    \"    cursor.execute(querys)\\n\" +\n" +
                "                    \"    connection.commit()\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"for i in range(len(ins)):\\n\" +\n" +
                "                    \"        \\n\" +\n" +
                "                    \"    dado = ins[i]\\n\" +\n" +
                "                    \"        \\n\" +\n" +
                "                    \"    componente = componentes[i]\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    query = \\\"INSERT INTO Registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (%s, %s, %s, %s)\\\"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    #banco de contenção\\n\" +\n" +
                "                    \"    cursor.execute(query, (dado, 22, componente, horarioFormatado))\\n\" +\n" +
                "                    \"    server_cursor.execute(query, (dado, idRobo, componente, horarioFormatado))\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"print(\\\"\\\\nDisco porcentagem:\\\", discoPorcentagem,\\n\" +\n" +
                "                    \"          \\\"\\\\nDisco total:\\\", discoTotal,\\n\" +\n" +
                "                    \"          '\\\\nTempo de leitura do disco em segundos:', discoTempoLeitura,\\n\" +\n" +
                "                    \"          '\\\\nTempo de escrita do disco em segundos:', discoTempoEscrita)\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"while True:\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"    #CPU\\n\" +\n" +
                "                    \"    cpuPorcentagem = psutil.cpu_percent(None)\\n\" +\n" +
                "                    \"    frequenciaCpuMhz = psutil.cpu_freq(percpu=False)\\n\" +\n" +
                "                    \"    cpuVelocidadeEmGhz = \\\"{:.2f}\\\".format(frequenciaCpuMhz.current / 1000)\\n\" +\n" +
                "                    \"    tempoSistema = psutil.cpu_times()[1] \\n\" +\n" +
                "                    \"    processos = len(psutil.pids())\\n\" +\n" +
                "                    \"    if(cpuPorcentagem > 60 and cpuPorcentagem > 70):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\"alerta na cpu da maquina: {idRobo} está em estado de alerta\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"    if(cpuPorcentagem > 70 and cpuPorcentagem > 80):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\"alerta na cpu da maquina: {idRobo} está em estado critico\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"    if(cpuPorcentagem > 80):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\"alerta na cpu da maquina: {idRobo} está em estado de urgencia\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"        \\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    #Memoria\\n\" +\n" +
                "                    \"    memoriaPorcentagem = psutil.virtual_memory()[2]\\n\" +\n" +
                "                    \"    memoriaTotal = \\\"{:.2f}\\\".format(bytes_para_gb(psutil.virtual_memory().total))\\n\" +\n" +
                "                    \"    memoriaUsada = \\\"{:.2f}\\\".format(bytes_para_gb(psutil.virtual_memory().used))\\n\" +\n" +
                "                    \"    memoriaSwapPorcentagem = psutil.swap_memory().percent\\n\" +\n" +
                "                    \"    memoriaSwapUso = \\\"{:.2f}\\\".format(bytes_para_gb(psutil.swap_memory().used))\\n\" +\n" +
                "                    \"    if(memoriaPorcentagem > 60 and memoriaPorcentagem > 70):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\"⚠️  Alerta na ram da maquina: {idRobo} está em estado de alerta\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"    if(memoriaPorcentagem > 70 and memoriaPorcentagem > 80):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\"⚠️  Alerta na ram da maquina: {idRobo} está em estado critico\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))  \\n\" +\n" +
                "                    \"    if(memoriaPorcentagem > 80):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\" ⚠️  Alerta na ram da maquina: {idRobo} está em estado de urgencia\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    \\\"\\\"\\\"\\n\" +\n" +
                "                    \"    Por enquanto não será usado\\n\" +\n" +
                "                    \"    for particao in particoes:\\n\" +\n" +
                "                    \"        try:\\n\" +\n" +
                "                    \"            info_dispositivo = psutil.disk_usage(particao.mountpoint)\\n\" +\n" +
                "                    \"            print(\\\"Ponto de Montagem:\\\", particao.mountpoint)\\n\" +\n" +
                "                    \"            print(\\\"Sistema de Arquivos:\\\", particao.fstype)\\n\" +\n" +
                "                    \"            print(\\\"Dispositivo:\\\", particao.device)\\n\" +\n" +
                "                    \"            print(\\\"Espaço Total: {0:.2f} GB\\\".format(info_dispositivo.total / (1024 ** 3)) )\\n\" +\n" +
                "                    \"            print(\\\"Espaço Usado: {0:.2f} GB\\\".format(info_dispositivo.used / (1024 ** 3)) )\\n\" +\n" +
                "                    \"            print(\\\"Espaço Livre: {0:.2f} GB\\\".format(info_dispositivo.free / (1024 ** 3)) )\\n\" +\n" +
                "                    \"            print(\\\"Porcentagem de Uso: {0:.2f}%\\\".format(info_dispositivo.percent))\\n\" +\n" +
                "                    \"            print()\\n\" +\n" +
                "                    \"        except PermissionError as e:\\n\" +\n" +
                "                    \"            print(f\\\"Erro de permissão ao acessar {particao.mountpoint}: {e}\\\")\\n\" +\n" +
                "                    \"        except Exception as e:\\n\" +\n" +
                "                    \"            print(f\\\"Erro ao acessar {particao.mountpoint}: {e}\\\")\\n\" +\n" +
                "                    \"            \\\"\\\"\\\"\\n\" +\n" +
                "                    \"    #Rede\\n\" +\n" +
                "                    \"    interval = 1\\n\" +\n" +
                "                    \"    statusRede = 0\\n\" +\n" +
                "                    \"    network_connections = psutil.net_connections()\\n\" +\n" +
                "                    \"    network_active = any(conn.status == psutil.CONN_ESTABLISHED for conn in network_connections)\\n\" +\n" +
                "                    \"    bytes_enviados = psutil.net_io_counters()[0]\\n\" +\n" +
                "                    \"    bytes_recebidos = psutil.net_io_counters()[1]\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    destino = \\\"google.com\\\"  \\n\" +\n" +
                "                    \"    latencia = ping3.ping(destino) * 1000\\n\" +\n" +
                "                    \"    if(latencia > 40 and latencia > 60):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\"⚠️Alerta no ping da maquina: {idRobo} está em estado de alerta\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"    if(latencia > 60 and latencia > 80):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\"⚠️Alerta no ping da maquina: {idRobo} está em estado critico\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"    if(latencia > 80):\\n\" +\n" +
                "                    \"        alerta = {\\\"text\\\": f\\\"⚠️Alerta no ping da maquina: {idRobo} está em estado de urgencia\\\"}\\n\" +\n" +
                "                    \"        requests.post(webhook, data=json.dumps(alerta))\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    if latencia is not None:\\n\" +\n" +
                "                    \"        print(f\\\"Latência para {destino}: {latencia:.2f} ms\\\")\\n\" +\n" +
                "                    \"    else:\\n\" +\n" +
                "                    \"        print(f\\\"Não foi possível alcançar {destino}\\\")  \\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    if network_active:\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"        print (\\\"A rede está ativa.\\\")\\n\" +\n" +
                "                    \"        statusRede= 1\\n\" +\n" +
                "                    \"    else:\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"        print (\\\"A rede não está ativa.\\\")\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"    #Outros\\n\" +\n" +
                "                    \"    boot_time = datetime.fromtimestamp(psutil.boot_time()).strftime(\\\"%Y-%m-%d %H:%M:%S\\\")\\n\" +\n" +
                "                    \"    print(\\\"A maquina está ligada desde: \\\",boot_time)\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"    horarioAtual = datetime.now()\\n\" +\n" +
                "                    \"    horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    ins = [cpuPorcentagem, cpuVelocidadeEmGhz, tempoSistema, processos, memoriaPorcentagem,\\n\" +\n" +
                "                    \"           memoriaTotal, memoriaUsada, memoriaSwapPorcentagem, memoriaSwapUso, statusRede, latencia,\\n\" +\n" +
                "                    \"           bytes_enviados, bytes_recebidos]\\n\" +\n" +
                "                    \"    componentes = [1,2,3,4,5,6,7,8,9,15,16,17,18]\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    cursor = connection.cursor()\\n\" +\n" +
                "                    \"    server_cursor = sqlserver_connection.cursor()\\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"    for i in range(len(ins)):\\n\" +\n" +
                "                    \"        dado = ins[i]\\n\" +\n" +
                "                    \"        componente = componentes[i]\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"        query = \\\"INSERT INTO Registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (%s, %s, %s, %s)\\\"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"        #banco de contenção abaixo\\n\" +\n" +
                "                    \"        cursor.execute(query, (dado, 22, componente, horarioFormatado))\\n\" +\n" +
                "                    \"        server_cursor.execute(query, (dado, idRobo, componente, horarioFormatado))\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"        \\n\" +\n" +
                "                    \"        connection.commit()\\n\" +\n" +
                "                    \"        sqlserver_connection.commit()\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"       \\n\" +\n" +
                "                    \"    print(\\\"\\\\nINFORMAÇÕES SOBRE PROCESSAMENTO: \\\")\\n\" +\n" +
                "                    \"    print('\\\\nPorcentagem utilizada da CPU: ',cpuPorcentagem,\\n\" +\n" +
                "                    \"          '\\\\nVelocidade da CPU: ',cpuVelocidadeEmGhz,\\n\" +\n" +
                "                    \"          '\\\\nTempo de atividade da CPU: ', tempoSistema,\\n\" +\n" +
                "                    \"          '\\\\nNumero de processos: ', processos,\\n\" +\n" +
                "                    \"          '\\\\nPorcentagem utilizada de memoria: ', memoriaPorcentagem,\\n\" +\n" +
                "                    \"          '\\\\nQuantidade usada de memoria: ', memoriaTotal,\\n\" +\n" +
                "                    \"          '\\\\nPorcentagem usada de memoria Swap: ', memoriaSwapPorcentagem,\\n\" +\n" +
                "                    \"          '\\\\nQuantidade usada de memoria Swap: ', memoriaSwapUso,\\n\" +\n" +
                "                    \"          '\\\\nBytes enviados', bytes_enviados,\\n\" +\n" +
                "                    \"          '\\\\nBytes recebidos', bytes_recebidos)\\n\" +\n" +
                "                    \"   \\n\" +\n" +
                "                    \"    \\n\" +\n" +
                "                    \"       \\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"    time.sleep(5)\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"cursor.close()\\n\" +\n" +
                "                    \"server_cursor.close()\\n\" +\n" +
                "                    \"connection.close()\\n\" +\n" +
                "                    \"sqlserver_connection.close()\\n\" +\n" +
                "                    \"    \\n")

    }

    fun cadastroMaquina(fkHospital: Int) {

        conexDbServer.execute(
            """
                INSERT INTO RoboCirurgiao (modelo, fabricacao, fkStatus, idProcess, fkHospital) 
VALUES ('Modelo A', '${Looca().processador.fabricante}', 1, '$id', $fkHospital);
                
                """
        )
        println("parabéns robo cadastrado baixando agora a solução MEDCONNECT")

        installPyhon()
        coletaDeDados()

    }


    fun loguinMaquina() {
        var pode = false
        println("para verificar se tem as credenciais necessárias digite seu email")
        var email = cli.nextLine()
        println("")
        println("agora sua senha")
        var senha = cli.nextLine()
        println("")

        var fkHospital = conexDbServer.queryForObject(
            """
    select fkHospital from usuario
    where email = '$email' AND senha = '$senha'
    """,
            Int::class.java
        )


        if (fkHospital != null) {
            pode = true
        }


        if (pode) {
            print("começando a registrar a maquina em nosso banco de dados")
            cadastroMaquina(fkHospital)

        } else {
            println("problema de autenticação")
        }


    }
}


fun main() {

    val janel = AppJanelas()
    janel.verificacao()
}
