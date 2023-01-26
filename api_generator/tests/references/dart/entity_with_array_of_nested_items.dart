// Generated code. Do not modify.

import './entity.dart';

class EntityWithArrayOfNestedItems {
  const EntityWithArrayOfNestedItems({
    required this.items,
  });

  final List<EntityWithArrayOfNestedItemsItem> items;
  static const String type = "entity_with_array_of_nested_items";
}

class EntityWithArrayOfNestedItemsItem {
  const EntityWithArrayOfNestedItemsItem({
    required this.entity,
    required this.property,
  });

  final Entity entity;
  final String property;
}
