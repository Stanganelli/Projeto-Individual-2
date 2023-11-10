import com.github.britooo.looca.api.core.Looca
import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import javax.swing.JOptionPane
import java.io.File
import java.util.Scanner

class AppJanelas {
    var conexDb: JdbcTemplate
    var id = Looca().processador.id
    var cli = Scanner(System.`in`)


    init {
        val dataSource = BasicDataSource()
        dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
        val serverName = "localhost"
        val mydatabase = "medconnect"
        dataSource.username = "medconnect"
        dataSource.password = "medconnect123"
        dataSource.url = "jdbc:mysql://$serverName/$mydatabase"
        conexDb = JdbcTemplate(dataSource)
    }


    fun verificacao() {


        val qtdIds = conexDb.queryForObject(
            """
    select count(*) as count from RoboCirurgiao where idProcess = '$id'
    """,
            Int::class.java,
        )

        if (qtdIds == 0) {
            loguinMaquina()
        } else {
            coletaDeDados()
        }

    }

    fun coletaDeDados() {

    }

    fun installPyhon() {

        val roboId = conexDb.queryForObject(
            """
    select idRobo from RoboCirurgiao where idProcess = '$id'
    """,
            Int::class.java,
        )


        var nome = "SolucaoJanelas.py"
        var arqivoPython = File(nome)
        arqivoPython.writeText("\n" +
                "from mysql.connector import connect\n" +
                "import psutil\n" +
                "import platform\n" +
                "import time\n" +
                "import mysql.connector\n" +
                "from datetime import datetime\n" +
                "\n" +
                "\n" +
                "idRobo = 1\n" +
                "\n" +
                "#descomentar abaixo para gerar pelo kotlin\n" +
                "idRobo = #${roboId}\n" +
                "\n" +
                "\n" +
                "\n" +
                "def mysql_connection(host, user, passwd, database=None):\n" +
                "    connection = connect(\n" +
                "        host=host,\n" +
                "        user=user,\n" +
                "        passwd=passwd,\n" +
                "        database=database\n" +
                "    )\n" +
                "    return connection\n" +
                "\n" +
                "def bytes_para_gb(bytes_value):\n" +
                "    return bytes_value / (1024 ** 3)\n" +
                "\n" +
                "def milissegundos_para_segundos(ms_value):\n" +
                "    return ms_value / 1000\n" +
                "\n" +
                "connection = mysql_connection('localhost', 'medconnect', 'medconnect123', 'medconnect')\n" +
                "\n" +
                "\n" +
                "\n" +
                "meu_so = platform.system()\n" +
                "if(meu_so == \"Linux\"):\n" +
                "    nome_disco = '/'\n" +
                "    disco = psutil.disk_usage(nome_disco)\n" +
                "elif(meu_so == \"Windows\"):\n" +
                "    nome_disco = 'C:\\\\'\n" +
                "disco = psutil.disk_usage(nome_disco)\n" +
                "discoPorcentagem = disco.percent\n" +
                "discoTotal = \"{:.2f}\".format(bytes_para_gb(disco.total))\n" +
                "discoUsado = \"{:.2f}\".format(bytes_para_gb(disco.used)) \n" +
                "discoTempoLeitura = milissegundos_para_segundos(psutil.disk_io_counters(perdisk=False, nowrap=True)[4])\n" +
                "discoTempoEscrita = milissegundos_para_segundos(psutil.disk_io_counters(perdisk=False, nowrap=True)[5])\n" +
                "\n" +
                "ins = [discoPorcentagem, discoTotal, discoUsado, discoTempoLeitura, discoTempoEscrita]\n" +
                "componentes = [10,11,12,13,14]\n" +
                "\n" +
                "horarioAtual = datetime.now()\n" +
                "horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')\n" +
                "\n" +
                "cursor = connection.cursor()\n" +
                "for i in range(len(ins)):\n" +
                "        \n" +
                "    dado = ins[i]\n" +
                "        \n" +
                "    componente = componentes[i]\n" +
                "    \n" +
                "    query = \"INSERT INTO Registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (%s, %s, %s, %s)\"\n" +
                "    \n" +
                "    cursor.execute(query, (dado, idRobo, componente, horarioFormatado))\n" +
                "\n" +
                "\n" +
                "\n" +
                "print(\"\\nDisco porcentagem:\", discoPorcentagem,\n" +
                "          \"\\nDisco total:\", discoTotal,\n" +
                "          '\\nTempo de leitura do disco em segundos:', discoTempoLeitura,\n" +
                "          '\\nTempo de escrita do disco em segundos:', discoTempoEscrita)\n" +
                "\n" +
                "\n" +
                "while True:\n" +
                "\n" +
                "    #CPU\n" +
                "    cpuPorcentagem = psutil.cpu_percent(None)\n" +
                "    frequenciaCpuMhz = psutil.cpu_freq(percpu=False)\n" +
                "    cpuVelocidadeEmGhz = \"{:.2f}\".format(frequenciaCpuMhz.current / 1000)\n" +
                "    tempoSistema = psutil.cpu_times()[1] \n" +
                "    processos = len(psutil.pids())\n" +
                "        \n" +
                "\n" +
                "\n" +
                "    memoriaPorcentagem = psutil.virtual_memory()[2]\n" +
                "    memoriaTotal = \"{:.2f}\".format(bytes_para_gb(psutil.virtual_memory().total))\n" +
                "    memoriaUsada = \"{:.2f}\".format(bytes_para_gb(psutil.virtual_memory().used))\n" +
                "    memoriaSwapPorcentagem = psutil.swap_memory().percent\n" +
                "    memoriaSwapUso = \"{:.2f}\".format(bytes_para_gb(psutil.swap_memory().used))\n" +
                "\n" +
                "    \n" +
                "\n" +
                "    #Outros\n" +
                "    boot_time = datetime.fromtimestamp(psutil.boot_time()).strftime(\"%Y-%m-%d %H:%M:%S\")\n" +
                "    print(\"A maquina está ligada desde: \",boot_time)\n" +
                "\n" +
                "    horarioAtual = datetime.now()\n" +
                "    horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')\n" +
                "    \n" +
                "    ins = [cpuPorcentagem, cpuVelocidadeEmGhz, tempoSistema, processos, memoriaPorcentagem,\n" +
                "           memoriaTotal, memoriaUsada]\n" +
                "    componentes = [1,2,3,4,5,6,7,8,9,15,16,17,18]\n" +
                "    \n" +
                "    cursor = connection.cursor()\n" +
                "    \n" +
                "    for i in range(len(ins)):\n" +
                "        dado = ins[i]\n" +
                "        componente = componentes[i]\n" +
                "\n" +
                "        query = \"INSERT INTO Registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (%s, %s, %s, %s)\"\n" +
                "\n" +
                "        cursor.execute(query, (dado, idRobo, componente, horarioFormatado))\n" +
                "        connection.commit()\n" +
                "\n" +
                "       \n" +
                "    print(\"\\nINFORMAÇÕES SOBRE PROCESSAMENTO: \")\n" +
                "    print('\\nPorcentagem utilizada da CPU: ',cpuPorcentagem,\n" +
                "          '\\nVelocidade da CPU: ',cpuVelocidadeEmGhz,\n" +
                "          '\\nTempo de atividade da CPU: ', tempoSistema,\n" +
                "          '\\nNumero de processos: ', processos,\n" +
                "          '\\nPorcentagem utilizada de memoria: ', memoriaPorcentagem,\n" +
                "          '\\nQuantidade usada de memoria: ', memoriaTotal)\n" +
                "       \n" +
                "    \n" +
                "       \n" +
                "\n" +
                "\n" +
                "    time.sleep(5)\n" +
                "\n" +
                "cursor.close()\n" +
                "connection.close()\n" +
                "    \n")

    }

    fun cadastroMaquina(fkHospital: Int) {

        conexDb.execute(
            """
                INSERT INTO RoboCirurgiao (modelo, fabricacao, fkStatus, idProcess, fkHospital) 
VALUES ('Modelo A', '${Looca().processador.fabricante}', 1, '$id', $fkHospital);
                
                """
        )
        println("parabéns robo cadastrado baixando agora a solução MEDCONNECT")

        installPyhon()

    }


    fun loguinMaquina() {
        var pode = false
        println("para verificar se tem as credenciais necessárias digite seu email")
        var email = cli.nextLine()
        println("")
        println("agora sua senha")
        var senha = cli.nextLine()
        println("")

        var fkHospital = conexDb.queryForObject(
            """
    select fkHospital from Funcionarios
    where email = '$email' AND senha = '$senha'
    """,
            Int::class.java
        )


        if (fkHospital != null) {
            pode = true
        }


        if (pode == true) {
            JOptionPane.showMessageDialog(
                null,
                "começando a registrar a maquina em nosso banco de dados"
            )
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