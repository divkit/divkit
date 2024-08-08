import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/trace.dart';
import 'package:equatable/equatable.dart';

@Deprecated('Use DefaultDivKitData')
typedef DefaultLazyDivKitData = DefaultDivKitData;

class DefaultDivKitData extends DivKitData with EquatableMixin {
  final Map<String, dynamic> _card;
  final Map<String, dynamic>? _templates;

  DefaultDivKitData.fromJson(
    Map<String, dynamic> json,
  )   : _card = json['card']!,
        _templates = json['templates'];

  DefaultDivKitData.fromScheme({
    required Map<String, dynamic> card,
    Map<String, dynamic>? templates,
  })  : _card = card,
        _templates = templates;

  DivData? _source;

  @override
  DivData? get source => _source;

  @override
  bool get hasSource => _source != null;

  @override
  Future<DivKitData> build() async {
    _source ??= await traceAsyncFunc(
      'buildData [A]',
      () async => DivData.parse(
        await traceAsyncFunc(
          'mergeTemplate [A]',
          () => TemplatesResolver(
            layout: _card,
            templates: _templates,
          ).merge(),
        ),
      ),
    );
    return this;
  }

  @override
  DivKitData buildSync() {
    _source ??= traceFunc(
      'buildData [S]',
      () => DivData.fromJson(
        traceFunc(
          'mergeTemplate [S]',
          () => TemplatesResolver(
            layout: _card,
            templates: _templates,
          ).mergeSync(),
        ),
      ),
    );
    return this;
  }

  bool _preloaded = false;

  @override
  bool get preloaded => _preloaded;

  @override
  Future<DivKitData> preload({
    DivVariableStorage? variableStorage,
  }) async {
    if (source != null) {
      await traceAsyncFunc(
        'precacheExpressions',
        () => source!.preload(
          Map.fromEntries([
            ...?variableStorage?.value.values.map((v) => v.raw),
            ...source!.variables?.map((v) => v.pass.raw) ?? [],
          ]),
        ),
      );
      _preloaded = true;
    }
    return this;
  }

  @override
  List<Object?> get props => [_source];
}
