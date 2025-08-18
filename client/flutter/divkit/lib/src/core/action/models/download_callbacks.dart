import 'package:divkit/src/core/action/models/action.dart';
import 'package:equatable/equatable.dart';

class DivDownloadCallbacksModel with EquatableMixin {
  const DivDownloadCallbacksModel({
    this.onFailActions,
    this.onSuccessActions,
  });

  final List<DivActionModel>? onFailActions;

  final List<DivActionModel>? onSuccessActions;

  @override
  List<Object?> get props => [
        onFailActions,
        onSuccessActions,
      ];
}
