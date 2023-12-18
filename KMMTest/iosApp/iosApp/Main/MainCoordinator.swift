//
//  MainCoordinator.swift
//  iosApp
//
//  Created by Greg Zenkov on 10/10/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import UIKit
import SwiftUI
import SharedModule

final class FlowCoordinator: ObservableObject, Flowable {
    var window: UIWindow?
    
    init(window: UIWindow) {
        self.window = window
    }
    
    func showRootView() {
        var swiftUIView: any View
        if #available(iOS 16.2, *) {
            
            swiftUIView = ContentView().environmentObject(self)
        } else {
            swiftUIView = Text("Hello")
        }
        setRoot(swiftUIView).configure { vc in
            vc?.title = "Nerd"
        }
    }
    
    func showDetailView() {
        let newView = SomeView().environmentObject(self)
        push(newView).configure { vc in
            vc?.title = "Hello"
        }
    }
    
    func presenrThird() {
        let third = SomeView1()
        presentFullScreen(third).configure { vc in
            vc?.title = "redddd"
            vc?.view.backgroundColor = .red
        }
    }
}

//class BaseCoordinator: Flowable {
//    
//    final private var window
//}
