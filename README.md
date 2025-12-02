# Food API
## <br> Документация к API доступна по локальным ссылкам: 
#### user-service: 
   http://localhost:8910/swagger-ui/index.html#/
#### menu-service: 
   http://localhost:8080/swagger-ui/index.html#/
#### order-service: 
   http://localhost:8096/swagger-ui/index.html#/
#### cart-service: 
      http://localhost:5621/swagger/index.html

#### <br> Ссылка на проект с cart-service: 
   https://github.com/rkadl77/FoodService


## <br> Запуск проекта из контейнеров:
1) Зайти в проект FoodService
2) sudo docker build -t cart-image:latest .
   
1) зайти в корень проекта hits-food-auth-service
2) sudo docker build -f user-service/Dockerfile -t user-image:latest .
3) sudo docker build -f menu/Dockerfile -t menu-image:latest .
4) sudo docker build -f orderservice/Dockerfile -t order-image:latest .

<br><br>После билда в корне этого же проекта проекта запустить команду
sudo docker compose up
