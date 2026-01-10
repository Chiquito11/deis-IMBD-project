-- A. Base de dados (DROP + CREATE)
drop table if exists MovieGenre;
drop table if exists MovieActor;
drop table if exists MovieDirector;
drop table if exists MovieVotes;
drop table if exists Movies;
drop table if exists Actor;
drop table if exists Director;
drop table if exists Genre;

-- Platforms [2ª Etapa]
CREATE TABLE Platform (
    platformId   INT NOT NULL,
    platformName VARCHAR(63) NOT NULL,
    CONSTRAINT PK_Platform PRIMARY KEY (platformId)
);

-- 1. Continentes [2ª Etapa]
CREATE TABLE Continent (
    continentId   INT NOT NULL,
    continentName VARCHAR(31) NOT NULL,
    CONSTRAINT PK_Continent PRIMARY KEY (continentId)
);

-- 2. Países [2ª Etapa]
CREATE TABLE Country (
    countryId     INT NOT NULL,
    countryName   VARCHAR(63) NOT NULL,
    continentId   INT NOT NULL,
    CONSTRAINT PK_Country PRIMARY KEY (countryId),
    CONSTRAINT FK_Country_Continent
        FOREIGN KEY (continentId) REFERENCES Continent(continentId)
        ON UPDATE CASCADE
);

-- Tabela de classificações etárias [2ª Etapa]
CREATE TABLE AgeRating (
    ageRatingId   INT NOT NULL,
    ageRatingCode VARCHAR(15) NOT NULL, -- ex: 'M/18' , 'M/14', 'M/16' , 'M/18' , 'M/21' 
    description   VARCHAR(127),
    CONSTRAINT PK_AgeRating PRIMARY KEY (ageRatingId)
);

-- 1. Movies [1ª Etapa + 2ª Etapa]
CREATE TABLE Movies (
    movieId          INT NOT NULL,
    movieName        VARCHAR(127) NOT NULL,
    movieDuration    INT CHECK (movieDuration >= 0), -- Duração pode ser 0
    movieBudget      BIGINT NOT NULL CHECK (movieBudget >= 0),  -- 2.2: NOT NULL
    movieReleaseDate DATE NOT NULL,                            -- 2.1: NOT NULL
    movieRegisterDate DATE NOT NULL DEFAULT '2026-01-01',      -- 2.3: DEFAULT fixo
    ageRatingId      INT NULL,          -- FK para AgeRating
    countryId        INT NULL,          -- FK para Country
    CONSTRAINT PK_Movies PRIMARY KEY (movieId),
    CONSTRAINT FK_Movies_AgeRating
        FOREIGN KEY (ageRatingId) REFERENCES AgeRating(ageRatingId)
        ON UPDATE CASCADE,
    CONSTRAINT FK_Movies_Country
        FOREIGN KEY (countryId) REFERENCES Country(countryId)
        ON UPDATE CASCADE
);

-- 2. Genres [1ª Etapa]
CREATE TABLE Genre (
    genreId   INT NOT NULL,
    genreName VARCHAR(31) NOT NULL,
    CONSTRAINT PK_Genre PRIMARY KEY (genreId),
);

-- 3. Actors [1ª Etapa]
CREATE TABLE Actor (
    actorId     INT NOT NULL,
    actorName   VARCHAR(63) NOT NULL,
    actorGender CHAR(1) CHECK (actorGender IN ('M', 'F')) DEFAULT NULL,  -- Só M ou F
    CONSTRAINT PK_Actor PRIMARY KEY (actorId),
);

-- 4. Directors [1ª Etapa]
CREATE TABLE Director (
    directorId   INT NOT NULL,
    directorName VARCHAR(63) NOT NULL,
    CONSTRAINT PK_Director PRIMARY KEY (directorId),
);

-- MovieInteraction (Log de interações) [2ª Etapa]
CREATE TABLE MovieInteraction (
    interactionId   INT NOT NULL,
    movieId         INT NOT NULL,
    interactionType VARCHAR(31) NOT NULL,   -- ex: 'VIEW', 'RATING', 'ASSOCIATION'
    interactionDate DATETIME NOT NULL,
    CONSTRAINT PK_MovieInteraction PRIMARY KEY (interactionId),
    CONSTRAINT FK_MovieInteraction_Movies
        FOREIGN KEY (movieId) REFERENCES Movies(movieId)
        ON DELETE CASCADE ON UPDATE CASCADE
);


