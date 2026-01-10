DROP TABLE IF EXISTS MovieActor;
DROP TABLE IF EXISTS MovieGenre;
DROP TABLE IF EXISTS MovieDirector;
DROP TABLE IF EXISTS Director;
DROP TABLE IF EXISTS MovieVotes;
-- DROP TABLE IF EXISTS Genre;
DROP TABLE IF EXISTS AgeRating;
DROP TABLE IF EXISTS Actor;
DROP TABLE IF EXISTS Country;
DROP TABLE IF EXISTS Continent;
DROP TABLE IF EXISTS Platform; 
DROP TABLE IF EXISTS MoviePlatform;
DROP TABLE IF EXISTS Movies;
DROP TABLE IF EXISTS AuditLog;

CREATE TABLE Movies (
    movieId          INT NOT NULL,
    movieName        NVARCHAR(255) NOT NULL,
    movieDuration    INT CHECK (movieDuration >= 0),
    movieBudget      BIGINT NOT NULL CHECK (movieBudget >= 0),
    movieReleaseDate DATE NOT NULL,
    movieRegisterDate DATE NOT NULL DEFAULT '2026-01-01',
    ageRatingId      INT NULL,
    countryId        INT NULL,
    CONSTRAINT PK_Movies PRIMARY KEY (movieId)
);

-- Exemplo se a coluna se chama 'movieReleaseDate'
INSERT INTO dbo.Movies (movieId, movieName, movieDuration, movieBudget, movieReleaseDate, movieRegisterDate, ageRatingId, countryId)
SELECT 
    movieId,
    LTRIM(RTRIM(movieName)) AS movieName,
    CAST(ROUND(movieDuration, 0) AS INT) AS movieDuration,
    movieBudget,
    -- Ajustar para o nome correto da coluna
    TRY_CONVERT(DATE, 
        SUBSTRING(LTRIM(movieReleaseDate), 7, 4) + '-' + 
        SUBSTRING(LTRIM(movieReleaseDate), 4, 2) + '-' + 
        SUBSTRING(LTRIM(movieReleaseDate), 1, 2)
    ) AS movieReleaseDate,
    '2026-01-01' AS movieRegisterDate,
    NULL AS ageRatingId,
    NULL AS countryId
FROM (
    SELECT *,
        ROW_NUMBER() OVER (PARTITION BY movieId ORDER BY movieId) AS rn
    FROM dbo.MoviesImport
) AS Deduplicated
WHERE rn = 1
    AND movieId IS NOT NULL
    AND movieName IS NOT NULL
    AND LTRIM(RTRIM(movieName)) != ''
    AND movieDuration IS NOT NULL
    AND movieDuration >= 0
    AND movieBudget IS NOT NULL
    AND movieBudget >= 0
    AND movieReleaseDate IS NOT NULL  -- Nome correto
    AND LTRIM(movieReleaseDate) != ''  -- Nome correto
    AND TRY_CONVERT(DATE, 
        SUBSTRING(LTRIM(movieReleaseDate), 7, 4) + '-' + 
        SUBSTRING(LTRIM(movieReleaseDate), 4, 2) + '-' + 
        SUBSTRING(LTRIM(movieReleaseDate), 1, 2)
    ) IS NOT NULL;



-- STEP 1: Create Actor table
CREATE TABLE Actor (
    actorId     INT NOT NULL,
    actorName   VARCHAR(63) NOT NULL,
    actorGender CHAR(1) CHECK (actorGender IN ('M', 'F')) DEFAULT NULL,
    CONSTRAINT PK_Actor PRIMARY KEY (actorId)
);

-- STEP 2: Create MovieActor junction table
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

-- STEP 3: Insert ALL unique actors first (not just those matching movies)
INSERT INTO Actor (actorId, actorName, actorGender)
SELECT 
    CAST(actorId AS INT),
    LEFT(LTRIM(RTRIM(actorName)), 63) AS actorName,
    UPPER(LTRIM(RTRIM(actorGender))) AS actorGender
FROM (
    SELECT 
        actorId, 
        actorName, 
        actorGender,
        ROW_NUMBER() OVER (PARTITION BY actorId ORDER BY (SELECT NULL)) AS rn
    FROM ActorImport
    WHERE actorId IS NOT NULL
        AND LTRIM(RTRIM(actorId)) != ''
        AND actorName IS NOT NULL
        AND LTRIM(RTRIM(actorName)) != ''
        AND actorGender IS NOT NULL
        AND LTRIM(RTRIM(actorGender)) != ''
        AND ISNUMERIC(actorId) = 1
        AND UPPER(LTRIM(RTRIM(actorGender))) IN ('M', 'F')
) AS UniqueActors
WHERE rn = 1;

