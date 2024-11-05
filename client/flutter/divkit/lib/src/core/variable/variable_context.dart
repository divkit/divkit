import 'package:equatable/equatable.dart';

class DivVariableContext with EquatableMixin {
  final Map<String, dynamic>? last;
  final Map<String, dynamic> current;

  DivVariableContext({
    required this.current,
    this.last,
  });

  DivVariableContext.empty()
      : current = const {},
        last = null;

  /// Caching the last calculated update.
  Set<String>? _update;

  /// Find difference between current and last. It's updated variables names.
  Set<String> get update {
    if (_update != null) {
      return _update!;
    }

    // Create an empty set to store the names of updated variables.
    _update = {};

    // Iterate over the current map to find new or updated keys.
    current.forEach((key, value) {
      // Check if the key is not present in the last map, or its value has changed.
      if (last == null || last![key] != value) {
        // If so, add the key to the set of updated variables.
        _update!.add(key);
      }
    });

    // Check if any keys have been deleted.
    if (last != null) {
      for (var key in last!.keys) {
        if (!current.containsKey(key)) {
          _update!.add(key);
        }
      }
    }

    return _update!;
  }

  @override
  List<Object?> get props => [
        last,
        current,
      ];
}
