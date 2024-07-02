import 'package:divkit/src/core/action/action.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;

extension PassDivAction on dto.DivAction {
  Future<DivAction> resolve({
    required DivVariableContext context,
  }) async =>
      DivAction(
        url: await url?.resolveValue(context: context),
        enabled: await isEnabled.resolveValue(context: context),
        typedAction: typed,
        payload: payload,
        downloadCallbacks: await downloadCallbacks?.resolve(context: context),
      );
}

extension PassDivDownloadCallbacks on dto.DivDownloadCallbacks {
  Future<DivDownloadCallbacks> resolve({
    required DivVariableContext context,
  }) async {
    List<DivAction>? success;
    if (onSuccessActions != null) {
      success = [];
      for (final a in onSuccessActions!) {
        success.add(await a.resolve(context: context));
      }
    }

    List<DivAction>? fail;
    if (onFailActions != null) {
      fail = [];
      for (final a in onFailActions!) {
        fail.add(await a.resolve(context: context));
      }
    }

    return DivDownloadCallbacks(
      onSuccessActions: success,
      onFailActions: fail,
    );
  }
}
