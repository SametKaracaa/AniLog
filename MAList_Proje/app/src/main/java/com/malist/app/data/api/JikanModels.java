package com.malist.app.data.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JikanModels {

    public static class JikanTopAnimeResponse {
        @SerializedName("data")
        public List<JikanAnimeData> data;
        @SerializedName("pagination")
        public JikanPagination pagination;
    }

    public static class JikanAnimeData {
        @SerializedName("mal_id")
        public int malId;
        @SerializedName("title")
        public String title;
        @SerializedName("title_english")
        public String titleEnglish;
        @SerializedName("images")
        public JikanImages images;
        @SerializedName("score")
        public Double score;
        @SerializedName("episodes")
        public Integer episodes;
        @SerializedName("chapters")
        public Integer chapters;
        @SerializedName("synopsis")
        public String synopsis;
        @SerializedName("status")
        public String status;
        @SerializedName("type")
        public String type;
        @SerializedName("genres")
        public List<JikanGenre> genres;
    }

    public static class JikanImages {
        @SerializedName("jpg")
        public JikanImageFormat jpg;
    }

    public static class JikanImageFormat {
        @SerializedName("image_url")
        public String imageUrl;
        @SerializedName("large_image_url")
        public String largeImageUrl;
    }

    public static class JikanPagination {
        @SerializedName("last_visible_page")
        public int lastVisiblePage;
        @SerializedName("has_next_page")
        public boolean hasNextPage;
    }

    public static class JikanGenre {
        @SerializedName("mal_id")
        public int malId;
        @SerializedName("name")
        public String name;
    }

    public static class JikanCharacterResponse {
        @SerializedName("data")
        public List<JikanCharacterData> data;
    }

    public static class JikanCharacterData {
        @SerializedName("character")
        public JikanCharacterInfo character;
        @SerializedName("role")
        public String role;
        @SerializedName("voice_actors")
        public List<JikanVoiceActor> voiceActors;
    }

    public static class JikanCharacterInfo {
        @SerializedName("mal_id")
        public int malId;
        @SerializedName("name")
        public String name;
        @SerializedName("images")
        public JikanImages images;
    }

    public static class JikanVoiceActor {
        @SerializedName("person")
        public JikanPersonInfo person;
        @SerializedName("language")
        public String language;
    }

    public static class JikanPersonInfo {
        @SerializedName("mal_id")
        public int malId;
        @SerializedName("name")
        public String name;
        @SerializedName("images")
        public JikanImages images;
    }
}
