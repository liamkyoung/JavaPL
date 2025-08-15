# 🚀 JavaPL - Custom Programming Language

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)
[![Academic](https://img.shields.io/badge/Academic-Project-green?style=flat-square)](https://github.com/liamkyoung/JavaPL)

> A custom programming language implementation built from the ground up in Java, featuring a complete lexer, parser, AST, and interpreter. Developed as part of Programming Language Concepts coursework (2021).

## 📋 Table of Contents

- [Overview](#-overview)
- [Language Features](#-language-features)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Language Syntax](#-language-syntax)
- [Examples](#-examples)
- [Implementation Details](#-implementation-details)
- [Development](#-development)
- [Contributing](#-contributing)

## 🎯 Overview

JavaPL is a custom interpreted programming language designed to demonstrate fundamental compiler and interpreter construction concepts. This project implements a complete language processing pipeline from source code to execution, showcasing modern language design principles and implementation techniques.

### 🎓 Educational Objectives

- **Lexical Analysis**: Tokenization of source code into meaningful symbols
- **Syntax Analysis**: Parsing tokens into an Abstract Syntax Tree (AST)
- **Semantic Analysis**: Type checking and symbol table management
- **Code Interpretation**: Tree-walking interpreter for program execution
- **Error Handling**: Comprehensive error reporting and recovery

## ✨ Language Features

### Core Language Constructs
- **Variables & Data Types**: Integers, floats, strings, booleans
- **Operators**: Arithmetic, logical, comparison, and assignment operators
- **Control Flow**: if/else statements, while loops, for loops
- **Functions**: User-defined functions with parameters and return values
- **Scope Management**: Lexical scoping with nested environments
- **Built-in Functions**: I/O operations and utility functions

### Advanced Features
- **Dynamic Typing**: Runtime type checking and coercion
- **First-Class Functions**: Functions as values (closures)
- **Recursive Functions**: Support for recursive function calls
- **Error Handling**: Runtime error detection and reporting
- **Interactive REPL**: Read-Eval-Print Loop for testing

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Source Code   │───▶│     Lexer       │───▶│     Tokens      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
┌─────────────────┐    ┌─────────────────┐    ┌─────────▼─────────┐
│   Interpreter   │◀───│       AST       │◀───│     Parser      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │
         ▼
┌─────────────────┐
│    Execution    │
└─────────────────┘
```

### Core Components

| Component | Responsibility |
|-----------|----------------|
| **Lexer** | Converts source code into tokens |
| **Parser** | Builds Abstract Syntax Tree from tokens |
| **AST Nodes** | Represents program structure |
| **Interpreter** | Executes the AST |
| **Environment** | Manages variable scoping |
| **Error Handler** | Reports syntax and runtime errors |

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven or Gradle (for dependency management)
- IDE with Java support (IntelliJ IDEA, Eclipse, VS Code)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/liamkyoung/JavaPL.git
   cd JavaPL
   ```

2. **Compile the project**
   ```bash
   javac -d bin src/**/*.java
   ```

3. **Run the interpreter**
   ```bash
   # Interactive REPL mode
   java -cp bin Main

   # Execute a file
   java -cp bin Main examples/hello.jpl
   ```

### Quick Example

Create a file `hello.jpl`:
```javascript
// Hello World in JavaPL
print("Hello, JavaPL!");

// Variables and arithmetic
var x = 10;
var y = 20;
print("Sum: " + (x + y));

// Functions
fun greet(name) {
    return "Hello, " + name + "!";
}

print(greet("World"));
```

Run it:
```bash
java -cp bin Main hello.jpl
```

## 📝 Language Syntax

### Variables
```javascript
var name = "JavaPL";
var version = 1.0;
var isActive = true;
```

### Control Flow
```javascript
// Conditional statements
if (x > 0) {
    print("Positive");
} else {
    print("Non-positive");
}

// Loops
for (var i = 0; i < 10; i = i + 1) {
    print(i);
}

while (condition) {
    // loop body
}
```

### Functions
```javascript
// Function declaration
fun factorial(n) {
    if (n <= 1) {
        return 1;
    }
    return n * factorial(n - 1);
}

// Function call
var result = factorial(5);
print("5! = " + result);
```

### Built-in Functions
```javascript
print(value)        // Output to console
input()             // Read user input
typeof(value)       // Get type of value
toString(value)     // Convert to string
toNumber(value)     // Convert to number
```

## 💡 Examples

### Fibonacci Sequence
```javascript
fun fibonacci(n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2);
}

for (var i = 0; i < 10; i = i + 1) {
    print("fib(" + i + ") = " + fibonacci(i));
}
```

### Higher-Order Functions
```javascript
fun map(array, fn) {
    var result = [];
    for (var i = 0; i < length(array); i = i + 1) {
        append(result, fn(get(array, i)));
    }
    return result;
}

fun double(x) {
    return x * 2;
}

var numbers = [1, 2, 3, 4, 5];
var doubled = map(numbers, double);
print(doubled); // [2, 4, 6, 8, 10]
```

## 🔧 Implementation Details

### Lexer (Tokenizer)
- **Token Types**: Keywords, identifiers, literals, operators, delimiters
- **Regular Expressions**: Pattern matching for different token types
- **Error Handling**: Invalid character detection and reporting

### Parser
- **Grammar**: Recursive descent parser with precedence climbing
- **AST Generation**: Creates typed nodes for different language constructs
- **Error Recovery**: Synchronization points for continued parsing after errors

### AST Nodes
```java
abstract class Expr {
    interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitUnaryExpr(Unary expr);
        R visitLiteralExpr(Literal expr);
        // ... other visit methods
    }
}

class Binary extends Expr {
    final Expr left;
    final Token operator;
    final Expr right;
    // ...
}
```

### Interpreter
- **Visitor Pattern**: Clean separation of tree traversal and operations
- **Environment Chaining**: Lexical scoping implementation
- **Runtime Type Checking**: Dynamic type validation during execution

### Error Handling
- **Syntax Errors**: Detailed parse error messages with line numbers
- **Runtime Errors**: Type mismatches, undefined variables, division by zero
- **Error Recovery**: Graceful handling without crashing

## 🛠️ Development

### Running Tests
```bash
# Compile test files
javac -d bin src/**/*.java test/**/*.java

# Run test suite
java -cp bin TestRunner
```

### Adding New Features

1. **Add Token Types** (if needed) in `TokenType.java`
2. **Update Lexer** to recognize new syntax in `Lexer.java`
3. **Extend Grammar** in `Parser.java`
4. **Create AST Nodes** for new constructs
5. **Implement Interpreter Logic** in `Interpreter.java`
6. **Add Tests** for the new feature

## 🎓 Learning Outcomes

This project demonstrates:

- **Compiler Design Principles**: Understanding of language processing pipeline
- **Object-Oriented Design**: Clean architecture with design patterns
- **Recursive Algorithms**: Parser implementation and tree traversal
- **Error Handling**: Robust error detection and reporting
- **Software Engineering**: Code organization and testing practices

## 📚 References

- **Crafting Interpreters** by Robert Nystrom
- **Compilers: Principles, Techniques, and Tools** (Dragon Book)
- **Modern Compiler Implementation in Java** by Andrew Appel
- **Programming Language Pragmatics** by Michael Scott

## 🤝 Contributing

This is an academic project, but contributions for educational purposes are welcome:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


**Built with ❤️ as part of Programming Language Concepts coursework**

*This project showcases the fundamental concepts of programming language implementation, from lexical analysis to code execution, demonstrating both theoretical understanding and practical implementation skills.*
