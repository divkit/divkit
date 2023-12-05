// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';
import 'entity.dart';

class EntityWithArrayOfNestedItems with EquatableMixin {
  const EntityWithArrayOfNestedItems({
    required this.items,
  });

  static const type = "entity_with_array_of_nested_items";
  // at least 1 elements
  final List<EntityWithArrayOfNestedItemsItem> items;

  @override
  List<Object?> get props => [
        items,
      ];

  static EntityWithArrayOfNestedItems? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithArrayOfNestedItems(
      items: (json['items'] as List<dynamic>).map((j) => EntityWithArrayOfNestedItemsItem.fromJson(j as Map <String, dynamic>)!).toList(),
    );
  }
}

class EntityWithArrayOfNestedItemsItem with EquatableMixin {
  const EntityWithArrayOfNestedItemsItem({
    required this.entity,
    required this.property,
  });


  final Entity entity;
  // at least 1 char
  final String property;

  @override
  List<Object?> get props => [
        entity,
        property,
      ];

  static EntityWithArrayOfNestedItemsItem? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithArrayOfNestedItemsItem(
      entity: Entity.fromJson(json['entity'])!,
      property: json['property']!.toString(),
    );
  }
}
