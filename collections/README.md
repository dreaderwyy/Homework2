ДЗ 7: Java Collections

Запуск тестов: mvn test (или в IntelliJ правой кнопкой по src/test/java -> Run 'All Tests').

Где что лежит:
Задание 1 - CustomList, CustomArrayList, тесты в CustomArrayListTest.
Задание 2 - CollectionPerformanceExperiment (запускается через main).
Задание 3 - Student, StudentRegistry, тесты в StudentTest и StudentRegistryTest.

Результаты эксперимента (10 000 элементов, медиана из 7 замеров после 3 прогревов):
Добавление в конец: ArrayList 0.636 мс, LinkedList 0.271 мс
Добавление в начало: ArrayList 1.455 мс, LinkedList 0.403 мс
Вставка в середину: ArrayList 0.981 мс, LinkedList 40.520 мс
Доступ по индексу: ArrayList 0.066 мс, LinkedList 48.387 мс
Удаление из начала: ArrayList 1.848 мс, LinkedList 0.152 мс
Удаление из конца: ArrayList 0.065 мс, LinkedList 0.254 мс

Выводы:
ArrayList сильно быстрее при доступе по индексу, потому что сразу берёт элемент из массива, а LinkedList идёт по узлам.
Вставка в середину у LinkedList тоже медленная: сначала нужно дойти до нужного узла.
У ArrayList при вставке нужно сдвигать элементы, но массив лежит в памяти подряд, поэтому это всё равно быстро.
В начало LinkedList добавляет и удаляет быстрее, потому что ArrayList каждый раз сдвигает все элементы.
Добавление в конец у обоих быстрое, разница небольшая и может меняться от запуска к запуску.
В большинстве случаев лучше брать ArrayList, а LinkedList - если часто работаешь с началом списка.
