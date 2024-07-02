import 'package:divkit/src/generated_sources/div_data.dart';

abstract class DivKitData {
  const DivKitData();

  /// The layout described by dto entities.
  DivData? get source;
}
