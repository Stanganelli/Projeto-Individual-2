
from mysql.connector import connect
import psutil
import platform
import time
import mysql.connector
from datetime import datetime


idRobo = 1

#descomentar abaixo para gerar pelo kotlin
#idRobo = #${roboId}



def mysql_connection(host, user, passwd, database=None):
    connection = connect(
        host=host,
        user=user,
        passwd=passwd,
        database=database
    )
    return connection

def bytes_para_gb(bytes_value):
    return bytes_value / (1024 ** 3)

def milissegundos_para_segundos(ms_value):
    return ms_value / 1000

connection = mysql_connection('localhost', 'medconnect', 'medconnect123', 'medconnect')



meu_so = platform.system()
if(meu_so == "Linux"):
    nome_disco = '/'
    disco = psutil.disk_usage(nome_disco)
elif(meu_so == "Windows"):
    nome_disco = 'C:\\'
disco = psutil.disk_usage(nome_disco)
discoPorcentagem = disco.percent
discoTotal = "{:.2f}".format(bytes_para_gb(disco.total))
discoUsado = "{:.2f}".format(bytes_para_gb(disco.used)) 
discoTempoLeitura = milissegundos_para_segundos(psutil.disk_io_counters(perdisk=False, nowrap=True)[4])
discoTempoEscrita = milissegundos_para_segundos(psutil.disk_io_counters(perdisk=False, nowrap=True)[5])

ins = [discoPorcentagem, discoTotal, discoUsado, discoTempoLeitura, discoTempoEscrita]
componentes = [10,11,12,13,14]

horarioAtual = datetime.now()
horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')

cursor = connection.cursor()
for i in range(len(ins)):
        
    dado = ins[i]
        
    componente = componentes[i]
    
    query = "INSERT INTO Registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (%s, %s, %s, %s)"
    
    cursor.execute(query, (dado, idRobo, componente, horarioFormatado))



print("\nDisco porcentagem:", discoPorcentagem,
          "\nDisco total:", discoTotal,
          '\nTempo de leitura do disco em segundos:', discoTempoLeitura,
          '\nTempo de escrita do disco em segundos:', discoTempoEscrita)


while True:

    #CPU
    cpuPorcentagem = psutil.cpu_percent(None)
    frequenciaCpuMhz = psutil.cpu_freq(percpu=False)
    cpuVelocidadeEmGhz = "{:.2f}".format(frequenciaCpuMhz.current / 1000)
    tempoSistema = psutil.cpu_times()[1] 
    processos = len(psutil.pids())
        


    memoriaPorcentagem = psutil.virtual_memory()[2]
    memoriaTotal = "{:.2f}".format(bytes_para_gb(psutil.virtual_memory().total))
    memoriaUsada = "{:.2f}".format(bytes_para_gb(psutil.virtual_memory().used))
    memoriaSwapPorcentagem = psutil.swap_memory().percent
    memoriaSwapUso = "{:.2f}".format(bytes_para_gb(psutil.swap_memory().used))

    

    #Outros
    boot_time = datetime.fromtimestamp(psutil.boot_time()).strftime("%Y-%m-%d %H:%M:%S")
    print("A maquina está ligada desde: ",boot_time)

    horarioAtual = datetime.now()
    horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')
    
    ins = [cpuPorcentagem, cpuVelocidadeEmGhz, tempoSistema, processos, memoriaPorcentagem,
           memoriaTotal, memoriaUsada]
    componentes = [1,2,3,4,5,6,7,8,9,15,16,17,18]
    
    cursor = connection.cursor()
    
    for i in range(len(ins)):
        dado = ins[i]
        componente = componentes[i]

        query = "INSERT INTO Registros (dado, fkRoboRegistro, fkComponente, HorarioDado) VALUES (%s, %s, %s, %s)"

        cursor.execute(query, (dado, idRobo, componente, horarioFormatado))
        connection.commit()

       
    print("\nINFORMAÇÕES SOBRE PROCESSAMENTO: ")
    print('\nPorcentagem utilizada da CPU: ',cpuPorcentagem,
          '\nVelocidade da CPU: ',cpuVelocidadeEmGhz,
          '\nTempo de atividade da CPU: ', tempoSistema,
          '\nNumero de processos: ', processos,
          '\nPorcentagem utilizada de memoria: ', memoriaPorcentagem,
          '\nQuantidade usada de memoria: ', memoriaTotal)
       
    
       


    time.sleep(5)

cursor.close()
connection.close()
    
