import numpy as np
# Создаем русский алфавит с пробелом


# (1)
def gcd(a, b):
    if b == 0:
        print(f"НОД равен {a}")
        return a
    else:
        q = a // b
        r = a % b
        print(f"{a} / {b} = {q} ост {r}")
        return gcd(b, r)

# (2)
def print_row(q, r, s, t, col_width=10):
    print(f"{q:^{col_width}}│ {r:^{col_width}}│ {s:^{col_width}}│ {t:^{col_width}}")

def extended_gcd_pretty(a, b):
    # Инициализация списков
    r = [a, b]
    s = [1, 0]
    t = [0, 1]
    q = []
    steps = []
    
    # Выполняем шаги алгоритма
    while True:
        current_b = r[-1]
        if current_b == 0:
            break
        q_i = r[-2] // current_b
        r_i = r[-2] % current_b
        
        # Вычисляем новые коэффициенты s и t
        s_i = s[-2] - s[-1] * q_i
        t_i = t[-2] - t[-1] * q_i
        
        # Сохраняем шаг
        steps.append({
            "q": q_i,
            "r": r_i,
            "s": s_i,
            "t": t_i
        })
        
        # Добавляем новые значения в списки
        q.append(q_i)
        r.append(r_i)
        s.append(s_i)
        t.append(t_i)
    
    # Форматирование таблицы
    col_width = 12
    header = f"{'q_i':^{col_width}}│ {'r_i':^{col_width}}│ {'s_i':^{col_width}}│ {'t_i':^{col_width}}"
    separator = "─" * col_width + "┼" + "─" * (col_width + 1) + "┼" + "─" * (col_width + 1) + "┼" + "─" * col_width
    
    print(header)
    print(separator)
    
    # Первые две строки
    print_row("-", r[0], s[0], t[0], col_width)
    print_row("-", r[1], s[1], t[1], col_width)
    print(separator)
    
    # Последующие строки
    for i, step in enumerate(steps):
        r_val = step['r']
        s_val = step['s'] if r_val != 0 else "-"
        t_val = step['t'] if r_val != 0 else "-"
        print_row(step['q'], step['r'], s_val, t_val, col_width)
        print(separator)
    
    # Получаем НОД и коэффициенты
    gcd = r[-2]
    s_final = s[-2]
    t_final = t[-2]
    
    # Форматируем ответ
    print(f"\nОтвет: {a} * ({s_final}) + {b} * ({t_final}) = {gcd}")
    return gcd, s_final, t_final
# (3)
def find_inverse(a, modulus):
    # Используем ранее созданную функцию для получения коэффициентов
    gcd, s, t = extended_gcd_pretty(a, modulus)
    
    # Проверяем, существует ли обратный элемент
    if gcd != 1:
        print(f"Обратного элемента не существует: НОД({a}, {modulus}) = {gcd}")
        return None
    
    # Приводим коэффициент к положительному виду в кольце вычетов
    inverse = s % modulus
    
    # Выводим результат
    print(f"\nОбратный элемент к {a} по модулю {modulus}: {inverse}")
    return inverse
