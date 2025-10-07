import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double numero1, numero2, resultado;
        char operacao;

        System.out.println("--- CALCULADORA SIMPLES JAVA ---");

        System.out.print("Digite o primeiro número: ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            scanner.next();
            System.out.print("Digite o primeiro número: ");
        }
        numero1 = scanner.nextDouble();

        System.out.print("Escolha a operação (+, -, *, /): ");
        operacao = scanner.next().charAt(0);

        System.out.print("Digite o segundo número: ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            scanner.next();
            System.out.print("Digite o segundo número: ");
        }
        numero2 = scanner.nextDouble();

        switch (operacao) {
            case '+':
                resultado = numero1 + numero2;
                System.out.println(numero1 + " + " + numero2 + " = " + resultado);
                break;
            case '-':
                resultado = numero1 - numero2;
                System.out.println(numero1 + " - " + numero2 + " = " + resultado);
                break;
            case '*':
                resultado = numero1 * numero2;
                System.out.println(numero1 + " * " + numero2 + " = " + resultado);
                break;
            case '/':
                if (numero2 != 0) {
                    resultado = numero1 / numero2;
                    System.out.println(numero1 + " / " + numero2 + " = " + resultado);
                } else {
                    System.out.println("Erro: Divisão por zero não é permitida.");
                }
                break;
            default:
                System.out.println("Erro: Operação inválida. Por favor, use +, -, *, ou /.");
                break;
        }

        scanner.close();
    }
}