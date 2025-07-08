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

### 🔒 Güvenlik Özellikleri

* **Environment Variables** - Veritabanı kimlik bilgileri güvenli yönetimi
* **HTTPS Support** - SSL/TLS şifreli iletişim
* **Rate Limiting** - API istek sınırlaması ve DDoS koruması
* **Input Validation** - Kapsamlı girdi doğrulama ve XSS koruması
* **Docker Security** - Güvenli container deployment

---

## 🧱 Teknolojiler

| Katman     | Teknoloji                      |
| ---------- | ------------------------------ |
| Backend    | Spring Boot (Java)             |
| Frontend   | React + Vite + Tailwind        |
| Mobil      | React Native + Expo            |
| Veritabanı | PostgreSQL                     |
| Auth       | JWT                            |
| Security   | Spring Security + Bucket4j     |
| Deployment | Docker + Docker Compose        |

---

## 📁 Klasör Yapısı (Monorepo)

```
fistein/
├── backend/           # Spring Boot backend
├── frontend/          # React (Vite + Tailwind)
├── mobile/            # React Native (Expo)
├── wireframes/        # Wireframe görselleri
├── docker-compose.yml # Docker deployment
├── .env.example       # Environment variables template
└── README.md
```

---

## � Hızlı Başlangıç (Docker)

### 1. Repository'yi klonlayın
```bash
git clone <repository-url>
cd fistein
```

### 2. Environment variables'ları yapılandırın
```bash
cp .env.example .env
# .env dosyasını düzenleyin
```

### 3. Uygulamayı başlatın
```bash
docker-compose up -d
```

### 4. Uygulamayı kontrol edin
```bash
# Health check
curl http://localhost:8080/actuator/health

# Logları görüntüle
docker-compose logs -f backend
```

---

## 📦 Manuel Kurulum

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

## 🔒 Güvenlik Yapılandırması

### Environment Variables

Uygulama aşağıdaki environment variables'ları kullanır:

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/fistein
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-secure-password

# JWT
JWT_SECRET=your-super-secret-jwt-key

# Rate Limiting
RATE_LIMIT_REQUESTS_PER_MINUTE=60
RATE_LIMIT_BURST_CAPACITY=100

# SSL (Production)
SSL_ENABLED=true
SSL_KEY_STORE=/path/to/keystore.p12
SSL_KEY_STORE_PASSWORD=your-keystore-password
```

### Production Deployment

Detaylı production deployment rehberi için [`DEPLOYMENT_GUIDE.md`](./DEPLOYMENT_GUIDE.md) dosyasını inceleyin.

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
| Backend    | ✅ Tamamlandı | Spring Boot API + Güvenlik özellikleri    |
| Frontend   | ✅ Tamamlandı | React ana yapı ve temel sayfalar hazır   |
| Mobile     | ❌ Bekliyor   | React Native uygulaması henüz başlanmadı  |
| Security   | ✅ Tamamlandı | Rate limiting, validation, HTTPS          |
| Docker     | ✅ Tamamlandı | Container deployment hazır                |

### 🎯 Frontend Özellikleri (Tamamlandı)

* ✅ JWT Authentication (Giriş/Kayıt)
* ✅ Responsive Layout ve Navigation
* ✅ Dashboard ile bakiye görüntüleme
* ✅ Tailwind CSS styling
* ✅ TypeScript type safety
* ✅ API integration hazır
* ⏳ Grup yönetimi (placeholder)
* ⏳ Harcama yönetimi (placeholder)

### 🔒 Güvenlik Özellikleri (Tamamlandı)

* ✅ Environment variables ile güvenli yapılandırma
* ✅ Rate limiting (Bucket4j)
* ✅ Input validation (Bean Validation)
* ✅ HTTPS support
* ✅ Global exception handling
* ✅ Docker security best practices

### 🎯 Sonraki Adımlar

1. **Grup yönetimi sayfaları** (CRUD operations)
2. **Harcama ekleme/düzenleme** sayfaları
3. **Mobile app** geliştirme (React Native)
4. **Testing** suite ekleme
5. **CI/CD** pipeline kurulumu

---

## 📚 Dokümantasyon

Detaylı proje dokümantasyonu ve API referansı için [`docs/`](./docs/) klasörünü ziyaret edin:

- **[📋 Proje Dokümantasyonu](./docs/PROJECT_DOCUMENTATION.md)** - Mimari, kurulum, geliştirme rehberi
- **[🔌 API Dokümantasyonu](./docs/API_DOCUMENTATION.md)** - Kapsamlı API referansı
- **[🚀 Deployment Guide](./DEPLOYMENT_GUIDE.md)** - Production deployment rehberi
- **[📚 Dokümantasyon İndeksi](./docs/README.md)** - Dokümantasyon ana sayfası

## 📄 Lisans

Bu proje MIT lisansı ile lisanslanmıştır.
