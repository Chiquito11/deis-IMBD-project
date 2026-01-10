-- 1.1 Top 5 directores com mais filmes produzidos
CREATE VIEW vw_Top5DirectorsWithMostMovies AS
SELECT TOP 5
    d.directorId,
    d.directorName,
    COUNT(DISTINCT md.movieId) AS TotalFilmes
FROM Director d
INNER JOIN MovieDirector md ON md.directorId = d.directorId
GROUP BY d.directorId, d.directorName
ORDER BY COUNT(DISTINCT md.movieId) DESC;
GO


-- 1.2 Top 10 atores participantes em filmes
CREATE VIEW vw_Top10ActorsWithMostMovies AS
SELECT TOP 10
    a.actorId,
    a.actorName,
    a.actorGender,
    COUNT(DISTINCT ma.movieId) AS TotalFilmes
FROM Actor a
INNER JOIN MovieActor ma ON ma.actorId = a.actorId
GROUP BY a.actorId, a.actorName, a.actorGender
ORDER BY COUNT(DISTINCT ma.movieId) DESC;
GO


-- 1.3 Países com menos de 5 filmes produzidos
CREATE VIEW vw_CountriesWithLessThan5Movies AS
SELECT 
    c.countryId,
    c.countryName,
    COUNT(DISTINCT m.movieId) AS TotalFilmes
FROM Country c
INNER JOIN Movies m ON m.countryId = c.countryId
GROUP BY c.countryId, c.countryName
HAVING COUNT(DISTINCT m.movieId) < 5;
GO


-- 1.4 Continentes com mais de 10 filmes produzidos
CREATE VIEW vw_ContinentsWithMoreThan10Movies AS
SELECT 
    ct.continentId,
    ct.continentName,
    COUNT(DISTINCT m.movieId) AS TotalFilmes
FROM Continent ct
INNER JOIN Country c ON c.continentId = ct.continentId
INNER JOIN Movies m ON m.countryId = c.countryId
GROUP BY ct.continentId, ct.continentName
HAVING COUNT(DISTINCT m.movieId) > 10;
GO
