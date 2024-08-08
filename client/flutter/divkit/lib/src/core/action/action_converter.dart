import 'package:divkit/divkit.dart';

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

  DivActionModel value() => DivActionModel(
        url: url?.value,
        enabled: isEnabled.value!,
        typedAction: typed,
        payload: payload,
        logId: logId.value!,
        downloadCallbacks: downloadCallbacks?.value(),
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

  DivDownloadCallbacksModel value() {
    List<DivActionModel>? success;
    if (onSuccessActions != null) {
      success = [];
      for (final a in onSuccessActions!) {
        success.add(a.value());
      }
    }

    List<DivActionModel>? fail;
    if (onFailActions != null) {
      fail = [];
      for (final a in onFailActions!) {
        fail.add(a.value());
      }
    }

    return DivDownloadCallbacksModel(
      onSuccessActions: success,
      onFailActions: fail,
    );
  }
}
