# Бэкенд для Divkit Marketplace

## Описание проекта
Бэкенд для divkit marketplace. [DivKit](https://divkit.tech/en/) - это опен-сорс serven-driven UI Framework. DivKit Marketplace - это маркетплейс компонентов для divkit. На маркетплейс можно загрузить template в формате json либо большой архив файлов дизайн-макета. Также можно скачать template в формате json. Поддерживается поиск по тегам и названиям. :tada:

## Prerequesties
 - Docker
 - Python ^3.10
 - poetry `pip install poetry`

## Установка
 - Склонируйте проект
 - Зайдите в папку divkit-marketplace-backend
 - `poetry install` устанавливает зависимости и создает виртуальное окружение
 - `poetry shell` активация виртуального окружения

## Запуск
 - `make env` создает .env файл и заполняет его дефолтными данными из файла [.env.sample](https://git.yandex-academy.ru/school/2022-09/projects/team1/divkit-marketplace-backend/-/blob/master/.env.sample)
 - `make up` билдит докер образ с бэкендом для приложения и запускает докер контейнер с этим образом и контейнер с базой данных постгрес <details><summary>команда в [Makefile](https://git.yandex-academy.ru/school/2022-09/projects/team1/divkit-marketplace-backend/-/blob/master/Makefile)</summary>
```docker-compose -f docker-compose.yml up -d --remove-orphans```
</details>

## Работа с базой данных
 - `make migrate args=head` накатывает миграции <details><summary>команда в [Makefile](https://git.yandex-academy.ru/school/2022-09/projects/team1/divkit-marketplace-backend/-/blob/master/Makefile)</summary>
```docker-compose exec app alembic upgrade args=head```</details>
 - `make revision` автоматически генерирует миграции для алембика <details><summary>команда в [Makefile](https://git.yandex-academy.ru/school/2022-09/projects/team1/divkit-marketplace-backend/-/blob/master/Makefile)</summary>
```docker-compose exec app alembic revision --autogenerate```</details>
 - `make open_db` открывает базу данных в командной строке <details><summary>команда в [Makefile](https://git.yandex-academy.ru/school/2022-09/projects/team1/divkit-marketplace-backend/-/blob/master/Makefile)</summary>
```docker exec -it divkit_db psql -d $(POSTGRES_DB) -U $(POSTGRES_USERNAME)```</details>

## Тестирование
 - `make test` <details><summary>команда в [Makefile](https://git.yandex-academy.ru/school/2022-09/projects/team1/divkit-marketplace-backend/-/blob/master/Makefile)</summary>
```poetry run python -m pytest --verbosity=2 --showlocals --log-level=DEBUG```
</details>

## Форматирование кода
 - `make format` <details><summary>команда в [Makefile](https://git.yandex-academy.ru/school/2022-09/projects/team1/divkit-marketplace-backend/-/blob/master/Makefile)</summary>
```poetry run python3 -m isort app tests; poetry run python3 -m black app tests```</details>

## Проверка кода
 - `make lint` <details><summary>команда в [Makefile](https://git.yandex-academy.ru/school/2022-09/projects/team1/divkit-marketplace-backend/-/blob/master/Makefile)</summary>
```poetry run python3 -m pylint app tests; poetry run python3 -m mypy app tests```</details>
