//
//  ChildStack.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/10/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SharedModule

struct StackView<T: AnyObject, Content: View>: View {
    
    @ObservedObject
    var stackValue: ObservableValue<ChildStack<AnyObject, T>>
    
    var getTitle: (T) -> String
    var tintColor: SwiftUI.Color
    var onBack: () -> Void
    
    @ViewBuilder
    var childContent: (T) -> Content
    
    var stack: [Child<AnyObject, T>] { stackValue.value.items }
    
    var body: some View {
        NavigationStack(
            path: Binding(
                get: {
                    stack.dropFirst()
                },
                set: { _ in onBack() }))
        {
            childContent(stack.first!.instance!)
                .navigationDestination(for: Child<AnyObject, T>.self) {
                    childContent($0.instance!)
                }
        }
        .tint(tintColor)
        .onAppear {
            let appearance = UINavigationBarAppearance()
            appearance.backgroundEffect = UIBlurEffect(style: .systemUltraThinMaterialLight)
            appearance.backgroundColor = MR.colors().backgroundColor.getUIColor()
            appearance.shadowColor = .clear
            UINavigationBar.appearance().standardAppearance = appearance
            UINavigationBar.appearance().scrollEdgeAppearance = appearance
        }
    }
}
