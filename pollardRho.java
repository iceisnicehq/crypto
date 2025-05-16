import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

// Класс для хранения данных одного шага алгоритма
class StepData {
    int step;
    BigInteger xi;
    BigInteger yi;
    BigInteger absDiff; // |xi - yi|
    BigInteger gcd;    // gcd(|xi - yi|, n)

    public StepData(int step, BigInteger xi, BigInteger yi, BigInteger absDiff, BigInteger gcd) {
        this.step = step;
        this.xi = xi;
        this.yi = yi;
        this.absDiff = absDiff;
        this.gcd = gcd;
    }
}

// Реализация алгоритма факторизации Ро-методом Полларда
class PollardRhoAlgorithm {

    private List<StepData> steps;
    private List<BigInteger> factors;

    public PollardRhoAlgorithm() {
        steps = new ArrayList<>();
        factors = new ArrayList<>();
    }

    // Основной метод факторизации, возвращающий шаги и находящий множители
    public List<StepData> factorizeWithSteps(BigInteger n) {
        steps.clear(); // Очищаем предыдущие шаги
        factors.clear(); // Очищаем предыдущие множители

        if (n.compareTo(BigInteger.ONE) <= 0) {
            return steps;
        }
         // Добавим проверку на простые числа до запуска алгоритма
        if (n.isProbablePrime(20)) { // Увеличим уверенность проверки
             factors.add(n);
             return steps;
        }

        // Реализация алгоритма Ро-метома Полларда
        BigInteger x = new BigInteger("2"); // x_0
        BigInteger y = new BigInteger("2"); // y_0
        BigInteger d = BigInteger.ONE; // gcd
        int stepCount = 0;

        // Функция итерации: f(x) = (x^2 + c) mod n
        // Можно попробовать разные константы, если 1 не сработает
        BigInteger c = BigInteger.ONE;

        // Ограничение на количество шагов
        int maxSteps = 300000; // Увеличим лимит

        while (d.equals(BigInteger.ONE) && stepCount < maxSteps) {
            stepCount++;

            // Шаг 1: x = f(x) mod n
            x = x.multiply(x).add(c).mod(n);

            // Шаг 2: y = f(f(y)) mod n
            y = y.multiply(y).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);

            // Шаг 3: Вычисляем d = gcd(|x - y|, n)
            BigInteger diff = x.subtract(y).abs();
            d = diff.gcd(n);

             // Добавляем шаг в список
             steps.add(new StepData(stepCount, x, y, diff, d));


            // Если найден нетривиальный делитель
            if (!d.equals(BigInteger.ONE) && !d.equals(n)) {
                factors.add(d);
                 BigInteger otherFactor = n.divide(d);
                 if (!otherFactor.equals(BigInteger.ONE)) {
                     if (!factors.contains(otherFactor)) {
                          // Рекурсивно факторизовать otherFactor для полной факторизации (опционально)
                          // В простом варианте просто добавляем его
                          if (otherFactor.isProbablePrime(20)) { // Если второй множитель простой
                              factors.add(otherFactor);
                          } else {
                               // Если второй множитель составной, пытаемся его факторизовать
                               // Это упрощенная рекурсия для примера. В реальной жизни нужно более надежно.
                               PollardRhoAlgorithm subFactorizer = new PollardRhoAlgorithm();
                               subFactorizer.factorizeWithSteps(otherFactor); // Запускаем для второго множителя
                               List<BigInteger> subFactors = subFactorizer.getFactors();
                               if (subFactors != null && !subFactors.isEmpty() && !subFactors.contains(otherFactor)) {
                                    // Если найдены под-множители, добавляем их
                                    for(BigInteger sf : subFactors) {
                                        if (!factors.contains(sf)) {
                                            factors.add(sf);
                                        }
                                    }
                               } else {
                                   // Если под-множители не найдены или он сам простой
                                    if (!factors.contains(otherFactor)) {
                                         factors.add(otherFactor);
                                    }
                               }
                          }
                     }
                 }


                // Сортируем множители
                factors.sort(BigInteger::compareTo);

                // После нахождения первого делителя и попытки факторизовать второй, можно остановиться
                 return steps;
            }

            // Если НОД стал равен n, возможно, нужно сменить функцию или начальную точку
             if (d.equals(n) && stepCount < maxSteps) {
                 // Пробуем сменить константу 'c' и сбросить
                 System.out.println("Pollard Rho: GCD became n at step " + stepCount + ". Restarting with c=2.");
                 steps.clear(); // Очищаем шаги
                 x = new BigInteger("2");
                 y = new BigInteger("2");
                 c = new BigInteger("2"); // Меняем константу
                 d = BigInteger.ONE;
                 stepCount = 0; // Сброс счетчика шагов
                 maxSteps = 300000; // Новый лимит для новой попытки (можно сделать отдельный лимит)

                  // Если и с c=2 не поможет, можно попробовать c=3 и т.д., или использовать другой алгоритм.
                  // Для простоты, если и с c=2 не найдем, остановимся.
             }
        }

