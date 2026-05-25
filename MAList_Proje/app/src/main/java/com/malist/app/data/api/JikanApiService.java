package com.malist.app.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Jikan API v4 endpoint tanimlari.
 * Base URL: https://api.jikan.moe/v4/
 */
public interface JikanApiService {

    @GET("top/anime")
    Call<JikanModels.JikanTopAnimeResponse> getTopAnime(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("seasons/now")
    Call<JikanModels.JikanTopAnimeResponse> getAiringAnime(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("anime")
    Call<JikanModels.JikanTopAnimeResponse> searchAnime(
            @Query("q") String query,
            @Query("page") int page,
            @Query("genres") String genres,
            @Query("order_by") String orderBy,
            @Query("sort") String sort
    );

    @GET("top/manga")
    Call<JikanModels.JikanTopAnimeResponse> getTopManga(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("manga")
    Call<JikanModels.JikanTopAnimeResponse> searchManga(
            @Query("q") String query,
            @Query("page") int page,
            @Query("genres") String genres,
            @Query("order_by") String orderBy,
            @Query("sort") String sort
    );

    @GET("anime/{id}/characters")
    Call<JikanModels.JikanCharacterResponse> getAnimeCharacters(
            @Path("id") int id
    );
}
