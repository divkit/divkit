import 'package:divkit/divkit.dart';
import 'package:flutter/widgets.dart';

extension DivEdgeInsetsConverter on DivEdgeInsets {
  EdgeInsetsGeometry resolve(
    DivVariableContext context, {
    required double viewScale,
  }) {
    final safeStart = start?.resolve(context);
    final safeEnd = end?.resolve(context);
    final safeLeft = left.resolve(context);
    final safeRight = right.resolve(context);
    final safeTop = top.resolve(context);
    final safeBottom = bottom.resolve(context);

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
