# Система для мессенджера, выполненная в микросервисной архитектуре

### Как запустить?
Необходимо клонировать репозиторий, запустить проект в среде разработки **IntelliJ IDEA**.

Необходимо установить **RabbitMQ** через докер:

`docker run -it --restart always --name rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=rmq-user -e RABBITMQ_DEFAULT_PASS=rmq-pass rabbitmq:3`

Также потребуется перейти в модули user-app, notifications-app, friends-app, file-storage-app и chat-app и поднять **Docker Compose** с помощью `docker-compose up -d`.

После чего можно запускать все сервисы, используя интерфейс среды разработки.