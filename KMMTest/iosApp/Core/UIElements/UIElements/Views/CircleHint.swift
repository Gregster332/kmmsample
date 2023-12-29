//
//  CircleHint.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/29/23.
//

import SwiftUI

public struct CircleHint: View {
    
    let image: Image
    let color: Color
    let backgroundColor: Color
    
    public init(
        _ systemImageName: String,
        _ color: Color,
        _ backgroundColor: Color
    ) {
        self.image = Image(systemName: systemImageName)
        self.color = color
        self.backgroundColor = backgroundColor
    }
    
    public var body: some View {
        image
            .font(.system(size: 18, weight: .semibold))
            .foregroundStyle(color)
            .transition(.opacity)
            .padding(10)
            .background {
                Circle()
                    .fill(backgroundColor)
            }
    }
}
