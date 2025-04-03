# TOKEN = "8031566999:AAGNUizJFyMVl8Yld36k4h5EGCgOOc1Xeh4"
from telegram import Update, ReplyKeyboardMarkup
from telegram.ext import Application, CommandHandler, MessageHandler, filters, CallbackContext
import sys
import io
import numpy as np
from contextlib import redirect_stdout
from kr2 import (
    gcd,
    extended_gcd_pretty,
    find_inverse,
    matrix_inverse_mod,
    hill_cipher,
    hill_decrypt
)

TOKEN = "8031566999:AAGNUizJFyMVl8Yld36k4h5EGCgOOc1Xeh4"
TASKS = [
    "1. НОД", 
    "2. Расширенный НОД",
    "3. Обратный элемент",
    "4. Обратная матрица",
    "5. Шифр Хилла",
    "6. Дешифр Хилла"
]

async def start(update: Update, context: CallbackContext):
    keyboard = [TASKS[i:i+2] for i in range(0, len(TASKS), 2)]
    reply_markup = ReplyKeyboardMarkup(keyboard, resize_keyboard=True)
    await update.message.reply_text(
        "Выберите задачу:",
        reply_markup=reply_markup
    )

async def handle_task(update: Update, context: CallbackContext):
    task = update.message.text
    context.user_data['task'] = task
    example = get_example(task)
    await update.message.reply_text(f"Введите данные ({example}):")

def get_example(task: str) -> str:
    examples = {
        TASKS[0]: "2784 246",
        TASKS[1]: "2784 246",
        TASKS[2]: "357 451",
        TASKS[3]: "13 5 9 11 9 11 7 6 13 18 10 5 7 3 10 15",
        TASKS[4]: "тестовый вариант 5 6 3 1 14 2 3 11 13 4 26 5 6 7 8 9",
        TASKS[5]: "фъооию 6 24 1 13 16 10 20 17 15"
    }
    return examples.get(task, "")

async def execute_code(update: Update, context: CallbackContext):
    task = context.user_data.get('task')
    text = update.message.text
    output_buffer = io.StringIO()
    
    try:
        with redirect_stdout(output_buffer), redirect_stderr(output_buffer):
            if task == TASKS[0]:
                a, b = map(int, text.split())
                result = gcd(a, b)
                output = f"{output_buffer.getvalue()}\nРезультат: {result}"
            
            elif task == TASKS[1]:
                a, b = map(int, text.split())
                g, s, t = extended_gcd_pretty(a, b)
                output = f"{output_buffer.getvalue()}\nКоэффициенты: s={s}, t={t}"
            
            elif task == TASKS[2]:
                a, mod = map(int, text.split())
                inv = find_inverse(a, mod)
                output = f"{output_buffer.getvalue()}\nОбратный элемент: {inv if inv else 'не существует'}"
            
            elif task == TASKS[3]:
                matrix = list(map(int, text.split()))
                inv = matrix_inverse_mod(text, 34)
                output = f"{output_buffer.getvalue()}\nОбратная матрица:\n{np.array_str(inv) if inv is not None else 'Не существует'}"
            
            elif task == TASKS[4]:
                *text_parts, matrix_str = text.rsplit(" ", 15)
                plaintext = " ".join(text_parts)
                cipher = hill_cipher(plaintext, matrix_str)
                output = f"{output_buffer.getvalue()}\nШифртекст: {cipher}"
            
            elif task == TASKS[5]:
                ciphertext, matrix_str = text.split(" ", 1)
                plain = hill_decrypt(ciphertext, matrix_str)
                output = f"{output_buffer.getvalue()}\nРасшифровка: {plain}"
            
            escaped_output = output.replace('_', '\\_').replace('*', '\\*')
            await update.message.reply_text(f"```\n{escaped_output}\n```", parse_mode='MarkdownV2')
    
    except Exception as e:
        error_msg = f"Ошибка: {str(e)}".replace('_', '\\_').replace('*', '\\*')
        await update.message.reply_text(f"```\n{error_msg}\n```", parse_mode='MarkdownV2')

def main():
    app = Application.builder().token(TOKEN).build()
    
    app.add_handler(CommandHandler("start", start))
    app.add_handler(MessageHandler(filters.TEXT & ~filters.COMMAND, handle_task))
    app.add_handler(MessageHandler(filters.TEXT & ~filters.COMMAND, execute_code))
    
    app.run_polling()

if __name__ == "__main__":
    main()