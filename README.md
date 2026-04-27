# Facil de correr!

### DOCKER - IMAGENES INDEPENDIENTES
1. docker build -t devsu-api .
2. docker run -p 5001:5001  -e SPRING_PROFILES_ACTIVE=sqlserver   -e SPRING_DATASOURCE_URL="{ENTORNO}"   -e SPRING_DATASOURCE_USERNAME={ENTORNO}  -e SPRING_DATASOURCE_PASSWORD={ENTORNO}  devsu-api

3. docker build -t devsufront .
4. docker run -p 5003:80 -e API_BASE_URL=http://SERVER:PUERTO devsufront

### BASE DE DATOS CREADA EN AZURE (Nada de container similando un nodo real)
Credenciales en comentarios de la entrega

