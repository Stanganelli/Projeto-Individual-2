
from mysql.connector import connect
import psutil
import platform
import time
import mysql.connector
from datetime import datetime
import ping3
import json
import requests

#alerta = {"text": "alerta"}

webhook = "https://hooks.slack.com/services/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"
#requests.post(webhook, data=json.dumps(alerta))


idRobo = 1

#descomente abaixo quando for ora criar esse arquivo peo kotlin
idRobo = 2



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

#Disco

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
    if(cpuPorcentagem > 60 and cpuPorcentagem > 70):
        alerta = {"text": f"alerta na cpu da maquina: {idRobo} está em estado de alerta"}
        requests.post(webhook, data=json.dumps(alerta))
    if(cpuPorcentagem > 70 and cpuPorcentagem > 80):
        alerta = {"text": f"alerta na cpu da maquina: {idRobo} está em estado critico"}
        requests.post(webhook, data=json.dumps(alerta))
    if(cpuPorcentagem > 80):
        alerta = {"text": f"alerta na cpu da maquina: {idRobo} está em estado de urgencia"}
        requests.post(webhook, data=json.dumps(alerta))
        



    
    #Memoria
    memoriaPorcentagem = psutil.virtual_memory()[2]
    memoriaTotal = "{:.2f}".format(bytes_para_gb(psutil.virtual_memory().total))
    memoriaUsada = "{:.2f}".format(bytes_para_gb(psutil.virtual_memory().used))
    memoriaSwapPorcentagem = psutil.swap_memory().percent
    memoriaSwapUso = "{:.2f}".format(bytes_para_gb(psutil.swap_memory().used))
    if(memoriaPorcentagem > 60 and memoriaPorcentagem > 70):
        alerta = {"text": f"⚠️  Alerta na ram da maquina: {idRobo} está em estado de alerta"}
        requests.post(webhook, data=json.dumps(alerta))
    if(memoriaPorcentagem > 70 and memoriaPorcentagem > 80):
        alerta = {"text": f"⚠️  Alerta na ram da maquina: {idRobo} está em estado critico"}
        requests.post(webhook, data=json.dumps(alerta))  
    if(memoriaPorcentagem > 80):
        alerta = {"text": f" ⚠️  Alerta na ram da maquina: {idRobo} está em estado de urgencia"}
        requests.post(webhook, data=json.dumps(alerta))
    
    """
    Por enquanto não será usado
    for particao in particoes:
        try:
            info_dispositivo = psutil.disk_usage(particao.mountpoint)
            print("Ponto de Montagem:", particao.mountpoint)
            print("Sistema de Arquivos:", particao.fstype)
            print("Dispositivo:", particao.device)
            print("Espaço Total: {0:.2f} GB".format(info_dispositivo.total / (1024 ** 3)) )
            print("Espaço Usado: {0:.2f} GB".format(info_dispositivo.used / (1024 ** 3)) )
            print("Espaço Livre: {0:.2f} GB".format(info_dispositivo.free / (1024 ** 3)) )
            print("Porcentagem de Uso: {0:.2f}%".format(info_dispositivo.percent))
            print()
        except PermissionError as e:
            print(f"Erro de permissão ao acessar {particao.mountpoint}: {e}")
        except Exception as e:
            print(f"Erro ao acessar {particao.mountpoint}: {e}")
            """
    #Rede
    interval = 1
    statusRede = 0
    network_connections = psutil.net_connections()
    network_active = any(conn.status == psutil.CONN_ESTABLISHED for conn in network_connections)
    bytes_enviados = psutil.net_io_counters()[0]
    bytes_recebidos = psutil.net_io_counters()[1]
    
    destino = "google.com"  
    latencia = ping3.ping(destino) * 1000
    if(latencia > 40 and latencia > 60):
        alerta = {"text": f"⚠️Alerta no ping da maquina: {idRobo} está em estado de alerta"}
        requests.post(webhook, data=json.dumps(alerta))
    if(latencia > 60 and latencia > 80):
        alerta = {"text": f"⚠️Alerta no ping da maquina: {idRobo} está em estado critico"}
        requests.post(webhook, data=json.dumps(alerta))
    if(latencia > 80):
        alerta = {"text": f"⚠️Alerta no ping da maquina: {idRobo} está em estado de urgencia"}
        requests.post(webhook, data=json.dumps(alerta))
    
    if latencia is not None:
        print(f"Latência para {destino}: {latencia:.2f} ms")
    else:
        print(f"Não foi possível alcançar {destino}")  

    
    if network_active:

        print ("A rede está ativa.")
        statusRede= 1
    else:

        print ("A rede não está ativa.")

    #Outros
    boot_time = datetime.fromtimestamp(psutil.boot_time()).strftime("%Y-%m-%d %H:%M:%S")
    print("A maquina está ligada desde: ",boot_time)

    horarioAtual = datetime.now()
    horarioFormatado = horarioAtual.strftime('%Y-%m-%d %H:%M:%S')
    
    ins = [cpuPorcentagem, cpuVelocidadeEmGhz, tempoSistema, processos, memoriaPorcentagem,
           memoriaTotal, memoriaUsada, memoriaSwapPorcentagem, memoriaSwapUso, statusRede, latencia,
           bytes_enviados, bytes_recebidos]
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
          '\nQuantidade usada de memoria: ', memoriaTotal,
          '\nPorcentagem usada de memoria Swap: ', memoriaSwapPorcentagem,
          '\nQuantidade usada de memoria Swap: ', memoriaSwapUso,
          '\nBytes enviados', bytes_enviados,
          '\nBytes recebidos', bytes_recebidos)
   
    
       


    time.sleep(5)

cursor.close()
connection.close()
    