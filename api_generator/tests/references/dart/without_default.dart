// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class WithoutDefault extends Preloadable with EquatableMixin  {
  const WithoutDefault();

  static const type = "non_default";

  @override
  List<Object?> get props => [];

  WithoutDefault? copyWith() => this;

  static WithoutDefault? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    return const WithoutDefault();
  }

  static Future<WithoutDefault?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    return const WithoutDefault();
  }

  Future<void> preload(Map<String, dynamic> context,) async {}
}
