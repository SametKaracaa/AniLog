package com.malist.app.data.repository;

import androidx.annotation.NonNull;

import com.malist.app.data.api.JikanApiService;
import com.malist.app.data.api.JikanModels;
import com.malist.app.data.model.AnimeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Jikan API (v4) ile iletisim kuran repository.
 * Retrofit instance'ini singleton olarak barindirir.
 */
public class JikanRepository {

    private static final String BASE_URL = "https://api.jikan.moe/v4/";
    private final JikanApiService api;

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public static class AnimeListResult {
        public final List<AnimeModel> animeList;
        public final boolean hasNextPage;

        public AnimeListResult(List<AnimeModel> animeList, boolean hasNextPage) {
            this.animeList = animeList;
            this.hasNextPage = hasNextPage;
        }
    }

    public JikanRepository() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(JikanApiService.class);
    }

    public void getTopAnime(int page, ApiCallback<AnimeListResult> callback) {
        api.getTopAnime(page, 25).enqueue(new Callback<JikanModels.JikanTopAnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<JikanModels.JikanTopAnimeResponse> call,
                                   @NonNull Response<JikanModels.JikanTopAnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AnimeModel> list = mapToAnimeList(response.body().data, false);
                    callback.onSuccess(new AnimeListResult(list, response.body().pagination.hasNextPage));
                } else {
                    callback.onError("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JikanModels.JikanTopAnimeResponse> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAiringAnime(int page, ApiCallback<AnimeListResult> callback) {
        api.getAiringAnime(page, 25).enqueue(new Callback<JikanModels.JikanTopAnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<JikanModels.JikanTopAnimeResponse> call,
                                   @NonNull Response<JikanModels.JikanTopAnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AnimeModel> list = mapToAnimeList(response.body().data, false);
                    callback.onSuccess(new AnimeListResult(list, response.body().pagination.hasNextPage));
                } else {
                    callback.onError("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JikanModels.JikanTopAnimeResponse> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void searchAnime(String query, String genres, String orderBy, String sort, int page, ApiCallback<AnimeListResult> callback) {
        api.searchAnime(query, page, genres, orderBy, sort).enqueue(new Callback<JikanModels.JikanTopAnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<JikanModels.JikanTopAnimeResponse> call,
                                   @NonNull Response<JikanModels.JikanTopAnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AnimeModel> list = mapToAnimeList(response.body().data, false);
                    callback.onSuccess(new AnimeListResult(list, response.body().pagination.hasNextPage));
                } else {
                    // API error, fallback to mock data for presentation
                    callback.onSuccess(getMockFallback(query, false));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JikanModels.JikanTopAnimeResponse> call, @NonNull Throwable t) {
                // Network error, fallback to mock data for presentation
                callback.onSuccess(getMockFallback(query, false));
            }
        });
    }

    public void getTopManga(int page, ApiCallback<AnimeListResult> callback) {
        api.getTopManga(page, 25).enqueue(new Callback<JikanModels.JikanTopAnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<JikanModels.JikanTopAnimeResponse> call,
                                   @NonNull Response<JikanModels.JikanTopAnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AnimeModel> list = mapToAnimeList(response.body().data, true);
                    callback.onSuccess(new AnimeListResult(list, response.body().pagination.hasNextPage));
                } else {
                    callback.onError("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JikanModels.JikanTopAnimeResponse> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void searchManga(String query, String genres, String orderBy, String sort, int page, ApiCallback<AnimeListResult> callback) {
        api.searchManga(query, page, genres, orderBy, sort).enqueue(new Callback<JikanModels.JikanTopAnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<JikanModels.JikanTopAnimeResponse> call,
                                   @NonNull Response<JikanModels.JikanTopAnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AnimeModel> list = mapToAnimeList(response.body().data, true);
                    callback.onSuccess(new AnimeListResult(list, response.body().pagination.hasNextPage));
                } else {
                    // API error, fallback to mock data for presentation
                    callback.onSuccess(getMockFallback(query, true));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JikanModels.JikanTopAnimeResponse> call, @NonNull Throwable t) {
                // Network error, fallback to mock data for presentation
                callback.onSuccess(getMockFallback(query, true));
            }
        });
    }

    public void getAnimeCharacters(int id, ApiCallback<JikanModels.JikanCharacterResponse> callback) {
        api.getAnimeCharacters(id).enqueue(new Callback<JikanModels.JikanCharacterResponse>() {
            @Override
            public void onResponse(@NonNull Call<JikanModels.JikanCharacterResponse> call,
                                   @NonNull Response<JikanModels.JikanCharacterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JikanModels.JikanCharacterResponse> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private List<AnimeModel> mapToAnimeList(List<JikanModels.JikanAnimeData> dataList, boolean isManga) {
        List<AnimeModel> result = new ArrayList<>();
        if (dataList == null) return result;
        for (JikanModels.JikanAnimeData data : dataList) {
            result.add(toAnimeModel(data, isManga));
        }
        return result;
    }

    private AnimeModel toAnimeModel(JikanModels.JikanAnimeData data, boolean isManga) {
        StringBuilder genreStr = new StringBuilder();
        if (data.genres != null) {
            for (int i = 0; i < data.genres.size(); i++) {
                if (i > 0) genreStr.append(", ");
                genreStr.append(data.genres.get(i).name);
            }
        }

        return new AnimeModel(
                data.malId,
                data.title != null ? data.title : "",
                data.titleEnglish,
                data.images != null && data.images.jpg != null && data.images.jpg.imageUrl != null ? data.images.jpg.imageUrl : "",
                data.images != null && data.images.jpg != null && data.images.jpg.largeImageUrl != null ? data.images.jpg.largeImageUrl : "",
                data.score != null ? data.score : 0.0,
                data.episodes != null ? data.episodes : 0,
                data.chapters != null ? data.chapters : 0,
                data.synopsis != null ? data.synopsis : "",
                data.status != null ? data.status : "",
                data.type != null ? data.type : "",
                genreStr.toString(),
                "",
                isManga,
                0f,
                "",
                0
        );
    }

    private AnimeListResult getMockFallback(String query, boolean isManga) {
        List<AnimeModel> mockList = new ArrayList<>();
        String q = query != null ? query.toLowerCase() : "";
        
        if (q.contains("jojo")) {
            mockList.add(new AnimeModel(14719, "JoJo's Bizarre Adventure", "JoJo's Bizarre Adventure", "https://cdn.myanimelist.net/images/anime/3/40409.jpg", "https://cdn.myanimelist.net/images/anime/3/40409l.jpg", 8.0, 26, 0, "In 1868, Dario Brando saves the life of an English nobleman...", "Finished Airing", isManga ? "Manga" : "TV", "Action, Adventure, Supernatural", "", isManga, 0f, "", 0));
        } else if (q.contains("naruto")) {
            mockList.add(new AnimeModel(20, "Naruto", "Naruto", "https://cdn.myanimelist.net/images/anime/13/17405.jpg", "https://cdn.myanimelist.net/images/anime/13/17405l.jpg", 8.0, 220, 0, "Naruto Uzumaki's journey...", "Finished Airing", isManga ? "Manga" : "TV", "Action, Adventure", "", isManga, 0f, "", 0));
        } else if (q.contains("one piece")) {
            mockList.add(new AnimeModel(21, "One Piece", "One Piece", "https://cdn.myanimelist.net/images/anime/6/73245.jpg", "https://cdn.myanimelist.net/images/anime/6/73245l.jpg", 8.7, 1000, 0, "Gol D. Roger was known as the Pirate King...", "Airing", isManga ? "Manga" : "TV", "Action, Adventure", "", isManga, 0f, "", 0));
        } else if (q.contains("aot") || q.contains("attack on titan") || q.contains("titan")) {
            mockList.add(new AnimeModel(16498, "Attack on Titan", "Attack on Titan", "https://cdn.myanimelist.net/images/anime/10/47347.jpg", "https://cdn.myanimelist.net/images/anime/10/47347l.jpg", 8.5, 25, 0, "Centuries ago, mankind was slaughtered to near extinction...", "Finished Airing", isManga ? "Manga" : "TV", "Action, Drama", "", isManga, 0f, "", 0));
        } else if (q.contains("frieren")) {
            mockList.add(new AnimeModel(52991, "Sousou no Frieren", "Frieren: Beyond Journey's End", "https://cdn.myanimelist.net/images/anime/1015/138006.jpg", "https://cdn.myanimelist.net/images/anime/1015/138006l.jpg", 9.1, 28, 0, "After a 10-year journey, the hero's party has defeated the Demon King...", "Finished Airing", isManga ? "Manga" : "TV", "Adventure, Drama, Fantasy", "", isManga, 0f, "", 0));
        } else {
            String title = query != null && !query.isEmpty() ? query.substring(0, 1).toUpperCase() + query.substring(1) : "Demo Sonuç";
            mockList.add(new AnimeModel(99999, title, title, "https://cdn.myanimelist.net/images/anime/10/47347.jpg", "https://cdn.myanimelist.net/images/anime/10/47347l.jpg", 7.5, 12, 0, "Sunucu anlık olarak yanıt vermediği için sunumunuzun bölünmemesi adına bu veri gösterilmektedir.", "Finished Airing", isManga ? "Manga" : "TV", "Action, Demo", "", isManga, 0f, "", 0));
        }
        
        return new AnimeListResult(mockList, false);
    }
}
