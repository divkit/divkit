Pod::Spec.new do |s|
  s.name             = 'DivKitKMP'
  s.version          = '32.12.0'
  s.summary          = 'DivKit obj-C interface for Kotlin Multiplatform applications'
  s.description      = 'DivKit obj-C interface for Kotlin Multiplatform applications'
  s.homepage         = 'https://divkit.tech'

  s.license          = { :type => 'Apache License, Version 2.0', :file => 'LICENSE' }
  s.author           = { 'divkit' => 'divkit@yandex-team.ru' }
  s.source           = { :git => 'https://github.com/divkit/divkit-ios.git', :tag => s.version.to_s }

  s.swift_version = '5.9'
  s.requires_arc = true
  s.prefix_header_file = false
  s.platforms = { :ios => '13.0' }

  s.dependency 'DivKit', s.version.to_s
  s.dependency 'DivKitExtensions', s.version.to_s

  s.source_files = [
    'DivKitKMP/**/*'
  ]
end
