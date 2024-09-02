import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';
import 'package:logging/logging.dart';

final logger = DefaultDivLogger._();

DivLogger loggerUse(DivLoggerContext? context) => IntegratedDivLogger(
      logger,
      context,
    );

DivLogger loggerOf(BuildContext context) => loggerUse(
      read<DivLoggerContext>(context),
    );

abstract class DivLoggerContext {
  String? get metaInfo;
}

class DefaultDivLoggerContext implements DivLoggerContext {
  @override
  final String? metaInfo;

  const DefaultDivLoggerContext(this.metaInfo);
}

abstract class DivLogger {
  const DivLogger();

  /// Log a message at level [Level.FINE].
  void debug(dynamic message, {Object? error, StackTrace? stackTrace});

  /// Log a message at level [Level.INFO].
  void info(dynamic message, {Object? error, StackTrace? stackTrace});

  /// Log a message at level [Level.WARNING].
  void warning(dynamic message, {Object? error, StackTrace? stackTrace});

  /// Log a message at level [Level.SEVERE].
  void error(message, {Object? error, StackTrace? stackTrace});
}

extension on Level {
  String get sign {
    switch (value) {
      case 500:
        return '👨‍💻Debug';
      case 800:
        return '📝Info';
      case 900:
        return '⚠Warning';
      case 1000:
        return '🚨Error';
      default:
        return '';
    }
  }
}

class DefaultDivLogger implements DivLogger {
  final _logger = Logger('DivKit');

  bool keepLog = true;

  void Function(String)? onLog;

  DefaultDivLogger._() {
    hierarchicalLoggingEnabled = true;
    _logger.level = Level.ALL;
    _logger.onRecord.listen((record) {
      onLog?.call(prepareLog(record));
    });
  }

  String prepareLog(LogRecord record) {
    if (record.error != null) {
      return '️${record.level.sign}: ${record.time}: ${record.message} (${record.error})\nStackTrace: ${record.stackTrace}';
    }

    return '️${record.level.sign}: ${record.time}: ${record.message}';
  }

  @override
  void debug(message, {Object? error, StackTrace? stackTrace}) {
    if (keepLog) {
      _logger.fine(message, error, stackTrace);
    }
  }

  @override
  void info(message, {Object? error, StackTrace? stackTrace}) {
    if (keepLog) {
      _logger.info(message, error, stackTrace);
    }
  }

  @override
  void warning(message, {Object? error, StackTrace? stackTrace}) {
    if (keepLog) {
      _logger.warning(message, error, stackTrace);
    }
  }

  @override
  void error(message, {Object? error, StackTrace? stackTrace}) {
    if (keepLog) {
      _logger.severe(message, error, stackTrace);
    }
  }
}

class IntegratedDivLogger implements DivLogger {
  final DivLogger logger;
  final DivLoggerContext? context;

  const IntegratedDivLogger(
    this.logger,
    this.context,
  );

  @override
  void debug(message, {Object? error, StackTrace? stackTrace}) => logger.debug(
        "[${context?.metaInfo ?? 'main'}] $message",
        error: error,
        stackTrace: stackTrace,
      );

  @override
  void info(message, {Object? error, StackTrace? stackTrace}) => logger.info(
        "[${context?.metaInfo ?? 'main'}] $message",
        error: error,
        stackTrace: stackTrace,
      );

  @override
  void warning(message, {Object? error, StackTrace? stackTrace}) =>
      logger.warning(
        "[${context?.metaInfo ?? 'main'}] $message",
        error: error,
        stackTrace: stackTrace,
      );

  @override
  void error(message, {Object? error, StackTrace? stackTrace}) => logger.error(
        "[${context?.metaInfo ?? 'main'}] $message",
        error: error,
        stackTrace: stackTrace,
      );
}
