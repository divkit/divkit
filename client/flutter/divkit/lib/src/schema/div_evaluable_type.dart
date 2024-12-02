// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';

enum DivEvaluableType implements Resolvable {
  string('string'),
  integer('integer'),
  number('number'),
  boolean('boolean'),
  datetime('datetime'),
  color('color'),
  url('url'),
  dict('dict'),
  array('array');

  final String value;

  const DivEvaluableType(this.value);
  bool get isString => this == string;

  bool get isInteger => this == integer;

  bool get isNumber => this == number;

  bool get isBoolean => this == boolean;

  bool get isDatetime => this == datetime;

  bool get isColor => this == color;

  bool get isUrl => this == url;

  bool get isDict => this == dict;

  bool get isArray => this == array;

  T map<T>({
    required T Function() string,
    required T Function() integer,
    required T Function() number,
    required T Function() boolean,
    required T Function() datetime,
    required T Function() color,
    required T Function() url,
    required T Function() dict,
    required T Function() array,
  }) {
    switch (this) {
      case DivEvaluableType.string:
        return string();
      case DivEvaluableType.integer:
        return integer();
      case DivEvaluableType.number:
        return number();
      case DivEvaluableType.boolean:
        return boolean();
      case DivEvaluableType.datetime:
        return datetime();
      case DivEvaluableType.color:
        return color();
      case DivEvaluableType.url:
        return url();
      case DivEvaluableType.dict:
        return dict();
      case DivEvaluableType.array:
        return array();
    }
  }

  T maybeMap<T>({
    T Function()? string,
    T Function()? integer,
    T Function()? number,
    T Function()? boolean,
    T Function()? datetime,
    T Function()? color,
    T Function()? url,
    T Function()? dict,
    T Function()? array,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivEvaluableType.string:
        return string?.call() ?? orElse();
      case DivEvaluableType.integer:
        return integer?.call() ?? orElse();
      case DivEvaluableType.number:
        return number?.call() ?? orElse();
      case DivEvaluableType.boolean:
        return boolean?.call() ?? orElse();
      case DivEvaluableType.datetime:
        return datetime?.call() ?? orElse();
      case DivEvaluableType.color:
        return color?.call() ?? orElse();
      case DivEvaluableType.url:
        return url?.call() ?? orElse();
      case DivEvaluableType.dict:
        return dict?.call() ?? orElse();
      case DivEvaluableType.array:
        return array?.call() ?? orElse();
    }
  }

  static DivEvaluableType? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'string':
          return DivEvaluableType.string;
        case 'integer':
          return DivEvaluableType.integer;
        case 'number':
          return DivEvaluableType.number;
        case 'boolean':
          return DivEvaluableType.boolean;
        case 'datetime':
          return DivEvaluableType.datetime;
        case 'color':
          return DivEvaluableType.color;
        case 'url':
          return DivEvaluableType.url;
        case 'dict':
          return DivEvaluableType.dict;
        case 'array':
          return DivEvaluableType.array;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivEvaluableType: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivEvaluableType resolve(DivVariableContext context) => this;
}
