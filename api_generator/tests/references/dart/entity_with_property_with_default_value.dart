// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithPropertyWithDefaultValue with EquatableMixin {
  const EntityWithPropertyWithDefaultValue({
    this.iNum = const ValueExpression(0),
    this.nested,
    this.url = const ValueExpression(const Uri.parse("https://yandex.ru")),
  });

  static const type = "entity_with_property_with_default_value";
  // constraint: number >= 0; default value: 0
  final Expression<int> iNum;

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

  static EntityWithPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithPropertyWithDefaultValue(
      iNum: safeParseIntExpr(json['iNum'], fallback: 0,)!,
      nested: safeParseObj(EntityWithPropertyWithDefaultValueNested.fromJson(json['nested']),),
      url: safeParseUriExpr(json['url'])!,
    );
  }
}

class EntityWithPropertyWithDefaultValueNested with EquatableMixin {
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

  static EntityWithPropertyWithDefaultValueNested? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithPropertyWithDefaultValueNested(
      iNum: safeParseIntExpr(json['iNum'], fallback: 0,)!,
      nonOptional: safeParseStrExpr(json['non_optional']?.toString(),)!,
      url: safeParseUriExpr(json['url'])!,
    );
  }
}
