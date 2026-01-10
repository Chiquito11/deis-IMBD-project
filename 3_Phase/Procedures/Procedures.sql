/* 2.1 COUNT_MOVIES_MONTH_YEAR <month> <year> */
CREATE OR ALTER PROCEDURE COUNT_MOVIES_MONTH_YEAR
    @Month INT,
    @Year  INT
AS
BEGIN
    SELECT COUNT(*) AS MovieCount
    FROM Movies
    WHERE MONTH(movieReleaseDate) = @Month
      AND YEAR(movieReleaseDate)  = @Year;
END;
GO


/* 2.2 COUNT_MOVIES_DIRECTOR <full-name> */
CREATE OR ALTER PROCEDURE COUNT_MOVIES_DIRECTOR
    @FullName NVARCHAR(255)
AS
BEGIN
    SELECT d.directorId,
           d.directorName,
           COUNT(DISTINCT md.movieId) AS MovieCount
    FROM Director d
    INNER JOIN MovieDirector md ON md.directorId = d.directorId
    WHERE d.directorName = @FullName
    GROUP BY d.directorId, d.directorName;
END;
GO


/* 2.3 COUNT_ACTORS_IN_2_YEARS <year-1> <year-2> */
CREATE OR ALTER PROCEDURE COUNT_ACTORS_IN_2_YEARS
    @Year1 INT,
    @Year2 INT
AS
BEGIN
    SELECT 
        YEAR(m.movieReleaseDate) AS Year,
        COUNT(DISTINCT ma.actorId) AS ActorCount
    FROM Movies m
    INNER JOIN MovieActor ma ON ma.movieId = m.movieId
    WHERE YEAR(m.movieReleaseDate) IN (@Year1, @Year2)
    GROUP BY YEAR(m.movieReleaseDate);
END;
GO


/* 2.4 COUNT_MOVIES_BETWEEN_YEARS_WITH_N_ACTORS <year-start> <year-end> <min> <max> */
CREATE OR ALTER PROCEDURE COUNT_MOVIES_BETWEEN_YEARS_WITH_N_ACTORS
    @YearStart INT,
    @YearEnd   INT,
    @MinActors INT,
    @MaxActors INT
AS
BEGIN
    ;WITH MovieActorCounts AS (
        SELECT 
            m.movieId,
            COUNT(DISTINCT ma.actorId) AS ActorCount
        FROM Movies m
        LEFT JOIN MovieActor ma ON ma.movieId = m.movieId
        WHERE YEAR(m.movieReleaseDate) BETWEEN @YearStart AND @YearEnd
        GROUP BY m.movieId
    )
    SELECT COUNT(*) AS MovieCount
    FROM MovieActorCounts
    WHERE ActorCount BETWEEN @MinActors AND @MaxActors;
END;
GO


/* 2.5 GET_MOVIES_ACTOR_YEAR <year> <full-name> */
CREATE OR ALTER PROCEDURE GET_MOVIES_ACTOR_YEAR
    @Year     INT,
    @FullName NVARCHAR(255)
AS
BEGIN
    SELECT m.*
    FROM Movies m
    INNER JOIN MovieActor ma ON ma.movieId = m.movieId
    INNER JOIN Actor a       ON a.actorId = ma.actorId
    WHERE a.actorName = @FullName
      AND YEAR(m.movieReleaseDate) = @Year;
END;
GO


/* 2.6 GET_MOVIES_WITH_ACTOR_CONTAINING <name> */
CREATE OR ALTER PROCEDURE GET_MOVIES_WITH_ACTOR_CONTAINING
    @Search NVARCHAR(255)
AS
BEGIN
    SELECT DISTINCT m.*
    FROM Movies m
    INNER JOIN MovieActor ma ON ma.movieId = m.movieId
    INNER JOIN Actor a       ON a.actorId = ma.actorId
    WHERE a.actorName LIKE '%' + @Search + '%';
END;
GO


/* 2.7 GET_TOP_4_YEARS_WITH_MOVIES_CONTAINING <search-string> */
CREATE OR ALTER PROCEDURE GET_TOP_4_YEARS_WITH_MOVIES_CONTAINING
    @Search NVARCHAR(255)
AS
BEGIN
    SELECT TOP 4
        YEAR(m.movieReleaseDate) AS Year,
        COUNT(*) AS MovieCount
    FROM Movies m
    WHERE m.movieName LIKE '%' + @Search + '%'
    GROUP BY YEAR(m.movieReleaseDate)
    ORDER BY MovieCount DESC;
END;
GO


/* 2.8 GET_ACTORS_BY_DIRECTOR <num> <full-name> */
CREATE OR ALTER PROCEDURE GET_ACTORS_BY_DIRECTOR
    @Num     INT,
    @DirName NVARCHAR(255)
AS
BEGIN
    SELECT DISTINCT TOP (@Num)
        a.actorId,
        a.actorName
    FROM Director d
    INNER JOIN MovieDirector md ON md.directorId = d.directorId
    INNER JOIN Movies m         ON m.movieId = md.movieId
    INNER JOIN MovieActor ma    ON ma.movieId = m.movieId
    INNER JOIN Actor a          ON a.actorId = ma.actorId
    WHERE d.directorName = @DirName
    ORDER BY a.actorName;
