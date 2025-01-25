import 'package:divkit/divkit.dart';

// ToDo: Needs REDO!
class DivFilters {
  final bool isRtl;

  final double? blurRadius;

  const DivFilters({
    this.isRtl = false,
    this.blurRadius,
  });

  static DivFilters combine({
    required List<DivFilter> filters,
    required double viewScale,
  }) {
    double? blurRadius;
    bool isRtl = false;
    for (var el in filters) {
      el.map(
        divBlur: (divBlur) {
          blurRadius = divBlur.radius.value.toDouble() * viewScale;
        },
        divFilterRtlMirror: (divFilterRtlMirror) {
          isRtl = true;
        },
      );
    }
    return DivFilters(
      blurRadius: blurRadius,
      isRtl: isRtl,
    );
  }
}
