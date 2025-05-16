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

class StepData {
    int step;
    BigInteger xi;
    BigInteger yi;
    BigInteger absDiff; 
    BigInteger gcd;    
    public StepData(int step, BigInteger xi, BigInteger yi, BigInteger absDiff, BigInteger gcd) {
        this.step = step;
        this.xi = xi;
        this.yi = yi;
        this.absDiff = absDiff;
        this.gcd = gcd;
    }
}
class PollardRhoAlgorithm {
    private List<StepData> steps;
    private List<BigInteger> factors;
    public PollardRhoAlgorithm() {
        steps = new ArrayList<>();
        factors = new ArrayList<>();
    }

    public List<StepData> factorizeWithSteps(BigInteger n) {
        steps.clear(); 
        factors.clear(); 
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return steps;
        }
        if (n.isProbablePrime(20)) { 
             factors.add(n);
             return steps;
        }
        
        BigInteger x = new BigInteger("2"); 
        BigInteger y = new BigInteger("2"); 
        BigInteger d = BigInteger.ONE; 
        int stepCount = 0;
        BigInteger c = BigInteger.ONE;
        int maxSteps = 300000; 
        while (d.equals(BigInteger.ONE) && stepCount < maxSteps) {
            stepCount++;
            x = x.multiply(x).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);
            BigInteger diff = x.subtract(y).abs();
            d = diff.gcd(n);
             steps.add(new StepData(stepCount, x, y, diff, d));
            if (!d.equals(BigInteger.ONE) && !d.equals(n)) {
                factors.add(d);
                 BigInteger otherFactor = n.divide(d);
                 if (!otherFactor.equals(BigInteger.ONE)) {
                     if (!factors.contains(otherFactor)) {    
                          
                          if (otherFactor.isProbablePrime(20)) { 
                              factors.add(otherFactor);
                          } else {
                               
                               
                               PollardRhoAlgorithm subFactorizer = new PollardRhoAlgorithm();
                               subFactorizer.factorizeWithSteps(otherFactor); 
                               List<BigInteger> subFactors = subFactorizer.getFactors();
                               if (subFactors != null && !subFactors.isEmpty() && !subFactors.contains(otherFactor)) {
                                    
                                    for(BigInteger sf : subFactors) {
                                        if (!factors.contains(sf)) {
                                            factors.add(sf);
                                        }
                                    }
                               } else {
                                   
                                    if (!factors.contains(otherFactor)) {
                                         factors.add(otherFactor);
                                    }
                               }
                          }
                     }
                 }
                
                factors.sort(BigInteger::compareTo);
                
                 return steps;
            }
            
             if (d.equals(n) && stepCount < maxSteps) {
                 
                 System.out.println("НОД стало равно n на шаге " + stepCount + ". Перезапуск с c=2.");
                 steps.clear(); 
                 x = new BigInteger("2");
                 y = new BigInteger("2");
                 c = new BigInteger("2"); 
                 d = BigInteger.ONE;
                 stepCount = 0;
                 maxSteps = 300000;
             }
        }
         
         if (factors.isEmpty()) {
             
             
             System.out.println("Не удалось найти делители для " + n + " в пределах лимита шагов или с опробованными константами.");
             
             
             
         }
        return steps; 
    }
    
    public List<BigInteger> getFactors() {
        return factors;
    }
}
public class PollardRhoFrame extends JFrame {
    private JTextField inputField;
    private JButton factorButton;
    private JTable stepsTable;
    private DefaultTableModel tableModel;
    private JLabel resultLabel; 
    private PollardRhoAlgorithm algorithm; 
    private JLabel inputLabel; 
    
    private static final Color YA_OAK = new Color(114, 101, 80); 
    private static final Color APPLE_PIE = new Color(145, 185, 12); 
    private static final Color DARK_BLUE_BORDER = new Color(0, 150, 0); 
    
    private static final Font CUSTOM_FONT = new Font("Chancery", Font.PLAIN, 14);
    public PollardRhoFrame() {
        
        super("Факторизация Ро-методом Полларда");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550); 
        setLocationRelativeTo(null); 
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setLayout(new BorderLayout(15, 15)); 
        inputLabel = new JLabel("Введите число для факторизации:");
        inputField = new JTextField(20); 
        inputField.setBackground(YA_OAK); 
        inputField.setForeground(APPLE_PIE); 
        factorButton = new JButton("Факторизовать");
        factorButton.setBackground(YA_OAK); 
        factorButton.setOpaque(true);
        factorButton.setBorderPainted(false);
        factorButton.setFocusPainted(false); 
        factorButton.setForeground(APPLE_PIE); 
        resultLabel = new JLabel("Факторы: ");
        
        
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
        
        stepsTable.setBackground(YA_OAK); 
        stepsTable.setForeground(APPLE_PIE);    
        stepsTable.setShowGrid(true);
        stepsTable.setGridColor(APPLE_PIE); 
        
        stepsTable.setFont(new Font("Consolas", Font.BOLD, 15)); 
        
        stepsTable.getTableHeader().setBackground(YA_OAK); 
        stepsTable.getTableHeader().setForeground(APPLE_PIE);    
        stepsTable.getTableHeader().setFont(new Font("Consolas", Font.BOLD, 15));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumnModel columnModel = stepsTable.getColumnModel();
        
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
                CUSTOM_FONT, 
                Color.BLACK
            ),
            new EmptyBorder(5, 5, 5, 5)
        ));
        tableScrollPane.setBackground(APPLE_PIE);
        tableScrollPane.getViewport().setBackground(Color.BLACK);
        algorithm = new PollardRhoAlgorithm();
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 15));
        inputPanel.setBackground(APPLE_PIE);
        inputPanel.setOpaque(true);
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(factorButton);
        inputLabel.setFont(CUSTOM_FONT);
        inputField.setFont(CUSTOM_FONT);
        factorButton.setFont(CUSTOM_FONT);
        resultLabel.setFont(CUSTOM_FONT);
        getContentPane().setBackground(YA_OAK);
        ((JPanel) getContentPane()).setOpaque(true);
        factorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                factorizeNumber();
            }
        });
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        setVisible(true);
    }
    
    private void applyFontToAll(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyFontToAll(child, font);
            }
        }
    }
    private void factorizeNumber() {
        tableModel.setRowCount(0); 
        resultLabel.setText("Факторы: "); 
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