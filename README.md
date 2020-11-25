# RvStore - Cars

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Tavisco_rvstore-cars&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=Tavisco_rvstore-cars) [![codecov](https://codecov.io/gh/Tavisco/rvstore-cars/branch/master/graph/badge.svg?token=M3PPPIVDDO)](https://codecov.io/gh/Tavisco/rvstore-cars) [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Tavisco_rvstore-cars&metric=ncloc)](https://sonarcloud.io/dashboard?id=Tavisco_rvstore-cars)

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Setting up the containers

This project comes with an docker-compose file that will start all necessary dependencies (Postgres SQL, Adminer (for sql), and Jaeger UI)

In the root of the project, start the containers using

```
docker-compose -f rvstore-compose.yml up -d
```

### Services
- Jaeger UI: http://localhost:16686
- Adminer: http://localhost:8090
- Postgres: http://localhost:5432 (postgres:example)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application is packageable using `./mvnw package`.
It produces the executable `code-with-quarkus-1.0.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/code-with-quarkus-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or you can use Docker to build the native executable using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .