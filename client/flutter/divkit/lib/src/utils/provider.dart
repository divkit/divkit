import 'package:flutter/widgets.dart';

/// Provides dependencies on any object in the element tree.
class DivKitProvider<T> extends InheritedWidget {
  final T value;

  const DivKitProvider({
    required this.value,
    required super.child,
    super.key,
  });

  @override
  bool updateShouldNotify(DivKitProvider<T> oldWidget) =>
      value != oldWidget.value;

  /// Reads dependencies on a one-time basis.
  static T? read<T>(BuildContext context) => (context
          .getElementForInheritedWidgetOfExactType<DivKitProvider<T>>()
          ?.widget as DivKitProvider<T>?)
      ?.value;

  /// Subscribes to dependencies and triggers a rebuild when updating.
  static T? watch<T>(BuildContext context) =>
      context.dependOnInheritedWidgetOfExactType<DivKitProvider<T>>()?.value;
}
