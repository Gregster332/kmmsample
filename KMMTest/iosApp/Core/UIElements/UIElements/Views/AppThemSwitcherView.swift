//
//  AppThemSwitcherView.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/29/23.
//

import SwiftUI



public struct AppThemSwitcherView: View {
    
    public enum AppTheme: String, CaseIterable {
        case systemDefault = "Default"
        case dark = "Dark"
        case light = "Light"
    }
    
    @Environment(\.colorScheme) var colorScheme: ColorScheme
    @AppStorage("app_theme") private var appTheme: AppTheme = .systemDefault
    @Namespace private var animation
    
    public init() {}
    
    public var body: some View {
        VStack(alignment: .center, spacing: 8) {
            Text("Choose the main app theme")
                .font(.title2.bold())
                .padding()
            
            HStack {
                ForEach(AppTheme.allCases, id: \.self) { theme in
                    Text(theme.rawValue)
                        .font(.system(size: 18, weight: .semibold))
                        .padding(10)
                        .foregroundStyle(colorScheme == .dark ? .black : .white)
                        .frame(width: 90)
                        .background {
                            ZStack {
                                if appTheme == theme {
                                    Capsule()
                                        .fill(.primary)
                                        .matchedGeometryEffect(id: "Active", in: animation)
                                }
                            }
                            .animation(.snappy, value: appTheme)
                        }
                        .contentShape(.rect)
                        .onTapGesture {
                            withAnimation(.easeOut) {
                                appTheme = theme
                            }
                        }
                }
            }
            .padding(2)
            .background {
                Capsule()
                    .fill(.gray.opacity(0.3))
                    
            }
        }
        .ignoresSafeArea(.all)
    }
    
    private var selectedColor: Color {
        switch colorScheme {
        case .light:
            return .black
        case .dark:
            return .white
        @unknown default:
            return .clear
        }
    }
}

#Preview {
    AppThemSwitcherView()
        .environment(\.colorScheme, .dark)
}
