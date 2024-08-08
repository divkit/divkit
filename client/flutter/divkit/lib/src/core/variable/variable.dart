import 'package:equatable/equatable.dart';

export 'variable_converter.dart';

class DivVariableModel<T> with EquatableMixin {
  final String name;
  T value;

  // An auxiliary function, since we do not have reliability by type.
  final T? Function(String raw)? safeParse;

  DivVariableModel({
    required this.name,
    required this.value,
    this.safeParse,
  });

  MapEntry<String, dynamic> get raw => MapEntry(name, value);

  @override
  List<Object?> get props => [
        name,
        value,
      ];
}
