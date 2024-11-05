import 'package:divkit/divkit.dart';
import 'package:flutter/widgets.dart';

extension DivEdgeInsetsConverter on DivEdgeInsets {
  EdgeInsetsGeometry convert({
    required double viewScale,
  }) {
    final safeStart = start?.value;
    final safeEnd = end?.value;
    final safeLeft = left.value;
    final safeRight = right.value;
    final safeTop = top.value;
    final safeBottom = bottom.value;

    if (safeStart != null || safeEnd != null) {
      return EdgeInsetsDirectional.fromSTEB(
        (safeStart?.toDouble() ?? 0) * viewScale,
        safeTop.toDouble() * viewScale,
        (safeEnd?.toDouble() ?? 0) * viewScale,
        safeBottom.toDouble() * viewScale,
      );
    }
    return EdgeInsets.fromLTRB(
      safeLeft.toDouble() * viewScale,
      safeTop.toDouble() * viewScale,
      safeRight.toDouble() * viewScale,
      safeBottom.toDouble() * viewScale,
    );
  }
}
