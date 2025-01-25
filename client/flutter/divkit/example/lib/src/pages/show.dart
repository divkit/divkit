import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:divkit/divkit.dart';
import 'package:example/src/configuration/playground_custom_handler.dart';
import 'package:example/src/configuration/playground_font_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'package:example/src/state.dart';

class ShowModel {
  const ShowModel();
}

class UrlShow extends ShowModel {
  final Uri url;

  const UrlShow(this.url);
}

class ObjShow extends ShowModel {
  final Obj obj;

  const ObjShow(this.obj);
}

class UpdateResponse {
  final String? eTag;
  final Obj? data;

  const UpdateResponse(this.eTag, this.data);
}

Future<UpdateResponse> fetch(Uri uri, [String? eTag]) async {
  try {
    final client = HttpClient();
    final request = await client.getUrl(uri);
    if (eTag != null) {
      request.headers.add('If-None-Match', eTag);
    }
    final response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      final data = await response.transform(utf8.decoder).join();
      return UpdateResponse(response.headers.value('ETag'), jsonDecode(data));
    } else if (response.statusCode == HttpStatus.notModified) {
      return UpdateResponse(eTag, null);
    } else {
      throw 'HTTP request failed, status: ${response.statusCode}';
    }
  } catch (e) {
    throw 'Error fetching data: $e';
  }
}

DivKitData buildData(Obj json) {
  return DefaultDivKitData.fromScheme(
    card: json.containsKey('card') ? json['card'] : json,
    templates: json['templates'],
  ).build();
}

class ShowPage extends ConsumerStatefulWidget {
  final ShowModel mode;

  const ShowPage(this.mode, {super.key});

  @override
  ConsumerState<ShowPage> createState() => _ShowPageState();
}

class _ShowPageState extends ConsumerState<ShowPage> {
  String? eTag;

  Timer? timer;
  DivKitData? data;

  @override
  void initState() {
    super.initState();

    if (widget.mode is ObjShow) {
      data = buildData((widget.mode as ObjShow).obj);
    } else if (widget.mode is UrlShow) {
      final url = (widget.mode as UrlShow).url;

      Future(() async {
        final value = await fetch(url);
        if (mounted && value.data != null) {
          setState(() {
            eTag = value.eTag;
            data = buildData(value.data!);
          });
        }
      });

      final polling = ref.read(pollingIntervalProvider);
      if (polling != null) {
        timer = Timer.periodic(
          Duration(milliseconds: polling),
          (_) async {
            final value = await fetch(url, eTag);
            if (mounted && value.data != null) {
              setState(() {
                eTag = value.eTag;
                data = buildData(value.data!);
              });
            }
          },
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final isNightModeEnabled = ref.watch(nightModeProvider);
    final isRtlEnabled = ref.watch(isRTLProvider);
    final reloadN = ref.watch(reloadNProvider);
    final mq = MediaQuery.of(context);

    return Scaffold(
      appBar: AppBar(
        title: FittedBox(
          child: Row(
            children: [
              Text("${mq.size.width.round()}x${mq.size.height.round() - 64}"),
              const SizedBox(width: 32),
              ElevatedButton(
                child: const Text('reset'),
                onPressed: () {
                  ref.read(reloadNProvider.notifier).state = reloadN + 1;
                },
              ),
              const SizedBox(width: 8),
              ElevatedButton(
                child: Text(isNightModeEnabled ? 'dark' : 'light'),
                onPressed: () {
                  ref.read(nightModeProvider.notifier).state =
                      !isNightModeEnabled;
                },
              ),
              const SizedBox(width: 8),
              ElevatedButton(
                child: Text(isRtlEnabled ? 'RTL' : 'LTR'),
                onPressed: () {
                  ref.read(isRTLProvider.notifier).state = !isRtlEnabled;
                },
              ),
            ],
          ),
        ),
      ),
      body: data != null
          ? SingleChildScrollView(
              child: DivKitView(
                key: ObjectKey(reloadN),
                data: data!,
                customHandler: PlaygroundAppCustomHandler(),
                fontProvider: PlaygroundDivFontProvider(),
              ),
            )
          : const Center(
              child: CircularProgressIndicator(),
            ),
    );
  }

  @override
  void dispose() {
    timer?.cancel();
    super.dispose();
  }
}
