# SEARCH ENGINE PROJECT
---
## Краткое описание 
Spring Boot проект представляет собой систему индексации сайтов. Он разработан для автоматического сбора информации с веб-страниц и создания поискового индекса, который облегчает поиск и поисковые запросы на этих сайтах. Проект осуществляет обход сайтов, скачивание и анализ HTML-контента, извлечение текстовых данных и создание структурированного индекса, позволяющего быстрый и эффективный доступ к информации на этих сайтах.

***
### Руководство по эксплуатации проекта

Данный проект запускается с помощью локального веб-сервера (URL-адрес: http://localhost:8080/) и состоит из трех модулей.
#### 1. Модуль Management
1.1 _Кнопка(Start Indexing)_ - ручной запуск индексации сайтов.
![StartIndexing](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/72801098-e487-4087-8f19-06ad2c2db413)
1.2 _Кнопка(Stop Indexing)_ - ручное отключение индексации сайтов.
![StopIndexing](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/b8813293-8779-443c-a1c3-ac090b6a0263)
1.3 _Поле ввода страницы сайта_ - добавление/обновление страницы сайта для индексации (при необходимости).  
![AddPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/8c0a2f1f-26ba-4f7a-b8c3-5fa154e9ee85)
1.3.1 Если страница сайта указана неправильно, ожидается следующее сообщение:
![ErrorAddPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/43a673ca-43e4-463c-ab62-2f8c8d38ab50)
1.3.2 Если страница сайта пуста, ожидается следующее сообщение:
![EmptyAddPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/61a4b8d0-d462-4b01-bf0f-4822b04436c3)
#### 2. Модуль Dashboard
2.1.1 После запуска индексации сайтов, на окне модуля отражается статистика об общем количестве индексируемых сайтов, их страниц и полученных лем.
![Indexing](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/5ba396d5-2f6c-4159-8c5d-53cfcf37e56d)
2.1.2 Также можно отслеживать за статусом индексации, датой и временем старта индексации и количеством индексированных страниц и полученных лем с каждого сайта по отдельности.
![PartIndexing](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/3329801c-5fd3-4c40-9d96-5751f7211b94)
2.2 После ручного отключения индексации сайтов, процесс индексации завершается и статус "indexing" меняется на статус "indexed".
![IndexedPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/f810bc7b-917c-4f09-b5ce-75a8de7f706d)
2.3 Если возникла ошибка в процессе индексации сайтов, процесс индексации завершается и статус "indexing" меняется на статус "failed":
![ErrorIndexingPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/68c5e63b-2660-4c15-adb4-7421236e9b51)
#### 3. Модуль Search
3.1 Поиск ключевого слова осуществляется с помощью _поля поиска_, _кнопки("search")_ и _кнопки выпадающего списка_ ("All sites" - поиск по всем или отдельным сайтам).
- поиск по всем сайтам:
![SearchResult](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/a4e7c0f6-ed02-459a-8035-5ee11371baf2)
- поиск по отдельным сайтам:
![SearchSingleSite](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/c11d4c67-5965-4e3b-be13-3e940dac633c)
- если поле поиска пусто, ожидается следующее сообщение:
![SearchEmpty](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/bb188ed8-f481-4580-bbfd-0c9e236e1fd8)
- если заданный запрос отсутствует в странице сайта, ожидается следующее сообщение:
![SearchError2](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/eb821bfc-ae52-47fa-9be3-acf44c410529)

***
### Настройка проекта
При разработке данного проекта использованы Java, Spring Boot, Maven, JDBC, Morphology, Hibernate, SQL, JSOUP, Lombok.


