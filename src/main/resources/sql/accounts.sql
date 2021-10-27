CREATE TABLE "accounts" (
account_id serial PRIMARY KEY,
account_name varchar(200) NOT NULL,
account_currency varchar(3) NOT NULL,
account_userid INTEGER
);