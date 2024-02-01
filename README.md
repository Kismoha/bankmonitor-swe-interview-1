## Transaction microservice 
This is a CRUD application set up to handle transactions. It provides a basic api to list, update and create transactions.

## Connecting to Database

The database is an embedded H2 database with H2 dialect.
The user, password and url is configured in the `application.yml` file.
The console is available at '/h2-console'. Database available at 'jdbc:h2:mem:myDb'