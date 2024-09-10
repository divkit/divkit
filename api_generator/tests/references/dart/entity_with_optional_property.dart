// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class EntityWithOptionalProperty extends Preloadable with EquatableMixin  {
  const EntityWithOptionalProperty({
    this.property,
  });

  static const type = "entity_with_optional_property";
  final Expression<String>? property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithOptionalProperty copyWith({
      Expression<String>? Function()?  property,
  }) => EntityWithOptionalProperty(
      property: property != null ? property.call() : this.property,
    );

  static EntityWithOptionalProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithOptionalProperty(
        property: safeParseStrExpr(json['property']?.toString(),),
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithOptionalProperty?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithOptionalProperty(
        property: await safeParseStrExprAsync(json['property']?.toString(),),
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await property?.preload(context);
    } catch (e, st) {
      return;
    }
  }
}
