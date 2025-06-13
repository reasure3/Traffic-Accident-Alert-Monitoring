# Traffic Accident Alert Monitoring

This project is a traffic accident alert and monitoring system.

## Features

The main features of this project are:
- Real-time accident alerts
- Accident monitoring dashboard
- Admin panel for managing notification conditions

## Getting Started

### Prerequisites

- Node.js
- npm or yarn
- Git

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd Traffic-Accident-Alert-Monitoring
    ```

2.  **Install backend dependencies:**
    ```bash
    cd backend
    npm install
    ```

3.  **Install frontend dependencies:**
    ```bash
    cd ../frontend
    npm install
    ```

4.  **Set up environment variables:**
    Create a `.env` file in the `backend` directory and add the necessary environment variables, such as your Firebase credentials.

    ```
    # .env example
    FIREBASE_API_KEY=your_api_key
    FIREBASE_AUTH_DOMAIN=your_auth_domain
    # ... and so on
    ```

### Running the application

1.  **Start the backend server:**
    ```bash
    cd backend
    npm start
    ```

2.  **Start the frontend development server:**
    ```bash
    cd ../frontend
    npm start
    ```

# 교통사고 알림 및 분석 시스템

- 라이선스: [MIT LICENSE](LICENSE)
- 서드파티 라이선스: [라이선스 목록](dependency-license/licenses.md)