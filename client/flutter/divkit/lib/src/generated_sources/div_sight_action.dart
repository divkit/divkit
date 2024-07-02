// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_action_typed.dart';
import 'package:divkit/src/generated_sources/div_download_callbacks.dart';

abstract class DivSightAction {
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
}
