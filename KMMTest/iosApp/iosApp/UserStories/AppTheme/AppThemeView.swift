//
//  AppThemeView.swift
//  iosApp
//
//  Created by Greg Zenkov on 1/13/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SharedModule
import UIElements

struct OffsetPK: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value += nextValue()
    }
}

struct AppThemeView: View {
    
    @State private var offset: CGFloat = 0
    
    private let component: SettingsPageComponent
    
    @ObservedObject
    private var settings: ObservableValue<SettingsStoreSettingsUIState>
    
    init(_ component: SettingsPageComponent) {
        self.component = component
        self.settings = ObservableValue(component.settings)
    }
    
    var body: some View {
        ScrollView(.vertical, showsIndicators: false) {
            VStack(spacing: 12) {
                profileView()
                Text("\(offset)")
                appearenceSection()
            }
            .background {
                GeometryReader { proxy in
                    Color.clear
                        .preference(
                            key: OffsetPK.self,
                            value: proxy.frame(in: .named("SCROLL")).origin.y
                        )
                }
            }
            .onPreferenceChange(OffsetPK.self, perform: { value in
                self.offset = value
            })
        }
        .coordinateSpace(name: "SCROLL")
        .viewBackground(
            color: MR.colors().backgroundColor.getUIColor()
        )
    }
    
    
    private func profileView() -> some View {
        var ideal = offset / 120
        if offset > 0 {
            ideal = 1
        } else {
            ideal = (-ideal) < 0 ? 0 : ideal
            ideal = ideal > 1 ? 1 : ideal
            ideal = 1 + ideal
        }
        
        return VStack(spacing: 8) {
            Circle()
                .fill(Color.blue)
                .frame(width: 100, height: 100)
            
            VStack(spacing: 4) {
                Text(settings.value.userName)
                    .font(.title)
                    .fontWeight(.semibold)
                
                HStack(spacing: 2) {
                    Text(settings.value.userEmail)
                        .tint(.primary)
                    Text("@\(settings.value.userName)")
                        .foregroundStyle(.gray)
                }
            }
        }
        .scaleEffect(x: ideal, y: ideal)
        .opacity(ideal)
    }
    
    @ViewBuilder
    private func appearenceSection() -> some View {
        Section {
            VStack(alignment: .leading) {
                Text("App theme")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundStyle(Color(MR.colors().textPrimary.getUIColor()))
                
                Picker(
                    "",
                    selection: Binding(
                        get: { settings.value.appThemeSection.selectedTheme },
                        set: { component.changeAppTheme(to: $0) })
                ) {
                    ForEach(SharedModule.AppThemeEnum.entries, id: \.self) { theme in
                        Text(theme.name)
                    }
                }
                .pickerStyle(.segmented)
            }
        }
        .padding(8)
        .background(Color.black.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 8))
        .padding(.horizontal, 16)
        
    }
}

extension SharedModule.AppThemeEnum {
    func colorScheme() -> ColorScheme? {
        switch self {
        case .dark: return .dark
        case .light: return .light
        default:
            return nil
        }
    }
}

//#Preview {
//    AppThemeView(AppThemePreview())
//}
