# Calculadora-Cientifica

# Calculadora de Números Complexos com Avaliação por AST (Java)

Uma aplicação robusta desenvolvida em Java (Swing) para o cálculo e análise estrutural de expressões matemáticas contendo números complexos. Além de operar como uma calculadora padrão, este projeto se destaca por implementar um interpretador de expressões (Parser) customizado utilizando a abordagem de **Descida Recursiva (Recursive Descent Parsing)** para construir e avaliar uma **Árvore de Sintaxe Abstrata (AST)**.

## Principais Funcionalidades e Diferenciais Técnicos

* **Motor Matemático Abrangente:** Suporta soma, subtração, multiplicação (propriedade distributiva), divisão (técnica do conjugado), raízes quadradas e potenciação utilizando a conversão para coordenadas polares (Fórmula de De Moivre).
* **Análise e Construção de AST:** A expressão digitada não é apenas calculada linearmente; ela é convertida em uma estrutura hierárquica de nós (AST) que respeita estritamente a precedência matemática dos operadores.
* **Resolução Dinâmica de Variáveis:** O sistema varre a expressão em busca de caracteres alfabéticos (ignorando a unidade imaginária `i`) e solicita dinamicamente, via interface, que o usuário atribua valores complexos a essas variáveis no momento da avaliação.
* **Pré-processamento Inteligente:** O parser identifica e injeta automaticamente operadores de multiplicação implícitos (ex: `2(x+i)` é pré-processado para `2*(x+i)`).
* **Tratamento de Ponto Flutuante:** O modelo contorna imprecisões decimais da linguagem implementando margens de tolerância de precisão (`1e-9`) nas comparações de igualdade e na formatação de saída.

## Arquitetura do Sistema e Classes

A base de código foi estruturada com alta coesão, separando a lógica matemática, a interpretação gramatical e a interface do usuário.

### 1. Núcleo Matemático (`Complex.java`)
A classe fundamental do projeto. Ela encapsula as propriedades de imutabilidade de um número complexo (partes `real` e `imag` marcadas como `final`).
* **Regras de Negócio:** Aplica fórmulas matemáticas avançadas. Por exemplo, o método `pow()` converte o número para a forma polar calculando o raio (hipotenusa) e o ângulo (arco tangente) para então aplicar o expoente e retornar à forma retangular.
* **Parsing de Entrada (`parse`):** Converte a entrada de texto do usuário em um objeto `Complex`. Trata omissões lógicas como "i" (implícito como 1.0 imaginário) e o sinal unário.

### 2. Motor de Avaliação AST (`ExpressionParser.java`)
O coração algorítmico do interpretador de expressões.
* **Construção da Árvore:** Segue a hierarquia lógica de avaliação: `Adição/Subtração` -> `Multiplicação/Divisão` -> `Potenciação` -> `Unidades Primárias` (variáveis, parênteses, raízes e números base).
* **Conversões de Visualização:** Permite extrair a árvore gerada tanto para o formato gráfico Swing (`getExecutionTree()`) quanto para o formato textual em LISP (`getLispTree()`).
* **Igualdade Estrutural:** O método `structurallyEquals` percorre duas árvores binárias recursivamente para garantir se duas expressões matemáticas digitadas produzem exatamente a mesma árvore de execução.

### 3. Interface e Controle Gráfico (`CalculatorGUI.java`)
Interface Desktop que faz a ponte entre o interpretador e o usuário.
* **Múltiplas Visões (`JTabbedPane`):** Separa o visor principal da calculadora, a representação visual da AST (`JTree`) e a saída da lógica em notação LISP.
* **Interatividade de Variáveis:** O método `collectVariables` utiliza `HashSet` para isolar variáveis únicas da string inserida, mapeando-as em um `HashMap<String, Complex>` através de requisições por pop-up (`JOptionPane`) antes do cálculo final.

### 4. Classe Utilitária (`ExpressionComparator.java`)
Utilitário auxiliar focado em sanitizar strings para verificar igualdade literal (baseada em texto, ignorando espaços), diferenciando-se da comparação estrutural focada em nós feita pelo Parser.

## Como Executar

1.  Pré-requisitos
JDK versão 8 ou superior.

2. Clone o código

3. Acesse o diretório do código-fonte

4. Compile todos os arquivos Java
  
5. Inicie a interface gráfica principal
java CalculatorGUI

## Exemplos de Comportamento do Interpretador
Aqui estão alguns cenários de como o motor lida com as entradas do usuário:

Entrada: 3 + 4i * 2

AST Interna: O parser processa 4i * 2 primeiro (maior precedência) antes de somar com 3.

Saída LISP: (+ 3 (* 4i 2))

Resultado: 3.0000 + 8.0000i

<img width="786" height="692" alt="Gravando 2026-06-16 201018" src="https://github.com/user-attachments/assets/9b34649c-90dc-4d19-84d8-8105b40daf0c" />


Entrada com Variáveis: x^2 + y

Ação do Sistema: Identifica x e y. Pede os valores via caixa de diálogo. (Ex: se x = i e y = 5).

Resultado: i^2 gera -1, somado a 5, retorna 4.0000.

<img width="786" height="692" alt="Gravando 2026-06-16 201223" src="https://github.com/user-attachments/assets/0cde1cd6-7137-4463-94d4-5f5521e6bf9d" />


### Comparação de Expressões (Botão ==): 
Se você comparar 2 + 3 e 3 + 2, o sistema avaliará como diferentes sob a ótica estrutural da Árvore, pois o nó à esquerda do operador raiz + difere entre elas.

<img width="328" height="170" alt="Gravando 2026-06-16 200814" src="https://github.com/user-attachments/assets/93f6674f-174a-418a-b18d-7e8ca1959579" />


Estruturas de Dados Utilizadas
Árvores Binárias (Custom Node): Utilizada no ExpressionParser para mapear operandos nos nós "folha" e operadores nos nós "pai/raiz".

Mapas (HashMap): Armazenamento em memória com complexidade O(1) para buscar o valor das variáveis digitadas na hora do parse.

Conjuntos (HashSet): Prevenção de duplicidade ao catalogar variáveis na interface gráfica.
