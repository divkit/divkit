import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';

class DivActionModel with EquatableMixin {
  final Uri? url;
  final bool enabled;
  final DivActionTyped? typedAction;
  final Map<String, dynamic>? payload;
  final DivDownloadCallbacksModel? downloadCallbacks;
  final String logId;

  const DivActionModel({
    required this.enabled,
    required this.logId,
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
        logId,
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
