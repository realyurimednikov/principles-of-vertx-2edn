CREATE TABLE "payment_categories" (
category_id serial PRIMARY KEY,
category_name varchar(200) NOT NULL,
category_type varchar(3) NOT NULL,
category_userid INTEGER
);