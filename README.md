# Сервис-ориентированная архитектура

## Лабораторная работа № 2

### Вызываемый сервис, написанный на JAX-RS

Wildfly располагаем в корневую директорию проекта

Для сборки: `mvn clean package`<br>
Эта команда упаковывает проект в `war` и копирует `war` в `wildfly`

И запускаем wildfly:<br>
`./wildfly/bin/standalone.sh -c standalone-my.xml`

Пример конфигурации `standalone/configuration/standalone-my.xml`

Генерация ключа:
```
keytool -genkeypair -alias wildfly -keyalg RSA -keysize 4096 \
  -validity 3650 -keystore keystore.p12 \
  -storetype PKCS12 -storepass changeit -keypass changeit \
  -dname "CN=localhost, OU=Development, O=Company, L=City, ST=State, C=RU"
```

Экспорт сертификата:
```
keytool -exportcert -alias wildfly -keystore wildfly.p12 -storetype PKCS12 -storepass changeit -file wildfly.crt
```

Добавление сертификата в trustore:
```
keytool -importcert -alias wildfly -file wildfly.crt -keystore wildfly-truststore.p12 -storetype PKCS12 -storepass changeit -noprompt
```
