import 'package:divkit/src/core/runtime/core.dart';
import 'package:divkit/src/core/runtime/entities.dart';
import 'package:petitparser/petitparser.dart';

final parser = const RuntimeGrammar().build();

/// Parser that works with grammar of expressions represented by DivKit specification.
class RuntimeGrammar extends GrammarDefinition {
  const RuntimeGrammar();

  @override
  Parser start() => ref0(base).end();

  Parser _() => whitespace().star();

  Parser identifier() =>
      (pattern('a-zA-Z_') & pattern('a-zA-Z_0-9').star()).flatten();

  Parser base() => ref0(content).star().map((v) => buildBase(v, true));

  Parser content() =>
      ref0(embedded) |
      ref0(plainEmbedded) |
      ref0(invalidEmbedded) |
      ref0(incompleteEmbedded) |
      ref0(escapedAtCurly) |
      ref0(escapedChar) |
      ref0(singleBackslash);

  Parser embedded() =>
      (string('@{') & ref0(expression).optional().trim() & char('}'))
          .map((v) => v[1] ?? '');

  Parser plainEmbedded() => ((char('\\') | string('@{'))).neg();

  Parser invalidEmbedded() =>
      (string('@{') & pattern('^}').plus() & char('}')).map(
        (v) => throw "Error tokenizing top '@{}'",
      );

  Parser incompleteEmbedded() => (string('@{') & any().star()).flatten().map(
        (v) => throw "Error tokenizing '$v'",
      );

  Parser escapedAtCurly() => (char('\\') & string('@{')).pick(1);

  Parser escapedChar() => (char('\\') & any()).map((v) => escapeChar(v[1]));

  String escapeChar(String char) {
    if (char == '\'' || char == '\\') {
      return char;
    }
    throw 'Incorrect string escape';
  }

  Parser singleBackslash() =>
      char('\\').map((_) => throw "Error tokenizing '\\'");

  Parser expression() => ref0(condition);

  Parser condition() => (ref0(guard) &
          (char('?').trim() &
                  ref0(expression) &
                  char(':').trim() &
                  ref0(expression))
              .optional())
      .map(buildCondition);

  Parser guard() =>
      (ref0(or) & (string('!:').trim() & ref0(expression)).optional())
          .map(buildGuard);

  Parser or() => ref0(and).plusSeparated(string('||').trim()).map(buildLogical);

  Parser and() =>
      ref0(equality).plusSeparated(string('&&').trim()).map(buildLogical);

  Parser equality() => ref0(comparison)
      .plusSeparated((string('==') | string('!=')).trim())
      .map(buildBinary);

  Parser comparison() => ref0(sum)
      .plusSeparated(
        (string('>=') | string('>') | string('<=') | string('<')).trim(),
      )
      .map(buildBinary);

  Parser sum() =>
      ref0(factor).plusSeparated(anyOf("+-").trim()).map(buildBinary);

  Parser factor() =>
      ref0(unaryPrefix).plusSeparated(anyOf("/*%").trim()).map(buildBinary);

  Parser unaryPrefix() =>
      (char('-').skip(before: ref0(_)) & ref0(number))
          .flatten()
          .map(buildNumber) |
      (char('-').skip(before: ref0(_)) & ref0(integer))
          .flatten()
          .map(buildInteger) |
      (anyOf('-+!') & ref0(_) & (ref0(doubleUnaryPrefixCatch) | ref0(member)))
          .map(buildUnaryPrefix) |
      ref0(member);

  Parser doubleUnaryPrefixCatch() =>
      (anyOf('-+')).map((_) => throw "Incorrect unary operator");

  Parser member() =>
      (ref0(function).plusSeparated(char('.').trim())).map(buildMember) |
      ref0(function);

  Parser function() =>
      (ref0(identifier) &
              ref0(arguments).optional().skip(
                    before: char('(').trim(),
                    after: ref0(_) & char(')'),
                  ))
          .map(buildFunction) |
      ref0(primary);

  Parser arguments() =>
      ref0(expression).plusSeparated(char(',').trim()).map(buildArguments);

  Parser primary() =>
      ref0(boolean).flatten().map(buildBoolean) |
      ref0(identifier).flatten().map(buildReference) |
      ref0($string) |
      ref0(number).flatten().map(buildNumber) |
      ref0(integer).flatten().map(buildInteger) |
      ref0($group);

