import 'dart:async';

import 'package:divkit/src/core/protocol/div_action.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';

class DefaultDivActionHandlerUrl implements DivActionHandler {
  static const handlers = [
    DivSetVariableHandlerUrl(),
    DivSetStateHandlerUrl(),
    DivTimerHandlerUrl(),
  ];

  @override
  bool canHandle(context, action) {
    final url = action.url;
    if (url != null) {
      try {
        return handlers
            .firstWhere(
              (h) => h.canHandle(context, action),
              orElse: () => const DivEmptyHandler(),
            )
            .canHandle(context, action);
      } catch (e, st) {
        logger.error(
          'Something went wrong in `canHandle`',
          error: e,
          stackTrace: st,
        );
        return false;
      }
    }
    return false;
  }

  @override
  FutureOr<bool> handleAction(context, action) {
    final url = action.url;
    if (url != null) {
      try {
        return handlers
            .firstWhere((h) => h.canHandle(context, action))
            .handleAction(context, action);
      } catch (e, st) {
        logger.error(
          'Something went wrong in `handleAction`',
          error: e,
          stackTrace: st,
        );
        return false;
      }
    }
    return false;
  }
}

class DivEmptyHandler implements DivActionHandler {
  const DivEmptyHandler();

  @override
  bool canHandle(context, action) => false;

  @override
  FutureOr<bool> handleAction(context, action) async {
    logger.error('Handler not found for: ${action.url}');
    return true;
  }
}

class DivSetVariableHandlerUrl implements DivActionHandler {
  static const name = 'set_variable';

  const DivSetVariableHandlerUrl();

  @override
  bool canHandle(context, action) =>
      'div-action' == action.url?.scheme && name == action.url?.host;

  @override
  FutureOr<bool> handleAction(context, action) async {
    final url = action.url;
    if (url != null) {
      final args = url.queryParameters;
      final name = args['name'];
      final value = args['value'];

      if (name != null && value != null) {
        context.variableManager.updateVariable(name, value);
        return true;
      }
    }
    return false;
  }
}

class DivSetStateHandlerUrl implements DivActionHandler {
  static const name = 'set_state';

  const DivSetStateHandlerUrl();

  @override
  bool canHandle(context, action) =>
      'div-action' == action.url?.scheme && name == action.url?.host;

  @override
  FutureOr<bool> handleAction(context, action) async {
    final url = action.url;
    if (url != null) {
      final path = url.queryParameters['state_id']?.split('/');

      if (path != null) {
        final stateId = path.removeLast();
        var divId = path.join('/');
        if (divId.isEmpty) {
          divId = 'root';
        }

        context.stateManager.updateState(divId, stateId);
        return true;
      }
    }
    return false;
  }
}

class DivTimerHandlerUrl implements DivActionHandler {
  static const name = 'timer';

  const DivTimerHandlerUrl();

  @override
  bool canHandle(context, action) =>
      'div-action' == action.url?.scheme && name == action.url?.host;

  @override
  FutureOr<bool> handleAction(context, action) async {
    final url = action.url;
    if (url != null) {
      final args = url.queryParameters;
      final id = args['id'];
      final action = args['action'];

      if (id != null && action != null) {
        switch (action) {
          case 'start':
            context.timerManager.start(id);
            break;
          case 'stop':
            context.timerManager.stop(id);
            break;
          case 'pause':
            context.timerManager.pause(id);
            break;
          case 'resume':
            context.timerManager.resume(id);
            break;
          case 'cancel':
            context.timerManager.cancel(id);
            break;
          case 'reset':
            context.timerManager.reset(id);
            break;
          default:
            throw Exception('Not supported action on timer: $action');
        }
        return true;
      }
    }
    return false;
  }
}
