import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorGUI extends JFrame implements ActionListener {

    private JTextField tela;
    private final ScriptEngine scriptEngine;

    private final String[] botoes = {
            "sin", "cos", "tan", "log", "(",
            "7", "8", "9", "/", "C",
            "4", "5", "6", "*", "√",
            "1", "2", "3", "-", "^", // O botão ^ insere **
            "0", ".", "=", "+", ")",
    };

    public CalculatorGUI() {
        super("Calculadora Científica");

        ScriptEngineManager manager = new ScriptEngineManager();
        scriptEngine = manager.getEngineByName("JavaScript");

        if (scriptEngine == null) {
            JOptionPane.showMessageDialog(this,
                    "Erro: O motor JavaScript (ScriptEngine) não foi encontrado.\n" +
                            "Por favor, use Java 8 ou Java 11.",
                    "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Calculadora Científica");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        tela = new JTextField();
        tela.setEditable(false);
        tela.setFont(new Font("Arial", Font.BOLD, 32));
        tela.setHorizontalAlignment(SwingConstants.RIGHT);
        tela.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(tela, BorderLayout.NORTH);

        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(5, 5, 8, 8));

        for (String texto : botoes) {
            JButton botao = new JButton(texto);
            botao.setFont(new Font("Arial", Font.BOLD, 20));
            botao.addActionListener(this);

            if (Objects.equals(texto, "=")) {
                botao.setBackground(new Color(0, 128, 255));
                botao.setForeground(Color.WHITE);
            } else if (texto.length() > 1 || !Character.isDigit(texto.charAt(0)) && !Objects.equals(texto, ".")) {
                botao.setBackground(new Color(200, 200, 200));
            }

            painel.add(botao);
        }

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(painel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        String textoAtual = tela.getText();

        if (comando.equals("C")) {
            tela.setText("");
        } else if (comando.equals("=")) {
            if (scriptEngine == null || textoAtual.isEmpty()) return;

            try {
                // 1. Prepara a expressão (Converte ** para Math.pow, Log, etc.)
                String expressao = prepararExpressao(textoAtual);

                // 2. Avalia a expressão
                Object resultado = scriptEngine.eval(expressao);

                // 3. Formatação
                String resultadoFormatado = resultado.toString().endsWith(".0")
                        ? resultado.toString().substring(0, resultado.toString().length() - 2)
                        : resultado.toString();

                tela.setText(resultadoFormatado);

            } catch (ScriptException ex) {
                tela.setText("Erro de Sintaxe!");
                System.err.println("Erro de Sintaxe no cálculo: " + ex.getMessage());
            } catch (Exception ex) {
                tela.setText("Erro");
                System.err.println("Erro Inesperado: " + ex.getMessage());
            }

        } else if (comando.equals("√")) {
            tela.setText(textoAtual + "Math.sqrt(");
        } else if (comando.equals("^")) {

            tela.setText(textoAtual + "**");
        } else if (comando.equals("log") || comando.equals("sin") || comando.equals("cos") || comando.equals("tan")) {
            tela.setText(textoAtual + comando + "(");
        }
        else {
            tela.setText(textoAtual + comando);
        }
    }

    /**
     * Converte as funções do usuário para o formato que o JavaScript entende.
     */
    private String prepararExpressao(String expressao) {
        String resultado = expressao;


        resultado = resultado.replaceAll("(.+?)\\*\\*(.+?)", "Math.pow($1,$2)");

        resultado = resultado.replaceAll("log\\(([^)]+)\\)",
                "(Math.log($1) / Math.log(10))");

        resultado = resultado.replaceAll("sin\\(([^)]+)\\)",
                "Math.sin( ($1) * (Math.PI/180) )");

        resultado = resultado.replaceAll("cos\\(([^)]+)\\)",
                "Math.cos( ($1) * (Math.PI/180) )");

        resultado = resultado.replaceAll("tan\\(([^)]+)\\)",
                "Math.tan( ($1) * (Math.PI/180) )");

        return resultado;
    }


    public static void main(String[] args){
        SwingUtilities.invokeLater(() ->{
            new CalculatorGUI().setVisible(true);
        });
    }
}