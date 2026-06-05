import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Classe que representa um número complexo (composto por parte real e imaginária)
// e fornece os métodos para realizar operações matemáticas com ele.
public class Complex {
    private final double real;
    private final double imag;

    // Construtor: inicializa o número complexo com suas partes real e imaginária
    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    // Métodos para obter os valores das partes (Getters)
    public double getReal() { return real; }
    public double getImag() { return imag; }

    // Soma este número complexo com outro (soma reais com reais, imaginários com imaginários)
    public Complex plus(Complex b) {
        return new Complex(real + b.real, imag + b.imag);
    }

    // Subtrai outro número complexo deste
    public Complex minus(Complex b) {
        return new Complex(real - b.real, imag - b.imag);
    }

    // Multiplica este número complexo por outro aplicando a propriedade distributiva
    public Complex times(Complex b) {
        double novoReal = (real * b.real) - (imag * b.imag);
        double novoImag = (real * b.imag) + (imag * b.real);
        return new Complex(novoReal, novoImag);
    }

    // Multiplica o número complexo por um número real (escalar)
    public Complex scale(double alpha) {
        return new Complex(real * alpha, imag * alpha);
    }

    // Divide este número complexo por outro utilizando a técnica do conjugado
    public Complex divide(Complex b) {
        double divisor = (b.real * b.real) + (b.imag * b.imag);
        if (divisor == 0.0) {
            throw new ArithmeticException("Divisão por zero no número complexo.");
        }
        Complex numerador = this.times(b.conjugate());
        return new Complex(numerador.real / divisor, numerador.imag / divisor);
    }

    // Retorna o conjugado do complexo (inverte o sinal da parte imaginária)
    public Complex conjugate() {
        return new Complex(real, -imag);
    }

    // Calcula a potência do número complexo utilizando sua forma polar (fórmula de De Moivre)
    public Complex pow(double exponent) {
        if (real == 0 && imag == 0 && exponent > 0) return new Complex(0, 0);

        // Converte para coordenadas polares (raio e ângulo/theta)
        double r = Math.sqrt(real * real + imag * imag);
        double theta = Math.atan2(imag, real);

        // Aplica a potência na forma polar
        double novoR = Math.pow(r, exponent);
        double novoTheta = theta * exponent;

        // Converte de volta para a forma retangular (real e imaginário)
        double novoReal = novoR * Math.cos(novoTheta);
        double novoImag = novoR * Math.sin(novoTheta);

        return new Complex(novoReal, novoImag);
    }

    // Calcula a raiz quadrada de um número real, retornando um Complexo (útil para raízes negativas)
    public static Complex sqrt(double x){
        if(x >= 0){
            return new Complex(Math.sqrt(x), 0);
        }else{
            return new Complex(0, Math.sqrt(-x));
        }
    }

    // Método auxiliar interno: extrai o valor numérico de um pedaço de string (ex: transforma "-i" em "-1.0")
    private static double parseComponentValue(String s) {
        s = s.replaceAll("\\s+", "");
        if (s.isEmpty()) return 0.0;

        // Remove a letra "i" para sobrar apenas o número
        String numStr = s.replaceAll("[iI]", "");

        // Trata os casos onde o número é implícito (ex: "i" = 1, "-i" = -1)
        if (numStr.isEmpty()) {
            if (s.contains("+")) return 1.0;
            if (s.contains("-")) return -1.0;
            return 1.0;
        }

        if (numStr.equals("+")) return 1.0;
        if (numStr.equals("-")) return -1.0;

        try {
            return Double.parseDouble(numStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Componente numérico inválido: " + s);
        }
    }

    // Converte uma string digitada pelo usuário (ex: "3+4i", "-5", "2i") em um objeto Complex
    public static Complex parse(String s) {
        String cleanS = s.replaceAll("\\s+", "");
        if (cleanS.isEmpty()) throw new IllegalArgumentException("Entrada vazia.");

        // Caso seja apenas um número imaginário (ex: "4i" ou "-3i")
        if (cleanS.endsWith("i") || cleanS.endsWith("I")) {
            if (cleanS.indexOf('+') == -1 && cleanS.indexOf('-', 1) == -1) {
                double imag = parseComponentValue(cleanS);
                return new Complex(0.0, imag);
            }
        }

        // Caso seja apenas um número real sem a letra "i" (ex: "5.5")
        if (!cleanS.contains("i") && !cleanS.contains("I")) {
            try {
                double real = Double.parseDouble(cleanS);
                return new Complex(real, 0.0);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Formato inválido: " + s);
            }
        }

        // Procura onde está o sinal de + ou - que separa a parte real da imaginária
        int splitIndex = -1;
        for (int i = 1; i < cleanS.length(); i++) {
            char c = cleanS.charAt(i);
            // Ignora o 'e' ou 'E' para não quebrar números em notação científica (ex: 1e-9)
            if ((c == '+' || c == '-') && (cleanS.charAt(i-1) != 'e' && cleanS.charAt(i-1) != 'E')) {
                splitIndex = i;
                break;
            }
        }

        // Divide a string em duas metades baseadas no sinal encontrado
        String realStr, imagStr;
        if (splitIndex != -1) {
            realStr = cleanS.substring(0, splitIndex);
            imagStr = cleanS.substring(splitIndex);
        } else {
            throw new IllegalArgumentException("Formato do número complexo inválido: " + s);
        }

        // Converte as duas metades para double
        double realPart = parseComponentValue(realStr);
        double imagPart = parseComponentValue(imagStr);

        return new Complex(realPart, imagPart);
    }

    // Formata o número complexo em texto visualmente limpo e familiar para o usuário
    @Override
    public String toString() {
        // Se não tiver parte imaginária, mostra só o real
        if (Math.abs(imag) < 1e-9) {
            return String.format("%.4f", real);
        }
        // Se não tiver parte real, mostra só a imaginária
        if (Math.abs(real) < 1e-9) {
            if (Math.abs(imag - 1.0) < 1e-9) return "i";
            if (Math.abs(imag + 1.0) < 1e-9) return "-i";
            return String.format("%.4fi", imag);
        }

        // Mostra o número completo, ajustando o sinal do meio caso o imaginário seja negativo
        if (imag < 0) {
            return String.format("%.4f - %.4fi", real, -imag);
        }
        return String.format("%.4f + %.4fi", real, imag);
    }

    // Compara dois números complexos, usando uma margem de tolerância (1e-9) para evitar falhas por precisão decimal
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Complex complex = (Complex) obj;
        return Math.abs(real - complex.real) < 1e-9 && Math.abs(imag - complex.imag) < 1e-9;
    }
}