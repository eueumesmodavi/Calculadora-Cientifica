import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorGUI extends javax.swing.JFrame implements ActionListener {

    private JTextField tela;
    private double num1 = 0, num2 = 0, resultado = 0;
    private char operator;
    private final String[] botoes = {
            "7","8","9","/","√",
            "4","5","6","*","x",
            "1","2","3","-","(",
            "0",".","=","+",")",
            "C"
    };

    public CalculatorGUI() {
        super("Calculator");

        setTitle("Calculator");
        setSize(600, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(30, 30));

        tela = new JTextField();
        tela.setEditable(false);
        tela.setFont(new Font("Arial", Font.BOLD, 30));
        tela.setHorizontalAlignment(SwingConstants.RIGHT);
        add(tela, BorderLayout.NORTH);

        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(5, 4, 5, 5));

        for(String texto:botoes){
            JButton botao = new JButton(texto);
            botao.setFont(new Font("Arial", Font.BOLD, 20));
            botao.addActionListener(this);
            painel.add(botao);
        }
        add(painel, BorderLayout.CENTER);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (comando.charAt(0) >= '0' && comando.charAt(0) <= '9' || comando.equals(".")) {
            tela.setText(tela.getText() + comando);
        }else if (comando.equals("C")){
            tela.setText("");
            num1 = num2 = resultado = 0;
        }else if(comando.equals("=")){
            num2 = Double.parseDouble(tela.getText());

            switch (operator) {
                case '+': resultado = num1 + num2; break;
                case '-': resultado = num1 - num2; break;
                case '*': resultado = num1 * num2; break;
                case '/': resultado = num2 != 0 ? num1/num2 : 0 ; break;
                case '√': resultado = Math.sqrt(num1); break;
                case 'x': resultado = Math.pow(num1, num2); break;
            }

            tela.setText(String.valueOf(resultado));
            num1 = resultado;
        }else {
            if(!tela.getText().isEmpty()){
                num1 = Double.parseDouble(tela.getText());
                operator = comando.charAt(0);
                tela.setText("");
            }
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() ->{
            new CalculatorGUI().setVisible(true);
        });
    }
}
