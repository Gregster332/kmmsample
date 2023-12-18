import SwiftUI
import SharedModule

@UIApplicationMain
class AppDelegate: NSObject, UIApplicationDelegate {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        return true
    }

    private func setupMyApp() {
    }
}

final class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?
    private lazy var flowController = FlowCoordinator(window: window!)
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        KoinKt.doInitKoinIOS()
        guard let windowScene = (scene as? UIWindowScene) else { return }
        
        window = UIWindow(windowScene: windowScene)
        flowController.showRootView()
        window?.makeKeyAndVisible()
    }
    
    func sceneDidDisconnect(_ scene: UIScene) {}
    
    func sceneDidBecomeActive(_ scene: UIScene) {}
    
    func sceneWillResignActive(_ scene: UIScene) {}
    
    func sceneWillEnterForeground(_ scene: UIScene) {}
    
    func sceneDidEnterBackground(_ scene: UIScene) {}
}

protocol Flowable {
    var window: UIWindow? { get }
    
    func setRoot(_ view: some View) -> UIViewController
    func push(_ view: some View) -> UIViewController
    func presentFullScreen(_ view: some View) -> UIViewController
}


extension Flowable {
    
    private var navigationController: UINavigationController? {
        (window?.rootViewController as? UINavigationController)
    }
    
    func push(_ view: some View) -> UIViewController {
        let hosting = UIHostingController(rootView: view)
        navigationController?.pushViewController(hosting, animated: true)
        return hosting
    }
    
    func presentFullScreen(_ view: some View) -> UIViewController {
        let hosting = UIHostingController(rootView: view)
        hosting.modalPresentationStyle = .fullScreen
        navigationController?.topViewController?.present(hosting, animated: true)
        return hosting
    }
    
    func setRoot(_ view: some View) -> UIViewController {
        let hostingView = UIHostingController(rootView: view)
        makeRoot(hostingView)
        return hostingView
    }
    
    private func makeRoot(_ viewController: UIViewController) {
        let navigationController = UINavigationController()
        navigationController.setViewControllers([viewController], animated: true)
        window?.rootViewController = navigationController
    }
}

extension UIViewController {
    
    func configure(completion: (UIViewController?) -> Void) {
        completion(self)
    }
}
