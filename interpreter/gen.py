import random
import string

# Функция для создания случайного идентификатора переменной
def generate_variable_id():
    return ''.join(random.choices(string.ascii_letters, k=8))

# Функция для создания случайного арифметического выражения
def generate_arithmetic_expression():
    operators = ['+', '-', ' * ', '/']
    operands = [f'x{i}' for i in range(10)]
    expression = []
    while len(operands) > 0:
        op = random.choice(operators)
        if op == '/' and operands[0] == 'x0':
            # Исключаем деление на ноль
            continue
        if op == '/' and operands[0][-1].isdigit() and int(operands[0][-1]) == 0:
            # Исключаем деление на число, которое дает ноль после деления
            continue
        expression.append(op)
        expression.append(random.choice(operands))
        operands.remove(random.choice(operands))
    return ''.join(expression)

# Создаем файл для записи результатов
# Потом вставляем объявления переменных и заменяем все `=  *` на `=`
with open('Contents.swift', 'w') as file:
    for _ in range(1000):
        variable_id = generate_variable_id()
        arithmetic_expression = generate_arithmetic_expression()
        file.write(f'let {variable_id} = {arithmetic_expression}\n')