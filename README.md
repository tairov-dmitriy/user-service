# User REST service
## Prepare
Create working database usersDB (may use script scripts/init.sh) on PostgreSQL 10.
## Build
mvn package
## Launch
java -jar target/userservice-0.0.1-SNAPSHOT.jar
## Test API
Go to http://localhost:8080/swagger-ui.html