import 'package:flutter/widgets.dart';

/// Provides `stateId` in the widget tree.
class InheritedDivId extends InheritedWidget {
  final String? divId;

  const InheritedDivId({
    this.divId,
    required super.child,
    super.key,
  });

  @override
  bool updateShouldNotify(InheritedDivId oldWidget) => divId != oldWidget.divId;

  /// Subscribes to dependencies and triggers a rebuild when updating.
  static String? of(BuildContext context) =>
      context.dependOnInheritedWidgetOfExactType<InheritedDivId>()?.divId;
}
