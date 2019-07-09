Transfer-Money
==============================================================================================
Author: Viacheslav Tcapaev

Technologies:
------------ 
- REST 
- JAR 
- Java
- H2
- Log4j2 
- Maven
- Jetty
- Apache HTTP Client

System requirements
-------------------

All you need to build this project is Java 11.0 (Java SDK 1.11) or better, Maven 3.6.1 or better.

Build and Deploy the Quickstart
-------------------------

There are several ways how to build and run application
 
### the first one:
 1. Set environment variable %MAVEN_HOME and $PATH to $MAVEN_HOME/bin. Also make sure you have set $JAVA_HOME  
 2. Open command line and navigate to the root directory of project
 3. Type this command to build:

        mvn clean package 

 4. Type this command to run:
        java -jar target/server-1.0-SNAPSHOT.jar

 6. Embedded Jetty will be started and will deploy application
 
### the second one:
  1. Set environment variable %MAVEN_HOME and $PATH to $MAVEN_HOME/bin. Also make sure you have set $JAVA_HOME  
  2. Open command line and navigate to the root directory of project
  3. Type this command to build and run:
        
        mvn exec:java
        
  4. Embedded Jetty will be started and will deploy application 
 


Access the application
---------------------

The application will be running at the following URL: <http://localhost:8080/>
REST Api will be available at the following URL: <http://localhost:8080/api/*>. 

### For an instance: 

- <http://localhost:8080/api/account>
- <http://localhost:8080/api/transfer-money/1/2/30>

### Available Services

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| GET | /account | get all users | 
| POST | /account | create a new account | 
| GET | /account/acc-number/{accNumber} | get account by account number | 
| GET | /account/username/{username} | get accounts by username |
| POST | /transfer-money-sync/{accNumberFrom}/{accNumberTo}/{amount} | transfer money from one account to another
| POST | /transfer-money-lock/{accNumberFrom}/{accNumberTo}/{amount} | transfer money from one account to another
| GET | /transaction-history | find all transactions  
