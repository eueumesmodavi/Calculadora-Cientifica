// Classe utilitária para comparar expressões matemáticas em formato de texto
public class ExpressionComparator {

    // Verifica se duas expressões são iguais, ignorando apenas os espaços em branco
    public static boolean areExpressionsEqual(String expr1, String expr2) {

        // Pega a primeira expressão e remove todos os espaços (" ")
        String e1 = expr1.replace(" ", "");

        // Pega a segunda expressão e também remove todos os espaços (" ")
        String e2 = expr2.replace(" ", "");

        // Compara as duas strings limpas. 
        // Retorna 'true' se forem exatamente iguais (mesmos caracteres na mesma ordem), senão 'false'
        return e1.equals(e2);
    }
}