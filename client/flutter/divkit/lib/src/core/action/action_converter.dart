import 'package:divkit/src/core/action/models/action.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';
import 'package:divkit/src/core/action/models/download_callbacks.dart';

extension PassDivAction on DivAction {
  Future<DivActionModel> resolve({
    required DivVariableContext context,
  }) async =>
      DivActionModel(
        url: await url?.resolveValue(context: context),
        enabled: await isEnabled.resolveValue(context: context),
        typedAction: typed,
        payload: payload,
        logId: await logId.resolveValue(context: context),
        downloadCallbacks: await downloadCallbacks?.resolve(context: context),
      );
}

extension PassDivDownloadCallbacks on DivDownloadCallbacks {
  Future<DivDownloadCallbacksModel> resolve({
    required DivVariableContext context,
  }) async {
    List<DivActionModel>? success;
    if (onSuccessActions != null) {
      success = [];
      for (final a in onSuccessActions!) {
        success.add(await a.resolve(context: context));
      }
    }

    List<DivActionModel>? fail;
    if (onFailActions != null) {
      fail = [];
      for (final a in onFailActions!) {
        fail.add(await a.resolve(context: context));
      }
    }

    return DivDownloadCallbacksModel(
      onSuccessActions: success,
      onFailActions: fail,
    );
  }
}
