CREATE DATABASE med_dissector;
CREATE USER 'spring'@'%' identified by 'somePassword';
GRANT ALL PRIVILEGES ON med_dissector.* TO 'spring'@'%';
USE med_dissector;
CREATE TABLE presuf (
    affix varchar(100),
    meaning varchar(255),
    examples varchar(255)
) engine = aria;

LOAD DATA LOCAL INFILE '/home/mysqlScripts/presuf.csv' INTO TABLE presuf FIELDS TERMINATED BY ',' ESCAPED BY '\\';