# Сервис-ориентированная архитектура

## Лабораторная работа № 2

### Вызывающий сервис, написанный на JAX-RS

Wildfly располагаем в корневую директорию проекта

`/flat-ejb` - логика сервиса, которая вызывается через remote интерфейс

`/flat-web` - основное приложение

Для сборки: `mvn clean package`<br>
Эта команда упаковывает `/flat-web` в `war` и `/flat-ejb` в `jar`, копирует `war` и `jar` в `wildfly/standalone/deployments`

Запуск wildfly:<br>
`./wildfly/bin/standalone.sh -c standalone-my.xml`

Пример конфигурации `standalone/configuration/standalone-my.xml`

Генерация ключа:
```
keytool -genkeypair -alias wildfly -keyalg RSA -keysize 4096 \
  -validity 3650 -keystore keystore.p12 \
  -storetype PKCS12 -storepass changeit -keypass changeit \
  -dname "CN=localhost, OU=Development, O=Company, L=City, ST=State, C=RU"
```

Параметры пула EJB:
- `strict-max-pool`: Сервер управляет максимумом экземпляров
- `derive-size="from-worker-pools"`: Размер пула рассчитывается автоматически на основе `worker-pool`
- `instance-acquisition-timeout`: Клиент ждёт свободный экземпляр
- `MINUTES`: Не падаем сразу при пике

```
<subsystem xmlns="urn:jboss:domain:ejb3:10.0">
<session-bean>
    <stateless>
        <bean-instance-pool-ref pool-name="slsb-strict-max-pool"/>
    </stateless>
    <singleton default-access-timeout="5000"/>
</session-bean>
<pools>
    <bean-instance-pools>
        <strict-max-pool
            name="slsb-strict-max-pool"
            derive-size="from-worker-pools"
            instance-acquisition-timeout="5"
            instance-acquisition-timeout-unit="MINUTES"/>
    </bean-instance-pools>
</pools>
```

### Ссылки на репозитории лабораторной

1. Ссылка на основной вызываемый сервис реализованный на Spring Boot - https://github.com/stoneshik/third-lab-soa
2. Ссылка на второй вызывающий сервис реализованный на JAX-RS - https://github.com/stoneshik/third-lab-soa-second
3. Ссылка на фронтенд - https://github.com/stoneshik/third-lab-soa-frontend
