--DROP TABLE FutureTravel;
--DROP TABLE Travel;
--DROP TABLE Entry;
--DROP TABLE Taxi;
--DROP TABLE Client;
--DROP TABLE Stand;
--DROP TABLE Address;
--DROP TABLE City;
--DROP TABLE Region;
--DROP TABLE Country;

CREATE TABLE Country (
	countryId SERIAL,
	code VARCHAR(32) UNIQUE NOT NULL,
	name VARCHAR(128) NOT NULL,
	CONSTRAINT country_PK PRIMARY KEY (countryId)
);

CREATE TABLE Region (
	regionId SERIAL,
	code VARCHAR(32) UNIQUE NOT NULL,
	name VARCHAR(128) NOT NULL,
	country BIGINT,
	CONSTRAINT region_PK PRIMARY KEY (regionId),
	CONSTRAINT regionCountry_FK FOREIGN KEY (country) REFERENCES Country(countryId)
);

CREATE TABLE City (
	cityId SERIAL,
	name VARCHAR(128) NOT NULL,
	location GEOMETRY(MULTIPOLYGON,4326),
	region BIGINT,
	CONSTRAINT city_PK PRIMARY KEY (cityId),
	CONSTRAINT cityRegion_FK FOREIGN KEY (region) REFERENCES Region(regionId)
);

CREATE TABLE Address (
	addressId SERIAL,
	name VARCHAR(128),
	location GEOMETRY(MULTILINESTRING,4326),
	city BIGINT,
	CONSTRAINT address_PK PRIMARY KEY (addressId),
	CONSTRAINT addressCity_FK FOREIGN KEY (city) REFERENCES City(cityId)
);

CREATE TABLE Stand (
	standId SERIAL,
	name VARCHAR(50),
	location GEOMETRY(POINT,4326),
	address BIGINT,
	CONSTRAINT stand_PK PRIMARY KEY (standId),
	CONSTRAINT standAddress_FK FOREIGN KEY (address) REFERENCES Address(addressId)
);

CREATE TABLE Client (
	clientId SERIAL,
	originCountry BIGINT NOT NULL,
	originRegion BIGINT NOT NULL,
	originCity BIGINT NOT NULL,
	originAddress BIGINT NOT NULL,
	entry TIMESTAMP,
	location GEOMETRY(POINT,4326),
	clientState BIGINT,
	CONSTRAINT client_PK PRIMARY KEY (clientId),
	CONSTRAINT clientOriginCountry_FK FOREIGN KEY (originCountry) REFERENCES Country(countryId),
	CONSTRAINT clientOriginRegion_FK FOREIGN KEY (originRegion) REFERENCES Region(regionId),
	CONSTRAINT clientOriginCity_FK FOREIGN KEY (originCity) REFERENCES City(cityId),
	CONSTRAINT clientOriginAddress_FK FOREIGN KEY (originAddress) REFERENCES Address(addressId)
);

CREATE TABLE Taxi (
	taxiId SERIAL,
	token VARCHAR(300),
	actualState BIGINT,
	futureState BIGINT,
	password VARCHAR(50),
	position GEOMETRY(POINT,4326),
	client BIGINT,
	city BIGINT,
	CONSTRAINT taxi_PK PRIMARY KEY (taxiId),
	CONSTRAINT taxiClient_FK FOREIGN KEY (client) REFERENCES Client(clientId),
	CONSTRAINT taxiCity_FK FOREIGN KEY (city) REFERENCES City(cityId)
);

CREATE TABLE Entry (
	entryId SERIAL,
	taxi BIGINT NOT NULL,
	stand BIGINT NOT NULL,
	arrival TIMESTAMP NOT NULL,
	CONSTRAINT entry_PK PRIMARY KEY (entryId),
	CONSTRAINT entryTaxi_FK FOREIGN KEY (taxi) REFERENCES Taxi(taxiId),
	CONSTRAINT entryStand_FK FOREIGN KEY (stand) REFERENCES Stand(standId)
);

