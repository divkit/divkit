import 'dart:math';

final exprAnalyzer = DivExpressionAnalyzer();

/// Analyzer of expressions to optimize their usage.
class DivExpressionAnalyzer {
  Future<Set<String>> extractVariables(
    String expression, {
    int depth = 0,
  }) async {
    final result = <String>{};
    int index = 0;
    while (index < expression.length) {
      // Search inner expressions.
      int startIndex = expression.indexOf('@{', index);
      if (startIndex == -1 && depth > 0) {
        result.addAll(
          await parseExpressionLine(expression.substring(index)),
        );
        break;
      }

      if (startIndex >= 1 && expression.startsWith('\\@{', startIndex - 1)) {
        index = startIndex + 1;
        continue;
      }

      if (startIndex > index && depth > 0) {
        // Add any variables before the nested expression.
        result.addAll(
          await parseExpressionLine(expression.substring(index, startIndex)),
        );
      }

      int endIndex = await findClosingBraceIndex(
        expression,
        max(startIndex, index) + 2,
      );

      if (endIndex != -1) {
        result.addAll(
          await extractVariables(
            expression.substring(startIndex + 2, endIndex),
            depth: depth + 1,
          ),
        );
        index = endIndex + 1;
      } else {
        // Handle the case where the closing brace is missing,
        // to avoid an infinite loop.
        break;
      }
    }

    return result;
  }

  Future<int> findClosingBraceIndex(String expression, int startIndex) async {
    int depth = 1;
    for (int i = startIndex; i < expression.length; i++) {
      if (expression.startsWith('@{', i)) {
        depth++;
      } else if (expression[i] == '}') {
        depth--;
        if (depth == 0) {
          return i;
        }
      }
    }
    return -1;
  }

  /// A regular expression for splitting by operator, ignoring those inside of strings or function arguments.
  static final operatorPattern = RegExp(r'[\s()?:><=!&|+\-*/%\[\],]+');

  /// A regular expression to ignore number and boolean literals.
  static final valuePattern = RegExp(r'\b(true|false|\d+(\.\d+)?)\b');

  /// A regular expression to ignore string literals.
  static final stringPattern = RegExp("'[^']*'|\"[^\"]*\"");

  /// A regular expression to ignore function names but keep the arguments.
  static final ignoreFunctionNamesPattern = RegExp(r'\b\w+\s*\((.*?)\)');

  Future<Set<String>> parseExpressionLine(String expression) async {
    Set<String> variables = {};

    // Temporary replacing string and number literals to avoid matching on these
    String temp =
        expression.replaceAll(stringPattern, ' ').replaceAll(valuePattern, ' ');

    // Iteratively apply ignoreFunctionNamesPattern to remove nested function calls.
    while (ignoreFunctionNamesPattern.hasMatch(temp)) {
      temp = temp.replaceAllMapped(
        ignoreFunctionNamesPattern,
        (match) => match.group(1) ?? '',
      );
    }

    // Splitting the resulting string by operators and filtering out empty and invalid entries.
    temp.split(operatorPattern).where((e) => e.isNotEmpty).forEach((varName) {
      // Adding variable name to the set if it's not a number or boolean.
      if (!valuePattern.hasMatch(varName)) {
        variables.add(varName);
      }
    });

    return variables;
  }
}
