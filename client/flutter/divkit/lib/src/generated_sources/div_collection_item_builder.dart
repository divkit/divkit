// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div.dart';

class DivCollectionItemBuilder with EquatableMixin {
  const DivCollectionItemBuilder({
    required this.data,
    this.dataElementName = "it",
    required this.prototypes,
  });

  final Expression<List<dynamic>> data;
  // default value: "it"
  final String dataElementName;
  // at least 1 elements
  final List<DivCollectionItemBuilderPrototype> prototypes;

  @override
  List<Object?> get props => [
        data,
        dataElementName,
        prototypes,
      ];

  static DivCollectionItemBuilder? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivCollectionItemBuilder(
      data: safeParseListExpr(
        json['data'],
      )!,
      dataElementName: safeParseStr(
        json['data_element_name']?.toString(),
        fallback: "it",
      )!,
      prototypes: safeParseObj(
        safeListMap(
            json['prototypes'],
            (v) => safeParseObj(
                  DivCollectionItemBuilderPrototype.fromJson(v),
                )!),
      )!,
    );
  }
}

class DivCollectionItemBuilderPrototype with EquatableMixin {
  const DivCollectionItemBuilderPrototype({
    required this.div,
    this.selector = const ValueExpression(true),
  });

  final Div div;
  // default value: true
  final Expression<bool> selector;

  @override
  List<Object?> get props => [
        div,
        selector,
      ];

  static DivCollectionItemBuilderPrototype? fromJson(
      Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivCollectionItemBuilderPrototype(
      div: safeParseObj(
        Div.fromJson(json['div']),
      )!,
      selector: safeParseBoolExpr(
        json['selector'],
        fallback: true,
      )!,
    );
  }
}
