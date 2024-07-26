This project is a complete solution for a module in the master's degree program in computer science at the Bochum university of applied science.  
The project was created and solved according to a predefined scheme. There was little scope for naming methods or libraries.

Requirements:
- Java JDK 22+ with JavaFX like Liberica JDK
- Tomcat 10.0.13
- IntelliJ (recommended) or Eclipse
- MariaDB 10+
- [EMU USB Datalogger](https://www.emuag.ch/produkte/steckdosen-messgeraet/)

For MariaDB a Docker compose file is in this repository for a local development. Just use `docker compose up -d` and access the PhpMyAdmin page on [localhost:8080](https://localhost:8080). Import the `db.sql` for the two tables.

In IntelliJ create a server configuration with your Tomcat 10.0.13 installation. Set as HTTP Port `8081`. In the Deployment tab add the `service_emu and service_db war exploded`. Set the application context for service_emu to /emu and for service_db to /db.

In Eclipse add the Tomcat 10.0.13 server. Update the HTTP/1.1 Port to `8081`. Under the Modules tab in the bottom left add the Web Module `service_emu` with the path `/emu` and the `service_db` with the path `/db`. Save the configuration with CTRL + S and start the server.