         // Если алгоритм завершился без нахождения нетривиального делителя
         if (factors.isEmpty()) {
             // Если множители не найдены, и число не было определено как простое ранее,
             // возможно, достигнут лимит шагов.
             System.out.println("Pollard Rho: Could not find factors for " + n + " within step limit or with tried constants.");
             // Если уверены, что число не простое и не нашли множитель,
             // можно добавить само число в список множителей как неразложенный остаток.
             // factors.add(n); // Опционально: добавить N, если не разложено
         }


        return steps; // Возвращаем собранные шаги
    }

    // Метод для получения найденных множителей
    public List<BigInteger> getFactors() {
        return factors;
    }
}


public class PollardRhoFrame extends JFrame {
    private JTextField inputField;
    private JButton factorButton;
    private JTable stepsTable;
    private DefaultTableModel tableModel;
    private JLabel resultLabel; // To display the final factors
    private PollardRhoAlgorithm algorithm; // Ваш класс алгоритма
    private JLabel inputLabel; // Label for input prompt

    // Define colors
    private static final Color YA_OAK = new Color(114, 101, 80); // Светло-голубой
    private static final Color APPLE_PIE = new Color(145, 185, 12); // Средне-голубой
    private static final Color DARK_BLUE_BORDER = new Color(0, 150, 0); // Темно-голубой для границ

    // Define custom font
    private static final Font CUSTOM_FONT = new Font("Chancery", Font.PLAIN, 14);


    public PollardRhoFrame() {
        // --- Frame Setup ---
        super("Факторизация Ро-методом Полларда");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550); // Увеличим размер окна
        setLocationRelativeTo(null); // Center the frame

        // --- Improve Look and Feel (Optional but good practice) ---
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Layout ---
        setLayout(new BorderLayout(15, 15)); // Add larger gaps between regions

        inputLabel = new JLabel("Введите число для факторизации:");
        inputField = new JTextField(20); // Увеличим ширину поля ввода
        inputField.setBackground(YA_OAK); // Set input field background color
        inputField.setForeground(APPLE_PIE); // Set input field text color for contrast
        factorButton = new JButton("Факторизовать");
        factorButton.setBackground(YA_OAK); // Set button background color
        factorButton.setOpaque(true);
        factorButton.setBorderPainted(false);
        factorButton.setFocusPainted(false); // <-- Add this line
        factorButton.setForeground(APPLE_PIE); // Set button text color for contrast
        resultLabel = new JLabel("Факторы: ");

