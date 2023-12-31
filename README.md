# SEARCH ENGINE PROJECT
---
## Краткое описание 
Spring Boot проект представляет собой систему индексации сайтов. Он разработан для автоматического сбора информации с веб-страниц и создания поискового индекса, который облегчает поиск и поисковые запросы на этих сайтах. Проект осуществляет обход сайтов, скачивание и анализ HTML-контента, извлечение текстовых данных и создание структурированного индекса, позволяющего быстрый и эффективный доступ к информации на этих сайтах.

***
### Cтек
При разработке данного проекта использованы Java, Spring Boot, Maven, JDBC, Morphology, Hibernate, SQL, JSOUP, Lombok.

***
### Руководство по эксплуатации проекта

Данный проект запускается с помощью локального веб-сервера (URL-адрес: http://localhost:8080/) и состоит из трех модулей.
#### 1. Модуль Management
1.1 _Кнопка(Start Indexing)_ - ручной запуск индексации сайтов.
![StartIndexing](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/f51966f8-9320-46fa-b8fe-9ebd8a0e0374)
1.2 _Кнопка(Stop Indexing)_ - ручное отключение индексации сайтов.
![StopIndexing](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/c3dbc818-6dc8-48af-8670-56c6953120ab)
1.3 _Поле ввода страницы сайта_ - добавление/обновление страницы сайта для индексации (при необходимости).  
![AddPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/52806c25-5b56-45db-85fa-c19076330dbd)
1.3.1 Если страница сайта указана неправильно, ожидается следующее сообщение:
![ErrorAddPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/f3cf5f38-21d3-4cc7-8ef1-139976182043)
1.3.2 Если страница сайта пуста, ожидается следующее сообщение:
![EmptyAddPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/56d78ae1-66df-4829-ae8f-8088ec3c6c26)
#### 2. Модуль Dashboard
2.1.1 После запуска индексации сайтов, на окне модуля отражается статистика об общем количестве индексируемых сайтов, их страниц и полученных лем.
![Indexing](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/d85d224b-1575-4b03-8fd5-cacb8aebf1ed)
2.1.2 Также можно отслеживать за статусом индексации, датой и временем старта индексации и количеством индексированных страниц и полученных лем с каждого сайта по отдельности.
![PartIndexing](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/f013ff95-cdd5-41f5-ba4c-087869253557)
2.2 После ручного отключения индексации сайтов, процесс индексации завершается и статус "indexing" меняется на статус "indexed".
![IndexedPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/b9a6e543-f1e8-4904-ac3a-6610a6f585c8)
2.3 Если возникла ошибка в процессе индексации сайтов, процесс индексации завершается и статус "indexing" меняется на статус "failed":
![ErrorIndexingPage](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/94f58d90-1a88-4dde-aac2-67ccdc3ab681)
#### 3. Модуль Search
3.1 Поиск ключевого слова осуществляется с помощью _поля поиска_, _кнопки("search")_ и _кнопки выпадающего списка_ ("All sites" - поиск по всем или отдельным сайтам).
- поиск по всем сайтам:
![SearchResult](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/65ac0506-38b8-4182-8ca0-2dfa7eacc6c5)
- поиск по отдельным сайтам:
![SearchSingleSite](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/2b07a4df-b26b-439e-964f-f492db5304e5)
- если поле поиска пусто, ожидается следующее сообщение:
![SearchEmpty](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/a595e237-43bc-4a08-a109-f8433bc035de)
- если заданный запрос отсутствует в странице сайта, ожидается следующее сообщение:
![SearchError2](https://github.com/DavlatbekRabbimov/search_engine/assets/110993036/2cb756f1-5dbb-48ec-9f75-14c4de4a05f8)




