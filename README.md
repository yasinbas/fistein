🧾 Fiştein – Gider Takip Uygulaması

**Fiştein**, arkadaş grupları ve ev arkadaşları için geliştirilmiş bir ortak gider takibi uygulamasıdır. Splitwise benzeri sade bir arayüz sunar. Hem web hem de mobil platformlarda çalışacak şekilde tasarlanmıştır.

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

## 📋 Geliştirme Yol Haritası

GitHub Projects sekmesinde bir **Kanban board** oluşturulmalı:

### 🔨 Kolonlar:

* TODO
* IN PROGRESS
* DONE

### 🎯 İlk İşler (Issues):

*

---

## 📄 Lisans

Bu proje MIT lisansı ile lisanslanmıştır.