        // Table setup
        // Упростим названия столбцов
        String[] columnNames = {"i", "x_i", "y_i", "|x_i - y_i|", "НОД(|x_i - y_i|, n)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stepsTable = new JTable(tableModel);
        stepsTable.setFillsViewportHeight(true);
        stepsTable.setRowHeight(20);

        // Set table background and font color
        stepsTable.setBackground(YA_OAK); // Light blue background for table
        stepsTable.setForeground(APPLE_PIE);    // Dark blue font color
        stepsTable.setShowGrid(true);
        stepsTable.setGridColor(APPLE_PIE); // or any Color you want
        // Set font for table
        stepsTable.setFont(new Font("Consolas", Font.BOLD, 15)); // Example: bold Consolas

        // Set header background and font color
        stepsTable.getTableHeader().setBackground(YA_OAK); // Header background
        stepsTable.getTableHeader().setForeground(APPLE_PIE);    // Header font color
        stepsTable.getTableHeader().setFont(new Font("Consolas", Font.BOLD, 15));



        // Настройка рендереров для выравнивания по центру для всех столбцов
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = stepsTable.getColumnModel();
        // Применяем рендерер для выравнивания по центру ко всем столбцам (от 0 до последнего)
        for (int i = 0; i < columnNames.length; i++) {
             columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }


        JScrollPane tableScrollPane = new JScrollPane(stepsTable);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(DARK_BLUE_BORDER),
                        "Шаги факторизации",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        CUSTOM_FONT, // Шрифт для заголовка рамки
                        Color.BLACK
                ),
                new EmptyBorder(5, 5, 5, 5)
        ));
        tableScrollPane.setBackground(APPLE_PIE);
        tableScrollPane.getViewport().setBackground(Color.BLACK); // Set background color for the table viewport


        // Initialize your algorithm
        algorithm = new PollardRhoAlgorithm(); // Используем рабочую реализацию

        // --- Panel for Input and Button ---
        // Уменьшим горизонтальный отступ в FlowLayout для более плотного размещения
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 15)); // Уменьшен горизонтальный gap
        inputPanel.setBackground(APPLE_PIE);
        inputPanel.setOpaque(true);

        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(factorButton);

        // Apply custom font to input components
        inputLabel.setFont(CUSTOM_FONT);
        inputField.setFont(CUSTOM_FONT);
        factorButton.setFont(CUSTOM_FONT);
        resultLabel.setFont(CUSTOM_FONT);

        // Опционально: попытаться применить шрифт ко всем компонентам рекурсивно.
        // Это может помочь с компонентами, которые создаются внутри других (например, в JOptionPane),
        // но не гарантировано для всех L&F.
        // applyFontToAll(this, CUSTOM_FONT);


        // --- Set background color for the main content pane ---
        getContentPane().setBackground(YA_OAK);
        ((JPanel) getContentPane()).setOpaque(true);


        // --- Action Listener ---
        factorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                factorizeNumber();
            }
        });

        // --- Add Components to Frame ---
        add(inputPanel, BorderLayout.NORTH); // Input and button at the top
        add(tableScrollPane, BorderLayout.CENTER); // Table in the center
        add(resultLabel, BorderLayout.SOUTH); // Result label at the bottom

        // Add some padding around the main content
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));


        setVisible(true);
    }

    // Опциональный рекурсивный метод для применения шрифта ко всем компонентам
    private void applyFontToAll(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyFontToAll(child, font);
            }
        }
    }


    private void factorizeNumber() {
        tableModel.setRowCount(0); // Clear previous results from the table
        resultLabel.setText("Факторы: "); // Clear previous factors

        String inputText = inputField.getText();
        if (inputText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите число.", "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigInteger numberToFactor;
        try {
            numberToFactor = new BigInteger(inputText);
            if (numberToFactor.compareTo(BigInteger.ONE) <= 0) {
                 JOptionPane.showMessageDialog(this, "Введите число больше 1.", "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
                 return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Некорректный ввод. Введите целое число.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Call the algorithm and update the GUI ---
        try {
             List<StepData> steps = algorithm.factorizeWithSteps(numberToFactor);
             List<BigInteger> factors = algorithm.getFactors();


            if (steps != null) {
                for (StepData step : steps) {
                    tableModel.addRow(new Object[]{
                            step.step,
                            step.xi,
                            step.yi,
                            step.absDiff,
                            step.gcd
                    });
                }
            }

            // Display factors
            if (factors != null && !factors.isEmpty()) {
                StringBuilder factorsText = new StringBuilder("Факторы: ");
                for (int i = 0; i < factors.size(); i++) {
                    factorsText.append(factors.get(i));
                    if (i < factors.size() - 1) {
                        factorsText.append(", ");
                    }
                }
                resultLabel.setText(factorsText.toString());
                resultLabel.setForeground(APPLE_PIE);

            } else {
                resultLabel.setText("Факторы: Не найдены (возможно, число простое или требуется больше итераций)");
                resultLabel.setForeground(APPLE_PIE);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Произошла ошибка во время факторизации: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PollardRhoFrame::new);
    }
}
