// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithSimpleProperties with EquatableMixin {
  const EntityWithSimpleProperties({
    this.boolean,
    this.booleanInt,
    this.color,
    this.dNum,
    this.id,
    this.integer,
    this.positiveInteger,
    this.string,
    this.url,
  });

  static const type = "entity_with_simple_properties";

  final bool? boolean;

  final bool? booleanInt;

  final int? color;

  final double? dNum;

  final int? id;

  final int? integer;
  // constraint: number > 0
  final int? positiveInteger;
  // at least 1 char
  final String? string;

  final Uri? url;

  @override
  List<Object?> get props => [
        boolean,
        booleanInt,
        color,
        dNum,
        id,
        integer,
        positiveInteger,
        string,
        url,
      ];

  static EntityWithSimpleProperties? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithSimpleProperties(
      boolean: safeParseBool(json['boolean']),
      booleanInt: safeParseBool(json['boolean_int']),
      color: safeParseColor(json['color']),
      dNum: safeParseDouble(json['dNum']),
      id: safeParseInt(json['id']),
      integer: safeParseInt(json['integer']),
      positiveInteger: safeParseInt(json['positive_integer']),
      string: json['string'],
      url: safeParseUri(json['url']),
    );
  }
}
