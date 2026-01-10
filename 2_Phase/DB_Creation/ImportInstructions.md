-- (A) IMPORT 'genre.csv' to the TABLE CALLED 'Genre' USING AZURE DATA STUDIO with:
-- (1) put as PK the genreId and int type (onyl genreId)

-- (B) IMPORT 'movies.csv' to the TABLE CALLED 'MoviesImport' USING AZURE DATA STUDIO with:
-- (1) All fields 'Allow null'
-- (2) movieName - nvarchar(MAX) | movieReleaseDate - nvarchar(100) | movieBudget - bigint

-- (C) IMPORT 'actors.csv' to the TABLE CALLED 'ActorImport' USING AZURE DATA STUDIO with:
-- (1) All fields 'Allow null'

-- (D) IMPORT 'genre_movies.csv' to the TABLE CALLED 'GenreMovieImport' USING AZURE DATA STUDIO with:
-- (1) int type (both of the fields)

-- (E) IMPORT 'movie_votes.csv' to the TABLE CALLED 'MovieVotesImport' USING AZURE DATA STUDIO with:
-- (1) All fields 'Allow null'

-- (F) IMPORT 'directors.csv' to the TABLE CALLED 'DirectorImport' USING AZURE DATA STUDIO with:
-- (1) All fields 'Allow null'