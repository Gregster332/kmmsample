//
//  AppThemSwitcherView.swift
//  UIElements
//
//  Created by Greg Zenkov on 12/29/23.
//

import SwiftUI

public enum AppTheme: String, CaseIterable {
    case systemDefault = "Default"
    case dark = "Dark"
    case light = "Light"
}

public struct AppThemSwitcherView: View {
    
    @Binding public var toggle: Bool
    var colorScheme: ColorScheme
    @AppStorage("app_theme") private var appTheme: AppTheme = .systemDefault
    
    public init(toggle: Binding<Bool>, colorScheme: ColorScheme) {
        self._toggle = toggle
        self.colorScheme = colorScheme
    }
    
    public var body: some View {
        
            VStack(alignment: .center, spacing: 8) {
                Text("Choose the main app theme")
                    .font(.system(size: 20, weight: .semibold))
                    .padding()
                
                HStack {
                    ForEach(AppTheme.allCases, id: \.self) { theme in
                        Text(theme.rawValue)
                            .font(.system(size: 16, weight: .medium))
                            .padding(10)
                            //.frame(width: geo.size.width * 0.2)
                            .background(theme == appTheme ? selectedColor : Color.clear)
                            .clipShape(Capsule())
                            .onTapGesture {
                                appTheme = theme
                            }
                    }
                }
                .padding(2)
                .background {
                    Capsule()
                        .fill(.gray.opacity(0.3))
                }
                .padding(.bottom, 24)
            //                        .gesture(DragGesture().onEnded {
            //                            if $0.translation.height > 0 {
            //                                withAnimation(.snappy) {
            //                                    toggle = false
            //                                }
            //                            }
            //                        })
        }
        .ignoresSafeArea(.all)
        //.preferredColorScheme(colorScheme)
        .environment(\.colorScheme, colorScheme)
    }
    
    private var selectedColor: Color {
        switch colorScheme {
        case .light:
            return .white
        case .dark:
            return .black
        @unknown default:
            return .clear
        }
    }
}

#Preview {
    AppThemSwitcherView(toggle: .constant(true), colorScheme: ColorScheme.light)
}