-- STEP 4: Insert into MovieActor (only valid movie-actor relationships)
INSERT INTO MovieActor (movieId, actorId)
SELECT DISTINCT
    CAST(LTRIM(RTRIM(at.movieId)) AS INT) AS movieId,
    CAST(LTRIM(RTRIM(at.actorId)) AS INT) AS actorId
FROM ActorImport at
WHERE at.movieId IS NOT NULL
    AND LTRIM(RTRIM(at.movieId)) != ''
    AND at.actorId IS NOT NULL
    AND LTRIM(RTRIM(at.actorId)) != ''
    AND ISNUMERIC(at.movieId) = 1
    AND ISNUMERIC(at.actorId) = 1
    AND EXISTS (SELECT 1 FROM Actor a WHERE a.actorId = CAST(LTRIM(RTRIM(at.actorId)) AS INT))
    AND EXISTS (SELECT 1 FROM Movies m WHERE m.movieId = CAST(LTRIM(RTRIM(at.movieId)) AS INT));

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


-- STEP 2: Insert into MovieGenre (only valid relationships from GenreMovieImport already imported)

INSERT INTO MovieGenre (movieId, genreId)
SELECT DISTINCT
    CAST(LTRIM(RTRIM(gmt.movieId)) AS INT) AS movieId,
    CAST(LTRIM(RTRIM(gmt.genreId)) AS INT) AS genreId
FROM GenreMovieImport gmt
WHERE gmt.movieId IS NOT NULL
    AND LTRIM(RTRIM(gmt.movieId)) != ''
    AND gmt.genreId IS NOT NULL
    AND LTRIM(RTRIM(gmt.genreId)) != ''
    AND ISNUMERIC(gmt.movieId) = 1
    AND ISNUMERIC(gmt.genreId) = 1
    AND EXISTS (SELECT 1 FROM Genre g WHERE g.genreId = CAST(LTRIM(RTRIM(gmt.genreId)) AS INT))
    AND EXISTS (SELECT 1 FROM Movies m WHERE m.movieId = CAST(LTRIM(RTRIM(gmt.movieId)) AS INT));

-- STEP 1: Create MovieVotes table
DROP TABLE IF EXISTS MovieVotes;

CREATE TABLE MovieVotes (
    movieId INT NOT NULL,
    movieRating DECIMAL(3,1) CHECK (movieRating >= 0 AND movieRating <= 10),
    movieRatingCount INT CHECK (movieRatingCount >= 0),
    CONSTRAINT PK_MovieVotes PRIMARY KEY (movieId),
    CONSTRAINT FK_MovieVotes_Movies 
        FOREIGN KEY (movieId) REFERENCES Movies(movieId) 
        ON DELETE CASCADE ON UPDATE CASCADE
);


-- STEP 2: Insert into MovieVotes (only valid data from MovieVotesImport already imported)
INSERT INTO MovieVotes (movieId, movieRating, movieRatingCount)
SELECT 
    CAST(LTRIM(RTRIM(mvt.movieId)) AS INT),
    CAST(LTRIM(RTRIM(mvt.movieRating)) AS DECIMAL(3,1)),
    CAST(LTRIM(RTRIM(mvt.movieRatingCount)) AS INT)
FROM (
    SELECT 
        movieId, 
        movieRating, 
        movieRatingCount,
        ROW_NUMBER() OVER (PARTITION BY movieId ORDER BY (SELECT NULL)) AS rn
    FROM MovieVotesImport
    WHERE movieId IS NOT NULL
        AND LTRIM(RTRIM(movieId)) != ''
        AND movieRating IS NOT NULL
        AND LTRIM(RTRIM(movieRating)) != ''
        AND movieRatingCount IS NOT NULL
        AND LTRIM(RTRIM(movieRatingCount)) != ''
        AND ISNUMERIC(movieId) = 1
        AND ISNUMERIC(movieRating) = 1
        AND ISNUMERIC(movieRatingCount) = 1
        AND CAST(movieRating AS DECIMAL(3,1)) BETWEEN 0 AND 10
        AND CAST(movieRatingCount AS INT) >= 0
        AND EXISTS (SELECT 1 FROM Movies m WHERE m.movieId = CAST(LTRIM(RTRIM(movieId)) AS INT))
) AS mvt
WHERE mvt.rn = 1;  -- Remove duplicates

-- STEP 1: Create Director table

CREATE TABLE Director (
    directorId INT NOT NULL,
    directorName VARCHAR(63) NOT NULL,
    CONSTRAINT PK_Director PRIMARY KEY (directorId)
);


-- STEP 2: Create MovieDirector junction table
CREATE TABLE MovieDirector (
    movieId INT NOT NULL,
    directorId INT NOT NULL,
    CONSTRAINT PK_MovieDirector PRIMARY KEY (movieId, directorId),
    CONSTRAINT FK_MovieDirector_Movies 
        FOREIGN KEY (movieId) REFERENCES Movies(movieId) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_MovieDirector_Director 
        FOREIGN KEY (directorId) REFERENCES Director(directorId) 
        ON DELETE CASCADE ON UPDATE CASCADE
);


