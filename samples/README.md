# Expression Parser Sample Files

This directory contains sample expression files to test the parser.

## Sample Files

| File | Expression | Description |
|------|------------|-------------|
| `01-simple-addition.txt` | `5 plus 3` | Basic addition |
| `02-simple-subtraction.txt` | `10 subtract 3` | Basic subtraction |
| `03-simple-multiplication.txt` | `7 multiply 6` | Basic multiplication |
| `04-simple-division.txt` | `20 divide 4` | Basic division |
| `05-mixed-operations.txt` | `5 plus 3 multiply 2` | Mixed operations (tests precedence) |
| `06-parentheses.txt` | `(5 plus 3) multiply 2` | Parentheses for grouping |
| `07-nested-parentheses.txt` | `((10 plus 5) multiply 2) subtract 3` | Nested parentheses |
| `08-decimal-numbers.txt` | `3.14 multiply 2.5` | Decimal number support |
| `09-complex-expression.txt` | `(100 divide 5) plus (3 multiply 7) subtract 2` | Complex expression |
| `10-operator-precedence.txt` | `2 plus 3 multiply 4 subtract 5 divide 2` | Operator precedence test |

## How to Use

### Test with GUI:
```bash
test-expression-gui.bat samples\01-simple-addition.txt
```

### Test with Tree Output:
```bash
test-expression-tree.bat samples\06-parentheses.txt
```

### Test with Maven:
```bash
mvn exec:java@test-gui -Dinput.file="samples/09-complex-expression.txt"
mvn exec:java@test-tree -Dinput.file="samples/10-operator-precedence.txt"
```

## Expected Results

### Example: `05-mixed-operations.txt` (5 plus 3 multiply 2)

**Parse Tree:**
```
(prog (expr (expr 5) plus (expr (expr 3) multiply (expr 2))) <EOF>)
```

This shows that multiplication has higher precedence than addition, so `3 multiply 2` is evaluated first.

### Example: `06-parentheses.txt` ((5 plus 3) multiply 2)

**Parse Tree:**
```
(prog (expr (expr ( (expr (expr 5) plus (expr 3)) )) multiply (expr 2)) <EOF>)
```

Parentheses force `5 plus 3` to be evaluated first.

## Operator Precedence

The grammar implements standard mathematical precedence:
1. **Highest:** Parentheses `( )`
2. **High:** `multiply`, `divide`
3. **Low:** `plus`, `subtract`

## Creating Your Own Samples

Create a text file with your expression:
```
echo 42 divide 6 plus 1 > samples\my-test.txt
test-expression-gui.bat samples\my-test.txt
```

## Grammar Rules

- **Numbers:** Integers or decimals (e.g., `5`, `3.14`)
- **Operators:** `plus`, `subtract`, `multiply`, `divide`
- **Grouping:** Use parentheses `( )` to override precedence
- **Whitespace:** Spaces are ignored