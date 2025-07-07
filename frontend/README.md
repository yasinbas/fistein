# Fiştein Frontend

Bu, Fiştein gider takip uygulamasının React frontend'idir. Splitwise benzeri bir arayüz sunar ve arkadaş grupları ile ev arkadaşları arasında ortak giderleri takip etmeyi sağlar.

## 🚀 Teknolojiler

- **React 19** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Styling
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **Lucide React** - Icons

## 📋 Gereksinimler

- Node.js 18+
- npm veya yarn
- Çalışan Fiştein backend (Spring Boot)

## 🔧 Kurulum

1. Bağımlılıkları yükleyin:
```bash
npm install
```

2. Environment dosyasını kontrol edin (`.env`):
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

## 🏃‍♂️ Geliştirme

Geliştirme sunucusunu başlatın:
```bash
npm run dev
```

Uygulama http://localhost:3000 adresinde açılacaktır.

## 📦 Build

Production için build almak:
```bash
npm run build
```

Build dosyalarını önizlemek:
```bash
npm run preview
```

## 🧹 Linting

Kod kalitesini kontrol etmek:
```bash
npm run lint
```

## 📁 Proje Yapısı

```
src/
├── components/          # Yeniden kullanılabilir bileşenler
│   ├── Layout.tsx      # Ana layout bileşeni
│   └── ProtectedRoute.tsx # Korumalı rota bileşeni
├── context/            # React Context'leri
│   └── AuthContext.tsx # Kimlik doğrulama context'i
├── pages/              # Sayfa bileşenleri
│   ├── Login.tsx       # Giriş sayfası
│   ├── Register.tsx    # Kayıt sayfası
│   └── Dashboard.tsx   # Ana dashboard
├── services/           # API servisleri
│   └── api.ts         # Axios yapılandırması ve API çağrıları
├── types/              # TypeScript tip tanımları
│   └── index.ts       # Tüm uygulama tipleri
├── utils/              # Yardımcı fonksiyonlar
│   └── index.ts       # Genel utility fonksiyonları
└── App.tsx            # Ana uygulama bileşeni
```

## 🔐 Kimlik Doğrulama

Uygulama JWT tabanlı kimlik doğrulama kullanır:
- Token localStorage'da saklanır
- API isteklerinde otomatik olarak header'a eklenir
- Token süresi dolduğunda otomatik logout
- Korumalı rotalar için ProtectedRoute wrapper'ı

## 🎨 Styling

Tailwind CSS ile özelleştirilmiş:
- Özel renk paleti (primary blue tones)
- Responsive design
- Component sınıfları (.btn-primary, .card, vb.)
- Balance durumu için renk kodlaması

## 🌍 Türkçe Arayüz

Tüm metin içeriği Türkçe olarak hazırlanmıştır:
- Form etiketleri ve mesajları
- Navigasyon menüleri
- Hata mesajları
- Para birimi formatı (TRY)

## 🔗 Backend Entegrasyonu

Spring Boot backend ile entegrasyon:
- RESTful API endpoints
- JWT authentication
- CORS yapılandırması
- Error handling

## 📱 Responsive Design

Mobil-first yaklaşım:
- Tüm ekran boyutlarında çalışır
- Touch-friendly interface
- Mobile navigation menu

## 🚧 Gelecek Özellikler

Şu anda placeholder olarak bulunan sayfalar:
- [ ] Groups management
- [ ] Expense creation and editing
- [ ] Group details and member management
- [ ] Expense settlement
- [ ] Advanced balance calculations
