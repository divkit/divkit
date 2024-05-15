# div_expressions_resolver
[![Pub](https://img.shields.io/pub/v/div_expressions_resolver.svg)](https://pub.dartlang.org/packages/div_expressions_resolver)
DivKit expressions resolver plugin for Flutter client.

## Overview
This library provides a wrapper around Native implementation of ExpressionResolver for iOS and Evaluator for Android.

## Features
Fully supports DivKit calculated expressions: 
- https://divkit.tech/en/doc/overview/concepts/expressions.html
- https://divkit.tech/en/doc/overview/concepts/functions.html

## Usage
```dart
    final nativeResolver = DivExpressionsResolverPlatform.instance;
    
    // Use simple math
    fianl r1 = await nativeResolver.executeExpression(
        "@{2 + 2}",
        context: {},
    );

    // Use variables
    fianl r2 = await nativeResolver.executeExpression(
        "@{a + b}",
        context: {
            "a" : 2,
            "b" : 2,
        },
    );

    // Use embedded functions
    fianl r3 = await nativeResolver.executeExpression(
        "@{sum(a, b)}",
        context: {
            "a" : 2,
            "b" : 2,
        },
    );
    
    // Use branching and more!
    fianl r3 = await nativeResolver.executeExpression(
        "@{sum(a, b) == 2 ? 2 : -1}",
        context: {
            "a" : 2,
            "b" : 2,
        },
    );
```