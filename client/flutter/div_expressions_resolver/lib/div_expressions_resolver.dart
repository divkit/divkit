import 'dart:async';

import 'package:flutter/services.dart';

/// Allows you to calculate expressions according to the
/// [DivKit specification](https://divkit.tech/docs/en/concepts/expressions)
/// Uses native implementations for its work.
class NativeDivExpressionsResolver {
  static const MessageCodec<Object?> _channelCodec = _NativeDivExpressionsResolverCodec();

  final BinaryMessenger? _binaryMessenger;

  /// Constructor for [NativeDivExpressionsResolver]. The [binaryMessenger] named argument is
  /// available for dependency injection. If it is left null, the default
  /// BinaryMessenger will be used which routes to the host platform.
  NativeDivExpressionsResolver({BinaryMessenger? binaryMessenger})
      : _binaryMessenger = binaryMessenger;

  /// Allows you to calculate an [expression] using variable data from the [context].
  Future<Object?> resolve(
    String expression, {
    Map<String, dynamic> context = const {},
  }) async {
    const channelName = 'div_expressions_resolver.NativeDivExpressionsResolver.resolve';
    final channel = BasicMessageChannel<Object?>(
      channelName,
      _channelCodec,
      binaryMessenger: _binaryMessenger,
    );
    final List<Object?>? replyList =
        await channel.send(<Object?>[expression, context]) as List<Object?>?;
    if (replyList == null) {
      throw _createConnectionError(channelName);
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return replyList[0];
    }
  }

  /// Allows you to reset the context. It is only used for the Android version.
  Future<void> clearVariables() async {
    const channelName =
        'div_expressions_resolver.NativeDivExpressionsResolver.clearVariables';
    final channel = BasicMessageChannel<Object?>(
      channelName,
      _channelCodec,
      binaryMessenger: _binaryMessenger,
    );
    final List<Object?>? replyList = await channel.send(null) as List<Object?>?;
    if (replyList == null) {
      throw _createConnectionError(channelName);
    } else if (replyList.length > 1) {
      throw PlatformException(
        code: replyList[0]! as String,
        message: replyList[1] as String?,
        details: replyList[2],
      );
    } else {
      return;
    }
  }

  PlatformException _createConnectionError(String channelName) {
    return PlatformException(
      code: 'channel-error',
      message: 'Unable to establish connection on channel: "$channelName".',
    );
  }
}

/// The code allows you to make a complete interop of the context.
class _NativeDivExpressionsResolverCodec extends StandardMessageCodec {
  const _NativeDivExpressionsResolverCodec();

  @override
  void writeValue(WriteBuffer buffer, Object? value) {
    if (value is Color) {
      buffer.putUint8(128);
      writeValue(buffer, _encodeColor(value));
    } else if (value is Uri) {
      buffer.putUint8(129);
      writeValue(buffer, _encodeUri(value));
    } else {
      super.writeValue(buffer, value);
    }
  }

  @override
  Object? readValueOfType(int type, ReadBuffer buffer) {
    switch (type) {
      case 128:
        return _decodeColor(readValue(buffer)!);
      case 129:
        return _decodeUri(readValue(buffer)!);
      default:
        return super.readValueOfType(type, buffer);
    }
  }

  Object _encodeColor(Color input) => <Object?>[
    input.value,
  ];

  Color _decodeColor(Object result) => Color(
    (result as List<Object?>)[0]! as int,
  );

  Object _encodeUri(Uri input) => <Object?>[
    input.toString(),
  ];

  Uri _decodeUri(Object result) => Uri.parse(
    (result as List<Object?>)[0]! as String,
  );
}
