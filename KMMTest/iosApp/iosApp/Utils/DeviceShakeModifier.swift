//
//  DeviceShakeModifier.swift
//  iosApp
//
//  Created by Greg Zenkov on 2/10/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct DeviceShakeModifier: ViewModifier {
    let action: () -> Void
    
    func body(content: Content) -> some View {
        content
            .onReceive(
                NotificationCenter.default.publisher(for: UIDevice.deviceShakeNotification),
                perform: { _ in
                action()
            })
    }
}

extension View {
    @ViewBuilder
    func didShake(action: @escaping () -> Void) -> some View {
        modifier(DeviceShakeModifier(action: action))
    }
}
