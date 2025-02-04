# Sample CAP Java Application Calling DAR Inference

## Prerequisites
- **npm**
- **maven**

Ensure your system has the following packages installed:

- **UI5 CLI**
- **SAP UX Tooling**

If these packages are not available, install them using:

```sh
npm install -g @ui5/cli 
npm install -g @sap/ux-ui5-tooling
```

## Clone the Repository

Clone this repository to your local machine:

```sh
git clone https://github.tools.sap/SAP-IntelligentProdRecmdn/Dar-Service.git
cd Dar-Service
```

## Running the Application

### Backend:

1. Navigate to the backend service directory:
   ```sh
   cd dar-service
   ```
2. Run the backend service using Maven:
   ```sh
   mvn clean install
   ```
   ```sh
   mvn clean spring-boot:run
   ```

### Frontend:

1. Navigate to the UI directory:
   ```sh
   cd dar-service-ui
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Start the frontend application:
   ```sh
   npm start
   ```

The application should now be running successfully.
