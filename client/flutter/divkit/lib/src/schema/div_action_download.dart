// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Loads additional data in `div-patch` format and updates the current element.
class DivActionDownload extends Resolvable with EquatableMixin {
  const DivActionDownload({
    this.onFailActions,
    this.onSuccessActions,
    required this.url,
  });

  static const type = "download";

  /// Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
  final Arr<DivAction>? onFailActions;

  /// Actions in case of successful loading.
  final Arr<DivAction>? onSuccessActions;

  /// Link for receiving changes.
  final Expression<String> url;

  @override
  List<Object?> get props => [
        onFailActions,
        onSuccessActions,
        url,
      ];

  DivActionDownload copyWith({
    Arr<DivAction>? Function()? onFailActions,
    Arr<DivAction>? Function()? onSuccessActions,
    Expression<String>? url,
  }) =>
      DivActionDownload(
        onFailActions:
            onFailActions != null ? onFailActions.call() : this.onFailActions,
        onSuccessActions: onSuccessActions != null
            ? onSuccessActions.call()
            : this.onSuccessActions,
        url: url ?? this.url,
      );

  static DivActionDownload? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionDownload(
        onFailActions: safeParseObjects(
          json['on_fail_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        onSuccessActions: safeParseObjects(
          json['on_success_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        url: reqVProp<String>(
          safeParseStrExpr(
            json['url'],
          ),
          name: 'url',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionDownload resolve(DivVariableContext context) {
    tryResolveList(onFailActions, (v) => v.resolve(context));
    tryResolveList(onSuccessActions, (v) => v.resolve(context));
    url.resolve(context);
    return this;
  }
}
