CREATE TABLE "users" (
user_id serial PRIMARY KEY,
user_email varchar(255) NOT NULL,
user_hash varchar(255) NOT NULL,
user_salt varchar(255) NOT NULL,
user_created DATE NOT NULL,
user_permissions TEXT []
);