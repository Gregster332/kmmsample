//
//  Flowable.swift
//  iosApp
//
//  Created by Greg Zenkov on 12/24/23.
//  Copyright © 2023 orgName. All rights reserved.
//

//import UIKit
//import SwiftUI
//
//protocol Flowable {
//    var window: UIWindow? { get }
//    
//    func start()
//    func setRoot(_ view: some View, isMakeRoot: Bool) -> UIViewController
//    func push(_ view: some View) -> UIViewController
//    func presentFullScreen(_ view: some View) -> UIViewController
//}
//
//class BaseCoordinator: Flowable {
//    var window: UIWindow?
//    
//    init(window: UIWindow?) {
//        self.window = window
//    }
//    
//    func start() {}
//}
//
//extension Flowable {
//    private var navigationController: UINavigationController? {
//        (window?.rootViewController as? UINavigationController)
//    }
//    
//    @discardableResult
//    func push(_ view: some View) -> UIViewController {
//        let hosting = UIHostingController(rootView: view)
//        navigationController?.pushViewController(hosting, animated: true)
//        return hosting
//    }
//    
//    @discardableResult
//    func presentFullScreen(_ view: some View) -> UIViewController {
//        let hosting = UIHostingController(rootView: view)
//        hosting.modalPresentationStyle = .fullScreen
//        navigationController?.topViewController?.present(hosting, animated: true)
//        return hosting
//    }
//    
//    @discardableResult
//    func setRoot(_ view: some View, isMakeRoot: Bool = false) -> UIViewController {
//        let hostingView = UIHostingController(rootView: view)
//        if isMakeRoot {
//            makeRoot(hostingView)
//        }
//        return hostingView
//    }
//    
//    func showCoordinator(
//        _ coordinator: some Flowable,
//        configureView: ((UIViewController) -> Void)?
//    ) {
//        coordinator.start()
//    }
//    
//    private func makeRoot(_ viewController: UIViewController) {
//        let navigationController = UINavigationController()
//        // navigationController.setNavigationBarHidden(true, animated: false)
//        // navigationController.isNavigationBarHidden = true
//        navigationController.navigationBar.isHidden = true
//        navigationController.setViewControllers([viewController], animated: true)
//        window?.rootViewController = navigationController
//    }
//}
//
//extension UIViewController {
//    func configure(completion: ((UIViewController) -> Void)? = nil) {
//        completion?(self)
//    }
//}
