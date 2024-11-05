import 'package:divkit/src/schema/div_data.dart';

abstract class DivKitData {
  const DivKitData();

  /// The layout described by dto entities.
  DivData? get source;

  /// The flag indicates that the data has been collected and can be drawn.
  bool get hasSource;

  /// Build [source] from raw.
  DivKitData build();
}
