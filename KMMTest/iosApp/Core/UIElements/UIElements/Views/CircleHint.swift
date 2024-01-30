//
//  CircleHint.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/29/23.
//

import SwiftUI

public enum CircleHintStyle {
    case success
    case failure
}

public struct CircleHint: View {
    
    let style: CircleHintStyle
    let color: Color
    
    public init(
        _ style: CircleHintStyle,
        _ color: Color
    ) {
        self.style = style
        self.color = color
    }
    
    public var body: some View {
        ZStack {
            Circle()
                .fill(color)
            
            style.image
                .font(.system(size: 16, weight: .semibold))
                .foregroundStyle(.primary)
        }
    }
}

extension CircleHintStyle {
    var image: Image {
        switch self {
        case .success:
            return Image(systemName: "checkmark")
        case .failure:
            return Image(systemName: "xmark")
        }
    }
}

#Preview {
    CircleHint(.failure, .accentColor)
}
