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
  DivVisibilityActionModel convert() {
    return DivVisibilityActionModel(
      divAction: DivActionModel(
        enabled: isEnabled.value,
        url: url?.value,
        typedAction: typed,
        payload: payload,
        downloadCallbacks: downloadCallbacks?.convert(),
        logId: logId.value,
      ),
      visibilityDuration: visibilityDuration.value,
      visibilityPercentage: visibilityPercentage.value,
    );
  }
}
