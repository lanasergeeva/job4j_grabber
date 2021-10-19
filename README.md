# job4j_grabber
[![Build Status](https://www.travis-ci.com/lanasergeeva/job4j_grabber.svg?branch=master)](https://www.travis-ci.com/lanasergeeva/job4j_grabber)
[![codecov](https://codecov.io/gh/lanasergeeva/job4j_grabber/branch/master/graph/badge.svg?token=MFCTSE2E69)](https://codecov.io/gh/lanasergeeva/job4j_grabber)

# Проект Агрегатор Java Вакансий

## Описание.

Система запускается по расписанию. Период запуска указывается в настройках - app.properties.

Основной сайт - sql.ru. В нем есть раздел job. 
Программа должна считывать все вакансии относящиеся к Java и записывать их в базу данных.
Доступ к интерфейсу будет через REST API.