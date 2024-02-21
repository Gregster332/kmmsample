import SwiftUI
import SharedModule
import UIElements

@main
struct MainApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        let deviceSensorApi = DeviceSensorApiImpl()
        IosMainDIKt.doInitKoinIOS(deviceSensorApi: deviceSensorApi)
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

extension UIDevice {
    static let deviceShakeNotification = Notification.Name(rawValue: "deviceDidShakeNotification")
}

extension UIWindow {
    override open func motionEnded(_ motion: UIEvent.EventSubtype, with event: UIEvent?) {
        guard case .motionShake = motion else { return }
        NotificationCenter.default.post(
            name: UIDevice.deviceShakeNotification,
            object: nil
        )
    }
}

final class DeviceSensorApiImpl: DeviceSensorApi {
    
    private var listener: () -> Void = {}
    
    func setSensorListener(listener: @escaping () -> Void) {
        self.listener = listener
    }
    
    func start() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(deviceDidMotion),
            name: Notification.Name(rawValue: "deviceDidShakeNotification"),
            object: nil
        )
    }
    
    func stop() {}
    
    @objc private func deviceDidMotion() {
        listener()
    }
}
