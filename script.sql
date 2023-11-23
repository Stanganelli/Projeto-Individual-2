
	DROP DATABASE IF EXISTS medconnect;
	CREATE DATABASE IF NOT EXISTS medconnect;
	USE medconnect;

	-- Crie a tabela Hospital
	CREATE TABLE IF NOT EXISTS Hospital (
		idHospital INT PRIMARY KEY AUTO_INCREMENT,
		nomeFantasia VARCHAR(45) NOT NULL,
		CNPJ CHAR(14) NOT NULL,
		razaoSocial VARCHAR(45) NOT NULL,
		sigla VARCHAR(45) NOT NULL,
		responsavelLegal VARCHAR(45) NOT NULL,
		fkHospitalSede INT,
		CONSTRAINT fkHospitalSede FOREIGN KEY (fkHospitalSede) REFERENCES Hospital (idHospital)
	);

	-- Inserir dados na tabela Hospital
	INSERT INTO Hospital (nomeFantasia, CNPJ, razaoSocial, sigla, responsavelLegal, fkHospitalSede) 
	VALUES 
		('Hospital ABC', '12345678901234', 'ABC Ltda', 'HABC', 'João da Silva', NULL),
		('Hospital Einstein', '12325678901234', 'Einstein Ltda', 'HEIN', 'Maria Silva', NULL);

	-- Crie a tabela EscalonamentoUsuario
	CREATE TABLE IF NOT EXISTS EscalonamentoUsuario (
		idEscalonamento INT PRIMARY KEY AUTO_INCREMENT,
		cargo VARCHAR(45) NOT NULL,
		prioridade INT NOT NULL
	);

	-- Inserir dados na tabela EscalonamentoUsuario
	INSERT INTO EscalonamentoUsuario (cargo, prioridade) 
	VALUES 
		('Atendente', 1),
		('Engenheiro De Noc', 2),
		('Admin', 3);

	-- Crie a tabela Usuario
	CREATE TABLE IF NOT EXISTS Usuario (
		idUsuario INT AUTO_INCREMENT,
		nome VARCHAR(45) NOT NULL,
		email VARCHAR(45) NOT NULL,
		CPF VARCHAR(15) NOT NULL,
		telefone VARCHAR(15) NOT NULL,
		senha VARCHAR(45) NOT NULL,
		fkHospital INT,
		fkEscalonamento INT,
		PRIMARY KEY (idUsuario, fkHospital),
		CONSTRAINT fkHospital FOREIGN KEY (fkHospital) REFERENCES Hospital (idHospital),
		CONSTRAINT fkEscalonamento FOREIGN KEY (fkEscalonamento) REFERENCES EscalonamentoUsuario (idEscalonamento)
	);

	-- Inserir dados na tabela Usuario
	INSERT INTO Usuario (nome, email, CPF, telefone, senha, fkHospital, fkEscalonamento) 
	VALUES 
		('Kayky', 'kayky@abc.com', '12345678901', '987654321', '123456', 1, 1),
		('Gabriel', 'gabriel@email.com', '12345678901', '987654321', '123456', 1, 2),
		('Maria Souza', 'maria@example.com', '12345678901', '987654321', 'senha123', 1, 3);

	-- Crie a tabela statusRobo
	CREATE TABLE IF NOT EXISTS statusRobo (
		idStatus INT PRIMARY KEY AUTO_INCREMENT,
		nome VARCHAR(45) NOT NULL
	);

	-- Inserir dados na tabela statusRobo
	INSERT INTO statusRobo (nome) 
	VALUES ('Ativo');

	-- Crie a tabela RoboCirurgiao
	CREATE TABLE IF NOT EXISTS RoboCirurgiao (
		idRobo INT PRIMARY KEY AUTO_INCREMENT,
		modelo VARCHAR(45) NOT NULL,
		fabricacao VARCHAR(45) NOT NULL,
		idProcess VARCHAR(45),
		telaAtual varchar(40),
		fkStatus INT,
		fkHospital INT,
		CONSTRAINT fkStatus FOREIGN KEY (fkStatus) REFERENCES statusRobo (idStatus),
		CONSTRAINT fkHospitalRobo FOREIGN KEY (fkHospital) REFERENCES Hospital (idHospital)
	);
    
    create table if not exists Janela(
    idJanela int primary key auto_increment,
    Janela_atual varchar(200),
    ativo tinyint,
    fkMaquina int,
    constraint fkMaquina foreign key (fkMaquina) references RoboCirurgiao (idRobo)
    );
    
    create table if not exists Janela_fechada(
    idJanela_fechada int primary key auto_increment,
    janela_a_fechar varchar(200),
    sinal_terminacao tinyint,
    fkMaquina1 int,
    constraint fkMaquina1 foreign key (fkMaquina1) references RoboCirurgiao (idRobo)
    );



	-- Inserir dados na tabela RoboCirurgiao
	INSERT INTO RoboCirurgiao (modelo, fabricacao, fkStatus, fkHospital, idProcess) 
	VALUES ('Modelo A', '2023-09-12', 1, 1, 'B2532B6');

	-- Crie a tabela SalaCirurgiao
	CREATE TABLE IF NOT EXISTS SalaCirurgiao (
		idSala INT AUTO_INCREMENT,
		numero VARCHAR(5) NOT NULL,
		fkHospitalSala INT,
		fkRoboSala INT,
		PRIMARY KEY (idSala, fkHospitalSala, fkRoboSala),
		CONSTRAINT fkHospitalSala FOREIGN KEY (fkHospitalSala) REFERENCES hospital (idHospital),
		CONSTRAINT fkRoboSala FOREIGN KEY (fkRoboSala) REFERENCES robocirurgiao (idRobo)
	);

	-- Inserir dados na tabela SalaCirurgiao
	INSERT INTO SalaCirurgiao (numero, fkHospitalSala, fkRoboSala) 
	VALUES ('101', 1, 1);

	-- Crie a tabela categoriaCirurgia
	CREATE TABLE IF NOT EXISTS categoriaCirurgia (
		idCategoria INT PRIMARY KEY AUTO_INCREMENT,
		niveisPericuloridade VARCHAR(45) NOT NULL
	);

	-- Inserir dados na tabela categoriaCirurgia
	INSERT INTO categoriaCirurgia (niveisPericuloridade) 
	VALUES ("Muito baixo"),
    ("Baixo"),
    ("Médio"),
    ("Alto"),
    ("Muito Alto");

	-- Crie a tabela cirurgia
	CREATE TABLE IF NOT EXISTS cirurgia (
    idCirurgia INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    dataInicio DATETIME NOT NULL,
    nomeMedico VARCHAR(45),
    duracao INT,
    nomePaciente VARCHAR(45),
    tipo VARCHAR(45),
    fkRoboCirurgia INT,
    CONSTRAINT fkRoboCirurgia FOREIGN KEY (fkRoboCirurgia) REFERENCES RoboCirurgiao (idRobo),
    fkCategoria INT,
    CONSTRAINT fkCategoria FOREIGN KEY (fkCategoria) REFERENCES categoriaCirurgia (idCategoria)
);

	-- Inserir dados na tabela cirurgia
	INSERT INTO cirurgia (idCirurgia, fkRoboCirurgia, dataInicio, nomeMedico, duracao, nomePaciente, tipo, fkCategoria) 
	VALUES (1, 1, '2023-09-15 14:00:00', "Dr. Henrique Castro", 134, "Alberto Fernandez","cardiologia",1);

	-- Crie a tabela Metrica
	CREATE TABLE IF NOT EXISTS Metrica (
		idMetrica INT PRIMARY KEY AUTO_INCREMENT,
		alerta DOUBLE,
		urgente DOUBLE,
		critico DOUBLE,
		tipo_dado VARCHAR(50)
	);

	-- Inserir Métricas de CPU
	INSERT INTO Metrica (idMetrica, alerta, urgente, critico, tipo_dado)
	VALUES
	(1, 60, 70, 80, 'Porcentagem de Uso'),
	(3, 10700, 10800, 10900, 'Tempo no sistema');

	-- Inserir Métricas de RAM
	INSERT INTO Metrica (idMetrica, alerta, urgente, critico, tipo_dado)
	VALUES
	(5, 90.1, 93, 95, 'Porcentagem de Uso'),
	(6, 17, 18, 20, 'Porcentagem da memoria Swap');

	-- Inserir Métricas de Disco Rígido
	INSERT INTO Metrica (idMetrica, alerta, urgente, critico, tipo_dado)
	VALUES
	(8, 70, 80, 90, 'Porcentagem de Uso');

	-- Inserir Métricas de Rede
	INSERT INTO Metrica (idMetrica, alerta, urgente, critico, tipo_dado)
	VALUES
	(10, 40, 60, 80, 'Latência de Rede');

	-- Crie a tabela categoriaComponente
	CREATE TABLE IF NOT EXISTS categoriaComponente (
		idCategoriaComponente INT PRIMARY KEY AUTO_INCREMENT,
		nome VARCHAR(45) NOT NULL
	);

	-- Inserir dados na tabela categoriaComponente
	INSERT INTO categoriaComponente (idCategoriaComponente, nome) VALUES
		(1, 'CPU'),
		(2, 'Memória RAM'),
		(3, 'Disco'),
		(4, 'Rede'),
        (5, 'Processos');

	-- Crie a tabela componentes
	CREATE TABLE IF NOT EXISTS componentes (
		idComponentes INT PRIMARY KEY AUTO_INCREMENT,
		nome VARCHAR(45) NOT NULL,
		unidade VARCHAR(10),
		descricaoAdd VARCHAR(45),
		fkCategoriaComponente INT,
		fkMetrica INT,
		CONSTRAINT fkCategoriaComponente FOREIGN KEY (fkCategoriaComponente) REFERENCES categoriaComponente (idCategoriaComponente),
		CONSTRAINT frkMetrica FOREIGN KEY (fkMetrica) REFERENCES Metrica (idMetrica)
	);
    
    select * from usuario;
    
		

	-- Inserir CPU
	INSERT INTO componentes (nome, unidade, fkCategoriaComponente, fkMetrica) 
	VALUES ('Porcentagem da CPU', "%", 1, 1),
	("Velocidade da CPU", "GHz", 1, null),
	("Tempo no sistema da CPU", "s", 1, 3),
	("Processos da CPU", null, 1, null),
	("Temperatura da CPU", "°C", 1, null),
	("Total de processos", "processos", 1, null),
	("Total de Threads", "threads", 1, null);

	-- Inserir Memória RAM
	INSERT INTO componentes (nome, unidade, fkCategoriaComponente, fkMetrica) 
	VALUES ('Porcentagem da Memoria', '%', 2, 5),
	('Total da Memoria', 'GB', 2, null),
	('Uso da Memoria', 'GB', 2, null),
	('Porcentagem da Memoria Swap', '%',2,6),
	('Uso da Memoria Swap', 'GB', 2, null);

	-- Inserir Disco
	INSERT INTO componentes (nome, unidade, fkCategoriaComponente, fkMetrica) 
	VALUES ('Porcentagem do Disco', '%', 3, 8),
	('Total do Disco', 'GB', 3, null),
	('Uso do Disco', 'GB', 3, null),
	('Tempo de Leitura do Disco', 's', 3, null),
	('Tempo de Escrita do Disco', 's', 3, null);

	-- Inserir Rede
	INSERT INTO componentes (nome, descricaoAdd, fkCategoriaComponente, fkMetrica) 
	VALUES ('Status da Rede', 'Conexao da Rede', 4, null),
	("Latencia de Rede", 'Latencia em MS', 4, 10),
	('Bytes enviados','Bytes enviados da Rede', 4, null),
	('Bytes recebidos','Bytes recebidos da Rede', 4, null);
    
    INSERT INTO componentes (nome, descricaoAdd, fkCategoriaComponente, fkMetrica) 
	VALUES ('Quantidade de processos', 'Quantidades de processos em execução', 5, null);

	CREATE TABLE dispositivos_usb (
		id INT AUTO_INCREMENT PRIMARY KEY,
		nome VARCHAR(255),
		dataHora DATETIME,
		id_produto VARCHAR(10),
		fornecedor VARCHAR(255),
		conectado BOOLEAN,
		fkRoboUsb int , 
	constraint fkRoboUsb foreign key (fkRoboUsb) references  RoboCirurgiao(idRobo)
	);

	-- Crie a tabela Registros
	CREATE TABLE IF NOT EXISTS Registros (
		idRegistro INT AUTO_INCREMENT,
		fkRoboRegistro INT,
		HorarioDado DATETIME NOT NULL,
		dado DOUBLE NOT NULL,
		fkComponente INT,
		PRIMARY KEY (idRegistro, fkRoboRegistro),
		CONSTRAINT fkRoboRegistro FOREIGN KEY (fkRoboRegistro) REFERENCES RoboCirurgiao (idRobo),
		CONSTRAINT fkComponente FOREIGN KEY (fkComponente) REFERENCES componentes (idComponentes)
	);
    

	-- Crie a tabela Alerta
	CREATE TABLE IF NOT EXISTS Alerta (
		idAlerta INT PRIMARY KEY AUTO_INCREMENT,
		tipo_alerta VARCHAR(15),
		fkRegistro INT,
		fkRobo INT,
		dtHora DATETIME,
		nome_componente VARCHAR(45),
		dado DOUBLE
	);

	-- Crie a tabela quantidadeAlerta
	CREATE TABLE IF NOT EXISTS quantidadeAlerta (
		idQuantidadeAlerta INT PRIMARY KEY AUTO_INCREMENT,
		tipo_alerta VARCHAR(10),
		dtHora DATETIME
	);

	-- Crie o gatilho criarAlerta
	DELIMITER $$
	CREATE TRIGGER criarAlerta
	AFTER INSERT ON Registros
	FOR EACH ROW
	BEGIN
		DECLARE id_metrica INT;
		DECLARE v_alerta DOUBLE;
		DECLARE v_urgente DOUBLE;
		DECLARE v_critico DOUBLE;
		DECLARE v_componente VARCHAR(45);

		SELECT fkMetrica, nome INTO id_metrica, v_componente
		FROM componentes
		WHERE NEW.fkComponente = idComponentes;

		SELECT critico, urgente, alerta INTO v_critico, v_urgente, v_alerta
		FROM Metrica
		WHERE idMetrica = id_metrica;

		IF NEW.dado >= v_critico THEN
			INSERT INTO Alerta (tipo_alerta, fkRegistro, fkRobo, dtHora, nome_componente, dado)
			VALUES ('critico', NEW.idRegistro, NEW.fkRoboRegistro, NOW(), v_componente, NEW.dado);
		ELSEIF NEW.dado >= v_urgente THEN
			INSERT INTO Alerta (tipo_alerta, fkRegistro, fkRobo, dtHora, nome_componente, dado)
			VALUES ('urgente', NEW.idRegistro, NEW.fkRoboRegistro, NOW(), v_componente, NEW.dado);
		ELSEIF NEW.dado >= v_alerta THEN
			INSERT INTO Alerta (tipo_alerta, fkRegistro, fkRobo, dtHora, nome_componente, dado)
			VALUES ('alerta', NEW.idRegistro, NEW.fkRoboRegistro, NOW(), v_componente, NEW.dado);
		END IF;
	END;
	$$ DELIMITER ;

	-- Crie o procedimento inserir_qtd_alerta
	DELIMITER $$
	CREATE PROCEDURE inserir_qtd_alerta()
	BEGIN
	DECLARE qtdAlertaAlerta INT;
	DECLARE qtdAlertaUrgente INT;
	DECLARE qtdAlertaCritico INT;
		
	SELECT COUNT(idAlerta) FROM Alerta
	WHERE tipo_alerta = "alerta" 
	AND dtHora <= date_sub(now(), INTERVAL 1 MINUTE)
	INTO qtdAlertaAlerta;
		
	SELECT COUNT(idAlerta) FROM Alerta
	WHERE tipo_alerta = "urgente" 
	AND dtHora <= date_sub(now(), INTERVAL 1 MINUTE)
	INTO qtdAlertaUrgente;
		
	SELECT COUNT(idAlerta) FROM Alerta
	WHERE tipo_alerta = "critico" 
	AND dtHora <= date_sub(now(), INTERVAL 1 MINUTE)
	INTO qtdAlertaCritico;
		
	IF qtdAlertaAlerta > 15 THEN
	INSERT INTO quantidadeAlerta (tipo_alerta, dtHora)
	VALUES ("alerta", now());
	END IF;
		
	IF qtdAlertaUrgente > 15 THEN
	INSERT INTO quantidadeAlerta (tipo_alerta, dtHora) 
	VALUES ("urgente", now());
	END IF;
		
	IF qtdAlertaCritico > 15 THEN
	INSERT INTO quantidadeAlerta (tipo_alerta, dtHora) 
	VALUES ("critico", now());
	END IF;
		
	END;
	 $$ DELIMITER ;

	DELIMITER $$
	CREATE PROCEDURE inserir_dados()
	BEGIN
	INSERT INTO Registros VALUES 
	(null, 1, now(), 0.98, 1),
	(null, 1, now(), 0.6, 1),
	(null, 1, now(), 0.7, 1);
	END;
	$$ DELIMITER 

	-- Criar a tabela processos
	CREATE TABLE IF NOT EXISTS Processos (
	    idProcesso INT PRIMARY KEY AUTO_INCREMENT,
	    pid INT,
	    nome VARCHAR(100),
	    processo_status VARCHAR(20),
	    momento_inicio DATETIME,
	    data_hora_captura DATETIME,
	    fkRobo INT,
			CONSTRAINT fkRoboProcesso FOREIGN KEY (fkRobo) REFERENCES RoboCirurgiao (idRobo)
	);

	-- Select Bianca
	SELECT registros.HorarioDado, round(registros.dado, 2) AS dado, componentes.nome, componentes.unidade 
	FROM registros 
	JOIN componentes 
	ON componentes.idComponentes = registros.fkComponente;
		
	-- Mostrar as tabelas do banco de dados
	SHOW TABLES;
