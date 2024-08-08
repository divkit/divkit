// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class EntityWithRequiredProperty extends Preloadable with EquatableMixin  {
  const EntityWithRequiredProperty({
    required this.property,
  });

  static const type = "entity_with_required_property";
  // at least 1 char
  final Expression<String> property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithRequiredProperty copyWith({
      Expression<String>?  property,
  }) => EntityWithRequiredProperty(
      property: property ?? this.property,
    );

  static EntityWithRequiredProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithRequiredProperty(
        property: safeParseStrExpr(json['property']?.toString(),)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithRequiredProperty?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithRequiredProperty(
        property: (await safeParseStrExprAsync(json['property']?.toString(),))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await property.preload(context);
    } catch (e, st) {
      return;
    }
  }
}
