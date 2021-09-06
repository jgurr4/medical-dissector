CREATE DATABASE med_dissector;
CREATE USER 'spring'@'%' identified by 'somePassword';
GRANT ALL PRIVILEGES ON med_dissector.* TO 'spring'@'%';
USE med_dissector;
CREATE TABLE affix (
    id int auto_increment primary key,
    affix varchar(100),
    meaning varchar(255),
    examples varchar(255),
    `prefix` tinyint(1) DEFAULT 0,
    readAffix varchar(100) as (replace(replace(replace(`affix`,'(',''),')',''),'-','')) invisible,
    KEY `afx_idx` (`affix`)
) engine = aria;

LOAD DATA LOCAL INFILE '/home/mysqlScripts/presuf.csv' INTO TABLE affix FIELDS TERMINATED BY ',' ESCAPED BY '\\';