# (4)
def extended_gcd(a, b):
    if b == 0:
        return (a, 1, 0)
    else:
        g, x, y = extended_gcd(b, a % b)
        return (g, y, x - (a // b) * y)

def mod_inv(a, mod):
    g, x, y = extended_gcd(a, mod)
    if g != 1:
        return None
    return x % mod

def matrix_inverse_mod(matrix_str, mod=34):
    elements = list(map(int, matrix_str.split()))
    n = int(len(elements)**0.5)
    
    if n*n != len(elements):
        print("Ошибка: Некорректный размер матрицы")
        return
    
    A = np.array(elements).reshape(n, n)
    print("Исходная матрица:")
    print(A)
    
    # Вычисление определителя
    det = int(round(np.linalg.det(A))) % mod
    print(f"\nШаг 1: Определитель = {det} (mod {mod})")
    
    if det == 0:
        print("Матрица вырождена")
        return
    
    # Поиск обратного к определителю
    det_inv = mod_inv(det, mod)
    if det_inv is None:
        print(f"Нет обратного для {det} mod {mod}")
        return
    print(f"Шаг 2: Обратный к определителю = {det_inv}")
    
    # Матрица алгебраических дополнений
    def cofactor(m):
        minors = np.zeros_like(m)
        for i in range(n):
            for j in range(n):
                minor = np.delete(np.delete(m, i, 0), j, 1)
                minors[i][j] = (-1)**(i+j) * int(round(np.linalg.det(minor)))
        return minors
    
    C = cofactor(A)
    print("\nШаг 3: Матрица алгебраических дополнений:")
    print(C)
    
    # Транспонирование и умножение на обратный определитель
    adjugate = C.T
    inv = (adjugate * det_inv) % mod
    inv = np.where(inv < 0, inv + mod, inv)
    
    print("\nРезультат: Обратная матрица")
    print(inv)
    return inv

# (5) 
def hill_cipher(text, matrix_str, mod=34):
    russian_alphabet = [
    'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й',
    'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф',
    'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', ' '
]
    def char_to_num(c):
        c = c.upper()
        if c == 'Ё':
            return 6
        try:
            return russian_alphabet.index(c)
        except ValueError:
            raise ValueError(f"Символ '{c}' не найден в алфавите")

    def num_to_char(n):
        return russian_alphabet[n]
    # Парсинг матрицы
    matrix = list(map(int, matrix_str.split()))
    matrix_size = int(len(matrix)**0.5)
    
    # Проверка на квадратную матрицу
    if matrix_size * matrix_size != len(matrix):
        print("Ошибка: Матрица не квадратная")
        return
    
    print(f"A = {[matrix[i*matrix_size:(i+1)*matrix_size] for i in range(matrix_size)]}\n")
    
    # Преобразование текста в числовой формат
    try:
        numeric = [char_to_num(c) for c in text.upper()]
    except ValueError as e:
        print(e)
        return
    
    # Дополнение пробелами
    padding = (-len(numeric)) % matrix_size
    numeric += [33] * padding
    
    # Разбиение на блоки
    blocks = [numeric[i:i+matrix_size] for i in range(0, len(numeric), matrix_size)]
    
    # Шифрование блоков
    encrypted = []
    for i, block in enumerate(blocks):
        print(f"a{i+1} = {block}")
        
        # Умножение матрицы на вектор
        cipher_block = []
        for row in range(matrix_size):
            total = 0
            for col in range(matrix_size):
                total += matrix[row*matrix_size + col] * block[col]
            cipher_block.append(total % mod)
        
        print(f"c{i+1} = {cipher_block}\n")
        encrypted.extend(cipher_block)
    
    # Преобразование в текст
    ciphertext = ''.join(num_to_char(n) for n in encrypted)
    print(f"Зашифрованный текст: {ciphertext}")
    return ciphertext

# (6)
def hill_decrypt(ciphertext, matrix_str, mod=34):
    russian_alphabet = [
    'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й',
    'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф',
    'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', ' '
]

    def char_to_num(c):
        c = c.upper()
        try:
            return russian_alphabet.index(c)
        except ValueError:
            raise ValueError(f"Символ '{c}' не найден в алфавите")

    def num_to_char(n):
        return russian_alphabet[n % 34]

    def mod_inv(a, mod):
        g, x, y = extended_gcd(a, mod)
        if g != 1:
            return None
        return x % mod

    def extended_gcd(a, b):
        if b == 0:
            return (a, 1, 0)
        else:
            g, x, y = extended_gcd(b, a % b)
            return (g, y, x - (a // b) * y)

    def matrix_inverse_mod(matrix, mod):
        n = len(matrix)
        det = int(round(np.linalg.det(matrix))) % mod
        det_inv = mod_inv(det, mod)
        
        minors = np.zeros((n, n), dtype=int)
        for i in range(n):
            for j in range(n):
                minor = np.delete(np.delete(matrix, i, 0), j, 1)
                minors[i][j] = (-1)**(i+j) * int(round(np.linalg.det(minor))) % mod
        
        adjugate = minors.T
        inv = (adjugate * det_inv) % mod
        return np.where(inv < 0, inv + mod, inv)
    matrix = list(map(int, matrix_str.split()))
    size = int(len(matrix)**0.5)
    matrix = np.array(matrix).reshape(size, size)
    
    inv_matrix = matrix_inverse_mod(matrix, mod)
    print("Обратная матрица A^{-1}:")
    print(inv_matrix)
    
    cipher_nums = [char_to_num(c) for c in ciphertext]
    blocks = [cipher_nums[i:i+size] for i in range(0, len(cipher_nums), size)]
    
    plain_nums = []
    for block in blocks:
        decrypted = (inv_matrix @ np.array(block)) % mod
        plain_nums.extend(decrypted)
        print(f"Вектор {block} -> {decrypted}")
    
    plaintext = ''.join(num_to_char(n) for n in plain_nums)
    return plaintext

# Пример вызова функции
def main():
    print("\n##############_1_##############")
    gcd(2784, 246)
    print("\n##############_2_##############")
    extended_gcd_pretty(2784, 246)
    print("\n##############_3_##############")
# Находим обратный элемент к 357 по модулю 451
    find_inverse(357, 451)
    # Пример 1
    print("\n##############_4_##############")
    print("Пример 1:")
    matrix_inverse_mod("13 5 9 11 9 11 7 6 13 18 10 5 7 3 10 15")

    # Пример 2
    print("\nПример 2:")
    matrix_inverse_mod("17 1 5 2 18 21 32 0 34 2 10 4 5 19 11 10")

    print("\n##############_5_##############")
    # Пример использования
    A = "5 6 3 1 14 2 3 11 13 4 26 5 6 7 8 9"
    message = "тестовый вариант"

    print("Пример 1:")
    hill_cipher(message, A)

    # Дополнительный пример
    A_example = "1 2 3 4 5 1 7 8 13 1 4 1 10 1 1 0"
    message_example = "шифр хилла"

    print("\nПример 2:")
    hill_cipher(message_example, A_example)
    
    
    print("\n##############_6_##############")
    A = "6 24 1 13 16 10 20 17 15"
    ciphertext = "фъооию"
    plaintext = hill_decrypt(ciphertext, A)
    print(f"\nРасшифрованный текст: {plaintext}")
    
    A = "5 6 3 1 14 2 3 11 13 4 26 5 6 7 8 9"
    ciphertext = "ЫЬНХКАЬЙЧЖЛШДГЯЮ"
    plaintext = hill_decrypt(ciphertext, A)
    print(f"\nРасшифрованный текст: {plaintext}")
if __name__ == "__main__":
    main()
