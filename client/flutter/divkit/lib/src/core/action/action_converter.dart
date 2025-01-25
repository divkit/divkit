import 'package:divkit/divkit.dart';

extension DivActionConverter on DivAction {
  DivActionModel resolve(DivVariableContext context) => DivActionModel(
        url: url?.resolve(context),
        enabled: isEnabled.resolve(context),
        typedAction: typed,
        payload: payload,
        logId: logId.resolve(context),
        downloadCallbacks: downloadCallbacks?.resolve(context),
      );
}

extension DivDownloadCallbacksConverter on DivDownloadCallbacks {
  DivDownloadCallbacksModel resolve(DivVariableContext context) {
    List<DivActionModel>? success;
    if (onSuccessActions != null) {
      success = [];
      for (final a in onSuccessActions!) {
        success.add(a.resolve(context));
      }
    }

    List<DivActionModel>? fail;
    if (onFailActions != null) {
      fail = [];
      for (final a in onFailActions!) {
        fail.add(a.resolve(context));
      }
    }

    return DivDownloadCallbacksModel(
      onSuccessActions: success,
      onFailActions: fail,
    );
  }
}
