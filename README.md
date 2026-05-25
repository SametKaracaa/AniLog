# AniLog 

> Anime & Manga Takip ve Keşif Uygulaması

AniLog, kullanıcıların izledikleri animeleri ve okudukları mangaları kişisel kütüphanelerinde takip etmelerini sağlayan, çevrim içi odaklı bir mobil uygulamadır. Harici bir REST API üzerinden anlık anime verilerini çekerek kullanıcıların kendi listelerini oluşturmasına, güncel izleme durumlarını ve puanlarını kaydetmesine olanak tanır.

[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-purple.svg)](https://developer.android.com/topic/architecture)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-yellow.svg)](https://firebase.google.com)

---

##  İçindekiler

- [Proje Hakkında](#-proje-hakkında)
- [Özellikler](#-özellikler)
- [Teknoloji Yığını](#-teknoloji-yığını)
- [Mimari](#-mimari)
- [Proje Yapısı](#-proje-yapısı)
- [Ekranlar ve Kullanım](#-ekranlar-ve-kullanım)
- [Kurulum](#-kurulum)
- [Firebase Yapılandırması](#-firebase-yapılandırması)
- [İzinler](#-i̇zinler)
- [Veri Modeli](#-veri-modeli)
- [Şart Uyumluluğu](#-şart-uyumluluğu)
- [Geliştirici Notları](#-geliştirici-notları)
- [Geliştiriciler](#-geliştiriciler)

---

##  Proje Hakkında

### Çözülen Problem

Anime ve manga severler izledikleri içerikleri takip etmekte zorlanır:

- ❌ İzlenen animeler ve okunan mangalar farklı yerlerde dağınık tutuluyor
- ❌ Hangi bölümde kalındığı sıklıkla unutuluyor
- ❌ Kişisel istatistikleri (toplam izleme süresi, tür tercihleri) hesaplamak zor
- ❌ Yeni anime keşfetmek için ayrı platformlara göz atmak gerekiyor
- ❌ Cihaz değişince tüm takip listesi kaybolur

### AniLog'un Çözümü

- ✅ Tek bir uygulamada hem anime hem manga organizasyonu
- ✅ Watching / Plan to Watch / Completed durumlarıyla net takip
- ✅ Otomatik hesaplanan kişisel istatistikler ve tür dağılımı
- ✅ Jikan API ile entegre keşif ve Tinder tarzı hızlı keşfetme
- ✅ Firebase bulut tabanlı sayesinde her cihazdan aynı listeye erişim

### Hedef Kitle

Anime ve manga severler — özellikle birden fazla seri takip eden, listelerini ve istatistiklerini görmek isteyen kullanıcılar.

---

##  Özellikler

###  Kimlik Doğrulama
- E-posta ve şifre ile kayıt / giriş
- Firebase Authentication ile güvenli oturum
- Otomatik oturum hatırlama
- Şifre sıfırlama desteği

###  Anime Keşif (Discover)
- Top anime listesi
- Sezon bazlı keşif
- Anahtar kelimeyle arama
- Tür (genre) bazlı filtreleme
- Sonsuz kaydırma (infinite scroll) ile sayfalama
- Aşağı çekme (pull-to-refresh) ile yenileme

###  Hızlı Keşif (Swipe)
- Kart yığını üzerinden hızlı keşfetme
- Sağa kaydır → "Plan to Watch" listesine ekle
- Sola kaydır → geç
- Eşleşme anında konfeti animasyonu
- Otomatik yeni içerik yükleme

###  Kişisel Kütüphane
- 3 sekmeli yapı: **Watching / Plan to Watch / Completed**
- Her anime için durum, puan ve yorum kaydetme
- Gerçek zamanlı senkronizasyon (Firebase Realtime Database)
- Long-press ile silme (onay diyaloğu ile)
- Tamamlanan anime için konfeti kutlaması

###  İstatistikler (Stats)
- Toplam anime sayısı
- Toplam izlenen bölüm sayısı
- Ortalama puan
- "Harcanan zaman" (gün + saat)
- Tür dağılımı görselleştirmesi
- İlk kullanım için tutorial diyaloğu

###  Hatırlatma Sistemi
- Günlük yerel bildirimler (varsayılan 20:00)
- TimePickerDialog ile saat değiştirme
- AlarmManager ile kesin zamanlama
- Cihaz yeniden başladıktan sonra otomatik kurulum (BootReceiver)
- Test bildirim butonu

###  Kullanıcı Ayarları
- Profil düzenleme (avatar + görünen ad)
- Bildirim aç/kapa
- Hatırlatma saati ayarlama
- Silme öncesi onay tercihi
- Varsayılan kütüphane sekmesi seçimi
- Hesap çıkışı

###  Anime Detay Sayfası
- Tam açıklama, puan, bölüm sayısı, sezon, tür bilgileri
- Karakter listesi (yatay RecyclerView)
- Yüksek çözünürlüklü kapak görseli
- "Kütüphaneye Ekle" hızlı erişimi
- Yorum yazma diyaloğu (puan + metin)

---

##  Teknoloji Yığını

### Dil ve Platform
| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| Java | JDK 17 | Ana programlama dili |
| Android SDK | API 24-34 | Android 7.0 - 14 desteği |
| Gradle KTS | 8.13.2 | Build sistemi (Kotlin DSL) |
| Android Gradle Plugin | 8.13.2 | AGP |

### Android Jetpack
| Bileşen | Açıklama |
|---------|----------|
| **ViewModel** | UI durumu ve iş mantığını tutar |
| **LiveData** | Gözlemlenebilir, yaşam döngüsü farkında veri |
| **Navigation Component** | Fragment geçişlerini merkezi olarak yönetir |
| **ViewBinding** | Tip güvenli view erişimi |
| **Lifecycle** | Yaşam döngüsü farkındalığı |

### Ağ Katmanı
| Kütüphane | Açıklama |
|-----------|----------|
| **Retrofit 2** | REST API istemcisi |
| **OkHttp** | HTTP istemcisi + LoggingInterceptor |
| **Gson** | JSON serileştirme/seri çözme |

### Backend
| Servis | Kullanım |
|--------|----------|
| **Firebase Authentication** | Kullanıcı kayıt/giriş |
| **Firebase Realtime Database** | NoSQL bulut veritabanı |
| **Jikan API v4** | MyAnimeList anime verisi |

### UI Kütüphaneleri
| Kütüphane | Açıklama |
|-----------|----------|
| **Material Components 3** | Material Design 3 bileşenleri |
| **Glide** | Resim yükleme ve cache |
| **CardStackView** | Tinder tarzı kart yığını |
| **Shimmer** | İskelet yükleme animasyonu |
| **Konfetti** | Konfeti animasyonu |
| **SwipeRefreshLayout** | Aşağı çekerek yenileme |

---

##  Mimari

AniLog **MVVM (Model-View-ViewModel)** mimarisi üzerine kurulu, **tek Activity – çok Fragment** yapısı kullanır.

```
┌─────────────────────────────────────────────┐
│              VIEW (UI Layer)                │
│  Activity, Fragment, RecyclerView Adapter   │
│         (ViewBinding ile bağlı)             │
└──────────────────┬──────────────────────────┘
                   │ observes
                   ▼
┌─────────────────────────────────────────────┐
│       VIEWMODEL (Presentation Layer)        │
│   LiveData, MutableLiveData, UI State       │
│       (Callback Pattern ile async)          │
└──────────────────┬──────────────────────────┘
                   │ calls
                   ▼
┌─────────────────────────────────────────────┐
│         MODEL (Data Layer)                  │
│  Repository, Data Models, API, DB           │
│  JikanRepository      FirebaseRepository    │
└─────────────────────────────────────────────┘
```

### Veri Akışı

1. **View** kullanıcı etkileşimini ViewModel'e iletir
2. **ViewModel** Repository üzerinden veri talep eder
3. **Repository** uygun veri kaynağından (API veya Firebase) veriyi getirir
4. Sonuç **Callback** ile ViewModel'e döner
5. ViewModel **LiveData**'yı günceller
6. View, LiveData değişikliğini gözlemleyip UI'ı yeniler

### Kullanılan Tasarım Desenleri

- **MVVM** — Katmanlı mimari
- **Repository Pattern** — Veri kaynaklarının soyutlanması
- **Singleton** — `PreferencesManager`, `MAListApp` (double-checked locking)
- **Observer** — LiveData mekanizması
- **Adapter** — RecyclerView adapter'ları
- **Callback** — Asenkron işlem yönetimi

---

##  Proje Yapısı

```
com.malist.app/
├── data/                          # Veri katmanı
│   ├── api/
│   │   ├── JikanApiService.java   # Retrofit arayüzü
│   │   └── JikanModels.java       # API yanıt modelleri
│   ├── model/
│   │   ├── AnimeModel.java        # Anime veri sınıfı
│   │   ├── UserProfile.java       # Kullanıcı profili
│   │   ├── WatchStatus.java       # Watching/Planned/Completed enum
│   │   ├── AnimeGenre.java
│   │   ├── AnimeOrderBy.java
│   │   └── AnimeSortOrder.java
│   ├── prefs/
│   │   └── PreferencesManager.java # SharedPreferences singleton
│   └── repository/
│       ├── JikanRepository.java   # API çağrıları
│       └── FirebaseRepository.java # Firebase işlemleri
│
├── ui/                            # UI katmanı
│   ├── auth/
│   │   ├── LoginFragment.java
│   │   ├── RegisterFragment.java
│   │   └── AuthViewModel.java
│   ├── onboarding/
│   │   ├── OnboardingFragment.java
│   │   └── OnboardingAdapter.java
│   ├── discover/
│   │   ├── DiscoverFragment.java
│   │   ├── DiscoverViewModel.java
│   │   ├── AnimeAdapter.java
│   │   └── FilterBottomSheet.java
│   ├── swipe/
│   │   ├── SwipeFragment.java
│   │   ├── SwipeViewModel.java
│   │   └── CardStackAdapter.java
│   ├── library/
│   │   ├── LibraryFragment.java
│   │   ├── LibraryViewModel.java
│   │   └── LibraryAdapter.java
│   ├── stats/
│   │   ├── StatsFragment.java
│   │   └── StatsViewModel.java
│   ├── settings/
│   │   ├── SettingsFragment.java
│   │   ├── SettingsViewModel.java
│   │   └── AvatarAdapter.java
│   └── detail/
│       ├── AnimeFullDetailFragment.java
│       ├── AnimeFullDetailViewModel.java
│       ├── AnimeDetailBottomSheet.java
│       └── CharacterAdapter.java
│
├── util/                          # Yardımcı sınıflar
│   ├── NotificationHelper.java    # Bildirim mantığı
│   ├── ReminderReceiver.java      # Alarm BroadcastReceiver
│   ├── BootReceiver.java          # Cihaz açılış receiver
│   └── FormatUtils.java
│
├── MainActivity.java              # Tek Activity
└── MAListApp.java                 # Application sınıfı
```

### Sayılarla Proje

| Metrik | Sayı |
|--------|------|
| Toplam Java dosyası | **43** |
| Activity sayısı | **1** |
| Ana Fragment sayısı | **9** |
| BottomSheetDialogFragment | **2** |
| ViewModel | **7** |
| RecyclerView Adapter | **6** |
| Repository | **2** |
| Model sınıfı | **6** |
| BroadcastReceiver | **2** |
| XML Layout | **23** |
| SharedPreferences anahtarı | **7** |

---

##  Ekranlar ve Kullanım

### 1. Onboarding (İlk Açılış)

Uygulama ilk kez açıldığında 3-4 slaytlık tanıtım gösterilir. Tamamlanınca `first_launch` SharedPreferences flag'i `false` yapılır ve bir daha gösterilmez.

**Nasıl kullanılır:** ViewPager2 üzerinde yatay olarak kaydırarak slaytları görüntüleyin. Son slayttaki "Başla" butonuna tıklayarak Login ekranına geçin.

### 2. Login / Register

**Yeni kullanıcı:**
1. "Hesabın yok mu? Kayıt ol" linkine tıkla
2. E-posta, şifre ve görünen ad gir
3. "Kayıt Ol" butonuna bas
4. Firebase'de hesap oluşturulur, profil otomatik yaratılır

**Mevcut kullanıcı:**
1. E-posta ve şifre gir
2. "Giriş Yap" butonuna bas
3. Başarılı girişte ana ekrana yönlendirilirsin

### 3. Discover (Keşif)

**Anime keşfi:**
- Açılışta Jikan API'den top anime listesi yüklenir
- 2 sütunlu grid yapısı
- **Aşağı çekerek** listeyi yenile
- **Aşağı kaydırarak** daha fazla anime yükle
- Üstteki **arama çubuğu** ile arama yap
- **Filtre butonu** ile tür bazlı filtreleme

**Bir animeye tıklayınca:**
- Hızlı önizleme için BottomSheet açılır
- "Tam Detay" butonu ile detay sayfasına geç

### 4. Swipe (Tinder Tarzı Keşif)

- Ekrana bir kart yığını gelir
- **Sağa kaydır:** Animeyi "Plan to Watch" listene ekle (konfeti animasyonu!)
- **Sola kaydır:** Animeyi geç, yenisi gelir
- Kartlar bittikçe otomatik yeni öneriler yüklenir

### 5. Library (Kütüphane)

Kişisel kütüphanen, 3 sekmeye ayrılır:

- **Watching:** Şu anda izlediklerim
- **Plan to Watch:** İzlemek için plandakiler
- **Completed:** Tamamladıklarım

**İşlemler:**
- Bir animeye **tıklayarak** detayını aç
- **Long-press** ile durum değiştir veya sil
- Silmeden önce AlertDialog onay isteyebilir (Settings'ten ayarlanabilir)
- Bir animeyi "Completed" yaptığında konfeti animasyonu oynar

### 6. Stats (İstatistikler)

İzleme alışkanlıklarının analizi:

- **Toplam anime:** Kütüphanedeki toplam sayı
- **Toplam bölüm:** İzlenen toplam bölüm
- **Ortalama puan:** Verdiğin puanların ortalaması
- **Harcanan zaman:** İzlenen toplam süre (gün + saat)
- **Tür dağılımı:** Hangi türde kaç anime izlediğin

İlk açılışta radar grafiği tanıtımı gösterilir (her kullanıcı için bir kez).

### 7. Settings (Ayarlar)

**Profil:**
- Avatar seç (RecyclerView'da listelenen seçenekler)
- Görünen adı düzenle (DialogFragment)

**Bildirimler:**
- Bildirim aç/kapa anahtarı
- Hatırlatma saati (TimePicker ile)
- Test bildirimi gönder

**Davranış:**
- Silme öncesi onay iste (toggle)
- Varsayılan kütüphane sekmesi (Watching/Plan/Completed)

**Hesap:**
- Çıkış yap (AlertDialog onayı ile)
- Hakkında bilgisi

### 8. Anime Full Detail

Bir animenin tam detay sayfası:

- Kapak görseli
- Tam açıklama metni
- Puan (Jikan'dan gelen toplum puanı)
- Bölüm sayısı, sezon, tür chip'leri
- **Karakterler bölümü:** Yatay RecyclerView
- "Kütüphaneye Ekle" butonu
- "Yorum yaz" butonu → puan + yorum diyaloğu

---

##  Kurulum

### Gereksinimler

- Android Studio Hedgehog (2023.1.1) veya üzeri
- JDK 17
- Android SDK API 24 - 34
- Bir Firebase projesi
- Aktif internet bağlantısı

### Adım Adım

1. **Repoyu klonla:**
   ```bash
   git clone <repo-url>
   cd MAList_Proje
   ```

2. **Android Studio'da aç:**
   - File → Open → klasörü seç

3. **`local.properties` dosyasını yapılandır:**
   ```properties
   sdk.dir=C:\\Users\\<USERNAME>\\AppData\\Local\\Android\\Sdk
   ```

4. **Firebase yapılandırması** (sonraki bölüm)

5. **Gradle sync** çalıştır (Android Studio bunu otomatik yapar)

6. **Çalıştır:** ▶️ Run butonuna bas veya `Shift+F10`

---

## Firebase Yapılandırması

### 1. Firebase Projesi Oluştur

1. [Firebase Console](https://console.firebase.google.com/)'a git
2. "Add project" → Proje adı: `AniLog` (veya istediğin ad)
3. Google Analytics opsiyonel (gerekmez)

### 2. Android Uygulamasını Ekle

1. Proje genel bakışında Android ikonuna tıkla
2. **Paket adı:** `com.malist.app`
3. **App nickname:** AniLog
4. SHA-1 (opsiyonel, sadece release imzası için)

### 3. `google-services.json` İndir

İndirilen dosyayı `app/` klasörüne kopyala:
```
MAList_Proje/
└── app/
    └── google-services.json  ← buraya
```

### 4. Authentication'ı Etkinleştir

1. Sol menüden **Authentication** → **Get started**
2. **Sign-in method** sekmesi
3. **Email/Password** → Enable → Save

### 5. Realtime Database Oluştur

1. Sol menüden **Realtime Database** → **Create database**
2. Bölge: Belçika (europe-west1) veya yakın bir bölge
3. Güvenlik kuralları için **Test mode**'da başla (sonra prod'a geç)

### 6. Güvenlik Kuralları (Production)

Realtime Database → Rules → şu kuralları kopyala:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

Bu kurallar, her kullanıcının **sadece kendi verisini** okuyup yazabilmesini garanti eder.

---

##  İzinler

`AndroidManifest.xml` dosyasında talep edilen izinler:

| İzin | Amaç |
|------|------|
| `INTERNET` | Jikan API ve Firebase için ağ erişimi |
| `POST_NOTIFICATIONS` | Android 13+ bildirim izni (runtime) |
| `RECEIVE_BOOT_COMPLETED` | Cihaz açıldığında hatırlatma alarmlarını yeniden kurmak |
| `SCHEDULE_EXACT_ALARM` | Android 12+ kesin saatte alarm kurma |

`POST_NOTIFICATIONS` izni Android 13'ten itibaren kullanıcıdan runtime'da istenir. İzin verilmezse bildirimler görünmez ama uygulamanın diğer özellikleri çalışmaya devam eder.

---

##  Veri Modeli

### Firebase Realtime Database Yapısı

```json
{
  "users": {
    "{uid}": {
      "profile": {
        "displayName": "Samet",
        "avatarId": 3,
        "createdAt": 1715299200
      },
      "library": {
        "1535": {
          "title": "Death Note",
          "imageUrl": "https://cdn.myanimelist.net/...",
          "watchStatus": "COMPLETED",
          "userRating": 9.5,
          "userReview": "Müthiş bir psikolojik gerilim!",
          "episodeCount": 37,
          "genres": ["Mystery", "Supernatural"]
        },
        "5114": {
          "title": "Fullmetal Alchemist: Brotherhood",
          ...
        }
      }
    }
  }
}
```

**Anahtar mantığı:**
- `{uid}` — Firebase Auth tarafından üretilen benzersiz kullanıcı kimliği
- `{animeId}` — MyAnimeList ID (Jikan API'den gelen `mal_id`)

### SharedPreferences Yapısı

Dosya: `malist_prefs` (MODE_PRIVATE)

| Anahtar | Tip | Varsayılan | Açıklama |
|---------|-----|------------|----------|
| `notifications_enabled` | boolean | `true` | Bildirimler aktif mi |
| `reminder_hour` | int | `20` | Günlük hatırlatma saati |
| `reminder_minute` | int | `0` | Günlük hatırlatma dakikası |
| `confirm_delete` | boolean | `true` | Silme öncesi onay |
| `default_library_tab` | int | `0` | Açılış sekmesi (0:Watching, 1:Planned, 2:Completed) |
| `first_launch` | boolean | `true` | Onboarding gösterilsin mi |
| `radar_tutorial_{uid}` | boolean | `true` | Stats radar tutorial flag (kullanıcı bazlı) |

---

##  



---

##  Geliştirici Notları

### ViewBinding Kullanımı

Her Fragment'ta şu şablon uygulanır:

```java
public class DiscoverFragment extends Fragment {
    private FragmentDiscoverBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Memory leak'i önlemek için
    }
}
```

### Asenkron İşlemler

Java kullandığımız için Coroutines yok. Bunun yerine:

**Retrofit Call/Callback:**
```java
Call<AnimeResponse> call = api.getTopAnime(page);
call.enqueue(new Callback<AnimeResponse>() {
    @Override
    public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
        // UI thread
    }
    @Override
    public void onFailure(Call<AnimeResponse> call, Throwable t) {
        // Hata yönetimi
    }
});
```

**Firebase Listener:**
```java
databaseRef.addOnSuccessListener(snapshot -> { /* başarı */ })
           .addOnFailureListener(e -> { /* hata */ });
```

### Sonsuz Kaydırma Mantığı

DiscoverFragment'taki scroll listener:

```java
binding.rvAnimeGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(RecyclerView rv, int dx, int dy) {
        if (dy > 0) {  // Sadece aşağı yön
            int total = gridLayoutManager.getItemCount();
            int lastVisible = gridLayoutManager.findLastVisibleItemPosition();
            if (lastVisible >= total - 6 && viewModel.canLoadMore()) {
                viewModel.loadMore();
            }
        }
    }
});
```

Eşik **6** seçilmesinin sebebi: kullanıcı sona ulaşmadan önce yeni veri yüklenip kesintisiz akış sağlanır.

### Bildirim Sistemi

Akış: `MAListApp` → `AlarmManager` → `ReminderReceiver` → `NotificationHelper`

1. **MAListApp.onCreate()** kanal kurar ve günlük alarm planlar
2. **AlarmManager** belirlenen saatte ReminderReceiver'ı tetikler
3. **ReminderReceiver** SharedPreferences kontrolü yapıp `NotificationHelper.show()` çağırır
4. **NotificationHelper** NotificationCompat.Builder ile bildirim oluşturur
5. **BootReceiver** cihaz yeniden başlayınca alarmı yeniden kurar

---

##  Geliştiriciler

| İsim | Okul No |
|------|---------|
| **Samet KARACA** | 23181616061 |
| **Deniz ŞENOL** | 23181616023 |

Bu proje üniversite **Mobil Uygulama Projesi** dersi kapsamında geliştirilmiştir.

---

##  Lisans

Bu proje eğitim amacıyla geliştirilmiş bir proje çalışmasıdır.

### Üçüncü Taraf Kaynaklar
- [Jikan API](https://docs.api.jikan.moe/) — MyAnimeList için resmi olmayan REST API
- [Firebase](https://firebase.google.com/) — Google'ın BaaS platformu
- Tüm anime verileri ve görselleri [MyAnimeList](https://myanimelist.net/) kaynaklıdır

---

<p align="center">
  <i>AniLog ile takip et, keşfet, kaydet. 🎌</i>
</p>
