import 'dart:async';

import 'package:divkit/src/core/action/action.dart';
import 'package:divkit/src/core/action/handler/div_action_handler_typed.dart';
import 'package:divkit/src/core/action/handler/div_action_handler_url.dart';
import 'package:divkit/src/core/protocol/div_action.dart';
import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';

class DefaultDivActionHandler implements DivActionHandler {
  final typedHandler = DefaultDivActionHandlerTyped();
  final urlHandler = DefaultDivActionHandlerUrl();

  @override
  bool canHandle(DivContext context, DivAction action) {
    try {
      if (typedHandler.canHandle(context, action)) {
        return true;
      }
      return urlHandler.canHandle(context, action);
    } catch (e, st) {
      logger.error(
        '[div-action] Can\'t CHECK action: $action',
        error: e,
        stackTrace: st,
      );
      return false;
    }
  }

  @override
  FutureOr<bool> handleAction(DivContext context, DivAction action) async {
    try {
      if (typedHandler.canHandle(context, action)) {
        return typedHandler.handleAction(context, action);
      }
      return urlHandler.handleAction(context, action);
    } catch (e, st) {
      logger.error(
        '[div-action] Can\'t HANDLE action: $action',
        error: e,
        stackTrace: st,
      );
      return false;
    }
  }
}
