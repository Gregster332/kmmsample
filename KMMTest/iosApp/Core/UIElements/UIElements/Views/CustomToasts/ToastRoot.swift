//
//  ToastRoot.swift
//  iosApp
//
//  Created by Greg Zenkov on 2/24/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import UIKit
import Combine
import SharedModule

struct ToastRoot<Content: View>: View {
    var content: () -> Content
    @State private var window: UIWindow?
    
    var body: some View {
        content()
            .onAppear {
                if let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene, window == nil {
                    let window = ClearWindow(windowScene: scene)
                    let root = UIHostingController(rootView: ToastsView())
                    root.view.backgroundColor = .clear
                    root.view.isUserInteractionEnabled = true
                    window.backgroundColor = .clear
                    window.isUserInteractionEnabled = true
                    window.isHidden = false
                    window.tag = 1009
                    window.rootViewController = root
                    self.window = window
                }
            }
    }
}

final class ClearWindow: UIWindow {
    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        guard let view = super.hitTest(point, with: event) else { return nil }
        return rootViewController?.view == view ? nil : view
    }
}

struct ToastView: View {
    let toast: ToastsStore.Toast
    
    @State private var animateIn = false
    @State private var animateOut = false
    
    var body: some View {
        HStack {
            Image(systemName: "xmark")
                .font(.system(size: 16))
            
            Text(toast.name)
                .font(.system(size: 15, weight: .medium))
                .lineLimit(1)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
        .background(
            .background
                .shadow(.drop(color: .black.opacity(0.1), radius: 5)).opacity(0.2),
            in: .rect(cornerRadius: 8),
            fillStyle: .init(eoFill: true, antialiased: false)
        )
        .background(MR.colors().backgroundColor.toSUIColor)
        .clipped()
        .clipShape(Capsule())
        .offset(y: animateIn ? 0 : 150)
        .offset(y: !animateOut ? 0 : 150)
        .animation(.easeIn, value: animateOut)
        .task {
            guard !animateIn else { return }
            withAnimation(.snappy) {
                animateIn = true
            }
            
            try? await Task.sleep(for: toast.timeInterval)
            
            guard !animateOut else { return }
            withAnimation(.snappy) {
                animateOut = true
            }
            
            remove(toast: toast)
        }
    }
    
    func remove(toast: ToastsStore.Toast) {
        ToastsStore.shared.remove(with: toast.id)
    }
}

struct ToastsView: View {
    let model = ToastsStore.shared
    @State private var toasts = [ToastsStore.Toast]()
    
    var body: some View {
        GeometryReader { _ in
            ZStack {
                ForEach(toasts) { toast in
                    ToastView(toast: toast)
                        .scaleEffect(x: scale(for: toast))
                        .offset(y: offset(for: toast))
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottom)
            .padding(.horizontal, 16)
        }
        .onReceive(model.$toasts, perform: { value in
            print(value)
            withAnimation(.snappy) {
                toasts = value
            }
        })
    }
    
    private func scale(for toast: ToastsStore.Toast) -> CGFloat {
        let index = model.toasts.firstIndex(where: { $0.id == toast.id }) ?? 0
        return 1 - (((model.toasts.count - 1) - index) >= 2 ? 0.2 : CGFloat(((model.toasts.count - 1) - index)) * 0.1)
    }
    
    private func offset(for toast: ToastsStore.Toast) -> CGFloat {
        let index = model.toasts.firstIndex(where: { $0.id == toast.id }) ?? 0
        return CGFloat(((model.toasts.count - 1) - index) >= 2
                       ? -20
                       : ((model.toasts.count - 1) - index) * -10)
    }
}

final class ToastsStore: ObservableObject {
    struct Toast: Hashable, Identifiable {
        let id = UUID()
        let name: String
        let timeInterval: Duration
    }
    
    static let shared = ToastsStore()
    @Published private(set) var toasts = [Toast]()
    
    private init() {}
    
    func presentToast(with str: String, timeInterval: Duration = .seconds(3)) {
        toasts.append(Toast(name: str, timeInterval: timeInterval))
    }
    
    func remove(with id: UUID) {
        toasts.removeAll(where: { $0.id == id })
    }
}

#Preview {
    ToastRoot {
        Button {
            ToastsStore.shared.presentToast(with: "Hi! Toast Here!")
        } label: {
            Text("dsds")
        }
    }
}
