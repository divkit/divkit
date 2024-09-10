// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class EntityWithArrayOfEnums extends Preloadable with EquatableMixin  {
  const EntityWithArrayOfEnums({
    required this.items,
  });

  static const type = "entity_with_array_of_enums";
   // at least 1 elements
  final List<EntityWithArrayOfEnumsItem> items;

  @override
  List<Object?> get props => [
        items,
      ];

  EntityWithArrayOfEnums copyWith({
      List<EntityWithArrayOfEnumsItem>?  items,
  }) => EntityWithArrayOfEnums(
      items: items ?? this.items,
    );

  static EntityWithArrayOfEnums? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArrayOfEnums(
        items: safeParseObj(safeListMap(json['items'], (v) => safeParseStrEnum(v, parse: EntityWithArrayOfEnumsItem.fromJson,)!,),)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithArrayOfEnums?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArrayOfEnums(
        items: (await safeParseObjAsync(await safeListMapAsync(json['items'], (v) => safeParseStrEnum(v, parse: EntityWithArrayOfEnumsItem.fromJson,)!,),))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await safeFuturesWait(items, (v) => v.preload(context));
    } catch (e, st) {
      return;
    }
  }
}

enum EntityWithArrayOfEnumsItem implements Preloadable {
  first('first'),
  second('second');

  final String value;

  const EntityWithArrayOfEnumsItem(this.value);
  bool get isFirst => this == first;

  bool get isSecond => this == second;


  T map<T>({
    required T Function() first,
    required T Function() second,
  }) {
    switch (this) {
      case EntityWithArrayOfEnumsItem.first:
        return first();
      case EntityWithArrayOfEnumsItem.second:
        return second();
     }
  }

  T maybeMap<T>({
    T Function()? first,
    T Function()? second,
    required T Function() orElse,
  }) {
    switch (this) {
      case EntityWithArrayOfEnumsItem.first:
        return first?.call() ?? orElse();
      case EntityWithArrayOfEnumsItem.second:
        return second?.call() ?? orElse();
     }
  }

  Future<void> preload(Map<String, dynamic> context) async {}

  static EntityWithArrayOfEnumsItem? fromJson(String? json,) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'first':
        return EntityWithArrayOfEnumsItem.first;
        case 'second':
        return EntityWithArrayOfEnumsItem.second;
      }
      return null;
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithArrayOfEnumsItem?> parse(String? json,) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'first':
        return EntityWithArrayOfEnumsItem.first;
        case 'second':
        return EntityWithArrayOfEnumsItem.second;
      }
      return null;
    } catch (e, st) {
      return null;
    }
  }
}
