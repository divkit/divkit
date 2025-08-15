import 'dart:collection';

import 'package:divkit/src/core/expression/analyzer.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('DivExpressionAnalyzer implementation tests', () {
    late DivExpressionAnalyzer analyzer;

    setUp(() {
      analyzer = DivExpressionAnalyzer();
    });

    test('No variables 0', () async {
      const String expression = "Hello world!";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 0-1', () async {
      const String expression = "1234";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 0-2', () async {
      const String expression = "1234.56789";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 0-3', () async {
      const String expression = "#fffff";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 0-4', () async {
      const String expression = "0xfffff";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 0-5', () async {
      const String expression = "true";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 0-6', () async {
      const String expression = "false";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 1-0-0', () async {
      const String expression = "@{"; // broken case!
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 1-0-1', () async {
      const String expression = "@{}}"; // broken case!
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 1-1', () async {
      const String expression = "@{}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 1-2', () async {
      const String expression = "@{}@{}@{}@{}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 1-3', () async {
      const String expression = "\\@{var}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 2-0', () async {
      const String expression = "'@{}'";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 2-1', () async {
      const String expression = "@{1234}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 2-2', () async {
      const String expression = "@{1234.56789}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 2-3', () async {
      const String expression = "@{'#fffff'}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 2-5', () async {
      const String expression = "@{true}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 2-6', () async {
      const String expression = "@{false}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, HashSet());
    });

    test('No variables 0-0', () async {
      const String expression = "\\@{var} @{a}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'a'});
    });

    test('Extract simple variable 0-1', () async {
      const String expression = "@{cool}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'cool'});
    });

    test('Extract simple variable 0-2', () async {
      const String expression = "@{sum}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'sum'});
    });

    test('Extract simple variable 0-3', () async {
      const String expression = "@{_var}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'_var'});
    });

    test('Extract simple variable 0-4', () async {
      const String expression = "@{cool2}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'cool2'});
    });

    test('Extract simple variables 0-5', () async {
      const String expression = "@{_cool_2}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'_cool_2'});
    });

    test('Extract simple variables 1-0', () async {
      const String expression = "x + @{y} - @{z}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'y', 'z'});
    });

    test('Extract simple variables 1-1', () async {
      const String expression = "x*(@{_y}/@{_z})";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'_y', '_z'});
    });

    test('Extract simple variables 1-2', () async {
      const String expression = "'@{cool}'";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'cool'});
    });

    test('Extract nested variables 0-1', () async {
      const String expression = "@{a + @{b + @{c}} - d}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'a', 'b', 'c', 'd'});
    });

    test('Extract nested variables 0-2', () async {
      const String expression = "a + @{b + @{c + @{d}}}}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'b', 'c', 'd'});
    });

    test('Extract nested variables 0-3', () async {
      const String expression = "a * @{b + c + (@{d} <= 5 ? 10 : one)}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'b', 'c', 'd', 'one'});
    });

    test('Extract variables separated by operators and branches 0-1', () async {
      const String expression =
          "@{logging_enabled && (user_name == 'John' || user_email != '')}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'logging_enabled', 'user_name', 'user_email'});
    });

    test('Extract variables separated by operators and branches 0-1', () async {
      const String expression = "@{(a && b) == (c || d)}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'a', 'b', 'c', 'd'});
    });

    test('Ignores non-variables', () async {
      const String expression = "@{'string' + true + 123 + @{x} + 1.99}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'x'});
    });

    test('Ignores functions literals', () async {
      const String expression =
          "@{mult(add(total_price, div(two, 4)), one) + three}";
      final variables = await analyzer.extractVariables(expression);
      expect(variables, {'total_price', 'one', 'two', 'three'});
    });
  });
}