END;
GO


/* 2.9 TOP_MONTH_MOVIE_COUNT <year> */
CREATE OR ALTER PROCEDURE TOP_MONTH_MOVIE_COUNT
    @Year INT
AS
BEGIN
    SELECT TOP 1
        MONTH(movieReleaseDate) AS Month,
        COUNT(*) AS MovieCount
    FROM Movies
    WHERE YEAR(movieReleaseDate) = @Year
    GROUP BY MONTH(movieReleaseDate)
    ORDER BY MovieCount DESC;
END;
GO


/* 2.10 TOP_VOTED_ACTORS <num> <year> */
CREATE OR ALTER PROCEDURE TOP_VOTED_ACTORS
    @Num  INT,
    @Year INT
AS
BEGIN
    SELECT TOP (@Num)
        a.actorId,
        a.actorName,
        SUM(v.movieRatingCount) AS TotalVotes
    FROM Actor a
    INNER JOIN MovieActor ma ON ma.actorId = a.actorId
    INNER JOIN Movies m      ON m.movieId = ma.movieId
    INNER JOIN MovieVotes v  ON v.movieId = m.movieId
    WHERE YEAR(m.movieReleaseDate) = @Year
    GROUP BY a.actorId, a.actorName
    ORDER BY TotalVotes DESC;
END;
GO


/* 2.11 TOP_MOVIES_WITH_MORE_GENDER <num> <year> <gender> */
CREATE OR ALTER PROCEDURE TOP_MOVIES_WITH_MORE_GENDER
    @Num    INT,
    @Year   INT,
    @Gender CHAR(1)
AS
BEGIN
    SELECT TOP (@Num)
        m.movieId,
        m.movieName,
        COUNT(*) AS GenderActorCount
    FROM Movies m
    INNER JOIN MovieActor ma ON ma.movieId = m.movieId
    INNER JOIN Actor a       ON a.actorId = ma.actorId
    WHERE YEAR(m.movieReleaseDate) = @Year
      AND a.actorGender = @Gender
    GROUP BY m.movieId, m.movieName
    ORDER BY GenderActorCount DESC;
END;
GO


/* 2.12 TOP_MOVIES_WITH_GENDER_BIAS <num> <year> */
-- diferença absoluta entre nº de atores M e F
CREATE OR ALTER PROCEDURE TOP_MOVIES_WITH_GENDER_BIAS
    @Num  INT,
    @Year INT
AS
BEGIN
    ;WITH GenderCount AS (
        SELECT 
            m.movieId,
            m.movieName,
            SUM(CASE WHEN a.actorGender = 'M' THEN 1 ELSE 0 END) AS MaleCount,
            SUM(CASE WHEN a.actorGender = 'F' THEN 1 ELSE 0 END) AS FemaleCount
        FROM Movies m
        INNER JOIN MovieActor ma ON ma.movieId = m.movieId
        INNER JOIN Actor a       ON a.actorId = ma.actorId
        WHERE YEAR(m.movieReleaseDate) = @Year
        GROUP BY m.movieId, m.movieName
    )
    SELECT TOP (@Num)
        movieId,
        movieName,
        MaleCount,
        FemaleCount,
        ABS(MaleCount - FemaleCount) AS GenderBias
    FROM GenderCount
    ORDER BY ABS(MaleCount - FemaleCount) DESC;
END;
GO


/* 2.13 TOP_6_DIRECTORS_WITHIN_FAMILY <year-start> <year-end> */
-- “family” = filmes com ageRating <= +10 e animation/family?
-- Versão simples: usar género 'Family' ou 'Animation'
CREATE OR ALTER PROCEDURE TOP_6_DIRECTORS_WITHIN_FAMILY
    @YearStart INT,
    @YearEnd   INT
AS
BEGIN
    SELECT TOP 6
        d.directorId,
        d.directorName,
        COUNT(DISTINCT m.movieId) AS FamilyMovies
    FROM Director d
    INNER JOIN MovieDirector md ON md.directorId = d.directorId
    INNER JOIN Movies m         ON m.movieId = md.movieId
    INNER JOIN MovieGenre mg    ON mg.movieId = m.movieId
    INNER JOIN Genre g          ON g.genreId = mg.genreId
    WHERE YEAR(m.movieReleaseDate) BETWEEN @YearStart AND @YearEnd
      AND g.genreName IN ('Family', 'Animation')
    GROUP BY d.directorId, d.directorName
    ORDER BY FamilyMovies DESC;
END;
GO


/* 2.14 DISTANCE_BETWEEN_ACTORS <actor-1> <actor-2>
   Nível de ligação via filmes em comum.
   Versão simples: nº de filmes onde os dois aparecem juntos.
*/
CREATE OR ALTER PROCEDURE DISTANCE_BETWEEN_ACTORS
    @Actor1 INT,
    @Actor2 INT
AS
BEGIN
    SELECT 
        @Actor1 AS Actor1Id,
        @Actor2 AS Actor2Id,
        COUNT(DISTINCT ma1.movieId) AS MoviesTogether
    FROM MovieActor ma1
    INNER JOIN MovieActor ma2 
        ON ma1.movieId = ma2.movieId
       AND ma1.actorId = @Actor1
       AND ma2.actorId = @Actor2;
END;
GO
