# SEARCH ENGINE PROJECT
---
## Краткое описание 
Spring Boot проект представляет собой систему индексации сайтов. Он разработан для автоматического сбора информации с веб-страниц и создания поискового индекса, который облегчает поиск и поисковые запросы на этих сайтах. Проект осуществляет обход сайтов, скачивание и анализ HTML-контента, извлечение текстовых данных и создание структурированного индекса, позволяющего быстрый и эффективный доступ к информации на этих сайтах.

***
### Руководство по эксплуатации проекта

Данный проект запускается с помощью локального веб-сервера (URL-адрес: http://localhost:8080/) и состоит из трех модулей.
#### 1. Модуль Management
1.1 _Кнопка(Start Indexing)_ - ручной запуск индексации сайтов.
![](StartIndexing.jpg)
1.2 _Кнопка(Stop Indexing)_ - ручное отключение индексации сайтов.
![](StopIndexing.jpg)
1.3 _Поле ввода страницы сайта_ - добавление/обновление страницы сайта для индексации (при необходимости).  
![](AddPage.jpg)
1.3.1 Если страница сайта указана неправильно, ожидается следующее сообщение:
![](ErrorAddPage.jpg)
1.3.2 Если страница сайта пуста, ожидается следующее сообщение:
![](EmptyAddPage.jpg)
#### 2. Модуль Dashboard
2.1.1 После запуска индексации сайтов, на окне модуля отражается статистика об общем количестве индексируемых сайтов, их страниц и полученных лем.
![](Indexing.jpg)
2.1.2 Также можно отслеживать за статусом индексации, датой и временем старта индексации и количеством индексированных страниц и полученных лем с каждого сайта по отдельности.
![](PartIndexing.jpg)
2.2 После ручного отключения индексации сайтов, процесс индексации завершается и статус "indexing" меняется на статус "indexed".
![](IndexedPage.jpg)
2.3 Если возникла ошибка в процессе индексации сайтов, процесс индексации завершается и статус "indexing" меняется на статус "failed":
![](ErrorIndexingPage.jpg)
#### 3. Модуль Search
3.1 Поиск ключевого слова осуществляется с помощью _поля поиска_, _кнопки("search")_ и _кнопки выпадающего списка_ ("All sites" - поиск по всем или отдельным сайтам).
- поиск по всем сайтам:
![](SearchResult.jpg)
- поиск по отдельным сайтам:
![](SearchSingleSite.jpg)
- если поле поиска пусто, ожидается следующее сообщение:
![](SearchEmpty.jpg)
- если заданный запрос отсутствует в странице сайта, ожидается следующее сообщение:
 ![](SearchError2.jpg)

***
### Настройка проекта
При разработке данного проекта использованы Java, Spring Boot, Maven, JDBC, Morphology, Hibernate, SQL, JSOUP, Lombok.


