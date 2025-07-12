🧾 Fiştein – Gider Takip Uygulaması

**Fiştein**, arkadaş grupları ve ev arkadaşları için geliştirilmiş bir ortak gider takibi uygulamasıdır. Splitwise benzeri sade bir arayüz sunar. Hem web hem de mobil platformlarda çalışacak şekilde tasarlanmıştır.

<img width="1894" height="828" alt="image" src="https://github.com/user-attachments/assets/7b4e0eee-a6c7-4a37-935b-5e232f37eb06" />


---

## 🚀 Özellikler

* Ortak harcama takibi (gruplar halinde)
* Harcama detaylandırma (Kim ödedi, kim borçlu?)
* Kişisel bakiye ve alacak/borç durumu
* Web ve mobil uyumlu (responsive)
* JWT ile kullanıcı kimlik doğrulama
* Kullanıcı dostu arayüz

---

## 🧱 Teknolojiler

| Katman     | Teknoloji                      |
| ---------- | ------------------------------ |
| Backend    | Spring Boot (Java)             |
| Frontend   | React + Vite + Tailwind        |
| Mobil      | React Native + Expo            |
| Veritabanı | PostgreSQL                     |
| Auth       | JWT                            |
| Deployment | Render (backend), Vercel (web) |

---

## 📁 Klasör Yapısı (Monorepo)

```
fistein/
├── backend/           # Spring Boot backend
├── frontend/          # React (Vite + Tailwind)
├── mobile/            # React Native (Expo)
├── wireframes/        # Wireframe görselleri
└── README.md
```

---

## 📦 Projeyi Çalıştırma

### Backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

### Mobile (Expo)

```bash
cd mobile
npm install
npx expo start
```

---

## 🖼️ Wireframe Önizlemesi

Tüm ekran tasarımları `wireframes/` klasöründe yer almaktadır.

* Giriş / Kayıt
* Ana Sayfa (Gruplar)
* Grup Detayı
* Harcama Ekleme
* Profil / Özet

---

## ✅ Proje Durumu

| Bileşen    | Durum      | Açıklama                                    |
| ---------- | ---------- | ------------------------------------------- |
| Backend    | ✅ Tamamlandı | Spring Boot API tam çalışır durumda      |
| Frontend   | ✅ Tamamlandı | React ana yapı ve temel sayfalar hazır   |
| Mobile     | ❌ Bekliyor   | React Native uygulaması henüz başlanmadı  |

### 🎯 Frontend Özellikleri (Tamamlandı)

* ✅ JWT Authentication (Giriş/Kayıt)
* ✅ Responsive Layout ve Navigation
* ✅ Dashboard ile bakiye görüntüleme
* ✅ Tailwind CSS styling
* ✅ TypeScript type safety
* ✅ API integration hazır
* ⏳ Grup yönetimi (placeholder)
* ⏳ Harcama yönetimi (placeholder)

### � Sonraki Adımlar

1. **Grup yönetimi sayfaları** (CRUD operations)
2. **Harcama ekleme/düzenleme** sayfaları
3. **Mobile app** geliştirme (React Native)
4. **Testing** suite ekleme
5. **Deployment** için Docker setup

---

## � Dokümantasyon

Detaylı proje dokümantasyonu ve API referansı için [`docs/`](./docs/) klasörünü ziyaret edin:

- **[📋 Proje Dokümantasyonu](./docs/PROJECT_DOCUMENTATION.md)** - Mimari, kurulum, geliştirme rehberi
- **[🔌 API Dokümantasyonu](./docs/API_DOCUMENTATION.md)** - Kapsamlı API referansı
- **[📚 Dokümantasyon İndeksi](./docs/README.md)** - Dokümantasyon ana sayfası

## �📄 Lisans

Bu proje MIT lisansı ile lisanslanmıştır.