CREATE TABLE Travel (
	travelId SERIAL,
	date TIMESTAMP NOT NULL,
	originCountry BIGINT NOT NULL,
	originRegion BIGINT NOT NULL,
	originCity BIGINT NOT NULL,
	originAddress BIGINT NOT NULL,
	destinationCountry BIGINT NOT NULL,
	destinationRegion BIGINT NOT NULL,
	destinationCity BIGINT NOT NULL,
	destinationAddress BIGINT NOT NULL,
	distance NUMERIC(10, 5) NOT NULL,
	originPoint GEOMETRY(POINT,4326),
	destinationPoint GEOMETRY(POINT,4326),
	path GEOMETRY(LINESTRING,4326),
	taxi BIGINT NOT NULL,
	CONSTRAINT travel_PK PRIMARY KEY (travelId),
	CONSTRAINT travelOriginCountry_FK FOREIGN KEY (originCountry) REFERENCES Country(countryId),
	CONSTRAINT travelOriginRegion_FK FOREIGN KEY (originRegion) REFERENCES Region(regionId),
	CONSTRAINT travelOriginCity_FK FOREIGN KEY (originCity) REFERENCES City(cityId),
	CONSTRAINT travelOriginAddress_FK FOREIGN KEY (originAddress) REFERENCES Address(addressId),
	CONSTRAINT travelDestinationCountry_FK FOREIGN KEY (destinationCountry) REFERENCES Country(countryId),
	CONSTRAINT travelDestinationRegion_FK FOREIGN KEY (destinationRegion) REFERENCES Region(regionId),
	CONSTRAINT travelDestinationCity_FK FOREIGN KEY (destinationCity) REFERENCES City(cityId),
	CONSTRAINT travelDestinationAddress_FK FOREIGN KEY (destinationAddress) REFERENCES Address(addressId),
	CONSTRAINT travelTaxi_FK FOREIGN KEY (taxi) REFERENCES Taxi(taxiId)
);

CREATE TABLE FutureTravel (
	futureTravelId SERIAL,
	date TIMESTAMP NOT NULL,
	originCountry BIGINT NOT NULL,
	originRegion BIGINT NOT NULL,
	originCity BIGINT NOT NULL,
	originAddress BIGINT NOT NULL,
	destinationCountry BIGINT NOT NULL,
	destinationRegion BIGINT NOT NULL,
	destinationCity BIGINT NOT NULL,
	destinationAddress BIGINT NOT NULL,
	taxi BIGINT NOT NULL,
	CONSTRAINT futureTravel_PK PRIMARY KEY (futureTravelId),
	CONSTRAINT futureTravelOriginCountry_FK FOREIGN KEY (originCountry) REFERENCES Country(countryId),
	CONSTRAINT futureTravelOriginRegion_FK FOREIGN KEY (originRegion) REFERENCES Region(regionId),
	CONSTRAINT futureTravelOriginCity_FK FOREIGN KEY (originCity) REFERENCES City(cityId),
	CONSTRAINT futureTravelOriginAddress_FK FOREIGN KEY (originAddress) REFERENCES Address(addressId),
	CONSTRAINT futureTravelDestinationCountry_FK FOREIGN KEY (destinationCountry) REFERENCES Country(countryId),
	CONSTRAINT futureTravelDestinationRegion_FK FOREIGN KEY (destinationRegion) REFERENCES Region(regionId),
	CONSTRAINT futureTravelDestinationCity_FK FOREIGN KEY (destinationCity) REFERENCES City(cityId),
	CONSTRAINT futureTravelDestinationAddress_FK FOREIGN KEY (destinationAddress) REFERENCES Address(addressId),
	CONSTRAINT futureTravelTaxi_FK FOREIGN KEY (taxi) REFERENCES Taxi(taxiId)
);

-- Insertar los países en Country
INSERT INTO Country(code, name)
VALUES ('ES', 'ESPAÑA');

-- Insertar todas las provincias en Region
INSERT INTO Region(code, name, country)
SELECT DISTINCT natcode, nameunit, (select countryId from Country where code = 'ES')
FROM Provincia;

