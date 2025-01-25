// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithOptionalComplexProperty with EquatableMixin  {
  const EntityWithOptionalComplexProperty({
    this.property,
  });

  static const type = "entity_with_optional_complex_property";
  final EntityWithOptionalComplexPropertyProperty? property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithOptionalComplexProperty copyWith({
      EntityWithOptionalComplexPropertyProperty? Function()?  property,
  }) => EntityWithOptionalComplexProperty(
      property: property != null ? property.call() : this.property,
    );

  static EntityWithOptionalComplexProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithOptionalComplexProperty(
        property: safeParseObject(json['property'], parse: EntityWithOptionalComplexPropertyProperty.fromJson,),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}


class EntityWithOptionalComplexPropertyProperty with EquatableMixin  {
  const EntityWithOptionalComplexPropertyProperty({
    required this.value,
  });

  final Expression<Uri> value;

  @override
  List<Object?> get props => [
        value,
      ];

  EntityWithOptionalComplexPropertyProperty copyWith({
      Expression<Uri>?  value,
  }) => EntityWithOptionalComplexPropertyProperty(
      value: value ?? this.value,
    );

  static EntityWithOptionalComplexPropertyProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithOptionalComplexPropertyProperty(
        value: reqVProp<Uri>(safeParseUriExpr(json['value'],), name: 'value',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
