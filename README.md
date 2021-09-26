# Mala
#### A simple dynamic programming language inspired by Lua and JavaScript. Interpreted through a stack-based VM.

### Truthy/Falsy Values

| Value            | Is Truthy?           |
|------------------|----------------------|
| true             | yes (duh)            |
| false            | no (also duh)        |
| any number value | yes                  |
| null             | no                   |
| strings          | yes, even empty ones |

### Basic Syntax

#### Variables
```
let x = 5 // number
let y = .5 // number
const str = "Hello, World!" // constant string
str = "Goodbye, World!" // errors
```

#### Functions
```
// regular functions
function inc(a) {
  return a + 1
}

// lambda functions
let lamdaInc = lamda x -> x + 1

inc(1) // 2
lambda_inc(1) // 2
```

#### Conditionals
```
// if statements
if (1 == 2) {  // parenthesis enclosing expression are OPTIONAL

}

if (true && true) {

}

if (true || false) {

}

// might implement ternary operators, but not too sure
```