-- 5. MovieRating (1:1 com Movies) [1ª Etapa]
CREATE TABLE MovieRating (
    movieId          INT NOT NULL,
    movieRating      DECIMAL(3,1) CHECK (movieRating >= 0.0 AND movieRating <= 10.0),
    movieRatingCount INT NOT NULL CHECK (movieRatingCount >= 0),
    CONSTRAINT PK_MovieRating PRIMARY KEY (movieId),
    CONSTRAINT FK_MovieRating_Movies 
        FOREIGN KEY (movieId) REFERENCES Movies(movieId) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Association table for Movies and Actors (N:N) [1ª Etapa]
CREATE TABLE MovieActor (
    movieId INT NOT NULL,
    actorId INT NOT NULL,
    CONSTRAINT PK_MovieActor PRIMARY KEY (movieId, actorId),
    CONSTRAINT FK_MovieActor_Movies 
        FOREIGN KEY (movieId) REFERENCES Movies(movieId) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_MovieActor_Actor 
        FOREIGN KEY (actorId) REFERENCES Actor(actorId) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Association table for Movies and Directors (N:N) [1ª Etapa]
CREATE TABLE MovieDirector (
    movieId    INT NOT NULL,
    directorId INT NOT NULL,
    CONSTRAINT PK_MovieDirector PRIMARY KEY (movieId, directorId),
    CONSTRAINT FK_MovieDirector_Movies 
        FOREIGN KEY (movieId) REFERENCES Movies(movieId) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_MovieDirector_Director 
        FOREIGN KEY (directorId) REFERENCES Director(directorId) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabela de associação Filme–Plataforma (N:N) [2ª Etapa]
CREATE TABLE MoviePlatform (
    movieId    INT NOT NULL,
    platformId INT NOT NULL,
    CONSTRAINT PK_MoviePlatform PRIMARY KEY (movieId, platformId),
    CONSTRAINT FK_MoviePlatform_Movies
        FOREIGN KEY (movieId) REFERENCES Movies(movieId)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_MoviePlatform_Platform
        FOREIGN KEY (platformId) REFERENCES Platform(platformId)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Association table for Movies and Genres (N:N) [1ª Etapa]
CREATE TABLE MovieGenre (
    movieId INT NOT NULL,
    genreId INT NOT NULL,
    CONSTRAINT PK_MovieGenre PRIMARY KEY (movieId, genreId),
    CONSTRAINT FK_MovieGenre_Movies 
        FOREIGN KEY (movieId) REFERENCES Movies(movieId) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_MovieGenre_Genre 
        FOREIGN KEY (genreId) REFERENCES Genre(genreId) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- B. Inserção de Dados fixos (INSERT)

-- 3.3: Inserir 5 Plataformas de Vídeos
INSERT INTO Platform (platformId, platformName) VALUES
(1, 'Netflix'),
(2, 'Amazon Prime'),
(3, 'Disney+'),
(4, 'HBO Max'),
(5, 'Apple TV+');

-- 3.5: Inserir 5 Continentes
INSERT INTO Continent (continentId, continentName) VALUES
(1, 'Europa'),
(2, 'América do Norte'),
(3, 'Ásia'),
(4, 'América do Sul'),
(5, 'Oceania');

-- 3.4: Inserir 10 Países Produtores
INSERT INTO Country (countryId, countryName, continentId) VALUES
(1, 'Estados Unidos', 2),
(2, 'Canadá', 2),
(3, 'Reino Unido', 1),
(4, 'França', 1),
(5, 'Alemanha', 1),
(6, 'Japão', 3),
(7, 'Coreia do Sul', 3),
(8, 'Brasil', 4),
(9, 'Austrália', 5),
(10, 'Índia', 3);

-- 3.6: Inserir 5 Classificações Etárias
INSERT INTO AgeRating (ageRatingId, ageRatingCode, description) VALUES
(1, '-10', 'Menor de 10 anos'),
(2, '+10', 'Maior de 10 anos'),
(3, '+14', 'Maior de 14 anos'),
(4, '+16', 'Maior de 16 anos'),
(5, '+18', 'Maior de 18 anos');
