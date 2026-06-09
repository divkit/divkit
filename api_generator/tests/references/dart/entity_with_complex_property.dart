// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithComplexProperty with EquatableMixin  {
  const EntityWithComplexProperty({
    required this.property,
  });

  static const type = "entity_with_complex_property";
  final EntityWithComplexPropertyComplexProperty property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithComplexProperty copyWith({
      EntityWithComplexPropertyComplexProperty?  property,
  }) => EntityWithComplexProperty(
      property: property ?? this.property,
    );

  static EntityWithComplexProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexProperty(
        property: reqProp<EntityWithComplexPropertyComplexProperty>(safeParseObject(json['property'], parse: EntityWithComplexPropertyComplexProperty.fromJson,), name: 'property',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}


class EntityWithComplexPropertyComplexProperty with EquatableMixin  {
  const EntityWithComplexPropertyComplexProperty({
    required this.value,
  });

  final Expression<Uri> value;

  @override
  List<Object?> get props => [
        value,
      ];

  EntityWithComplexPropertyComplexProperty copyWith({
      Expression<Uri>?  value,
  }) => EntityWithComplexPropertyComplexProperty(
      value: value ?? this.value,
    );

  static EntityWithComplexPropertyComplexProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyComplexProperty(
        value: reqVProp<Uri>(safeParseUriExpr(json['value'],), name: 'value',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
