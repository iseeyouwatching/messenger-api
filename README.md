# Система для мессенджера, выполненная в микросервисной архитектуре

### Как запустить?
Необходимо клонировать репозиторий и запустить проект в среде разработки **IntelliJ IDEA**.

### Запуск RabbitMQ
Установите **RabbitMQ** с помощью **Docker**. Выполните следующую команду в терминале:
```shell
docker run -it --restart always --name rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=rmq-user -e RABBITMQ_DEFAULT_PASS=rmq-pass rabbitmq:3
```

### Запуск сервисов
Также потребуется перейти в модули user-app, notifications-app, friends-app, file-storage-app и chat-app, использовав команду `cd`, и поднять **Docker Compose** с помощью `docker-compose up -d`.

После чего можно запускать все сервисы, используя интерфейс среды разработки.

### Ссылки на Swagger
- Сервис аутентификации и профилей: [**ссылка**](http://localhost:8191/swagger-ui/index.html#)
- Сервис чатов: [**ссылка**](http://localhost:8193/swagger-ui/index.html#)
- Сервис друзей: [**ссылка**](http://localhost:8192/swagger-ui/index.html#)
- Сервис уведомлений: [**ссылка**](http://localhost:8194/swagger-ui/index.html#)
- Сервис файлового хранилища: [**ссылка**](http://localhost:8195/swagger-ui/index.html#)


### Примечание
Перед запуском тестов убедитесь, что все контейнеры с базами данных и MinIO запущены.