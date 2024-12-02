// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class WithoutDefault extends Resolvable with EquatableMixin  {
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

  WithoutDefault resolve(DivVariableContext context) => this;
}
