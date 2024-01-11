import SwiftUI
import SharedModule
import UIElements

@main
struct MainApp: App {
    @AppStorage("app_theme") private var appTheme: AppTheme = .systemDefault
    @ObservedObject private var coordinator = Coordinator.shared
    
    init() {
        let kk = Multiplatform_settingsKeychainSettings(service: "default_serv")
        IosMainDIKt.doInitKoinIOS(settings: kk)
    }
    
    var body: some Scene {
        WindowGroup {
            //NavigationStack(path: $coordinator.path) {
                SplashView()
                .preferredColorScheme(appTheme.scheme)
//                    .navigationDestination(for: Coordinator.Path.self) { path in
//                        switch path {
//                        case .auth:
//                            AuthView()
//                        case .main:
//                            MainView()
//                        }
//                    }
            }
            
    }
}
