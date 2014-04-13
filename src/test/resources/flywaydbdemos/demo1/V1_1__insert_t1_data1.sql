-- flyway's default initialized version is 1 (2.3.1).
-- So, first migration sql version must be greater than 1.
insert into t1(name, age) values('jon', 10);
insert into t1(name, age) values('bob', 20);
