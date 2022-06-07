
[![Build Status](https://www.travis-ci.com/lanasergeeva/job4j_grabber.svg?branch=master)](https://www.travis-ci.com/lanasergeeva/job4j_grabber)
[![codecov](https://codecov.io/gh/lanasergeeva/job4j_grabber/branch/master/graph/badge.svg?token=MFCTSE2E69)](https://codecov.io/gh/lanasergeeva/job4j_grabber)

# Проект "Агрегатор Java Вакансий"

+ [Описание](#Описание)
+ [Технологии](#Используемые-технологии)
+ [Вид](#Общий вид приложения)
+ 
## Описание.

В данном приложении происходи парсинг Java вакансий с сайта https://career.habr.com/vacancies.
Парсинг запускается по расписанию раз в минуту, обрабатываем первые 5 страниц.
С помощью HTTP-сервера просиходит загрузка HTML-страницы.

## Используемые технологии

+ **Maven**
+ **Для парсинга страниц используется JSOUP**
+ **H2 исопльзуется для тестирования**, **JDBC**, **Liquibase**, **PostgresSQL**
+ **Java 17**, **SLF4J**
+ 
## Общий вид приложения

![alt text](https://github.com/lanasergeeva/job4j_grabber/blob/master/src/main/java/ru/job4j/habr/images/view.png)

