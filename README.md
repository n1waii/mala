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
Please note not all these features are implemented and not guaranteed to be implemented. This file is only serving as a guideline for me.

#### Variables
```js
let x = 5 // number
let y = .5 // number
let list = ["foo", "bar"]

log(list[0]) // "foo"
log(list[#list]) // "bar"

const str = "Hello, World!" // constant string
str = "Goodbye, World!" // errors
```

#### Functions
```js
// regular functions
func inc(a) {
  return a + 1
}

// lambda functions
let lamdaInc = lamda x -> x + 1

inc(1) // 2
lambda_inc(1) // 2
```

#### Conditionals
```lua
// if statements
if (1 == 2) {  // parenthesis enclosing expression are OPTIONAL

}

if (true and true) {

}

if (true or false) {

}

if (not false) {

}

```

#### Loops
```js
// numerical loops
// for VAR in [START..END], STEP = 1
for let i in [1..5, 1] {
  log(i) // prints 1 to 5, both inclusive
}

// key-value loops
let LIST = ["a", "b", "c"]

// for VAR [K, V?] of LIST
for let [K, V] of LIST {
  log(K, V) // prints index and value of each element
}
```
