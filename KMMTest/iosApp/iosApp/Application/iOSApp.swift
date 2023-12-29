import SwiftUI
import SharedModule
import Stinsen
import UIElements

@main
struct MainApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @AppStorage("app_theme") private var appTheme: AppTheme = .systemDefault
    
    init() {
        KoinKt.doInitKoinIOS()
    }
    
    var body: some Scene {
        WindowGroup {
            DefaultAuthCoordinator().view()
                .preferredColorScheme(appTheme.scheme)
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    
    var window: UIWindow?

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        self.window = UIWindow(frame: UIScreen.main.bounds)
        return true
    }
}
