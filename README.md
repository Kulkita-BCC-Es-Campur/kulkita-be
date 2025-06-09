# Kulkita Backend API

**Kulkita: Segar Terkelola, Gizi Tersalur**

Platform digital berbasis SaaS untuk optimalisasi manajemen bahan pangan segar pada Program Makan Bergizi Gratis (MBG) di SPPG Jawa Barat. Sistem ini menggunakan teknologi AI dan Machine Learning untuk mengurangi food loss dan meningkatkan efisiensi operasional.

## 📋 Daftar Isi

- [Tentang Project](#tentang-project)
- [Fitur Utama](#fitur-utama)
- [Teknologi](#teknologi)
- [Prerequisites](#prerequisites)
- [Instalasi](#instalasi)
- [Konfigurasi](#konfigurasi)
- [Menjalankan Aplikasi](#menjalankan-aplikasi)
- [Docker Deployment](#docker-deployment)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Monitoring](#monitoring)
- [Contributing](#contributing)

## 🎯 Tentang Project

Kulkita adalah solusi inovatif untuk mengatasi tantangan food loss dan food waste dalam Program Makan Bergizi Gratis di Indonesia. Sistem ini membantu SPPG (Satuan Pendidikan Penyelenggara Gizi) mengelola stok bahan pangan segar dengan lebih efisien.

### Problem Statement
- Indonesia menghadapi food loss 23-48 juta ton/tahun
- SPPG kesulitan mengelola bahan pangan segar dengan masa simpan terbatas
- Ketidaksesuaian pasokan dan permintaan menyebabkan pemborosan
- Koordinasi manual antar stakeholder memerlukan sistem terintegrasi

### Solution
Platform digital yang menyediakan:
- Real-time inventory management
- FIFO optimization
- Predictive spoilage alerts
- Intuitive dashboard
- User management & authentication

## 🚀 Fitur Utama

### Epic 1: Real-Time Inventory Management
- ✅ Input batch bahan segar (manual & otomatis dari timbangan digital)
- ✅ Prediksi tanggal kadaluarsa menggunakan ML
- ✅ Penggunaan bahan berdasarkan menu atau manual
- ✅ Notifikasi bahan mendekati expired
- ✅ Label digital otomatis untuk batch

### Epic 2: Intuitive Dashboard
- ✅ Ringkasan status stok berdasarkan kesegaran
- ✅ Daftar bahan mendekati kadaluarsa (Expiry Alert Table)
- ✅ Tren penggunaan harian
- ✅ Visualisasi waste loss per kategori
- ✅ Laporan nilai kerugian ekonomi

### Epic 3: FIFO Optimization
- ✅ Pengurutan batch otomatis berdasarkan prinsip FIFO
- ✅ Rekomendasi picking batch
- ✅ Storage layout recommendation

### Epic 4: Predictive Spoilage Alert
- ✅ Estimasi masa simpan dan status spoilage otomatis
- ✅ Notifikasi push dan dashboard alerts
- ✅ Manual update status kondisi bahan

### Epic 5: User Management
- ✅ Registrasi & login dengan email/password
- ✅ Update profil mandiri
- ✅ Role-based access control (Admin, Manajer, Operator)
- ✅ Password management

### Epic 6: Authentication & Authorization
- ✅ Multi-unit support
- ✅ Unit creation & joining via code/invitation
- ✅ User management per unit

## 🛠 Teknologi

### Backend Stack
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: PostgreSQL / MySQL
- **ORM**: Spring Data JPA
- **Security**: Spring Security + JWT
- **Documentation**: OpenAPI 3 (Swagger)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

### Cloud Services
- **Storage**: Azure Blob Storage / Firebase Storage
- **ML Platform**: Azure Machine Learning Studio
- **IoT**: Azure IoT Hub (untuk timbangan digital)
- **Analytics**: Azure Stream Analytics
- **Database**: Azure Cosmos DB (NoSQL)
- **Monitoring**: Azure Application Insights

### External Integrations
- **Email Service**: SMTP
- **Push Notifications**: Firebase Cloud Messaging
- **File Processing**: Apache POI (Excel), OpenCSV

## 📋 Prerequisites

- Java 17 atau lebih tinggi
- Maven 3.6+
- PostgreSQL 13+ (atau MySQL 8+)
- Node.js 16+ (untuk frontend development)
- Docker & Docker Compose (opsional)
- Firebase Account (untuk push notifications)
- Azure Account (untuk cloud services)

## 🔧 Instalasi

### 1. Clone Repository
```bash
git clone https://github.com/your-org/kulkita-backend.git
cd kulkita-backend
```

### 2. Setup Database
```sql
-- PostgreSQL
CREATE DATABASE kulkita_db;
CREATE USER kulkita_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE kulkita_db TO kulkita_user;
```

### 3. Install Dependencies
```bash
mvn clean install
```

### 4. Setup Firebase (Opsional)
- Download `firebase-service-account.json` dari Firebase Console
- Letakkan file di root directory project

## ⚙️ Konfigurasi

### Application Properties

Buat file `application-dev.properties` untuk development:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/kulkita_db
spring.datasource.username=kulkita_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
app.jwtSecret=kulkitaSecretKey2025VerySecureAndLong
app.jwtExpirationInMs=86400000

# Email Configuration
spring.mail.host=smtp.goole.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=your_smtp_api_key

# Firebase Configuration
firebase.config.file=firebase-service-account.json

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.example.kulkita=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Environment Variables
```bash
# Database Configuration
DB_URL=
DB_USERNAME=
DB_PASSWORD=

# JWT Secret
JWT_SECRET=

# Email Configuration
MAIL_USERNAME=
MAIL_PASSWORD=

# Google OAuth2
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

# Azure ML
AZURE_ML_ENDPOINT=
AZURE_ML_API_KEY=

# CORS Origins (update for production)
CORS_ALLOWED_ORIGINS=
```
## 🤝 Contributing

### Development Workflow
1. Fork repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push branch: `git push origin feature/amazing-feature`
5. Submit Pull Request

### Code Style
- Gunakan Google Java Style Guide
- Jalankan `mvn spotless:apply` sebelum commit
- Minimum 80% test coverage untuk kode baru

### Commit Message Convention
```
type(scope): description

feat(auth): add JWT token refresh endpoint
fix(batch): resolve FIFO sorting issue
docs(api): update endpoint documentation
test(service): add unit tests for BatchService
```

### Branch Naming
- `feature/feature-name` - untuk fitur baru
- `bugfix/bug-description` - untuk bug fixes
- `hotfix/critical-fix` - untuk critical fixes
- `docs/documentation-update` - untuk dokumentasi

## 📄 License

Project ini menggunakan MIT License. Lihat file `LICENSE` untuk detail lengkap.

## 👥 Tim Pengembang

- **Project Manager**: Richard - Universitas Brawijaya
- **Backend Developer**: Kadek Nandana Tyo Nayotama - Universitas Brawijaya
- **Frontend Developer**: Jason Surya Wijaya - Universitas Brawijaya
- **Spoilage Prediction ML Engineer**: Fatoni Murfid - Universitas Brawijaya
- **Demand Forecasting ML Engineer**: Rafly Ash Shiddiqi - Universitas Brawijaya

## 📞 Support

Untuk pertanyaan atau issues:
- Email: support@kulkita.com
- GitHub Issues: [Repository Issues](https://github.com/your-org/kulkita-backend/issues)
- Documentation: [Wiki](https://github.com/your-org/kulkita-backend/wiki)

## 🔄 Changelog

### Version 1.0.0 (Target: 4 Juni 2025)
- ✅ Real-time inventory management
- ✅ FIFO optimization
- ✅ Intuitive dashboard
- ✅ Predictive spoilage alerts
- ✅ User management & authentication
- ✅ Weight scale integration

---

**Kulkita - Segar Terkelola, Gizi Tersalur** 🌱

Mendukung program Makan Bergizi Gratis Indonesia untuk mewujudkan Indonesia Emas 2045.
