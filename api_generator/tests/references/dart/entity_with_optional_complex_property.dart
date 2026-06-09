// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithOptionalComplexProperty with EquatableMixin  {
  const EntityWithOptionalComplexProperty({
    this.property,
  });

  static const type = "entity_with_optional_complex_property";
  final EntityWithOptionalComplexPropertyComplexProperty? property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithOptionalComplexProperty copyWith({
      EntityWithOptionalComplexPropertyComplexProperty? Function()?  property,
  }) => EntityWithOptionalComplexProperty(
      property: property != null ? property.call() : this.property,
    );

  static EntityWithOptionalComplexProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithOptionalComplexProperty(
        property: safeParseObject(json['property'], parse: EntityWithOptionalComplexPropertyComplexProperty.fromJson,),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}


class EntityWithOptionalComplexPropertyComplexProperty with EquatableMixin  {
  const EntityWithOptionalComplexPropertyComplexProperty({
    required this.value,
  });

  final Expression<Uri> value;

  @override
  List<Object?> get props => [
        value,
      ];

  EntityWithOptionalComplexPropertyComplexProperty copyWith({
      Expression<Uri>?  value,
  }) => EntityWithOptionalComplexPropertyComplexProperty(
      value: value ?? this.value,
    );

  static EntityWithOptionalComplexPropertyComplexProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithOptionalComplexPropertyComplexProperty(
        value: reqVProp<Uri>(safeParseUriExpr(json['value'],), name: 'value',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