  Parser boolean() =>
      (string('true') | string('false')) & pattern('a-zA-Z_0-9').not();

  Parser $string() =>
      ref0($stringContent).skip(before: char("'"), after: char("'"));

  Parser $stringContent() => ref0($char).star().map((v) => buildBase(v, false));

  Parser $char() =>
      ref0(embedded) |
      ref0(notSpecialChars) |
      ref0(invalidEmbeddedChar) |
      ref0(incompleteEmbedded) |
      ref0(escapedAtCurly) |
      ref0(escapedChar) |
      ref0(singleBackslash);

  Parser notSpecialChars() => ((char('\\') | char("'") | string('@{'))).neg();

  Parser invalidEmbeddedChar() =>
      (string('@{') & pattern('^\'}').plus() & char('}')).map(
        (_) => throw "Error tokenizing inner '@{}'",
      );

  Parser integer() => (char('-').optional() & digit().plus());

  Parser number() => ((char('-').optional() &
          digit().star() &
          char('.') &
          digit().plus() &
          (anyOf('eE') & anyOf('+-').optional() & digit().plus()).optional()) |
      (char('-').optional() &
          digit().plus() &
          anyOf('eE') &
          anyOf('+-').optional() &
          digit().plus()));

  Parser $group() => (char('(') & ref0(expression).trim() & char(')')).pick(1);

  dynamic buildCondition(List v) {
    // Skip if no operation
    if (v.length == 1 || v[1] == null) {
      return v[0];
    }

    return ConditionToken(
      test: v[0],
      consequent: v[1][1] ?? v[0],
      alternate: v[1][3] ?? v[0],
    );
  }

  dynamic buildGuard(List v) {
    // Skip if no operation
    if (v.length == 1 || v[1] == null) {
      return v[0];
    }

    return GuardToken(
      test: v[0],
      alternate: v[1][1] ?? v[0],
    );
  }

  UnaryPrefixToken buildUnaryPrefix(v) {
    return UnaryPrefixToken(
      operator: v[0],
      right: v[2],
    );
  }

  dynamic buildBinary(SeparatedList v) {
    // Skip if no right
    if (v.elements.length == 1) {
      return v.elements[0];
    }

    return v.foldLeft((left, operator, right) {
      return BinaryToken(
        left: left,
        operator: operator,
        right: right,
      );
    });
  }

  dynamic buildLogical(SeparatedList v) {
    // Skip if no right
    if (v.elements.length == 1) {
      return v.elements[0];
    }

    return v.foldLeft((left, operator, right) {
      return LogicalToken(
        left: left,
        operator: operator,
        right: right,
      );
    });
  }

  dynamic buildMember(SeparatedList v) {
    // Skip if no property
    if (v.elements.length == 1) {
      return v.elements[0];
    }

    return v.foldLeft((left, operator, right) {
      return MemberToken(
        object: left,
        property: right,
      );
    });
  }

  FunctionToken buildFunction(List v) {
    return FunctionToken(
      identifier: v[0],
      arguments: (v[1] as List?)?.cast<ExpressionToken>() ?? [],
    );
  }

  dynamic buildArguments(SeparatedList v) {
    return v.elements;
  }

  IntegerToken buildInteger(String v) {
    return IntegerToken(toInteger(v));
  }

  NumberToken buildNumber(String v) {
    return NumberToken(toNumber(v));
  }

  BooleanToken buildBoolean(String v) {
    return BooleanToken(v == 'true');
  }

  dynamic buildBase(List v, bool entry) {
    if (v.every((it) => it is String)) {
      return StringToken(v.join(''));
    }

    List<dynamic> composition = [];
    for (var element in v) {
      if (element is String &&
          composition.isNotEmpty &&
          composition.last is String) {
        composition[composition.length - 1] = composition.last + element;
      } else {
        composition.add(element);
      }
    }

    final quasis = [];
    final expressions = [];
    for (var element in composition) {
      if (element is String) {
        quasis.add(StringToken(element));
      } else {
        if (quasis.length == expressions.length) {
          quasis.add(const StringToken(''));
        }
        expressions.add(element);
      }
    }

    if (quasis.length == expressions.length) {
      quasis.add(const StringToken(''));
    }

    return CompositionToken(
      entry: entry,
      quasis: quasis,
      expressions: expressions,
    );
  }

  ReferenceToken buildReference(String v) {
    return ReferenceToken(v);
  }
}
