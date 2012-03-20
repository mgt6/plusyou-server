create schema ${database.server.username};
create user ${database.server.username}@localhost
identified by '${database.server.password}';
grant all on ${database.server.username}.* to ${database.server.username}@localhost;