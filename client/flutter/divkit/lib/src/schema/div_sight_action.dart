// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_typed.dart';
import 'package:divkit/src/schema/div_download_callbacks.dart';
import 'package:divkit/src/utils/parsing.dart';

abstract class DivSightAction extends Resolvable {
  DivDownloadCallbacks? get downloadCallbacks;

  // default value: true
  Expression<bool> get isEnabled;

  Expression<String> get logId;

  // constraint: number >= 0; default value: 1
  Expression<int> get logLimit;

  Obj? get payload;

  Expression<Uri>? get referer;

  String? get scopeId;

  DivActionTyped? get typed;

  Expression<Uri>? get url;
  @override
  DivSightAction resolve(DivVariableContext context);
}
