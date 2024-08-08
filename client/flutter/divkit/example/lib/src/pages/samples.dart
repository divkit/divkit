import 'dart:convert';
import 'dart:math';

import 'package:divkit/divkit.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Box {
  final RootIsolateToken token;
  final List<String> data;

  Box(this.data) : token = ServicesBinding.rootIsolateToken!;
}

Future<List<DivKitData>> process(Box box) async {
  // Register the background isolate with the root isolate.
  BackgroundIsolateBinaryMessenger.ensureInitialized(box.token);

  final it = <DivKitData>[];
  for (final str in box.data) {
    final json = jsonDecode(str);
    final data = DefaultDivKitData.fromJson(json);
    await data.build();
    await data.preload();
    it.add(data);
  }
  return it;
}

Future<List<DivKitData>> loadList() async {
  final Map<String, dynamic> manifestMap = json.decode(
    await rootBundle.loadString('AssetManifest.json'),
  );
  manifestMap.removeWhere(
    (key, value) =>
        !key.startsWith('assets/test_data/samples') || !key.endsWith('json'),
  );

  List<String> strings = [];
  for (var el in manifestMap.keys) {
    strings.add(await rootBundle.loadString(
      el.replaceAll('../', ''),
    ));
  }

  return compute<Box, List<DivKitData>>(process, Box(strings));
}

class SamplesPage extends StatelessWidget {
  const SamplesPage({super.key});

  @override
  Widget build(BuildContext context) => Scaffold(
        backgroundColor: const Color(0xFFA09DE5),
        body: FutureBuilder<List<DivKitData>>(
          future: loadList(),
          builder: (_, snapshot) {
            if (snapshot.hasError) {
              return Center(
                child: Text(snapshot.error.toString()),
              );
            }
            if (snapshot.hasData) {
              final data = snapshot.data;
              if (data != null) {
                return CustomScrollView(
                  slivers: [
                    SliverAppBar(
                      foregroundColor: Colors.black,
                      floating: false,
                      pinned: true,
                      expandedHeight: 220,
                      flexibleSpace: LayoutBuilder(
                        builder: (context, constraints) {
                          final percent =
                              (constraints.maxHeight - kToolbarHeight) *
                                  (100 / (220 - kToolbarHeight));

                          final dx = max(100 - percent, 16.0);

                          return ClipPath(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Padding(
                                  padding: EdgeInsets.only(
                                    left: dx,
                                    top: kToolbarHeight / 4 +
                                        min(constraints.maxHeight, 160) -
                                        kToolbarHeight,
                                  ),
                                  child: const Text(
                                    'Samples',
                                    style: TextStyle(
                                      fontSize: 24,
                                      fontWeight: FontWeight.w500,
                                    ),
                                  ),
                                ),
                                Flexible(
                                  child: Container(
                                    height: 100,
                                    padding:
                                        const EdgeInsets.fromLTRB(16, 8, 16, 0),
                                    child: const Text(
                                      'Here you can check, how Div technology might be useful for your projects',
                                      style: TextStyle(
                                        fontSize: 20,
                                      ),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                      backgroundColor: const Color(0xFFA09DE5),
                    ),
                    SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (context, i) => Container(
                          decoration: BoxDecoration(
                            borderRadius: i == 0
                                ? const BorderRadius.only(
                                    topLeft: Radius.circular(30),
                                    topRight: Radius.circular(30),
                                  )
                                : null,
                            color: Colors.white,
                          ),
                          child: DivKitView(
                            showUnsupportedDivs: true,
                            data: data[i],
                          ),
                        ),
                        childCount: data.length,
                      ),
                    ),
                  ],
                );
              }
            }
            return const Center(
              child: CircularProgressIndicator(),
            );
          },
        ),
      );
}
