# FileSharingApp

A modern Spring Boot application for secure file uploading, sharing, and expiring links with OAuth2 (Google & GitHub) authentication.

---

## 🚀 Features

- User authentication with Google and GitHub (Spring Security OAuth2)
- Upload files of any type, with expiry option (default 24 hours)
- Personal, expiring download/share links
- View and download your uploaded files
- Alerts for file upload status, expired links, logout, etc.
- Responsive UI with Bootstrap 4
- In-memory H2 database for demo/testing

---

## 🏗️ Tech Stack

- **Backend:** Java, Spring Boot, Spring Security (OAuth2), Thymeleaf, H2
- **Frontend:** Bootstrap 4, Thymeleaf
- **Authentication:** Google OAuth, GitHub OAuth

---

## 💻 Getting Started

### Prerequisites

- Java 17+ (recommended)
- Maven

### Running Locally

1. **Clone this repo:**


git clone https://github.com/SharavanMudaliar/FileSharingApp.git
cd FileSharingApp


2. **Create `.env` for your secrets (do NOT commit!):**
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret

3. **Run the app:**
- If using IntelliJ, add these variables in your Run Configuration.
- Or use [dotenv-java](https://github.com/cdimascio/dotenv-java) to load `.env` automatically.

./mvnw spring-boot:run

Then visit: [http://localhost:8080](http://localhost:8080)

4. **Set up Google & GitHub OAuth Apps:**
- Ensure Redirect URIs in provider console match:
  ```
  http://localhost:8080/login/oauth2/code/google
  http://localhost:8080/login/oauth2/code/github
  ```

---

## 📝 Contributors

- [Sharavan Mudaliar](https://github.com/SharavanMudaliar)
- [Praveen Nair](#)
- [Nayan Patil](#)

---

## ⚠️ Security

- **Never commit your `.env` or secret credentials!**
- Add `.env` to `.gitignore`
- For cloud deployment (Heroku, Render): use environment variables/config vars

---

## 🏢 License

[MIT](LICENSE) – Educational/demo use

---

## 🙏 Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Bootstrap](https://getbootstrap.com/)
- [Thymeleaf](https://www.thymeleaf.org/)
