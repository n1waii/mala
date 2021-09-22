# Mala
#### A simple dynamic programming language inspired by Lua and JavaScript. Interpreted through a stack-based VM.

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
func inc(a) {
  return a + 1
}

// lambda functions
let lamdaInc = lamda x -> x + 1

inc(1) // 2
lambda_inc(1) // 2
```