-- STEP 3: Insert into Director (unique directors only, from DirectorImport already imported)

INSERT INTO Director (directorId, directorName)
SELECT 
    CAST(LTRIM(RTRIM(directorId)) AS INT),
    LEFT(LTRIM(RTRIM(directorName)), 63) AS directorName
FROM (
    SELECT 
        directorId, 
        directorName,
        ROW_NUMBER() OVER (PARTITION BY directorId ORDER BY (SELECT NULL)) AS rn
    FROM DirectorImport
    WHERE directorId IS NOT NULL
        AND LTRIM(RTRIM(directorId)) != ''
        AND directorName IS NOT NULL
        AND LTRIM(RTRIM(directorName)) != ''
        AND ISNUMERIC(directorId) = 1
) AS UniqueDirectors
WHERE rn = 1;


-- STEP 4: Insert into MovieDirector (all valid relationships)

INSERT INTO MovieDirector (movieId, directorId)
SELECT DISTINCT
    CAST(LTRIM(RTRIM(dt.movieId)) AS INT) AS movieId,
    CAST(LTRIM(RTRIM(dt.directorId)) AS INT) AS directorId
FROM DirectorImport dt
WHERE dt.movieId IS NOT NULL
    AND LTRIM(RTRIM(dt.movieId)) != ''
    AND dt.directorId IS NOT NULL
    AND LTRIM(RTRIM(dt.directorId)) != ''
    AND ISNUMERIC(dt.movieId) = 1
    AND ISNUMERIC(dt.directorId) = 1
    AND EXISTS (SELECT 1 FROM Director d WHERE d.directorId = CAST(LTRIM(RTRIM(dt.directorId)) AS INT))
    AND EXISTS (SELECT 1 FROM Movies m WHERE m.movieId = CAST(LTRIM(RTRIM(dt.movieId)) AS INT));


-- STEP 5: Clean up (optional)

DROP TABLE IF EXISTS DirectorImport;
DROP TABLE IF EXISTS MovieVotesImport;
DROP TABLE IF EXISTS GenreMovieImport;
DROP TABLE IF EXISTS ActorImport;
DROP TABLE IF EXISTS MoviesImport;

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



-- UPDATE Movies com countryId e ageRatingId aleatórios


-- 1. Update countryId (aleatório entre 1-5: Netflix, Amazon Prime, Disney+, HBO Max, Apple TV+)
UPDATE Movies
SET countryId = (ABS(CHECKSUM(NEWID())) % 10) + 1  -- Random entre 1 e 10


-- 2. Update ageRatingId (aleatório entre 1-5: -10, +10, +14, +16, +18)
UPDATE Movies
SET ageRatingId = (ABS(CHECKSUM(NEWID())) % 5) + 1  -- Random entre 1 e 5


-- STEP 1: Create MoviePlatform table

DROP TABLE IF EXISTS MoviePlatform;

CREATE TABLE MoviePlatform (
    movieId INT NOT NULL,
    platformId INT NOT NULL,
    CONSTRAINT PK_MoviePlatform PRIMARY KEY (movieId, platformId),
    CONSTRAINT FK_MoviePlatform_Movies 
        FOREIGN KEY (movieId) REFERENCES Movies(movieId) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_MoviePlatform_Platform 
        FOREIGN KEY (platformId) REFERENCES Platform(platformId) 
        ON DELETE CASCADE ON UPDATE CASCADE
);


-- STEP 2: Insert 20000 random relationships
-- Insert all 5 platforms for 500 random movies
INSERT INTO MoviePlatform (movieId, platformId)
SELECT DISTINCT
    m.movieId,
    p.platformId
FROM (
    SELECT TOP 3500 
        movieId,
        (ABS(CHECKSUM(NEWID())) % 5) + 1 AS numPlatforms  -- Random 1-5
    FROM Movies 
    ORDER BY NEWID()
) AS m
CROSS JOIN Platform p
WHERE p.platformId <= m.numPlatforms  -- Só adiciona até o número aleatório
  AND NOT EXISTS (
    SELECT 1 FROM MoviePlatform mp
    WHERE mp.movieId = m.movieId 
      AND mp.platformId = p.platformId
);

CREATE TABLE AuditLog (
    auditId    INT IDENTITY PRIMARY KEY,
    tableName  VARCHAR(20),   -- 'Director' ou 'Actor'
    actionType VARCHAR(10),   -- 'DELETE' ou 'INSERT'
    recordId   INT,           -- directorId ou actorId
    actionDate DATETIME
);
GO

-- 2) Garantir coluna hidden em Director
IF COL_LENGTH('Director', 'hidden') IS NULL
    ALTER TABLE Director
    ADD hidden BIT NOT NULL CONSTRAINT DF_Director_Hidden DEFAULT 0;
GO