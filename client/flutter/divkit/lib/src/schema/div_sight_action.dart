// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_typed.dart';
import 'package:divkit/src/schema/div_download_callbacks.dart';
import 'package:divkit/src/utils/parsing_utils.dart';

abstract class DivSightAction extends Preloadable {
  DivDownloadCallbacks? get downloadCallbacks;

  // default value: true
  Expression<bool> get isEnabled;

  Expression<String> get logId;

  // constraint: number >= 0; default value: 1
  Expression<int> get logLimit;

  Map<String, dynamic>? get payload;

  Expression<Uri>? get referer;

  DivActionTyped? get typed;

  Expression<Uri>? get url;
  @override
  Future<void> preload(Map<String, dynamic> context);
}
