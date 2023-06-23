# FLEET Server

## Setup
1) Follow the [PPFL tutorial](https://github.com/HenryHu2000/PPFL/blob/main/README.md) to set up PPFL.
2) Install OpenJDK 17.
3) Install MySQL.
4) Configure the PPFL path (`fleet.aggregator-path`), the MySQL server and the Kafka server in [application.properties](src/main/resources/application.properties).
5) Run `./mvnw quarkus:dev` to start the FLEET server.
