import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class CalculatorGUI extends JFrame implements ActionListener {

    private JTextField tela;
    private JTree arvoreExecucao;
    private JScrollPane scrollArvore;
    private JPanel painelPrincipal;
    private JTabbedPane abas;
    private JTextArea lispArea;

    private final String[] botoes = {
            " ", " ", " ", "Conj", "C",
            "x", "y", "z", "(", ")",
            "7", "8", "9", "/", "*",
            "4", "5", "6", "+", "-",
            "1", "2", "3", "i", ".",
            "^", "√", "0", "=", "==",
    };

    /**
     * Construtor da classe CalculatorGUI.
     * Responsável por inicializar as configurações da janela principal e montar
     * as abas da interface (Calculadora, Árvore de Execução e formato LISP).
     */
    public CalculatorGUI() {
        super("Calculadora de Complexos");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        abas = new JTabbedPane();

        // Inicializa a aba principal da calculadora
        painelPrincipal = criarPainelCalculadora();
        abas.add("Calculadora", painelPrincipal);

        // Aba da Árvore Swing (mostra a hierarquia das operações)
        arvoreExecucao = new JTree(new DefaultMutableTreeNode("Nenhuma expressão avaliada"));
        scrollArvore = new JScrollPane(arvoreExecucao);
        abas.add("Árvore", scrollArvore);

        // Aba LISP (mostra a expressão em formato de texto estruturado)
        lispArea = new JTextArea();
        lispArea.setEditable(false);
        JScrollPane scrollLisp = new JScrollPane(lispArea);
        abas.add("LISP", scrollLisp);

        add(abas);
    }

    /**
     * Cria o painel visual da calculadora, incluindo o visor (JTextField)
     * e a grade de botões para inserção de números e operações.
     * * @return JPanel configurado com o layout da calculadora.
     */
    private JPanel criarPainelCalculadora() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));

        // Configuração do visor onde as expressões aparecerão
        tela = new JTextField();
        tela.setEditable(false);
        tela.setFont(new Font("Arial", Font.BOLD, 32));
        tela.setHorizontalAlignment(SwingConstants.RIGHT);
        tela.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(tela, BorderLayout.NORTH);

        // Configuração da grade de botões (6 linhas, 5 colunas)
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(6, 5, 8, 8));

        for (String texto : botoes) {
            JButton botao = new JButton(texto);
            botao.setFont(new Font("Arial", Font.BOLD, 20));
            botao.addActionListener(this);

            // Estilização customizada para o botão de "=" e para botões de operações/letras
            if (Objects.equals(texto, "=")) {
                botao.setBackground(new Color(0, 128, 255));
                botao.setForeground(Color.WHITE);
            } else if (texto.length() > 1 || (!Character.isDigit(texto.charAt(0)) && !Objects.equals(texto, "."))) {
                botao.setBackground(new Color(200, 200, 200));
            }
            painelBotoes.add(botao);
        }

        painel.add(painelBotoes, BorderLayout.CENTER);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return painel;
    }

    /**
     * Analisa a expressão matemática atual em busca de variáveis alfabéticas
     * (ignorando o 'i' que representa o número imaginário). Em seguida, pede
     * ao usuário para inserir o valor para cada variável encontrada.
     * * @param expression A expressão matemática sendo avaliada.
     * @return Um mapa (Map) ligando o nome da variável ao seu valor Complexo, ou null em caso de erro ou cancelamento.
     */
    private Map<String, Complex> collectVariables(String expression) {
        Set<String> variableNames = new HashSet<>();
        Map<String, Complex> variableMap = new HashMap<>();

        // Percorre a expressão procurando por letras para identificar variáveis
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isLetter(c)) {
                if (c == 'i' || c == 'I') continue; // Ignora a unidade imaginária
                StringBuilder sb = new StringBuilder();
                int j = i;
                while (j < expression.length() && Character.isLetter(expression.charAt(j))) {
                    sb.append(expression.charAt(j));
                    j++;
                }
                String var = sb.toString();
                variableNames.add(var);
                i = j - 1;
            }
        }

        // Abre pop-ups pedindo os valores de cada variável encontrada
        for (String varName : variableNames) {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Digite o valor complexo para a variável " + varName + " (Ex: 3+2i, -5, 1i, i)",
                    "Entrada de Variável",
                    JOptionPane.QUESTION_MESSAGE
            );

            // Cancela o processo se o usuário não digitar nada
            if (input == null || input.trim().isEmpty()) {
                return null;
            }

            // Tenta converter o valor digitado para a classe Complex
            try {
                Complex value = Complex.parse(input);
                variableMap.put(varName, value);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Valor complexo inválido para " + varName + ". Tente novamente.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                return null;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro inesperado ao analisar " + varName + ". Tente novamente.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        return variableMap;
    }

    /**
     * Solicita ao usuário duas expressões diferentes e verifica se as suas
     * Árvores de Sintaxe Abstrata (AST) são estruturalmente idênticas.
     */
    private void compararExpressoes() {
        String expr1 = JOptionPane.showInputDialog(this, "Digite a primeira expressão:");
        if (expr1 == null) return;

        String expr2 = JOptionPane.showInputDialog(this, "Digite a segunda expressão:");
        if (expr2 == null) return;

        try {
            // Cria parsers e constrói as árvores para as duas expressões
            ExpressionParser p1 = new ExpressionParser(expr1, new HashMap<>());
            p1.evaluate(); // Constrói a AST
            ExpressionParser p2 = new ExpressionParser(expr2, new HashMap<>());
            p2.evaluate();

            // Compara estruturalmente as duas árvores criadas
            boolean iguais = p1.structurallyEquals(p2);

            JOptionPane.showMessageDialog(this,
                    iguais ? "As expressões são ESTRUTURALMENTE iguais."
                            : "As expressões NÃO são iguais.");
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao comparar expressões:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Método acionado sempre que um botão da calculadora é clicado.
     * Direciona a ação baseada no texto do botão clicado (limpar, calcular, adicionar letra/número, etc.).
     * * @param e O evento de clique capturado pela interface.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        String textoAtual = tela.getText();

        if (comando.equals("C")) {
            // Limpa o visor
            tela.setText("");
        } else if (comando.equals("=")) {
            // Inicia o processo de avaliação matemática da expressão
            if (textoAtual.isEmpty()) return;

            Map<String, Complex> vars = collectVariables(textoAtual);
            if (vars == null) return;

            try {
                ExpressionParser parser = new ExpressionParser(textoAtual, vars);
                Complex resultado = parser.evaluate();
                tela.setText(resultado.toString());

                // Atualiza a visualização gráfica da árvore de execução
                DefaultMutableTreeNode raiz = parser.getExecutionTree();
                arvoreExecucao.setModel(new DefaultTreeModel(raiz));

                // Expande toda a árvore para facilitar visualização
                for (int i = 0; i < arvoreExecucao.getRowCount(); i++) {
                    arvoreExecucao.expandRow(i);
                }

                // Atualiza a aba com a formatação em texto LISP
                String lisp = parser.getLispTree();
                lispArea.setText(lisp);

                // Muda o foco automaticamente para a aba da Árvore
                abas.setSelectedIndex(1);

            } catch (Exception ex) {
                tela.setText("Erro");
                ex.printStackTrace();
            }

        } else if (comando.equals("i")) {
            tela.setText(textoAtual + "i");
        } else if ("xyz".contains(comando)) {
            // Adiciona variáveis simples ao visor
            tela.setText(textoAtual + comando);
        } else if ("log sin cos tan".contains(comando)) {
            // Adiciona funções trigonométricas abrindo parênteses
            tela.setText(textoAtual + comando + "(");
        } else if (comando.equals("==")) {
            // Dispara a funcionalidade de comparação de expressões
            compararExpressoes();
        } else if (comando.equals("Conj")) {
            // Calcula o conjugado de uma expressão avaliada
            try {
                textoAtual = tela.getText();
                if (textoAtual.isEmpty()) return;

                Map<String, Complex> vars = collectVariables(textoAtual);
                if (vars == null) return;

                ExpressionParser parser = new ExpressionParser(textoAtual, vars);
                Complex resultado = parser.evaluate();

                Complex conj = resultado.conjugate(); // Aplica a operação matemática de conjugado

                tela.setText(conj.toString());

                // Atualiza a árvore englobando-a em um nó "conjugado"
                DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("conjugado");
                raiz.add(parser.getExecutionTree());
                arvoreExecucao.setModel(new DefaultTreeModel(raiz));

                abas.setSelectedIndex(1);

            } catch (Exception ex) {
                tela.setText("Erro");
            }
        }
        else {
            // Qualquer outro caractere (números, operadores matemáticos) é apenas adicionado ao visor
            tela.setText(textoAtual + comando);
        }
    }

    /**
     * Ponto de entrada (entry point) principal do programa.
     * Inicia a Interface Gráfica na Thread correta (Event Dispatch Thread).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorGUI().setVisible(true));
    }
}