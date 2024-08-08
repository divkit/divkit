import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/schema/div_data.dart';

abstract class DivKitData {
  const DivKitData();

  /// The layout described by dto entities.
  DivData? get source;

  /// The flag indicates that the data has been collected and can be drawn.
  bool get hasSource;

  /// Build [source] from raw.
  Future<DivKitData> build();

  /// Build [source] from raw.
  DivKitData buildSync();

  /// The flag indicates that the computation of expressions
  /// has been performed and instant rendering is available.
  bool get preloaded;

  /// Precalculate values in expressions.
  Future<DivKitData> preload({
    DivVariableStorage? variableStorage,
  });
}
