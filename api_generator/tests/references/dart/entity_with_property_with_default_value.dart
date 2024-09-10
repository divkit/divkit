// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class EntityWithPropertyWithDefaultValue extends Preloadable with EquatableMixin  {
  const EntityWithPropertyWithDefaultValue({
    this.iNum = const ValueExpression(0),
    this.nested,
    this.url = const ValueExpression(const Uri.parse("https://yandex.ru")),
  });

  static const type = "entity_with_property_with_default_value";
   // constraint: number >= 0; default value: 0
  final Expression<int> iNum;
  /// non_optional is used to suppress auto-generation of default value for object with all-optional fields.
  final EntityWithPropertyWithDefaultValueNested? nested;
   // valid schemes: [https]; default value: const Uri.parse("https://yandex.ru")
  final Expression<Uri> url;

  @override
  List<Object?> get props => [
        iNum,
        nested,
        url,
      ];

  EntityWithPropertyWithDefaultValue copyWith({
      Expression<int>?  iNum,
      EntityWithPropertyWithDefaultValueNested? Function()?  nested,
      Expression<Uri>?  url,
  }) => EntityWithPropertyWithDefaultValue(
      iNum: iNum ?? this.iNum,
      nested: nested != null ? nested.call() : this.nested,
      url: url ?? this.url,
    );

  static EntityWithPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithPropertyWithDefaultValue(
        iNum: safeParseIntExpr(json['iNum'], fallback: 0,)!,
        nested: safeParseObj(EntityWithPropertyWithDefaultValueNested.fromJson(json['nested']),),
        url: safeParseUriExpr(json['url'])!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithPropertyWithDefaultValue?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithPropertyWithDefaultValue(
        iNum: (await safeParseIntExprAsync(json['iNum'], fallback: 0,))!,
        nested: await safeParseObjAsync(EntityWithPropertyWithDefaultValueNested.fromJson(json['nested']),),
        url: (await safeParseUriExprAsync(json['url']))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await iNum?.preload(context);
    await nested?.preload(context);
    await url?.preload(context);
    } catch (e, st) {
      return;
    }
  }
}

/// non_optional is used to suppress auto-generation of default value for object with all-optional fields.
class EntityWithPropertyWithDefaultValueNested extends Preloadable with EquatableMixin  {
  const EntityWithPropertyWithDefaultValueNested({
    this.iNum = const ValueExpression(0),
    required this.nonOptional,
    this.url = const ValueExpression(const Uri.parse("https://yandex.ru")),
  });

   // constraint: number >= 0; default value: 0
  final Expression<int> iNum;
  final Expression<String> nonOptional;
   // valid schemes: [https]; default value: const Uri.parse("https://yandex.ru")
  final Expression<Uri> url;

  @override
  List<Object?> get props => [
        iNum,
        nonOptional,
        url,
      ];

  EntityWithPropertyWithDefaultValueNested copyWith({
      Expression<int>?  iNum,
      Expression<String>?  nonOptional,
      Expression<Uri>?  url,
  }) => EntityWithPropertyWithDefaultValueNested(
      iNum: iNum ?? this.iNum,
      nonOptional: nonOptional ?? this.nonOptional,
      url: url ?? this.url,
    );

  static EntityWithPropertyWithDefaultValueNested? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithPropertyWithDefaultValueNested(
        iNum: safeParseIntExpr(json['iNum'], fallback: 0,)!,
        nonOptional: safeParseStrExpr(json['non_optional']?.toString(),)!,
        url: safeParseUriExpr(json['url'])!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithPropertyWithDefaultValueNested?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithPropertyWithDefaultValueNested(
        iNum: (await safeParseIntExprAsync(json['iNum'], fallback: 0,))!,
        nonOptional: (await safeParseStrExprAsync(json['non_optional']?.toString(),))!,
        url: (await safeParseUriExprAsync(json['url']))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await iNum?.preload(context);
    await nonOptional.preload(context);
    await url?.preload(context);
    } catch (e, st) {
      return;
    }
  }
}
