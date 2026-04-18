# 🔐 Telegram-Based Approval System for Secure DB Operations

A full-stack web application that ensures **secure database operations using Telegram-based multi-factor approval**.
Every sensitive action (like adding users) requires **real-time admin approval via Telegram** before execution.

---

## 🚀 Live Demo

* 🌐 Frontend (Vercel):
  https://telegram-based-approval-system-for.vercel.app

* ⚙️ Backend API (Render)

---

## 🧠 How It Works

1. User submits a request from the web UI
2. Backend sends a message to Telegram bot
3. Admin approves/rejects via Telegram
4. Backend processes the request accordingly
5. UI updates with real-time status

---

## 🏗️ Tech Stack

### Frontend

* React (Vite)
* JavaScript
* CSS

### Backend

* Spring Boot
* REST APIs
* Maven

### Database

* MySQL (Railway)

### Integrations

* Telegram Bot API
* Webhooks

### Deployment

* Frontend → Vercel
* Backend → Render

---

## 📂 Project Structure

```
Telegram-Based-Approval-System-for-Secure-DB-Operations/
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   └── config/
│
├── frontend/
│   ├── src/
│   ├── components/
│   ├── services/
│   └── vite.config.js
│
└── README.md
```

---

## ⚙️ Setup Instructions

### 🔧 Backend Setup

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

---

### 🌐 Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

---

## 🔐 Environment Variables

### Backend (.env / Render)

```
SPRING_DATASOURCE_URL=your_database_url
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

TELEGRAM_BOT_TOKEN=your_bot_token
TELEGRAM_CHAT_ID=your_chat_id
```

---

### Frontend (.env)

```
VITE_API_URL=https://telegram-based-approval-system-for.onrender.com/api
```

---

## 🔗 API Endpoints

| Method | Endpoint      | Description             |
| ------ | ------------- | ----------------------- |
| GET    | /api/users    | Get all users           |
| POST   | /api/users    | Submit add user request |
| GET    | /api/requests | Get pending requests    |

---

## 🤖 Telegram Bot Setup

1. Create bot using BotFather
2. Get Bot Token
3. Set webhook:

```
https://api.telegram.org/bot<YOUR_TOKEN>/setWebhook?url=<YOUR_BACKEND_URL>/api/telegram/webhook
```

---

## 📸 Features

* 🔐 Secure approval workflow
* 📲 Telegram integration
* ⚡ Real-time request handling
* 📊 Live audit logs
* 🌐 Full-stack deployment

---

## ⚠️ Known Issues

* Free hosting (Render) may sleep → first request delay
* CORS must be enabled in backend

---

## 💡 Future Improvements

* Authentication & role-based access
* Email notifications
* Better UI/UX
* Request history dashboard

---

## 👨‍💻 Author

**Dinagaran N**

---

## ⭐ Support

If you like this project:

* ⭐ Star the repo
* 🍴 Fork it
* 🧠 Improve it

---

## 📜 License

This project is open-source and available under the MIT License.
