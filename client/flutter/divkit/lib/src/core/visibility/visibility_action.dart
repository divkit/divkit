import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';

class DivVisibilityActionModel with EquatableMixin {
  const DivVisibilityActionModel({
    required this.divAction,
    required this.visibilityDuration,
    required this.visibilityPercentage,
  });

  final DivActionModel divAction;

  final int visibilityDuration;

  final int visibilityPercentage;

  @override
  List<Object?> get props => [
        divAction,
        visibilityDuration,
        visibilityPercentage,
      ];
}

extension DivVisibilityConverter on DivVisibilityAction {
  DivVisibilityActionModel resolve(DivVariableContext context) {
    return DivVisibilityActionModel(
      divAction: DivActionModel(
        enabled: isEnabled.resolve(context),
        url: url?.resolve(context),
        typedAction: typed,
        payload: payload,
        downloadCallbacks: downloadCallbacks?.resolve(context),
        logId: logId.resolve(context),
      ),
      visibilityDuration: visibilityDuration.resolve(context),
      visibilityPercentage: visibilityPercentage.resolve(context),
    );
  }
}
