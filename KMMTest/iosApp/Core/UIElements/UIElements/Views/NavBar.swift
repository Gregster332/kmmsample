//
//  NavBar.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/24/23.
//

import SwiftUI

public struct NavBar<Content: View>: View {
    
    let title: String
    let scheme: ColorScheme
    let subtitle: String?
    let onTapLeadingButton: (() -> Void)?
    let viewBlock: (() -> Content)?
    
    public init(
        title: String,
        scheme: ColorScheme,
        subtitle: String? = nil,
        onTapLeadingButton: (() -> Void)?,
        viewBlock: (() -> Content)?
    ) {
        self.title = title
        self.subtitle = subtitle
        self.scheme = scheme
        self.onTapLeadingButton = onTapLeadingButton
        self.viewBlock = viewBlock
    }
    
    public var body: some View {
        ZStack(alignment: .top) {
            viewBlock?()
            
            HStack(alignment: .center) {
                
                Button(action: {
                    onTapLeadingButton?()
                }, label: {
                    Text("Button")
                })
                .frame(maxHeight: 40)
                
                Spacer()
                
                VStack {
                    Text(title)
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundStyle(.white)
                    
                    if let subtitle = subtitle {
                        Text(subtitle)
                            .font(.system(size: 14, weight: .medium))
                            .foregroundStyle(.white)
                    }
                }
                
                Spacer()
                
                Button(action: {
                    onTapLeadingButton?()
                }, label: {
                    Text("Button")
                })
                .frame(maxHeight: 40)
            }
            .padding(.horizontal, 8)
            .frame(height: 44)
            .frame(maxWidth: .infinity)
            .background(makeNavBarColor())
            .environment(\.colorScheme, scheme)
        }
    }
    
    private func makeNavBarColor() -> Color {
        switch scheme {
        case .light:
            return .gray.opacity(0.4)
        case .dark:
            return .black.opacity(0.9)
        @unknown default:
            return .clear
        }
    }
}

#Preview {
    NavBar(title: "Hello", scheme: .dark, subtitle: "Hello", onTapLeadingButton: {}, viewBlock: {EmptyView()})
}
