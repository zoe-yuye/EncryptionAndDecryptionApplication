# Encryption and Decryption Application

This encryption and decryption application features a JavaFX frontend, utilizing diverse algorithms for secure data processing. The server-client architecture ensures smooth communication via sockets, while MySQL serves as the backend database for efficient data storage.

## Technologies Used

- **Backend Language:** Java
- **Frontend:** JavaFX, CSS
- **Database:** MySQL
- **Security:** Java Cryptography Extension (JCE), Bouncy Castle
  
## Key Features

- **Secure Communication:** Utilizing Java socket programming, MyApp ensures seamless and secure communication between the client and server sides. Extensive use of I/O streams and JDBC enhances the application's robustness.

- **Security Measures:** Explore various cipher methods, including traditional Caesar cipher, and modern DES and AES algorithms. The application incorporates external libraries such as Java's JCE and Bouncy Castle for encryption and decryption. Ongoing efforts to improve key encryption methods, potentially using RSA, demonstrate a commitment to heightened security.

- **JavaFX Frontend:** The frontend is built with JavaFX, enabling the creation of visually appealing and interactive UI components.
  
- **Cloud Deployment with AWS:** It is designed to leverage AWS services for cloud deployment. While initially exploring EC2 instances, the application seamlessly transitions to RDS for improved performance. Future plans include deeper exploration of AWS services and consideration of Linux or Ubuntu instances for server-side deployment.


## Getting Started

Follow these steps to get started:

### Prerequisites:

1. Java SDK 20 or higher
2. Maven (JavaFX SDK configured in pom.xml)
3. Import the 'cipher.sql' file into your database for testing
   
### Installation:

1. Clone or download the project.
2. Open the project using Eclipse, IntelliJ IDEA, or a similar IDE.
3. Configure JDBC Driver: Set up the JDBC driver (recommend using "mysql-connector-j-8.2.0") for database connection. Configure the build path by adding the JDBC DRIVER JAR to the library.
4. Run the Application:
   - Start the server-side by running `com.application.Server.ServerSide.java`.
   - Launch the client-side by running `com.application.Interface.UserInterface.java`.
   - Run both on the same or different devices.
   - If running on different devices, modify `serverAddress` (127.0.0.1) in `UserInterfaceController.java` and `LoginController.java` to the server's IP address.


