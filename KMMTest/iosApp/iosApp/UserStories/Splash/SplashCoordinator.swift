//
//  SplashCoordinator.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/5/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

//protocol SplashCoordinator: NavigationCoordinatable {
//    func openMain()
//    func openAuth()
//}
//
//final class DefaultSplashCoordinator: SplashCoordinator {
//    let stack = NavigationStack(initial: \DefaultSplashCoordinator.start)
//    
//    @Root var start = makeStart
//    @Root var main = makeMain
//    @Route(.push) var auth = makeAuth
//    
//    func openMain() {
//        self.root(\.main)
//    }
//    
//    func openAuth() {
//        self.route(to: \.auth)
//    }
//    
//    private func makeStart() -> some View {
//        SplashView()
//    }
//    
//    private func makeAuth() -> NavigationViewCoordinator<DefaultAuthCoordinator> {
//        NavigationViewCoordinator(DefaultAuthCoordinator())
//    }
//    
//    private func makeMain() -> DefaultMainCoordinator {
//        DefaultMainCoordinator()
//    }
//}

extension UINavigationController: UIGestureRecognizerDelegate {
    override open func viewDidLoad() {
        super.viewDidLoad()
        interactivePopGestureRecognizer?.delegate = self
    }
    
    public func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        return false
    }
}

final class Coordinator: ObservableObject {
    
    static let shared = Coordinator()
    
    enum Path {
        case auth
        case main
    }
    
    @Published var path: [Path] = []
    
    func showAuth() {
        path.removeAll()
        path.append(.auth)
    }
    
    func showMain() {
        path.removeAll()
        path.append(.main)
    }
}
