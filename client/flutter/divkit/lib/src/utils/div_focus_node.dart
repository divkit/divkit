import 'package:collection/collection.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:flutter/widgets.dart';

/// Allows to find FocusNode by id of Div component ([divId]) and to request focus on it
/// Using [child] is a hack to prevent keyboard blinking and to pass focus
/// directly to primary recipient widget (TextField)
class DivFocusNode extends FocusNode {
  final String? divId;
  final FocusNode? child;

  DivFocusNode({
    this.divId,
    bool isRoot = true,
  }) : child = isRoot ? FocusNode() : null;

  @override
  void dispose() {
    child?.dispose();
    super.dispose();
  }
}

extension DivFocusNodes on FocusScopeNode {
  /// Returns primary FocusNode for DivFocusNode with [divId]
  /// If there is no such DivFocusNode — returns null
  FocusNode? getById(String? divId) {
    if (divId == null) {
      return null;
    }

    try {
      final rootNode = children.firstWhereOrNull(
        (element) {
          if (element is DivFocusNode && element.divId == divId) {
            return true;
          }
          return false;
        },
      ) as DivFocusNode?;
      if (rootNode != null && rootNode.child != null) {
        return rootNode.child;
      }
      return rootNode;
    } catch (e, st) {
      logger.error(
        'Can not request focus for next element with $divId',
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }
}
