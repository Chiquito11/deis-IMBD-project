# DeisIMBD

Projeto de base de dados de filmes (scripts SQL + aplicação Java) para gerir/importar dados de filmes com a possibilidade de recorrer views, procedures e triggers.

## Índice
- [Estrutura das Diretorias](#estrutura)
- [Pré requisitos](#pré-requisitos)
- [Correr o Projeto](#Correr-Projeto)
    - [Configuração-do-servidor-SQL](#Configuração-do-servidor-SQL)
    - [Etapa 2](#Etapa-2)
    - [Etapa 3 - Procedures + Triggers + Views](#Etapa-3(Procedures+Triggers+Views))
    - [Etapa 3 - Aplicação Java](#Etapa-3(Aplicação-Java))

## Estrutura
- 1_Phase/
  - imbd-db.sql — esquema inicial ( não foi usado nas etapas 2 e 3)
- 2_Phase/
  - DB_Creation/
    - CreateDB.sql — criação do esquema após importação pelo Azure Data Studio
    - ImportInstructions.md — instruções para importar CSV (Azure Data Studio)
  - Listings/
    - Query9-Selects.sql — consultas da etapa 2
- 3_Phase/
  - deisIMBD-App/ — aplicação Java Swing
    - lib/ — jars externos (colocar driver JDBC aqui)
    - src/ — código Java (MainWindow, TestConnection, db, model, ui)
    - .vscode/
    - ERRORFIX.md — Se houver erros a executar a aplicação, consultar este file
  - Procedures/Procedures.sql, Test_Procedures.sql - Procedimentos da Etapa 3
  - Triggers/Triggers.sql - Triggers da Etapa 3
  - Views/Views.sql - Views da Etapa 3

## Pré-requisitos
- JDK 11+ (ou compatível)
- Servidor de BD compatível com os scripts (ex: SQL Server)
- Azure Data Studio (para importação CSV & o fornecido pelo .pdf de como se faz a importação dos dados)
- Driver JDBC no diretório 3_Phase/deisIMBD-App/lib/ (permite a ligação do server SQL com Java)
- SQL Server 2025 Express instalado
- Microsoft Server Management Studio 22

## Correr-Projeto

### Configuração-do-servidor-SQL
1. Adicionar a connexão no SQL Server(Ctrl+Alt+D) 
2. Profile Name: deis-imdb-project | servername: localhost\SQLEXPRESS | Trust server certificate: TRUE | Authentication Type: Windows Authentication
3. Clicar em connect
4. Clicar com o butão direito no deis-imdb-project e selecionar New Query e correr o comando seguinte:
```powershell
CREATE DATABASE [imbd-db];
GO
```
5. Abrir o Microsoft Server Management Studio 22
6. Meter a Conexão e ir a Security/Logins e clicar com o butão direito no sa e selecionar properties
7. Na aba General meter uma password nova e default database como imdb-db
8. Na aba Status meter o Login enabled
9. Clicar butao direito em cima do SQL Server , ir a Security e selecionar SQL Server and Windows Authentication mode

### Etapa-2
1. Começar por ler o ImportInstructions.md para importar os dados na diretoria Data usando o Azure Data Studio (Não foi por código porque foi nos fornecido o .pdf de como se importava usando o Azure Data Studio)

2. Após ter importado os dados corretamente, executa-se o ficheiro CreateDB.sql no servidor SQL na base de dados indicada (imbd-db)

3. Depois de ter corrido corretamente a base de dados, correr o código Listings/Query9-Selects.sql que permitirá correr os selects indicados no enunciado na Etapa 2.

### Etapa-3(Procedures+Triggers+Views)
1. Correr o código da Procedures.sql, que criará as procedures no imdb-db/Programmability/Stored Procedures.
Nota: para testar as procedures de uma maneira mais 'rápida', correr o Test_Procedures.sql

2. Correr o Triggers.sql, que criará os Triggers dentro da tabela referida (neste caso Actor e Director).

3. Correr o Views.sql de maneira a verificar as Views referidas no enunciado na Etapa 3.

### Etapa-3(Aplicação-Java)
1. Verificar as informações de conexão no DbConnection.java

2. Correr o src/MainWindow.java

3. Se ocorrer algum erro ao correr, ir ler o ERRORFIX.md


