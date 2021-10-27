CREATE TABLE "simple_operations" (
so_id serial PRIMARY KEY,
so_description varchar(200) NOT NULL,
so_currency varchar(3) NOT NULL,
so_category varchar(10) NOT NULL,
so_amount DECIMAL NOT NULL,
so_datetime TIMESTAMP,
so_accountid INTEGER,
so_userid INTEGER
);