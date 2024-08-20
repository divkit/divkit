Pod::Spec.new do |s|
  s.name             = 'div_expressions_resolver'
  s.version          = '0.4.3'
  s.summary          = 'Flutter DivKit expressions resolver'
  s.description      = 'DivKit expression resolver implementation for Flutter'
  s.homepage         = 'https://github.com/divkit/divkit/tree/main/client/flutter/div_expressions_resolver'
  s.author           = { 'divkit' => 'divkit@yandex-team.ru' }
  
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  
  s.dependency 'Flutter'
  s.dependency 'DivKit', '< 31.0', '>= 29.0'
  s.platform = :ios, '14.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
