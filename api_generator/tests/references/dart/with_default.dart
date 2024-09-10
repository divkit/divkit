// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class WithDefault extends Preloadable with EquatableMixin  {
  const WithDefault();

  static const type = "default";

  @override
  List<Object?> get props => [];

  WithDefault? copyWith() => this;

  static WithDefault? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    return const WithDefault();
  }

  static Future<WithDefault?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    return const WithDefault();
  }

  Future<void> preload(Map<String, dynamic> context,) async {}
}
