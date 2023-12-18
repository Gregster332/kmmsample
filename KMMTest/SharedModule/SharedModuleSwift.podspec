Pod::Spec.new do |spec|
    spec.name                     = 'SharedModuleSwift'
    spec.version                  = '1.0'
    spec.homepage                 = 'Link to a Kotlin/Native module homepage'
    spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Some description for a Kotlin/Native module'
    spec.module_name              = "SharedModuleSwift"
    
    spec.ios.deployment_target  = '15.0'
    spec.static_framework         = false
    spec.dependency 'SharedModule'
    spec.source_files = "build/cocoapods/framework/SharedModuleSwift/**/*.{h,m,swift}"
end