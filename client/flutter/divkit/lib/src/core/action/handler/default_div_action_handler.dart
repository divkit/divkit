import 'dart:async';

import 'package:divkit/divkit.dart';

class DefaultDivActionHandler implements DivActionHandler {
  final typedHandler = DefaultDivActionHandlerTyped();
  final urlHandler = DefaultDivActionHandlerUrl();

  @override
  bool canHandle(DivContext context, DivActionModel action) {
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
  FutureOr<bool> handleAction(DivContext context, DivActionModel action) async {
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
