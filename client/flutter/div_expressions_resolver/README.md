# div_expressions_resolver
[![Pub](https://img.shields.io/pub/v/div_expressions_resolver.svg)](https://pub.dartlang.org/packages/div_expressions_resolver)
DivKit expressions resolver plugin for Flutter client.

## Overview
This library provides a wrapper around native implementation of ExpressionResolver for iOS and Evaluator for Android.

## Features
The calculated part of the expression is written using the @{} construction and may contain variables, operators, and functions.

Calculated expressions support the following constructions:
- Logical operators: ==, !=, !, >, >=, <, =, &&, ||.
- Arithmetic operators: +, -, *, /, %.
- Ternary operator: counter > 0 ? true : false.
- Grouping logical expressions: logging_enabled && (user_name == 'John' || user_email != '').
- Function calls: mult(total_price, .83)
- Using calculated parts within strings and string-type calculated expressions: "mail: @{'@{username}@ya.ru'}" is converted to mail: john@ya.ru

So fully supports of DivKit specification: 
- https://divkit.tech/docs/en/concepts/expressions
- https://divkit.tech/docs/en/concepts/functions

## Usage
```dart
    final nativeResolver = NativeDivExpressionsResolver();
    
    // Use simple math
    fianl r1 = await nativeResolver.resolve( // Result: 1
        "@{0 + 1}",
        context: {},
    );

    // Use variables
    fianl r2 = await nativeResolver.resolve( // Result: 2
        "@{a + b}",
        context: {
            "a" : 1,
            "b" : 1,
        },
    );

    // Use embedded functions
    fianl r3 = await nativeResolver.resolve( // Result: 3
        "@{sum(a, b)}",
        context: {
            "a" : 1,
            "b" : 2,
        },
    );
    
    // Use branching and more!
    fianl r3 = await nativeResolver.resolve( // Result: 4
        "@{sum(a, b) == 5 ? 4 : 0}",
        context: {
            "a" : 3,
            "b" : 2,
        },
    );
```