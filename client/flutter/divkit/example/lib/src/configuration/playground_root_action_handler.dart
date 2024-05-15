import 'dart:async';

import 'package:divkit/divkit.dart';
import 'package:flutter/material.dart';

import '../pages/playground.dart';
import '../pages/samples.dart';
import '../pages/testing.dart';

const _openScreen = 'open_screen';
const _schemeDivAction = 'div-action';
const _activityDemo = 'demo';
const _activitySamples = 'samples';
const _activityRegression = 'regression';
const _activitySettings = 'settings';
const _paramActivity = 'activity';

class PlaygroundAppRootActionHandler implements DivActionHandler {
  final _typedHandler = DefaultDivActionHandlerTyped();
  final GlobalKey<NavigatorState> _navigator;

  NavigatorState get _navigationManager =>
      Navigator.of(_navigator.currentContext!);

  PlaygroundAppRootActionHandler({
    required GlobalKey<NavigatorState> navigator,
  }) : _navigator = navigator;

  @override
  bool canHandle(DivContext context, DivAction action) {
    if (_typedHandler.canHandle(context, action)) {
      return true;
    }

    final uri = action.url;
    if (uri != null) {
      if (uri.scheme == _schemeDivAction &&
          uri.host == _openScreen &&
          [
            _activityDemo,
            _activitySamples,
            _activityRegression,
            _activitySettings,
          ].contains(uri.queryParameters[_paramActivity])) {
        return true;
      }
    }

    return false;
  }

  @override
  FutureOr<bool> handleAction(
    DivContext context,
    DivAction action,
  ) async {
    if (_typedHandler.canHandle(context, action)) {
      return _typedHandler.handleAction(context, action);
    }

    final uri = action.url;
    if (uri == null) {
      return false;
    }

    return handleUrlAction(context, uri);
  }

  bool handleUrlAction(DivContext context, Uri uri) {
    if (uri.scheme != _schemeDivAction || uri.host != _openScreen) {
      return false;
    }
    switch (uri.queryParameters[_paramActivity]) {
      case _activityDemo:
        _navigationManager.push(
          MaterialPageRoute(
            builder: (_) => const PlaygroundPage(),
          ),
        );
        break;
      case _activitySamples:
        _navigationManager.push(
          MaterialPageRoute(
            builder: (_) => const SamplesPage(),
          ),
        );
        break;
      case _activityRegression:
        _navigationManager.push(
          MaterialPageRoute(
            builder: (_) => const TestingPage(),
          ),
        );
        break;
      case _activitySettings:
        // TODO: settings tab for Flutter playground
        showSnackBar('Action is not supported yet');
        break;
      default:
        return false;
    }

    return true;
  }

  void showSnackBar(String text) {
    ScaffoldMessenger.of(_navigator.currentContext!).hideCurrentSnackBar();
    ScaffoldMessenger.of(_navigator.currentContext!).showSnackBar(
      SnackBar(
        content: Text(text),
      ),
    );
  }
}
