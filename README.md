# Facil de correr!

### DOCKER - IMAGENES INDEPENDIENTES
1. docker build -t devsu-api .
2. docker run -p 5001:5001  -e SPRING_PROFILES_ACTIVE=sqlserver   -e SPRING_DATASOURCE_URL="{ENTORNO}"   -e SPRING_DATASOURCE_USERNAME={ENTORNO}  -e SPRING_DATASOURCE_PASSWORD={ENTORNO}  devsu-api

3. docker build -t devsufront .
4. docker run -p 5003:80 -e API_BASE_URL=http://SERVER:PUERTO devsufront

### BASE DE DATOS CREADA EN AZURE (Nada de container similando un nodo real)
Credenciales en comentarios de la entrega

### Evidencias

<img width="1916" height="1003" alt="image" src="https://github.com/user-attachments/assets/f1c2d802-ad73-4d27-b321-40863ce001ef" />
<img width="1914" height="991" alt="image" src="https://github.com/user-attachments/assets/882f7ad1-51c9-4d45-89f1-e2167c5247dc" />
<img width="1919" height="992" alt="image" src="https://github.com/user-attachments/assets/241f7219-72fe-4b77-92f7-0b4bb8eee26d" />
<img width="1916" height="994" alt="image" src="https://github.com/user-attachments/assets/2824f168-e64c-475b-8265-95b56a7afd4d" />
<img width="1915" height="1002" alt="image" src="https://github.com/user-attachments/assets/057c0c2c-aa5f-413a-9fcf-65c51c4fd8b9" />

