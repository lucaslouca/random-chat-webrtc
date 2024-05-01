## WebRTC

## Development Environment Setup

### Start MySQL Server

```bash
docker-compose down -v --remove-orphans && docker system prune --volumes --force
rm -rf sito # Erase old MySQL db
docker-compose up --detach
```

in case a local mysql service is running and already using the port, you can stop it using:

```bash
# Get instance name:
ls /Library/LaunchDaemons | grep mysql

# Stop MySQL instance (Works on MacOS Catalina, MySQL 8):
sudo launchctl unload /Library/LaunchDaemons/com.oracle.oss.mysql.mysqld.plist
```

### Grant MySQL user priviledges

In docker container run:

```bash
docker exec -it chat_users_database bash
mysql -h localhost -P 3306 -uroot -ppassword
mysql> GRANT ALL PRIVILEGES ON *.* TO 'user'@'%' WITH GRANT OPTION;
mysql> ALTER USER 'user'@'%' IDENTIFIED WITH mysql_native_password BY 'password';

```

Test outside container using:

```bash
mysql -h 127.0.0.1 -P 3306 -uuser -ppassword
```

### Create Database Schema

```bash
mysql -h 127.0.0.1 -P 3306 -uuser -ppassword
mysql> source create_mysql_schema.sql
```

