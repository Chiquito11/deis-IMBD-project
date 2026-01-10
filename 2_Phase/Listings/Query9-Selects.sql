-- 4.1 Obtenha/Liste todos os videos de um certo género
SELECT *
FROM Movies m
LEFT JOIN MovieGenre mg ON m.movieId = mg.movieId
LEFT JOIN Genre g ON mg.genreId = g.genreId
WHERE LOWER(TRIM(g.genreName)) = 'Action';

-- 4.2 Obtenha/Liste a informação de todos os directores de videos produzidos num qualquer país
SELECT DISTINCT *
FROM Director d
INNER JOIN MovieDirector md ON d.directorId = md.directorId
INNER JOIN Movies m ON md.movieId = m.movieId
INNER JOIN Country c ON m.countryId = c.countryId
WHERE c.countryName = 'Estados Unidos'  -- Alterar país conforme necessário

-- 4.3 Obtenha/Liste a informação de todos os atores do sexo Masculino que participaram em filmes de países Asiáticos
SELECT DISTINCT *
FROM Actor a
INNER JOIN MovieActor ma ON a.actorId = ma.actorId
INNER JOIN Movies m ON ma.movieId = m.movieId
INNER JOIN Country c ON m.countryId = c.countryId
INNER JOIN Continent cn ON c.continentId = cn.continentId
WHERE a.actorGender = 'M' AND cn.continentName = 'Ásia'

-- 4.4 Obtenha/Liste todos os videos lançados nos meses de Maio, Junho e Julho
SELECT 
    m.movieId,
    m.movieName,
    m.movieDuration,
    m.movieReleaseDate,
    MONTH(m.movieReleaseDate) AS ReleaseMonth
FROM Movies m
WHERE MONTH(m.movieReleaseDate) IN (5, 6, 7)
ORDER BY m.movieReleaseDate;

-- 4.5 Obtenha/Liste todos os videos de acção (Action) realizados num país europeu e lançados em Dezembro
SELECT DISTINCT *
FROM Movies m
INNER JOIN MovieGenre mg ON m.movieId = mg.movieId
INNER JOIN Genre g ON mg.genreId = g.genreId
INNER JOIN Country c ON m.countryId = c.countryId
INNER JOIN Continent cn ON c.continentId = cn.continentId
WHERE LOWER(TRIM(g.genreName)) = 'Action'
    AND cn.continentName = 'Europa'
    AND MONTH(m.movieReleaseDate) = 12
ORDER BY m.movieName;

-- 4.6 Obtenha/Liste todos os videos para maiores de 18 (ex: 18+)
SELECT *
FROM Movies m
LEFT JOIN AgeRating ar ON m.ageRatingId = ar.ageRatingId
WHERE ar.ageRatingCode = '+18'
ORDER BY m.movieName;

-- 4.7 Conte quantos videos existem para menores de 10 (ex: -10) produzidos por Continente
SELECT 
    cn.continentId,
    cn.continentName,
    COUNT(DISTINCT m.movieId) AS TotalMovies
FROM Movies m
LEFT JOIN AgeRating ar ON m.ageRatingId = ar.ageRatingId
LEFT JOIN Country c ON m.countryId = c.countryId
LEFT JOIN Continent cn ON c.continentId = cn.continentId
WHERE ar.ageRatingId = 1
GROUP BY cn.continentId, cn.continentName
ORDER BY TotalMovies DESC;

-- 4.8 Conte quantos videos existem para maiores de 18 (ex: 18+) produzidos por país da Europa
SELECT 
    c.countryId,
    c.countryName,
    COUNT(DISTINCT m.movieId) AS TotalMovies
FROM Movies m
LEFT JOIN AgeRating ar ON m.ageRatingId = ar.ageRatingId
LEFT JOIN Country c ON m.countryId = c.countryId
LEFT JOIN Continent cn ON c.continentId = cn.continentId
WHERE ar.ageRatingId = 5
    AND cn.continentName = 'Europa'
GROUP BY c.countryId, c.countryName
ORDER BY TotalMovies DESC;

-- 4.9 Qual o nome dos top 10 directores com melhor rating médio nos seus filmes
SELECT TOP 10
    d.directorId,
    d.directorName,
    COUNT(DISTINCT m.movieId) AS TotalFilmes,
    AVG(mv.movieRating) AS AverageRating
FROM Director d
LEFT JOIN MovieDirector md ON d.directorId = md.directorId
LEFT JOIN Movies m ON md.movieId = m.movieId
LEFT JOIN MovieVotes mv ON m.movieId = mv.movieId
GROUP BY d.directorId, d.directorName
HAVING COUNT(DISTINCT m.movieId) > 0
ORDER BY AverageRating DESC;
