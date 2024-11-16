Интерпретатор подмножества языка Swift с рядом условностей:
- Доступны типы Int и Double, константы и переменные, арифмтические операции.
- Есть обработка однострочных и многострочных комментариев.
- В переменную типа Int записываются значения типа Double округленные до Int.
- Пока нет концепции блоков, функций, нет ввода/вывода.

В пакетах `calc` промежуточные шаги - всякие калькуляторы, parse tree, abstract syntax tree.  
В пакете `interpeter` код интерпретатора для следующей грамматики:

<img src="./screenshots/grammar.png" width="100%" >
