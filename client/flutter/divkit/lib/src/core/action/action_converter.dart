import 'package:divkit/divkit.dart';

extension DivActionConverter on DivAction {
  DivActionModel convert() => DivActionModel(
        url: url?.value,
        enabled: isEnabled.value,
        typedAction: typed,
        payload: payload,
        logId: logId.value,
        downloadCallbacks: downloadCallbacks?.convert(),
      );
}

extension DivDownloadCallbacksConverter on DivDownloadCallbacks {
  DivDownloadCallbacksModel convert() {
    List<DivActionModel>? success;
    if (onSuccessActions != null) {
      success = [];
      for (final a in onSuccessActions!) {
        success.add(a.convert());
      }
    }

    List<DivActionModel>? fail;
    if (onFailActions != null) {
      fail = [];
      for (final a in onFailActions!) {
        fail.add(a.convert());
      }
    }

    return DivDownloadCallbacksModel(
      onSuccessActions: success,
      onFailActions: fail,
    );
  }
}
