# Traffic Accident Alert Monitoring

This project is a traffic accident alert and monitoring system.

## Features

The main features of this project are:
- Real-time accident alerts
- Accident monitoring dashboard
- Admin panel for managing notification conditions

## Getting Started

### Prerequisites

- Android Studio
- Node.js
- npm or yarn
- Git

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd Traffic-Accident-Alert-Monitoring
    ```

2. **Setting firebase API key**
    Create 'secrets.properties' file in the root directory and add the following lines
    ```properties
    MAPS_API_KEY=<your-api-key>
    ```

    If you only want to modify the app code, that's enough.
    To modify the Firebase Functions code, follow these steps

3. **Install backend dependencies:**
    ```bash
    cd functions
    npm install
    ```

### Deploy the functions to firebase

1. **Login to firebase**
    ```bash
    firebase login
    ```

2. **Deploy to firebase**
    ```bash
    npm start
    firebase deploy --only functions
    ```

   Note: Please run 'npm install' before deployment, otherwise the version before it is modified will be uploaded.

# 교통사고 알림 및 분석 시스템

- 라이선스: [MIT LICENSE](LICENSE)
- 서드파티 라이선스: [라이선스 목록](dependency-license/licenses.md)