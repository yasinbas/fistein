# 🚀 Fiştein Production Deployment Guide

Bu rehber Fiştein uygulamasının `fistein.info` domain'i üzerinden production'a alınması için gerekli adımları içerir.

## 🎯 Güncellemeler

### Domain Değişikliği
- ✅ `fistein.com` → `fistein.info` güncellendi
- ✅ CORS ayarları güncellendi
- ✅ Production URL'leri yapılandırıldı

### Production Hazırlığı
- ✅ Docker containerization
- ✅ Nginx reverse proxy
- ✅ SSL/HTTPS yapılandırması
- ✅ Environment separation
- ✅ Health checks
- ✅ Security headers

## 📋 Ön Gereksinimler

### Sunucu Gereksinimleri
- Docker & Docker Compose
- 2+ CPU cores
- 4GB+ RAM
- 20GB+ disk space
- Ubuntu 20.04+ / CentOS 8+

### Domain Yapılandırması
Aşağıdaki DNS kayıtlarını oluşturun:

```
A     fistein.info          → YOUR_SERVER_IP
A     www.fistein.info      → YOUR_SERVER_IP
A     app.fistein.info      → YOUR_SERVER_IP
A     api.fistein.info      → YOUR_SERVER_IP
```

### SSL Sertifikası
Let's Encrypt ile SSL sertifikası oluşturun:

```bash
# Certbot kurulumu
sudo apt update
sudo apt install certbot

# SSL sertifikası oluşturma
sudo certbot certonly --standalone -d fistein.info -d www.fistein.info -d app.fistein.info -d api.fistein.info

# Sertifikaları kopyalama
sudo cp /etc/letsencrypt/live/fistein.info/fullchain.pem nginx/ssl/fistein.info.crt
sudo cp /etc/letsencrypt/live/fistein.info/privkey.pem nginx/ssl/fistein.info.key
```

## 🛠️ Kurulum Adımları

### 1. Repository Clone
```bash
git clone <your-repo-url>
cd fistein
```

### 2. Environment Değişkenleri
Production environment dosyasını oluşturun:

```bash
cp .env.production .env.production.local
```

`.env.production.local` dosyasını düzenleyin:

```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://your-postgres-host:5432/fistein_db
DATABASE_USERNAME=fistein_user
DATABASE_PASSWORD=your-secure-database-password

# JWT Configuration  
JWT_SECRET=your-super-secure-256-bit-jwt-secret-key-here
JWT_EXPIRATION=86400

# Google OAuth Configuration
GOOGLE_CLIENT_ID=your-production-google-client-id

# Server Configuration
PORT=8080
SPRING_PROFILES_ACTIVE=production
```

