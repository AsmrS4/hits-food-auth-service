Документация к API доступна по локальным ссылкам: 
# user-service: http://localhost:8910/swagger-ui/index.html#/
# menu-service: http://localhost:8080/swagger-ui/index.html#/
# order-service: http://localhost:8096/swagger-ui/index.html#/

Для запуска всего приложения необходимо запускать сервисы в следующем порядке:
1) discovery-service
2) config-service
Далее остальные сервисы, кроме common-module(используется в качестве пакета с общими зависимостями и настройками безопасности)

Для использования единной точки входа(выполнения запросов к сервисам через один хост и порт) запустить gateway(порт по умолчанию 8000)
