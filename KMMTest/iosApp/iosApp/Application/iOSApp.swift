import SwiftUI
import SharedModule
import UIElements

@main
struct MainApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        let kk = Multiplatform_settingsKeychainSettings(service: "default_serv")
        IosMainDIKt.doInitKoinIOS(settings: kk)
    }
    
    var body: some Scene {
        WindowGroup {
            SplashView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
  func application(_ application: UIApplication,
                   didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
    return true
  }
}


extension AppThemSwitcherView.AppTheme {
    var scheme: ColorScheme? {
        switch self {
        case .systemDefault:
            return nil
        case .dark:
            return .dark
        case .light:
            return .light
        }
    }
}