### 3. Google OAuth Setup
1. [Google Cloud Console](https://console.cloud.google.com/) 'a gidin
2. Yeni proje oluşturun veya mevcut projeyi seçin
3. **APIs & Services** → **Credentials** bölümüne gidin
4. **OAuth 2.0 Client IDs** oluşturun:
   - **Authorized JavaScript origins**: 
     - `https://fistein.info`
     - `https://app.fistein.info`
   - **Authorized redirect URIs**:
     - `https://app.fistein.info/auth/google/callback`

### 4. Database Setup
Production PostgreSQL ayarları:

```bash
# PostgreSQL container çalıştırma
docker run -d \
  --name fistein-postgres \
  -e POSTGRES_DB=fistein_db \
  -e POSTGRES_USER=fistein_user \
  -e POSTGRES_PASSWORD=your-secure-password \
  -v postgres_data:/var/lib/postgresql/data \
  -p 5432:5432 \
  postgres:15-alpine
```

### 5. Deployment
```bash
# Setup çalıştırma
./scripts/setup.sh

# Build işlemi
./scripts/build.sh production

# Production deployment
./scripts/deploy.sh production
```

## 🌐 URL Yapısı

### Production URLs
- **Ana site**: https://fistein.info → https://app.fistein.info (redirect)
- **Web app**: https://app.fistein.info
- **API**: https://api.fistein.info/api
- **Health check**: https://api.fistein.info/actuator/health

### Development URLs
- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8080
- **API**: http://localhost:8080/api

## 🔧 Yönetim Komutları

### Container Yönetimi
```bash
# Servisleri başlatma
docker-compose -f docker-compose.production.yml up -d

# Servisleri durdurma
docker-compose -f docker-compose.production.yml down

# Logları görüntüleme
docker-compose -f docker-compose.production.yml logs -f

# Belirli servis logları
docker-compose -f docker-compose.production.yml logs -f backend
docker-compose -f docker-compose.production.yml logs -f frontend
```

### Database Yönetimi
```bash
# Database backup
docker exec fistein-postgres-prod pg_dump -U fistein_user fistein_db > backup.sql

# Database restore
docker exec -i fistein-postgres-prod psql -U fistein_user fistein_db < backup.sql

# Database connection
docker exec -it fistein-postgres-prod psql -U fistein_user -d fistein_db
```

## 🔒 Güvenlik

### Güvenlik Özellikleri
- ✅ HTTPS zorlaması
- ✅ Security headers (CSP, HSTS, XSS Protection)
- ✅ Rate limiting
- ✅ CORS yapılandırması
- ✅ Non-root containers
- ✅ Health checks

### Güvenlik Kontrolleri
```bash
# SSL sertifikası kontrolü
openssl s_client -connect fistein.info:443 -servername fistein.info

# Security headers kontrolü
curl -I https://app.fistein.info

# Health check
curl https://api.fistein.info/actuator/health
```

## 📊 Monitoring

### Health Checks
- **Frontend**: https://app.fistein.info/health
- **Backend**: https://api.fistein.info/actuator/health
- **Database**: Container health check

### Logs
```bash
# Tüm loglar
docker-compose -f docker-compose.production.yml logs -f

# Application logları
docker-compose -f docker-compose.production.yml logs -f backend

# Nginx access logları
docker exec fistein-nginx tail -f /var/log/nginx/access.log

# Nginx error logları
docker exec fistein-nginx tail -f /var/log/nginx/error.log
```

## 🔄 Updates

### Güncelleme Süreci
```bash
# Yeni code'u çek
git pull origin main

# Build
./scripts/build.sh production

# Deploy (zero-downtime değil, kısa kesinti olacak)
./scripts/deploy.sh production
```

### Rolling Update (Gelecekte)
Zero-downtime deployment için Kubernetes veya Docker Swarm kullanılabilir.

## 🆘 Troubleshooting

### Yaygın Sorunlar

#### 1. SSL Sertifika Hataları
```bash
# Sertifika yenileme
sudo certbot renew
sudo cp /etc/letsencrypt/live/fistein.info/* nginx/ssl/
docker-compose -f docker-compose.production.yml restart nginx
```

#### 2. Database Connection Issues
```bash
# Database durumu kontrol
docker-compose -f docker-compose.production.yml exec postgres pg_isready

# Connection test
docker-compose -f docker-compose.production.yml exec backend nc -zv postgres 5432
```

#### 3. Memory Issues
```bash
# Memory kullanımı
docker stats

# Container restart
docker-compose -f docker-compose.production.yml restart backend
```

## 📞 Support

- **Documentation**: `/docs` klasörü
- **Logs**: `./logs` klasörü
- **Issues**: GitHub Issues

---

## ✅ Production Checklist

- [ ] Domain DNS ayarları yapıldı
- [ ] SSL sertifikası oluşturuldu
- [ ] Environment variables ayarlandı
- [ ] Google OAuth yapılandırıldı
- [ ] Database ayarlandı
- [ ] Application deploy edildi
- [ ] Health checks geçiyor
- [ ] SSL/HTTPS çalışıyor
- [ ] API endpoints test edildi
- [ ] Frontend loading edildi
- [ ] User registration/login test edildi

🎉 **Başarılı deployment sonrası https://app.fistein.info adresinden uygulamaya erişebilirsiniz!**