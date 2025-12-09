# Food API
## <br> Описание проекта:
   Приложение разработанно с использованием микросервисной архитектуры. <br> Для запуска приложения необходимо установить сервис:
      https://github.com/rkadl77/FoodService
## <br> Документация к API доступна по локальным ссылкам: 
#### user-service: 
      http://localhost:8910/swagger-ui/index.html#/
#### menu-service: 
      http://localhost:8080/swagger-ui/index.html#/
#### order-service: 
      http://localhost:8096/swagger-ui/index.html#/
#### cart-service(сторонний сервис, который необходимо установить перед запуском контейнеров): 
      http://localhost:5621/swagger/index.html

## <br> Клиентская часть:
#### Необходимо установить клиентскую часть на локальную машину, перед запуском docker compose файла, и затем создать для них образы
1) #### ссылка на веб-приложение для обычных пользователей: https://github.com/egorycheva28/delivery-website
2) #### ссылка на веб-приложение для сотрудников: https://github.com/Viroos973/delivery-website-operator
3) #### зайти в корень проекта (для каждого по отдельности) и выполняем следующие комнады:
         sudo docker build -t front-client:latest .
         sudo docker build -t front-operator:latest .
## <br> Клиенты будут доступны после запуска проекта по локальным ссылкам:
      http://localhost:4175
      http://localhost:4173

## <br> Запуск проекта из контейнеров:
1) #### Зайти в проект FoodService (ссылку на установку см. выше) и выполнить команду:
            sudo docker build -t cart-image:latest .
   
1) #### Зайти в корень проекта hits-food-auth-service(!!!ЗАПУСКАТЬ ИЗ КОРНЯ)
            sudo docker build -f user-service/Dockerfile -t user-image:latest .
            sudo docker build -f menu/Dockerfile -t menu-image:latest .
            sudo docker build -f orderservice/Dockerfile -t order-image:latest .

2) #### После билда в корне этого же проекта проекта запустить команду:
            sudo docker compose up
