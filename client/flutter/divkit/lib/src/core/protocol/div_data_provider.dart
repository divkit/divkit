import 'package:divkit/src/generated_sources/div_data.dart';

abstract class DivDataProvider {
  const DivDataProvider();

  /// Current data.
  DivData get value;

  /// Data stream for further rendering.
  Stream<DivData> get stream;

  /// Update provided data.
  void update(DivData data);

  /// Safely destroy patch manager.
  Future<void> dispose();
}
