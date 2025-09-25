import tkinter as tk
from tkinter import messagebox

class Calculator:
    def __init__(self, root):
        self.root = root
        self.root.title("Calculator")
        self.root.geometry("300x400")
        self.root.resizable(False, False)

        self.current = ""
        self.total = 0
        self.operator = None
        self.new_num = True

        self.create_widgets()

    def create_widgets(self):
        # Display
        self.display_var = tk.StringVar()
        self.display_var.set("0")
        display = tk.Entry(self.root, textvariable=self.display_var,
                          font=("Arial", 16), justify="right",
                          state="readonly", bg="white")
        display.grid(row=0, column=0, columnspan=4, padx=5, pady=5, sticky="ew")

        # Button layout
        buttons = [
            ('C', 1, 0), ('±', 1, 1), ('%', 1, 2), ('÷', 1, 3),
            ('7', 2, 0), ('8', 2, 1), ('9', 2, 2), ('×', 2, 3),
            ('4', 3, 0), ('5', 3, 1), ('6', 3, 2), ('-', 3, 3),
            ('1', 4, 0), ('2', 4, 1), ('3', 4, 2), ('+', 4, 3),
            ('0', 5, 0), ('.', 5, 2), ('=', 5, 3)
        ]

        # Create buttons
        for (text, row, col) in buttons:
            if text == '0':
                btn = tk.Button(self.root, text=text, font=("Arial", 14),
                               command=lambda t=text: self.on_button_click(t))
                btn.grid(row=row, column=col, columnspan=2, padx=2, pady=2, sticky="nsew")
            else:
                btn = tk.Button(self.root, text=text, font=("Arial", 14),
                               command=lambda t=text: self.on_button_click(t))
                btn.grid(row=row, column=col, padx=2, pady=2, sticky="nsew")

        # Configure grid weights
        for i in range(6):
            self.root.grid_rowconfigure(i, weight=1)
        for i in range(4):
            self.root.grid_columnconfigure(i, weight=1)

    def on_button_click(self, char):
        if char.isdigit():
            self.on_number(char)
        elif char == '.':
            self.on_decimal()
        elif char in ['+', '-', '×', '÷']:
            self.on_operator(char)
        elif char == '=':
            self.on_equals()
        elif char == 'C':
            self.on_clear()
        elif char == '±':
            self.on_plus_minus()
        elif char == '%':
            self.on_percent()

    def on_number(self, num):
        if self.new_num:
            self.current = num
            self.new_num = False
        else:
            self.current += num
        self.display_var.set(self.current)

    def on_decimal(self):
        if self.new_num:
            self.current = "0."
            self.new_num = False
        elif '.' not in self.current:
            self.current += '.'
        self.display_var.set(self.current)

    def on_operator(self, op):
        if not self.new_num:
            self.on_equals()

        self.operator = op
        if self.current:
            self.total = float(self.current)
        self.new_num = True

    def on_equals(self):
        if self.operator and not self.new_num:
            try:
                current_num = float(self.current)
                if self.operator == '+':
                    result = self.total + current_num
                elif self.operator == '-':
                    result = self.total - current_num
                elif self.operator == '×':
                    result = self.total * current_num
                elif self.operator == '÷':
                    if current_num == 0:
                        messagebox.showerror("Error", "Cannot divide by zero")
                        return
                    result = self.total / current_num

                # Format result
                if result == int(result):
                    self.current = str(int(result))
                else:
                    self.current = str(result)

                self.display_var.set(self.current)
                self.total = result
                self.operator = None
                self.new_num = True

            except ValueError:
                messagebox.showerror("Error", "Invalid operation")

    def on_clear(self):
        self.current = ""
        self.total = 0
        self.operator = None
        self.new_num = True
        self.display_var.set("0")

    def on_plus_minus(self):
        if self.current and self.current != "0":
            if self.current.startswith('-'):
                self.current = self.current[1:]
            else:
                self.current = '-' + self.current
            self.display_var.set(self.current)

    def on_percent(self):
        if self.current:
            try:
                result = float(self.current) / 100
                if result == int(result):
                    self.current = str(int(result))
                else:
                    self.current = str(result)
                self.display_var.set(self.current)
            except ValueError:
                pass

if __name__ == "__main__":
    root = tk.Tk()
    calculator = Calculator(root)
    root.mainloop()