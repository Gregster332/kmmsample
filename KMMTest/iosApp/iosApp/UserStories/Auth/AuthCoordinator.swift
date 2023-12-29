//
//  AuthCoordinator.swift
//  iosApp
//
//  Created by Greg Zenkov on 12/25/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Stinsen
import SwiftUI

protocol AuthCoordinator: AnyObject {
    func openMain()
}

final class DefaultAuthCoordinator: AuthCoordinator, NavigationCoordinatable {
    var stack = NavigationStack(initial: \DefaultAuthCoordinator.start)
    
    @Root var start = makeStart
    @Root var main = makeMain
    
    private func makeStart() -> some View {
        return AuthView()
    }
    
    private func makeMain() -> DefaultMainCoordinator {
        DefaultMainCoordinator()
    }
    
    func openMain() {
        self.root(\.main)
    }
}

protocol MainCoordinator: AnyObject {
    func push()
}

final class DefaultMainCoordinator: MainCoordinator, NavigationCoordinatable {
    var stack = NavigationStack<DefaultMainCoordinator>(
        initial: \DefaultMainCoordinator.start
    )
    
    @Root var start = makeStart
    @Route(.push) var helloScreen = helloView
    
    func push() {
        self.route(to: \.helloScreen)
    }
    
    private func makeStart() -> some View {
        MainView()
    }
    
    private func helloView() -> some View {
        VStack {
            Text("Hello!")
        }
    }
}
