import 'dart:async';

import 'package:divkit/src/core/action/action.dart';
import 'package:divkit/src/core/action/handler/div_action_handler_typed.dart';
import 'package:divkit/src/core/action/handler/div_action_handler_url.dart';
import 'package:divkit/src/core/protocol/div_action.dart';
import 'package:divkit/src/core/protocol/div_context.dart';

class DefaultDivActionHandler implements DivActionHandler {
  final typedHandler = DefaultDivActionHandlerTyped();
  final urlHandler = DefaultDivActionHandlerUrl();

  @override
  bool canHandle(DivContext context, DivAction action) {
    if (typedHandler.canHandle(context, action)) {
      return true;
    }
    return urlHandler.canHandle(context, action);
  }

  @override
  FutureOr<bool> handleAction(DivContext context, DivAction action) async {
    if (typedHandler.canHandle(context, action)) {
      return typedHandler.handleAction(context, action);
    }
    return urlHandler.handleAction(context, action);
  }
}
