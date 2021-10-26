CREATE TABLE "payment_operations" (
operation_id serial PRIMARY KEY,
operation_name varchar(255) NOT NULL,
operation_userid INTEGER,
operation_accountid INTEGER,
operation_categoryid INTEGER,
operation_amount DECIMAL NOT NULL,
operation_currency varchar(3) NOT NULL,
operation_date DATE
);