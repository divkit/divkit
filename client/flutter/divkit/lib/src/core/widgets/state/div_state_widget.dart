import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/state/state_id.dart';
import 'package:divkit/src/core/widgets/state/div_state_model.dart';
import 'package:divkit/src/utils/mapping_widget.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';
import 'package:rxdart/rxdart.dart';

class DivStateWidget extends DivMappingWidget<DivState, DivStateModel> {
  const DivStateWidget(
    super.data, {
    super.key,
  });

  @override
  DivStateModel value(BuildContext context) {
    read<DivContext>(context)!
        .stateManager
        .registerState(data.resolveDivId(context));
    return data.resolve(context);
  }

  @override
  Stream<DivStateModel> stream(BuildContext context) {
    final divContext = watch<DivContext>(context)!;
    return CombineLatestStream.combine2(
      divContext.variableManager.contextStream,
      divContext.stateManager.statesStream,
      (values, states) => data.resolve(context),
    );
  }

  @override
  Widget build(BuildContext context, DivStateModel model) {
    return DivBaseWidget(
      data: data,
      child: provide(
        DivParentData.none,
        child: provide(
          DivStateId(model.divId),
          child: DivWidget(
            // The unique identifier of the subtree state
            key: ValueKey(model.path), model.state?.div,
          ),
        ),
      ),
    );
  }
}
