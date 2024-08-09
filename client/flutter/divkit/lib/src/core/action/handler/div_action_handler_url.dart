import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:divkit/divkit.dart';

class DefaultDivActionHandlerUrl implements DivActionHandler {
  static const handlers = [
    DivSetVariableHandlerUrl(),
    DivSetStateHandlerUrl(),
    DivTimerHandlerUrl(),
    DivDownloadHandlerUrl(),
    DivSwitchingElementHandlerUrl(),
  ];

  @override
  bool canHandle(context, action) {
    final url = action.url;
    if (url != null) {
      return handlers
          .firstWhere(
            (h) => h.canHandle(context, action),
            orElse: () => const DivEmptyHandler(),
          )
          .canHandle(context, action);
    }
    return false;
  }

  @override
  FutureOr<bool> handleAction(context, action) {
    final url = action.url;
    if (url != null) {
      return handlers
          .firstWhere((h) => h.canHandle(context, action))
          .handleAction(context, action);
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

class DivDownloadHandlerUrl implements DivActionHandler {
  static const name = 'download';

  const DivDownloadHandlerUrl();

  @override
  bool canHandle(context, action) =>
      'div-action' == action.url?.scheme && name == action.url?.host;

  Future<Map<String, dynamic>?> fetchPatch(String url) async {
    try {
      final uri = Uri.parse(url);
      final client = HttpClient();

      final request = await client.getUrl(uri);
      final response = await request.close();

      if (response.statusCode == HttpStatus.ok) {
        final responseBody = await response.transform(utf8.decoder).join();
        return json.decode(responseBody);
      } else {
        logger.error('HTTP request failed, status: ${response.statusCode}');
      }
    } catch (e) {
      logger.error('Error fetching data: $e');
    }
    return null;
  }

  @override
  FutureOr<bool> handleAction(context, action) async {
    final url = action.url;
    if (url != null) {
      final urlArg = url.queryParameters['url'];

      if (urlArg != null) {
        final json = await fetchPatch(urlArg);

        if (json != null) {
          final patch = await DivPatch.parse(
            await TemplatesResolver(
              layout: json['patch']!,
              templates: json['templates'],
            ).merge(),
          );

          if (patch != null) {
            final success = await context.patchManager.applyPatch(
              await patch.resolve(
                context: context.variables,
              ),
            );

            if (success) {
              logger.debug('Patch applied: $urlArg');
              // Call a callback when the patch is successfully uploaded over the network.
              if (action.downloadCallbacks?.onSuccessActions != null) {
                for (final a in action.downloadCallbacks!.onSuccessActions!) {
                  await a.execute(context);
                }
              }

              return true;
            }
          }
        }
      }
    }

    // Call a callback in case of errors in downloading the patch over the network.
    if (action.downloadCallbacks?.onFailActions != null) {
      for (final a in action.downloadCallbacks!.onFailActions!) {
        await a.execute(context);
      }
    }
    return false;
  }
}

class DivSwitchingElementHandlerUrl implements DivActionHandler {
  const DivSwitchingElementHandlerUrl();

  static const _scheme = 'div-action';
  static const _id = 'id';
  static const _item = 'item';

  @override
  bool canHandle(context, action) =>
      _scheme == action.url?.scheme &&
      SwitchingElementActionType.canHandle(action.url?.host);

  @override
  FutureOr<bool> handleAction(context, action) async {
    final url = action.url;

    if (url == null) return false;

    final args = url.queryParameters;
    final id = args[_id];

    if (id == null) {
      logger.error('The element id is not specified');
      return false;
    }

    switch (SwitchingElementActionType.getByName(url.host)) {
      case SwitchingElementActionType.setCurrentItem:
        final item = args[_item];
        if (item != null) {
          context.variableManager.updateVariable(id, item);
          return true;
        }
        logger.error('The element item is not specified');
        return false;
      case SwitchingElementActionType.setNextItem:
        final currentPage = context.variableManager.context.current[id];
        if (currentPage != null) {
          context.variableManager.updateVariable(id, currentPage + 1);
          return true;
        }
        logger.error('There is no current page');
        return false;
      case SwitchingElementActionType.setPreviousItem:
        final currentPage = context.variableManager.context.current[id];
        if (currentPage != null) {
          context.variableManager.updateVariable(id, currentPage - 1);
          return true;
        }
        logger.error('There is no current page');
        return false;
      default:
        logger.error('Unknown type');
        return false;
    }
  }
}

enum SwitchingElementActionType {
  setCurrentItem('set_current_item'),
  setNextItem('set_next_item'),
  setPreviousItem('set_previous_item'),
  unknown('');

  const SwitchingElementActionType(this.type);

  final String type;

  static bool canHandle(String? name) {
    if (name != null) {
      for (final customName in SwitchingElementActionType.values) {
        if (customName.type == name) {
          return true;
        }
      }
    }
    return false;
  }

  static SwitchingElementActionType getByName(String? name) {
    if (name != null) {
      for (final customName in SwitchingElementActionType.values) {
        if (customName.type == name) {
          return customName;
        }
      }
    }
    return unknown;
  }
}
