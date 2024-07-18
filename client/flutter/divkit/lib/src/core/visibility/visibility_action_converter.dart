import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/core/visibility/models/visibility_action.dart';
import 'package:divkit/src/core/action/action_converter.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;
import 'package:divkit/src/core/action/models/action.dart';

extension PassDivVisibilityAction on dto.DivVisibilityAction {
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
