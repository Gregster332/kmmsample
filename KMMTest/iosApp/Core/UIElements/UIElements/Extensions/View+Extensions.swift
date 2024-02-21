//
//  View+Extensions.swift
//  UIElements
//
//  Created by Greg Zenkov on 2/17/24.
//

import SwiftUI

public extension View {
    @ViewBuilder
    func viewBackground(color: UIColor) -> some View {
        modifier(ViewBackgroud(color: color))
    }
    
    func sheet<T, Content>(
        item: T?,
        onDismiss: (() -> Void)? = nil,
        isFullScreen: Bool = false,
        @ViewBuilder content: @escaping (T) -> Content
    ) -> some View where Content: View {
        ZStack {
            if isFullScreen {
                self.fullScreenCover(
                    isPresented: .init(
                        get: { item != nil },
                        set: { _ in }),
                    onDismiss: { onDismiss?() },
                    content: { content(item!) })
            } else {
                self.sheet(
                    isPresented: .init(
                        get: { item != nil },
                        set: { _ in }
                    ),
                    onDismiss: { onDismiss?() },
                    content: { content(item!) }
                    
                )
            }
        }
    }
}
