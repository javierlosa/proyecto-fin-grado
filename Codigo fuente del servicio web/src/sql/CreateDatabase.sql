-- Database: bd_postgis

DROP DATABASE bd_postgis;

CREATE DATABASE bd_postgis
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Spanish_Spain.1252'
       LC_CTYPE = 'Spanish_Spain.1252'
       CONNECTION LIMIT = -1;

CREATE EXTENSION postgis
   SCHEMA public
   VERSION "2.1.7"