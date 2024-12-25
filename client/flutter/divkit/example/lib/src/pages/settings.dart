import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../state.dart';

class SettingsPage extends ConsumerStatefulWidget {
  const SettingsPage({super.key});

  @override
  ConsumerState<SettingsPage> createState() => _SettingsPage();
}

class _SettingsPage extends ConsumerState<SettingsPage> {
  @override
  Widget build(BuildContext context) {
    final polling = ref.watch(pollingIntervalProvider);

    return Scaffold(
        appBar: AppBar(
          backgroundColor: const Color(0xff000000),
          leading: IconButton(
            icon: const Icon(
              Icons.arrow_back,
              color: Colors.white,
            ),
            onPressed: () {
              Navigator.of(context).pop();
            },
          ),
          title: const Text(
            'Settings',
            style: TextStyle(
              fontSize: 24,
              color: Colors.white,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        backgroundColor: const Color(0xffffffff),
        body: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            children: [
              Container(
                decoration: BoxDecoration(
                  color: const Color(0xffe7e4ef),
                  borderRadius: BorderRadius.circular(24),
                ),
                child: Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.fromLTRB(16.0, 8.0, 16.0, 0.0),
                      child: Row(
                        children: [
                          const Text(
                            'Preview url polling',
                            style: TextStyle(
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const Spacer(),
                          Switch(
                            value: polling != null,
                            onChanged: (value) {
                              if (!value) {
                                ref
                                    .read(pollingIntervalProvider.notifier)
                                    .update(null);
                              } else {
                                ref
                                    .read(pollingIntervalProvider.notifier)
                                    .update(1000);
                              }
                            },
                          ),
                        ],
                      ),
                    ),
                    const Divider(),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(16.0, 0.0, 16.0, 8.0),
                      child: Opacity(
                        opacity: polling == null ? 0.5 : 1.0,
                        child: Row(
                          children: [
                            SizedBox(
                              width: 100,
                              child: Text(
                                '${polling ?? 1000} ms',
                                style: const TextStyle(
                                  fontSize: 18,
                                ),
                              ),
                            ),
                            Expanded(
                              child: IgnorePointer(
                                ignoring: polling == null,
                                child: Slider(
                                  min: 100,
                                  divisions: 49,
                                  max: 5000,
                                  value: (polling ?? 1000).toDouble(),
                                  onChanged: (v) {
                                    ref
                                        .read(pollingIntervalProvider.notifier)
                                        .update(
                                          v.toInt(),
                                        );
                                  },
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              )
            ],
          ),
        ));
  }
}
