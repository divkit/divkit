import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/generated_sources/div_action_typed.dart';
import 'package:equatable/equatable.dart';

class DivAction with EquatableMixin {
  final Uri? url;
  final bool enabled;
  final DivActionTyped? typedAction;
  final Map<String, dynamic>? payload;
  final DivDownloadCallbacks? downloadCallbacks;

  const DivAction({
    required this.enabled,
    this.url,
    this.typedAction,
    this.payload,
    this.downloadCallbacks,
  });

  @override
  List<Object?> get props => [
        url,
        enabled,
        typedAction,
        payload,
        downloadCallbacks,
      ];

  Future<void> execute(DivContext context) async {
    if (!enabled) {
      return;
    }

    final handler = context.actionHandler;
    if (handler.canHandle(context, this)) {
      await handler.handleAction(context, this);
    }
  }
}

class DivDownloadCallbacks with EquatableMixin {
  const DivDownloadCallbacks({
    this.onFailActions,
    this.onSuccessActions,
  });

  final List<DivAction>? onFailActions;

  final List<DivAction>? onSuccessActions;

  @override
  List<Object?> get props => [
        onFailActions,
        onSuccessActions,
      ];
}
