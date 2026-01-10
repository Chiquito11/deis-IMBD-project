# IMDB-Project User Interface

## Possible error when try to run the Java App

### A. SQL Server not running
1. Win+R and then search for SQLServerManager17.msc and open it
2. The SQL Server Browser will be 'Stopped' and needs to be 'Running' in order to the App works otherwise it won't run connect from the Java App to the SQL

### B. Not Refering to the libraries
1. Go to the 'Java Projects' tab and add the mssql-jdbc-13.2.1.jre11 files in to the Referenced Libraries

### C. TCP/IP Blocking
1. Win+R and then search for SQLServerManager17.msc and open it
2. Go to SQL Server Network Configuration/ Protocols for SQLEXPRESS
3. TCP/IP needs to be enabled
