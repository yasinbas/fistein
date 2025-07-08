🧾 Fiştein – Gider Takip Uygulaması

**Fiştein**, arkadaş grupları ve ev arkadaşları için geliştirilmiş bir ortak gider takibi uygulamasıdır. Splitwise benzeri sade bir arayüz sunar. Hem web hem de mobil platformlarda çalışacak şekilde tasarlanmıştır.

**🌐 Production URL**: https://app.fistein.info

---

## 🚀 Özellikler

* Ortak harcama takibi (gruplar halinde)
* Harcama detaylandırma (Kim ödedi, kim borçlu?)
* Kişisel bakiye ve alacak/borç durumu
* Web ve mobil uyumlu (responsive)
* JWT ile kullanıcı kimlik doğrulama
* Google OAuth entegrasyonu
* Kullanıcı dostu arayüz

---

## 🧱 Teknolojiler

| Katman     | Teknoloji                      |
| ---------- | ------------------------------ |
| Backend    | Spring Boot (Java 21)         |
| Frontend   | React + Vite + Tailwind        |
| Mobil      | React Native + Expo            |
| Veritabanı | PostgreSQL                     |
| Auth       | JWT + Google OAuth             |
| Deployment | Docker + Nginx                 |
| Domain     | fistein.info                   |

---

## 📁 Klasör Yapısı (Monorepo)

```
fistein/
├── backend/           # Spring Boot backend
├── frontend/          # React (Vite + Tailwind)
├── mobile/            # React Native (Expo)
├── nginx/             # Nginx reverse proxy config
├── scripts/           # Deployment scripts
├── wireframes/        # Wireframe görselleri
├── docker-compose.yml # Development setup
├── docker-compose.production.yml # Production setup
└── DEPLOYMENT_GUIDE.md # Production deployment rehberi
```

---

## 🌐 URLs

### Production
- **Ana Site**: https://fistein.info (→ app.fistein.info'ya yönlendirir)
- **Web App**: https://app.fistein.info
- **API**: https://api.fistein.info/api

### Development
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api

---

## � Hızlı Başlangıç

### Development Ortamı

```bash
# 1. Projeyi klonlayın
git clone <repository-url>
cd fistein

# 2. Kurulum scriptini çalıştırın
./scripts/setup.sh

# 3. Environment dosyalarını düzenleyin
# frontend/.env.development ve backend/.env dosyalarını güncelleyin

# 4. Uygulamayı build edin
./scripts/build.sh development

# 5. Development ortamını başlatın
./scripts/deploy.sh development
```

### Production Deployment

Detaylı production deployment rehberi için: **[DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)**

```bash
# 1. Production environment ayarları
cp .env.production .env.production.local
# .env.production.local dosyasını düzenleyin

# 2. SSL sertifikası oluşturun
# (Rehberde detayları mevcuttur)

# 3. Production build ve deployment
./scripts/build.sh production
./scripts/deploy.sh production
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

### Database (PostgreSQL)

```bash
# Docker ile
docker run -d \
  --name fistein-postgres \
  -e POSTGRES_DB=fistein_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=Ysn2025! \
  -p 5432:5432 \
  postgres:15-alpine
```

---

## 🔧 Geliştirme

### Environment Variables

#### Frontend (.env.development)
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_GOOGLE_CLIENT_ID=your-google-client-id
```

#### Backend (.env)
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/fistein_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=Ysn2025!
JWT_SECRET=your-jwt-secret
GOOGLE_CLIENT_ID=your-google-client-id
```

### Scripts

```bash
# Kurulum
./scripts/setup.sh

# Build
./scripts/build.sh [development|production]

# Deployment
./scripts/deploy.sh [development|production]
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
| Deployment | ✅ Tamamlandı | Docker + Production ready                |

### 🎯 Frontend Özellikleri (Tamamlandı)

* ✅ JWT Authentication (Giriş/Kayıt)
* ✅ Google OAuth entegrasyonu
* ✅ Responsive Layout ve Navigation
* ✅ Dashboard ile bakiye görüntüleme
* ✅ Tailwind CSS styling
* ✅ TypeScript type safety
* ✅ API integration hazır
* ⏳ Grup yönetimi (placeholder)
* ⏳ Harcama yönetimi (placeholder)

### 🎯 Production Özellikleri (Yeni!)

* ✅ **Docker containerization**
* ✅ **Nginx reverse proxy**
* ✅ **SSL/HTTPS support**
* ✅ **Multi-domain setup** (app.fistein.info, api.fistein.info)
* ✅ **Environment separation**
* ✅ **Health checks**
* ✅ **Security headers**
* ✅ **Rate limiting**
* ✅ **Production-ready logging**

### 🎯 Sonraki Adımlar

1. **Grup yönetimi sayfaları** (CRUD operations)
2. **Harcama ekleme/düzenleme** sayfaları
3. **Mobile app** geliştirme (React Native)
4. **Testing** suite ekleme
5. **CI/CD** pipeline setup
6. **Monitoring** ve alerting

---

## 📚 Dokümantasyon

Detaylı proje dokümantasyonu ve API referansı için [`docs/`](./docs/) klasörünü ziyaret edin:

- **[📋 Proje Dokümantasyonu](./docs/PROJECT_DOCUMENTATION.md)** - Mimari, kurulum, geliştirme rehberi
- **[🔌 API Dokümantasyonu](./docs/API_DOCUMENTATION.md)** - Kapsamlı API referansı
- **[� Deployment Rehberi](./DEPLOYMENT_GUIDE.md)** - Production deployment adımları
- **[�📚 Dokümantasyon İndeksi](./docs/README.md)** - Dokümantasyon ana sayfası

---

## 🔒 Güvenlik

- **HTTPS** zorlaması
- **JWT** token authentication
- **Google OAuth** entegrasyonu
- **CORS** yapılandırması
- **Rate limiting**
- **Security headers** (CSP, HSTS, XSS Protection)
- **Non-root** container execution

---

## 📞 Destek

- **Issues**: GitHub Issues
- **Documentation**: `/docs` klasörü
- **Deployment**: `DEPLOYMENT_GUIDE.md`

---

## 📄 Lisans

Bu proje MIT lisansı ile lisanslanmıştır.
