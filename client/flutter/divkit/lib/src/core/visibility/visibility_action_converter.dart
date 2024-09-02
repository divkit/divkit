import 'package:divkit/divkit.dart';

extension PassDivVisibilityAction on DivVisibilityAction {
  DivVisibilityActionModel value() {
    final action = DivActionModel(
      enabled: isEnabled.value!,
      url: url?.value!,
      typedAction: typed,
      payload: payload,
      downloadCallbacks: downloadCallbacks?.value(),
      logId: logId.value!,
    );
    return DivVisibilityActionModel(
      divAction: action,
      visibilityDuration: visibilityDuration.value!,
      visibilityPercentage: visibilityPercentage.value!,
    );
  }

  Future<DivVisibilityActionModel> resolve({
    required DivVariableContext context,
  }) async {
    final action = DivActionModel(
      enabled: await isEnabled.resolveValue(
        context: context,
      ),
      url: await url?.resolveValue(
        context: context,
      ),
      typedAction: typed,
      payload: payload,
      downloadCallbacks: await downloadCallbacks?.resolve(
        context: context,
      ),
      logId: await logId.resolveValue(
        context: context,
      ),
    );
    return DivVisibilityActionModel(
      divAction: action,
      visibilityDuration: await visibilityDuration.resolveValue(
        context: context,
      ),
      visibilityPercentage: await visibilityPercentage.resolveValue(
        context: context,
      ),
    );
  }
}