-- Insertar municipios en City, siendo el geom un polygon
INSERT INTO City(name, location, region)
SELECT nameunit, geom, (SELECT regionId FROM Region r WHERE r.code=CONCAT(SUBSTR(natcode, 1, 6),'00000'))
FROM Municipio;

-- Unir todas las calles con el mismo ine_via e insertar en Address
INSERT INTO Address(name, location)
SELECT min(tip_via_in) || ' ' || min(nom_via), ST_SetSRID(st_union(geom),4326)
FROM Calle
GROUP BY ine_via;

-- Ponerle a cada Address la clave principal de la City en la que está contenida st_within
UPDATE Address
SET city = (SELECT cityId 
			FROM City
			WHERE ST_WITHIN(Address.location, City.location)=TRUE);

ALTER TABLE standoverpass
    ALTER COLUMN geom TYPE geometry(Point,4326) USING ST_GeometryN(geom, 1);
    
--INSERT INTO Stand(name, location)
--SELECT name, geom
--FROM standoverpass;

--DELETE FROM Stand
--WHERE standId = 18;

--UPDATE Stand
--SET address = (SELECT addressId 
--				FROM Address a 
--				WHERE ST_Distance(Stand.location, a.location) = (SELECT min(ST_Distance(Stand.location, ad.location)) FROM Address ad));

-- Para pruebas: No coger desde standoverpass e introducir:
INSERT INTO Stand(name, location, address)
VALUES ('Os Rosales', '0101000020E61000004216B36F38DD20C0F2A9AD22EDAF4540', 2419);
INSERT INTO Stand(name, location, address)
VALUES ('Matogrande', '0101000020E61000001B38561811CF20C0BCC8A942A8AB4540', 2397);
INSERT INTO Stand(name, location, address)
VALUES ('Calle Barcelona', '0101000020E6100000BAD2D7987CD820C03708292E7DAE4540', 2009);

-- Introducir las paradas
--INSERT INTO Stand(name, location)
--VALUES ('Avenida de Hércules', (SELECT ST_SetSRID(ST_MakePoint(-8.4069785,43.379142),4326)));
--INSERT INTO Stand(name, location)
--VALUES ('Rúa Manuel Murguía', (SELECT ST_SetSRID(ST_MakePoint(-8.4178806,43.369328),4326)));
--INSERT INTO Stand(name, location)
--VALUES ('Ronda Outeiro, 274', (SELECT ST_SetSRID(ST_MakePoint(-8.4232633,43.3628038),4326)));
--INSERT INTO Stand(name, location)
--VALUES ('Rúa Gregorio Hernández', (SELECT ST_SetSRID(ST_MakePoint(-8.4200416,43.3664419),4326)));
--INSERT INTO Stand(name, location)
--VALUES ('Rúa Gutenberg', (SELECT ST_SetSRID(ST_MakePoint(-8.4243584,43.3558217),4326)));
--INSERT INTO Stand(name, location)
--VALUES ('Calle de Rafael Alberti', (SELECT ST_SetSRID(ST_MakePoint(-8.4073842,43.3470416),4326)));
--INSERT INTO Stand(name, location)
--VALUES ('Matogrande', (SELECT ST_SetSRID(ST_MakePoint(-8.4040509,43.3407689),4326)));

-- Consultas Luaces
--SELECT ine_via, min(tip_via_in) || ' ' || min(nom_via), st_astext(st_union(geom) )
--FROM calles
--WHERE nom_via = 'GALERA' 
--GROUP BY ine_via

--select *
--from tramo_vial limit 10

--Consulta Addresses de una City
--select a.name, c.name from Address a join City c on a.city=c.cityId order by c.name

--Consulta Cities de una Region
--select c.name, r.name from City c join Region r on c.region=r.regionId order by r.name

--Consulta Regions de un Country
--select r.name, c.name from Region r join Country c on r.country=c.countryId order by c.name;

--Imprimir un punto con coordenadas
--select st_astext(position) from taxi